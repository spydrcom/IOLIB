
package net.myorb.data.font.truetype;

import net.myorb.data.font.TTF;
import net.myorb.data.abstractions.ComparableNumber;
import net.myorb.utilities.*;

/**
 * definition of encoding sub-tables from CMAP table
 * @author Michael Druckman
 */
public class EncodingTable extends BlockManager
{


//	Type	Description
//	USHORT	Platform ID.
//	USHORT	Platform-specific encoding ID.
//	ULONG	Byte offset from beginning of table to the subtable for this encoding.


	public static final String
	platformId			= "platformId",
	encodingId			= "encodingId",
	subtableOffset		= "subtableOffset",
	subtableBase		= "subtableBase";

	public static final BlockDefinition DEFINITION = new BlockDefinition ();

	static
	{
		DEFINITION.add (	platformId,			U16		);
		DEFINITION.add (	encodingId, 		U16		);
		DEFINITION.add (	subtableOffset,		U32		);
	}


	/**
	 * @param cmapBase the CMAP table base
	 * @param ttf the TTF object holding this tables
	 * @throws Exception for any errors
	 */
	public void readSubtableHeader (int cmapBase, TTF ttf) throws Exception
	{
		int computedSubtableBase;
		propertyMap.put (subtableBase, computedSubtableBase = cmapBase + getProperty (subtableOffset).intValue ());
		GenericSubBlock.supplement (this, SubtableHeader.SUBTABLE_HEADER_DEFINITION, ttf, computedSubtableBase);
	}


	/**
	 * @return the identifier for the format identified in sub-table
	 */
	public int getFormat ()
	{
		return getProperty (SubtableHeader.format).intValue ();
	}


	/**
	 * describe table properties
	 */
	public EncodingTable ()
	{
		super (DEFINITION); setBlockName (BLOCK_NAME);
	}
	public static final String BLOCK_NAME = "ENCD";


}


/**
 * encoding table discrim is found in 3 word header
 */
class SubtableHeader extends BlockManager
{

//	Type	Name	Description
//	USHORT	format	Format number is set to 0. 
//	USHORT	length	This is the length in bytes of the sub-table.
//	USHORT	version	Version number (starts at 0).
//	BYTE	glyphIdArray[256]	An array that maps character codes to GLYF index values.

	public static final String
	format		= "format",
	length		= "length",
	version		= "version";

	public static final BlockDefinition SUBTABLE_HEADER_DEFINITION = new BlockDefinition ();

	static
	{
		SUBTABLE_HEADER_DEFINITION.add (	format,			U16		);
		SUBTABLE_HEADER_DEFINITION.add (	length, 		U16		);
		SUBTABLE_HEADER_DEFINITION.add (	version,		U16		);
	}

	/**
	 * provide SUPER for sub-class defined tables
	 * @param subTableDefinition extended property list defined in sub-class
	 * @param blockName the block name for the sub-class
	 */
	public SubtableHeader
	(BlockDefinition subTableDefinition, String blockName)
	{
		super (SUBTABLE_HEADER_DEFINITION.plus (subTableDefinition)); setBlockName (blockName);
	}

	/**
	 * describe table properties
	 */
	public SubtableHeader ()
	{
		super (SUBTABLE_HEADER_DEFINITION); setBlockName (BLOCK_NAME);
	}
	public static final String BLOCK_NAME = "STHD";

}


/**
 * Format 6 encoding implementation
 */
class TrimmedMapping extends SubtableHeader implements CmapTable.GlyfMap
{

	/*
		Format 6: Trimmed table mapping

		Type	Name	Description
		USHORT	format	Format number is set to 6.
		USHORT	length	Length in bytes. 
		USHORT	version	Version number (starts at 0)
		USHORT	firstCode	First character code of subrange.
		USHORT	entryCount	Number of character codes in subrange.
		USHORT	glyphIdArray [entryCount]	Array of glyph index values
				for character codes in the range.

		The firstCode and entryCount values specify a subrange
		(beginning at firstCode,length = entryCount) within the range
		of possible character codes. Codes outside of this subrange are
		mapped to glyph index 0. The offset of the code (from the first code)
		within this subrange is used as index to the glyphIdArray,
		which provides the glyph index value.
	 */

