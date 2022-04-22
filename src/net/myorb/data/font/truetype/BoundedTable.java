
package net.myorb.data.font.truetype;

import net.myorb.utilities.*;

/**
 * support for value range on coordinates in this object structure
 * @author Michael Druckman
 */
public class BoundedTable extends BlockManager
{


	public static final String xMin = "xMin", yMin = "yMin", xMax = "xMax", yMax = "yMax";

	public static final BlockDefinition BOUNDARY_DEFINITION = new BlockDefinition ();

	static
	{
		BOUNDARY_DEFINITION.add (	xMin,	S16		);
		BOUNDARY_DEFINITION.add (	yMin, 	S16		);
		BOUNDARY_DEFINITION.add (	xMax,	S16		);
		BOUNDARY_DEFINITION.add (	yMax, 	S16		);
	}

	public BoundedTable
	(BlockDefinition definition)
	{
		super (definition);
	}

	public short getXMin () { return getProperty (xMin).shortValue (); }
	public short getYMin () { return getProperty (yMin).shortValue (); }
	public short getXMax () { return getProperty (xMax).shortValue (); }
	public short getYMax () { return getProperty (yMax).shortValue (); }


	/* (non-Javadoc)
	 * @see net.myorb.utilities.BlockManager#toString()
	 */
	public String toString ()
	{
		return super.toString () +
				" X: " + bounds (getXMin (), getXMax ()) + 
				" Y: " + bounds (getYMin (), getYMax ());
	}
	public String bounds (int lo, int hi) { return lo + ".." + hi; }


	/**
	 * @param x value to check against X range
	 * @return TRUE : in range
	 */
	public boolean isInXRange (int x)
	{
		return x >= getXMin () && x <= getXMax ();
	}


	/**
	 * @param y value to check against Y range
	 * @return TRUE : in range
	 */
	public boolean isInYRange (int y)
	{
		return y >= getYMin () && y <= getYMax ();
	}


}

