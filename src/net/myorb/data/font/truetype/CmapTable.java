
package net.myorb.data.font.truetype;

import net.myorb.data.font.TTF;

import net.myorb.utilities.BlockManager;
import net.myorb.utilities.AccessControl;

/**
 * definition of CMAP table
 * @author Michael Druckman
 */
public class CmapTable extends BlockManager
{


//	Type	Description
//	USHORT	Table version number (0).
//	USHORT	Number of encoding tables, n.


	/**
	 * map character codes to GLYF indicies
	 */
	public interface GlyfMap
	{
		/**
		 * find the GLYF index for a character code
		 * @param characterCode the character value to be translated
		 * @return the index into the GLYF table
		 * @throws Exception for any errors
		 */
		int getGlyfIndexFor (int characterCode) throws Exception;
	}


	public static final String
	versionNumber				= "versionNumber",
	numberOfEncodingTables		= "numberOfEncodingTables";

	public static final BlockDefinition DEFINITION = new BlockDefinition ();

	static
	{
		DEFINITION.add (	versionNumber,				U16		);
		DEFINITION.add (	numberOfEncodingTables, 	U16		);
	}

	/**
	 * describe table properties
	 */
	public CmapTable () { super (DEFINITION); setBlockName (BLOCK_NAME); }


	/* (non-Javadoc)
	 * @see net.myorb.utilities.BlockManager#readExtendedTableProperties(net.myorb.utilities.AccessControl.Access)
	 */
	public void readExtendedTableProperties (AccessControl.Access access) throws Exception
	{
		parseCollection
		(
			getProperty (numberOfEncodingTables).intValue (),
			EncodingTable.class, encodingTables, access, null
		);
	}
	protected SimleCollection<EncodingTable> encodingTables = new SimleCollection<EncodingTable>();


	/**
	 * @param ttf the font description holding this table
	 * @return array of encoding tables from this CMAP indexed by format
	 * @throws Exception for any errors
	 */
	public EncodingTable[] getEncodingTables (TTF ttf) throws Exception
	{
		int base = ttf.getFileHeader ().find ("cmap").getOffset ();
		EncodingTable[] tables = new EncodingTable[10];							// index by format number 0..9
		for (EncodingTable table : encodingTables)								// pass over all available tables
		{
			table.readSubtableHeader (base, ttf);								// read just headers (which holds format)
			tables[table.getFormat ()] = table;									// include each in table by format
		}
		return tables;
	}


	/**
	 * @param ttf the font description holding this table
	 * @return an EncodingTable based on the availability
	 * @throws Exception for any errors
	 */
	public EncodingTable getPreferredEncodingTable (TTF ttf) throws Exception
	{
		EncodingTable[]
		tables = getEncodingTables (ttf);										// get array of tables indexed by format
		for (byte preference : preferences)										// step thru preferences of format numbers
		{
			if (tables[preference] != null) return tables[preference];			// if preference is available return associated table
		}
		throw new Exception ("GlyfMap implementation not available");			// no form of preferred format is available
	}
	private byte[] preferences = new byte[]{4,6,0,2};


	/**
	 * @param ttf the font description holding this table
	 * @return a GlyfMap based on the available encoding tables
	 * @throws Exception for any errors
	 */
	public GlyfMap getGlyfMap (TTF ttf) throws Exception
	{
		EncodingTable table;
		switch ((table = getPreferredEncodingTable (ttf)).getFormat ())			// format number identifies which class to populate
		{
			case 4:		return new SegmentMapping (table).read (ttf);			// SegmentMapping (format 4) is the Microsoft standard Unicode table
			case 6:		return new TrimmedMapping (table).read (ttf);			// TrimmedMapping (format 6) is a single segment alternative to SegmentMapping
			case 0:		return new ByteEncoding (table).read (ttf);				// ByteEncoding (format 0) is the Apple ASCII substitution table
			case 2:		return new HighByteMap (table);							// HighByteMap (format 2) is used for CJK extended font types
			default:	return null;
		}
	}


	/**
	 * @param ttf the font description holding this table
	 * @return the CMAP table from the specified font
	 * @throws Exception for any errors
	 */
	public static CmapTable getTable (TTF ttf) throws Exception
	{
		return ttf.getFileHeader ().getTable (BLOCK_NAME, CmapTable.class, ttf);
	}
	public static final String BLOCK_NAME = "cmap";


}