	public static final String
	firstCode	= "firstCode",
	entryCount	= "entryCount";

	public static final BlockDefinition SUBTABLE = new BlockDefinition ();

	static
	{
		SUBTABLE.add (		firstCode,		U16		);
		SUBTABLE.add (		entryCount, 	U16		);
	}

	TrimmedMapping (EncodingTable table)
	{
		super (SUBTABLE, "TMET"); copyPropertiesFrom (table);
	}

	/**
	 * @param ttf the source TTF for this table
	 * @return the GLYF map based on this CMAP description
	 * @throws Exception for any errors
	 */
	CmapTable.GlyfMap read (TTF ttf) throws Exception
	{
		int offset = getProperty
			(EncodingTable.subtableBase).intValue ();
		ttf.readBlock (this, offset);
		return this;
	}

	/* (non-Javadoc)
	 * @see net.myorb.utilities.BlockManager#readExtendedTableProperties(net.myorb.utilities.AccessControl.Access)
	 */
	public void readExtendedTableProperties
	(AccessControl.Access access)
	throws Exception
	{
		int entries = propertyMap.get (entryCount).intValue ();
		characterMap = AccessControl.getElementList (access, U16, entries);
		startingCode = propertyMap.get (firstCode).intValue ();
		endingCode = startingCode + entries - 1;
	}
	ComparableNumber.List characterMap = new ComparableNumber.List ();

	/* (non-Javadoc)
	 * @see net.myorb.data.font.truetype.CmapTable.GlyfMap#getGlyfIndexFor(int)
	 */
	public int getGlyfIndexFor (int characterCode) throws Exception
	{
		int reducedCode = characterCode - startingCode;
		if (reducedCode < 0 || reducedCode > endingCode) return 0;
		return characterMap.get (reducedCode).intValue ();
	}
	int startingCode, endingCode;

}

/**
 * Format 4 encoding implementation
 */
class SegmentMapping extends SubtableHeader implements CmapTable.GlyfMap
{

