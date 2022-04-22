
package net.myorb.data.font.truetype;

import net.myorb.data.font.TTF;

import net.myorb.gui.graphics.Plotter;
import net.myorb.utilities.AccessControl;

import net.myorb.data.abstractions.ComparableNumber;
import net.myorb.data.abstractions.SimpleUtilities;

import java.util.ArrayList;
import java.util.List;

/**
 * the SIMPLE sub-class of the GLYF table entry type
 * @author Michael Druckman
 */
public class SimpleGlyf extends GlyfTableEntry
{


	public static final int
	ON_CURVE	= 0x01,
	X_IS_BYTE	= 0x02,
	Y_IS_BYTE	= 0x04,
	REPEAT		= 0x08,
	X_DELTA		= 0x10,
	Y_DELTA		= 0x20,
	RESERVED	= 0xC0;


	/**
	 * @param glyfTable header portion of table
	 */
	public SimpleGlyf (GlyfTableEntry glyfTable)
	{
		copyPropertiesFrom (glyfTable);
		// block header holds number of contours in this GLYF
		this.numberOfContours = getProperty (contourCount).intValue ();
	}


	/**
	 * compute the X or Y portion of the point list
	 * @param coord compiled list of coordinate values
	 * @param access the file pointer to the TTF source
	 * @param byteFlag a flag mask indicating that offset is byte
	 * @param deltaFlag a flag mask for indicating the offset is short
	 * @param name the name of the coordinate for use in error messages
	 * @param min the smallest value expected for this coordinate
	 * @param max the largest value expected for this coordinate
	 * @throws Exception for any errors
	 */
	public void readCoord
	(
		List<Integer> coord, TTF.Access access,
		int byteFlag, int deltaFlag, String name,
		int min, int max
	)
	throws Exception
	{
		int value = 0;
		for (int i = 0; i < numberOfPoints; i++)
		{
			value += getCoordinateDelta														// each point is delta from prior
				(
					flags.get (i), byteFlag, deltaFlag, access								// delta is computed from flag and read byte/word
				);
			if (value < min || value > max)													// check for expected range
			{ System.out.print (" *** " + name + "=" + value + " "); }
			coord.add (value);
		}
	}
	protected int getCoordinateDelta
	(
		int flag,
		int byteFlag, int deltaFlag,
		TTF.Access access
	)
	throws Exception
	{
		if (isSet (flag, byteFlag))															// delta is unsigned byte
		{
			int delta = access.readUnsignedByte ();
			if (!isSet (flag, deltaFlag)) return -delta;									// delta flag indicates sign of change
			else return delta;
		}
		else if (!isSet (flag, deltaFlag))													// not byte, check for delta flag clear
		{ return access.readShort (); }														// alternative is signed short
		else return 0;
	}
	protected List<Integer>
		x = new ArrayList<Integer>(),
		y = new ArrayList<Integer>();														// X and Y coordinate lists
	protected int numberOfPoints;


	/**
	 * read the extended block of the sub-class
	 * @param access the file pointer to the TTF source
	 * @return THIS extended version of the GLYF object
	 * @throws Exception for any errors
	 */
	public SimpleGlyf read (TTF.Access access) throws Exception
	{
		readContourEndPoints (access);														// list of contour end points
		computeAndRememberNumberOfPoints ();												// number of points found as highest contour end
		readInstructions (access);															// read instruction bytes

		if (numberOfContours > 0)
		{
			readFlags (access);																// read block of flag bytes
			readCoord (x, access, X_IS_BYTE, X_DELTA, "x", getXMin (), getXMax ());			// read X coordinates of points
			readCoord (y, access, Y_IS_BYTE, Y_DELTA, "y", getYMin (), getYMax ());			// read Y coordinates of points
		}

		return this;
	}
	void readContourEndPoints (TTF.Access access) throws Exception
	{
		endPtsOfContours = AccessControl.getElementList (access, U16, numberOfContours);	// point count of end of each contour
	}
	void computeAndRememberNumberOfPoints ()
	{
		numberOfPoints = SimpleUtilities.largestOf (endPtsOfContours).intValue () + 1;
		propertyMap.put ("numberOfPoints", numberOfPoints);
	}
	protected ComparableNumber.List endPtsOfContours;


