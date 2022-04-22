
package net.myorb.data.font.truetype;

import net.myorb.data.font.TTF;

/**
 * definition of HEAD table
 * @author Michael Druckman
 */
public class HeadTable extends BoundedTable
{


//	FIXED	Table version number
//	FIXED	fontRevision
//	ULONG	checkSumAdjustment
//	ULONG	magicNumber
//	USHORT	flags
//	USHORT	unitsPerEm
//	longDateTime	created
//	longDateTime	modified
//	FWORD	xMin
//	FWORD	yMin
//	FWORD	xMax
//	FWORD	yMax
//	USHORT	macStyle
//	USHORT	lowestRecPPEM
//	SHORT	fontDirectionHint
//	SHORT	indexToLocFormat
//	SHORT	glyphDataFormat


	public static final String
	tableVersionNumber	= "tableVersionNumber",
	fontRevision		= "fontRevision",
	checkSumAdjustment	= "checkSumAdjustment",
	magicNumber			= "magicNumber",
	flags				= "flags",
	unitsPerEm			= "unitsPerEm",
	created				= "created",
	modified			= "modified",
	macStyle			= "macStyle",
	lowestRecPPEM		= "lowestRecPPEM",
	fontDirectionHint	= "fontDirectionHint",
	indexToLocFormat 	= "indexToLocFormat",
	glyphDataFormat		= "glyphDataFormat";



	public static final BlockDefinition
		REVISION_DEFINITION = new BlockDefinition (),
		FORMATTING_DEFINITION = new BlockDefinition ();
	static
	{
		REVISION_DEFINITION.add		(	tableVersionNumber,		S32,	16	);	// Fixed 16.16
		REVISION_DEFINITION.add		(	fontRevision,	 		S32,	16	);
		REVISION_DEFINITION.add		(	checkSumAdjustment,	 	U32			);
		REVISION_DEFINITION.add		(	magicNumber,	 		U32			);
		REVISION_DEFINITION.add		(	flags,	 				U16			);
		REVISION_DEFINITION.add		(	unitsPerEm,	 			U16			);
		REVISION_DEFINITION.add		(	created,	 			S64			);
		REVISION_DEFINITION.add		(	modified,	 			S64			);

		FORMATTING_DEFINITION.add	(	macStyle, 				U16		);
		FORMATTING_DEFINITION.add	(	lowestRecPPEM, 			U16		);
		FORMATTING_DEFINITION.add	(	fontDirectionHint, 		S16		);
		FORMATTING_DEFINITION.add	(	indexToLocFormat, 		S16		);
		FORMATTING_DEFINITION.add	(	glyphDataFormat, 		S16		);
	}

	/**
	 * describe table properties
	 */
	public HeadTable ()
	{
		super
		(
			REVISION_DEFINITION.plus
			(BOUNDARY_DEFINITION, FORMATTING_DEFINITION)
		);
		setBlockName (BLOCK_NAME);
	}


	/**
	 * @return TRUE : 4 byte location offsets
	 */
	public boolean useLongLocationOffsets ()
	{
		return getProperty (indexToLocFormat).intValue () == 1;
	}


	/* (non-Javadoc)
	 * @see net.myorb.utilities.BlockManager#verifyTableIntegrity()
	 */
	public void verifyTableIntegrity () throws Exception
	{
		if (getProperty (magicNumber).intValue () != MAGIC)
		{
			throw new Exception ("Magic number test failed");
		}
		propertyMap.put ("magicTest", 1);
	}
	public static final int MAGIC = 0x5F0F3CF5;


	/**
	 * @param ttf the font description holding this table
	 * @return the head table from the specified font
	 * @throws Exception for any errors
	 */
	public static HeadTable getTable (TTF ttf) throws Exception
	{
		return ttf.getFileHeader ().getTable (BLOCK_NAME, HeadTable.class, ttf);
	}
	public static final String BLOCK_NAME = "head";


}