	/*
		Format 4: Segment mapping to delta values

		This is the Microsoft standard character to glyph index mapping table. 
		This format is used when the character codes for the characters represented by a font fall into several contiguous ranges,
		possibly with holes in some or all of the ranges (that is, some of the codes in a range may not have a representation in the font). 
		The format-dependent data is divided into three parts, which must occur in the following order: 

		1.	A four-word header gives parameters for an optimized search of the segment list; 
		2.	Four parallel arrays describe the segments (one segment for each contiguous range of codes); 
		3.	A variable-length array of glyph IDs (unsigned words).
		
		Type
		Name	Description
		USHORT	format	Format number is set to 4. 
		USHORT	length	Length in bytes. 
		USHORT	version	Version number (starts at 0).
		USHORT	segCountX2 	2 x segCount.
		USHORT	searchRange 	2 x (2**floor(log2(segCount)))
		USHORT	entrySelector 	log2(searchRange/2)
		USHORT	rangeShift 	2 x segCount - searchRange
		USHORT	endCount[segCount]	End characterCode for each segment, last =0xFFFF.
		USHORT	reservedPad	Set to 0.
		USHORT	startCount[segCount]	Start character code for each segment.
		USHORT	idDelta[segCount]	Delta for all character codes in segment.
		USHORT	idRangeOffset[segCount]	Offsets into glyphIdArray or 0
		USHORT	glyphIdArray[ ]	Glyph index array (arbitrary length)

		The number of segments is specified by segCount, which is not explicitly in the header; 
		however, all of the header parameters are derived from it. The searchRange value is twice 
		the largest power of 2 that is less than or equal to  segCount. For example, 
		if segCount=39, we have the following:

		segCountX2	78
		searchRange	64	(2 * largest power of 2  39)
		entrySelector	5	log2(32)
		rangeShift	14	2 x 39 - 64

		Each segment is described by a startCode and endCode, along with an idDelta and an idRangeOffset, 
		which are used for mapping the character codes in the segment. The segments are sorted in order of 
		increasing endCode values, and the segment values are specified in four parallel arrays. You search 
		for the first endCode that is greater than or equal to the character code you want to map. If the 
		corresponding startCode is less than or equal to the character code, then you use the corresponding 
		idDelta and idRangeOffset to map the character code to a glyph index (otherwise, the missingGlyph is 
		returned). 
		
		For the search to terminate, the final endCode value must be 0xFFFF. This segment need not 
		contain any valid mappings. (It can just map the single character code 0xFFFF to missingGlyph). 
		However, the segment must be present.

		If the idRangeOffset value for the segment is not 0, the mapping of character codes relies on glyphIdArray. 
		The character code offset from startCode is added to the idRangeOffset value. This sum is used as an offset 
		from the current location within idRangeOffset itself to index out the correct glyphIdArray value. This 
		obscure indexing trick works because glyphIdArray immediately follows idRangeOffset in the font file. 
		The C expression that yields the glyph index is:
		
		*(idRangeOffset[i]/2 + (c - startCount[i]) + &idRangeOffset[i])

		The value c  is the character code in question, and i  is the segment index in which c  appears. 
		If the value obtained from the indexing operation is not 0 (which indicates missingGlyph), 
		idDelta[i] is added to it to get the glyph index. The idDelta arithmetic is modulo 65536.

		If the idRangeOffset is 0, the idDelta value is added directly to the character code offset 
		(i.e. idDelta[i] + c) to get the corresponding glyph index. Again, the idDelta arithmetic is modulo 65536.
		As an example, the variant part of the table to map characters 10–20, 30–90, 
		and 100–153 onto a contiguous range of glyph indices may look like this:

		segCountX2:	8
		searchRange:	8
		entrySelector:	4
		rangeShift:	0
		endCode:	20	90	153	0xFFFF
		reservedPad:	0
		startCode:	10	30	100	0xFFFF
		idDelta:	-9	-18	-27	1
		idRangeOffset:	0	0	0	0
		 
		This table performs the following mappings:
		10 –> 10 – 9 = 1
		20 –> 20 – 9 = 11
		30 –> 30 – 18 = 12
		90 –> 90 – 18 = 72
		...and so on.

		Note that the delta values could be reworked so as to reorder the segments.
	 */

	public static final String
	segCountX2			= "segCountX2",
	searchRange			= "searchRange",
	entrySelector		= "entrySelector",
	glyfIdTableLength	= "glyfIdTableLength",
	rangeShift			= "rangeShift";

	public static final BlockDefinition SUBTABLE = new BlockDefinition ();

	static
	{
		SUBTABLE.add (	segCountX2,		U16		);
		SUBTABLE.add (	searchRange, 	U16		);
		SUBTABLE.add (	entrySelector, 	U16		);
		SUBTABLE.add (	rangeShift, 	U16		);
	}

	SegmentMapping (EncodingTable table)
	{
		super (SUBTABLE, "SMET"); copyPropertiesFrom (table);
	}

	/**
	 * @param ttf the source TTF for this table
	 * @return the GLYF map based on this CMAP description
	 * @throws Exception for any errors
	 */
	CmapTable.GlyfMap read (TTF ttf) throws Exception
	{
		int offset = getProperty
			(EncodingTable.subtableBase).intValue ();
		tableLength = getProperty (length).intValue ();
		ttf.readBlock (this, offset);
		return this;
	}
	private int tableLength;

	/* (non-Javadoc)
	 * @see net.myorb.utilities.BlockManager#readExtendedTableProperties(net.myorb.utilities.AccessControl.Access)
	 */
	public void readExtendedTableProperties
	(AccessControl.Access access)
	throws Exception
	{
		segCount = getProperty (segCountX2).intValue () / 2;
		endCount = getSegmentList (access, U16); reserved = access.readUnsignedShort ();
		startCount = getSegmentList (access, U16); idDelta = getSegmentList (access, S16);
		idRangeOffset = getSegmentList (access, U16);
		loadGlyphIdArray (access);
	}

