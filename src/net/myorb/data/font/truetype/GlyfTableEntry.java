
package net.myorb.data.font.truetype;

import net.myorb.data.font.TTF;

import net.myorb.utilities.*;

/**
 * the root of GLYF table entries
 * @author Michael Druckman
 */
public class GlyfTableEntry extends BoundedTable
{


	public static final String contourCount = "numberOfContours";

	public static final BlockDefinition DEFINITION = new BlockDefinition ();

	static
	{
		DEFINITION.add (	contourCount,	S16		);
	}

	/**
	 * describe table properties
	 */
	public GlyfTableEntry ()
	{
		super (DEFINITION.plus (BOUNDARY_DEFINITION)); setBlockName (BLOCK_NAME);
	}
	public static final String BLOCK_NAME = "glyf";


	/**
	 * @param forIndex the sequential index into the GLYF table
	 * @param ttf the TTF object holding the GLYF table being parsed
	 * @return the GLYF parsed from the specified index point of the table
	 * @throws Exception for any errors
	 */
	public static GlyfTableEntry getGlyfEntry (int forIndex, TTF ttf) throws Exception
	{
		OffsetTableDescriptor d =
				ttf.getFileHeader ().find ("glyf");
		int offset = d.getOffset (), length = d.getLength ();
		int glyfOffset = offset + ttf.getLocaTable ().readEntry (forIndex);
		int glyfStart = offset, glyfEnd = offset + length;
		if (glyfOffset >= glyfEnd) return null;

		if (glyfOffset < glyfStart || glyfOffset >= glyfEnd)
		{
			throw new Exception ("Error in glyf boundaries");
		}

		GlyfTableEntry glyf;
		ttf.readBlock (glyf = new GlyfTableEntry (), glyfOffset);
		return glyf.getExtendedTableEntry ();
	}


	/**
	 * @return TRUE : contour count implies GLYF is simple type
	 */
	public boolean isSimpleGlyf ()
	{
		return getProperty (contourCount).intValue () >= 0;
	}
	protected int numberOfContours;


	/* (non-Javadoc)
	 * @see net.myorb.utilities.BlockManager#readExtendedTableProperties(net.myorb.utilities.AccessControl.Access)
	 */
	public void readExtendedTableProperties (AccessControl.Access access) throws Exception
	{ extendedTableEntry = isSimpleGlyf () ? new SimpleGlyf (this).read (access) : new CompoundGlyf (this).read (access); }
	public GlyfTableEntry getExtendedTableEntry () { return extendedTableEntry; }
	protected GlyfTableEntry extendedTableEntry;


}