	/**
	 * draw the GLYF using a plotter implementation
	 * @param plotter the graphics interface to use for drawing
	 * @throws Exception for any errors
	 */
	public void draw (Plotter plotter) throws Exception
	{
		errorCheck ();																		// check coordinate lists

		plotter.scaleFor (getXMin (), getXMax (), getYMin (), getYMax ());					// set scaling in plotter object

		int contour = 0;
		int lastContour = endPtsOfContours.size ();
		int nextEnd = endPtsOfContours.get (contour++).intValue ();							// first contour end point

		int priorX = 0, priorY = 0,															// prior point
				startX = 0, startY = 0,														// first contour point
				nextX = 0, nextY = 0;														// next point
		int pointCount = x.size ();
        boolean penUp = true;

        for (int point = 0; point < pointCount; point++)
        {
        	if (!penUp)
        	{ priorX = nextX; priorY = nextY; }												// copy next point to prior
        	nextX = x.get (point); nextY = y.get (point);									// get next point in contour

        	if (penUp)
        	{
        		startX = nextX; startY = nextY;												// remember start of contour
        		penUp = false;
        	}
        	else
        	{
        		plotter.drawLine (priorX, priorY, nextX, nextY);							// draw line from prior to next points
        	}

        	if (point == nextEnd)															// found end of current contour
        	{
        		if (contour < lastContour)													// is this the last contour?
        		{ nextEnd = endPtsOfContours.get (contour++).intValue (); }					// note next contour end

        		plotter.drawLine (nextX, nextY, startX, startY);							// close the contour

        		penUp = true;
        	}
        }
	}
	private void errorCheck () throws Exception
	{
		int xsize = x.size ();
		if (xsize != y.size ())
			throw new Exception ("Coordinates not consistant");								// X and Y lists must be same size
		if (xsize < 2) throw new Exception ("Too few points");								// at least one line must be present
	}


	/**
	 * instructions read as counted array of bytes
	 * @param access the file pointer to the TTF source
	 * @throws Exception for any errors
	 */
	public void readInstructions (TTF.Access access) throws Exception
	{
		int instructionByteCount = access.readUnsignedShort ();
		instructions = new byte[instructionByteCount];
		access.readFully (instructions);
	}
	protected byte instructions[];


	/**
	 * collect a flag per point of GLYF
	 * @param access the file pointer to the TTF source
	 * @throws Exception for any errors
	 */
	public void readFlags (TTF.Access access) throws Exception
	{
		int rpt = 0, flag = 0,
			rem = numberOfPoints;															// expect a flag for each point
		while (rem-- > 0)
		{
			flags.add (flag = access.readUnsignedByte ());									// flag always posted at least once
			if (isSet (flag, RESERVED)) { System.out.print (" *FLAG ERR* "); }				// RESERVED bits must be found to be zero
			if (isSet (flag, REPEAT)) rem -= (rpt = access.readUnsignedByte ());			// read repeat count if REPEAT flag is set in flag
			if (rem < 0) { System.out.print (" *RPT ERR* "); }								// error for repeat count over-reduced remainder
			while (rpt-- > 0) flags.add (flag);												// post flag repeat-count times
		}
	}
	protected List<Integer> flags = new ArrayList<Integer>();


	/* (non-Javadoc)
	 * @see net.myorb.data.font.truetype.GlyfTable#toString()
	 */
	public String toString ()
	{
		return super.toString () + " Flags: " + flags +
			" Points: [" + x + "," + y + "] Ends: {" + endPtsOfContours + "}";
	}


}