	/**
	 * determine entry count from the table length
	 *  and read all entries of the glyphIdArray into the local list
	 * @param access source of the TTF table data
	 * @throws Exception for any errors
	 */
	private void loadGlyphIdArray (AccessControl.Access access) throws Exception
	{
		// 8 header words (16 bytes) and 4 segment tables (2 byte/entry)
		int used = 8 * segCount + 16, remaining = tableLength - used, entries;
		glyphIdArray = AccessControl.getElementList (access, U16, entries = remaining / 2);
		propertyMap.put (glyfIdTableLength, entries);
	}
	private ComparableNumber.List endCount, startCount, idDelta, idRangeOffset, glyphIdArray;
	protected int segCount, reserved;

	/**
	 * get a list of segment size
	 * @param access source of the TTF
	 * @param element the type of elements of the list
	 * @return a list of the typed elements
	 * @throws Exception for any errors
	 */
	private ComparableNumber.List getSegmentList
	(AccessControl.Access access, AccessControl.Element element) throws Exception
	{ return AccessControl.getElementList (access, element, segCount); }

	/* (non-Javadoc)
	 * @see net.myorb.utilities.BlockManager#toString()
	 */
	public String toString ()
	{
		return super.toString () + ": " + startCount + endCount;
	}

	/* (non-Javadoc)
	 * @see net.myorb.data.font.truetype.CmapTable.GlyfMap#getGlyfIndexFor(int)
	 */
	public int getGlyfIndexFor (int characterCode) throws Exception
	{
		int segments = endCount.size ();
		int segment = findSegment (characterCode, segments);
		if (gyfNotDefined (characterCode, segment, segments)) return 0;
		return determineIndex (characterCode, segment, segments);
	}

	/**
	 * @param characterCode the code for the character
	 * @param segment the segment the character code was found in
	 * @param segments the number of segments in the table
	 * @return the index computed from table
	 */
	private int determineIndex (int characterCode, int segment, int segments)
	{
		int rangeOffset, index = characterCode;
		if ((rangeOffset = idRangeOffset.get (segment).intValue ()) != 0)
		{
			index = lookupIndex
			(
				rangeOffset,
				characterCode - startCount.get (segment).intValue (),
				segments - segment
			);
			if (index == 0) return 0;
		}
		return index + idDelta.get (segment).intValue ();
	}

	/**
	 * @param characterCode the code for the character
	 * @param segments the number of segments in the table
	 * @return the segment number where the character is found
	 */
	private int findSegment (int characterCode, int segments)
	{
		for (int segment = 0; segment < segments; segment++)
		{ if (endCount.get (segment).intValue () >= characterCode) return segment; }
		return segments;
	}

	/**
	 * @param characterCode the number of segments in the table
	 * @param segment the segment the character code was found in
	 * @param segments the number of segments in the table
	 * @return TRUE => map to 0 GLYF index
	 */
	private boolean gyfNotDefined (int characterCode, int segment, int segments)
	{ return segment == segments || startCount.get (segment).intValue () > characterCode; }

	/**
	 * @param rangeOffset range offset for segment
	 * @param characterOffset character offset from segment start
	 * @param segmentOffset offset of segment to last segment
	 * @return GLYF ID array entry for character
	 */
	private int lookupIndex (int rangeOffset, int characterOffset, int segmentOffset)
	{
		/*  *(idRangeOffset[i]/2 + (c - startCount[i]) + &idRangeOffset[i])  */
		int glyphIdArrayIndex = rangeOffset/2 + characterOffset - segmentOffset;
		return glyphIdArray.get (glyphIdArrayIndex).intValue ();
	}

}

/**
 * Format 2 encoding implementation
 */
class HighByteMap extends SubtableHeader implements CmapTable.GlyfMap
{

	/*
		Format 2: High-byte mapping through table

		This subtable is useful for the national character code standards used for Japanese,
		Chinese, and Korean characters. These code standards use a mixed 8/16-bit encoding,
		in which certain byte values signal the first byte of a 2-byte character
		(but these values are also legal as the second byte of a 2-byte character).  
		Character codes are always 1-byte. The glyph set is limited to 256.

		In addition, even for the 2-byte characters, the mapping of character codes to glyph 
		index values depends heavily on the first byte. Consequently, the table begins with an 
		array that maps the first byte to a 4-word subHeader. For 2-byte character codes,
		the subHeader is used to map the second byte’s value through a subArray, 
		as described below. When processing mixed 8/16-bit text, 
		subHeader 0 is special: it is used for single-byte character codes.
		When subHeader zero is used, a second byte is not needed;
		the single byte value is mapped through the subArray.
		 
		Type	Name	Description
		USHORT	format	Format number is set to 2.
		USHORT	length	Length in bytes.
		USHORT	version	Version number (starts at 0)
		USHORT	subHeaderKeys[256]	Array that maps high bytes to subHeaders: value is subHeader index * 8.
		4 words struct	subHeaders[ ]	Variable-length array of subHeader structures.
		4 words-struct	subHeaders[ ]	
		USHORT	glyphIndexArray[ ]	Variable-length array containing subarrays used for mapping the low byte of 2-byte characters.

		A subHeader is structured as follows:
		
		Type	Name	Description
		USHORT	firstCode	First valid low byte for this subHeader.
		USHORT	entryCount	Number of valid low bytes for this subHeader. 
		SHORT	idDelta	See text below.
		USHORT	idRangeOffset	See text below.

		The firstCode and entryCount values specify a subrange that begins at firstCode and has a length equal to the value of entryCount. 
		This subrange stays within the 0–255 range of the byte being mapped. Bytes outside of this subrange are mapped to glyph index 0 (missing glyph).
		The offset of the byte within this subrange is then used as index into a corresponding subarray of glyphIndexArray. This subarray is also of length entryCount. 
		The value of the idRangeOffset is the number of bytes past the actual location of the idRangeOffset word where the glyphIndexArray element corresponding to firstCode appears.
		Finally, if the value obtained from the subarray is not 0 (which indicates the missing glyph), you should add idDelta to it in order to get the glyphIndex. 
		The value idDelta permits the same subarray to be used for several different subheaders. The idDelta arithmetic is modulo 65536.
	 */


	HighByteMap (EncodingTable table)
	{
		super (null, "HBET");
	}


	public int getGlyfIndexFor (int characterCode) throws Exception
	{
		throw new Exception ("HighByteMap format has not been implemented");
	}

}

/**
 * Format 0 encoding implementation
 */
class ByteEncoding extends SubtableHeader implements CmapTable.GlyfMap
{

	/*
		Format 0: Byte encoding table
		
		This is the Apple standard character to glyph index mapping table.
		
		Type	Name	Description
		USHORT	format	Format number is set to 0. 
		USHORT	length	This is the length in bytes of the subtable.
		USHORT	version	Version number (starts at 0).
		BYTE	glyphIdArray[256]	An array that maps character codes to glyph index values.

		This is a simple 1 to 1 mapping of character codes to glyph indices. The glyph set is
		limited to 256. Note that if this format is used to index into a larger glyph set,
		only the first 256 glyphs will be accessible.
	 */

	ByteEncoding (EncodingTable table)
	{
		super (null, "BEET"); copyPropertiesFrom (table);
	}

	/**
	 * @param ttf the source TTF for this table
	 * @return the GLYF map based on this CMAP description
	 * @throws Exception for any errors
	 */
	CmapTable.GlyfMap read (TTF ttf) throws Exception
	{
		int offset = getProperty
			(EncodingTable.subtableBase).intValue ();
		ttf.readBlock (this, offset);
		return this;
	}

	/* (non-Javadoc)
	 * @see net.myorb.utilities.BlockManager#readExtendedTableProperties(net.myorb.utilities.AccessControl.Access)
	 */
	public void readExtendedTableProperties (AccessControl.Access access) throws Exception
	{ characterMap = AccessControl.getElementList (access, U8, 256); }
	private ComparableNumber.List characterMap;

	/* (non-Javadoc)
	 * @see net.myorb.data.font.truetype.CmapTable.GlyfMap#getGlyfIndexFor(int)
	 */
	public int getGlyfIndexFor (int characterCode) throws Exception
	{
		if (characterCode < 0 || characterCode > 255) return 0;
		return characterMap.get (characterCode).intValue ();
	}

}

