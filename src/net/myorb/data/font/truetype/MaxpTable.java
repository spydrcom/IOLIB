
package net.myorb.data.font.truetype;

import net.myorb.utilities.BlockManager;

import net.myorb.data.font.TTF;

/**
 * definition of MAXP table
 * @author Michael Druckman
 */
public class MaxpTable extends BlockManager
{


//	Fixed	Table version number
//	USHORT	numGlyphs
//	USHORT	maxPoints
//	USHORT	maxContours
//	USHORT	maxCompositePoints
//	USHORT	maxCompositeContours
//	USHORT	maxZones
//	USHORT	maxTwilightPoints
//	USHORT	maxStorage
//	USHORT	maxFunctionDefs
//	USHORT	maxInstructionDefs
//	USHORT	maxStackElements
//	USHORT	maxSizeOfInstructions
//	USHORT	maxComponentElements
//	USHORT	maxComponentDepth


	public static final String
	versionNumber			= "versionNumber",
	numGlyphs				= "numGlyphs",
	maxPoints				= "maxPoints",
	maxContours				= "maxContours",
	maxCompositePoints		= "maxCompositePoints",
	maxCompositeContours	= "maxCompositeContours",
	maxZones				= "maxZones",
	maxTwilightPoints		= "maxTwilightPoints",
	maxStorage				= "maxStorage",
	maxFunctionDefs			= "maxFunctionDefs",
	maxInstructionDefs		= "maxInstructionDefs",
	maxStackElements		= "maxStackElements",
	maxSizeOfInstructions	= "maxSizeOfInstructions",
	maxComponentElements	= "maxComponentElements",
	maxComponentDepth		= "maxComponentDepth";

	public static final BlockDefinition DEFINITION = new BlockDefinition ();

	static
	{
		DEFINITION.add (	versionNumber,			S32,	16	);	// Fixed 16.16
		DEFINITION.add (	numGlyphs, 				U16			);
		DEFINITION.add (	maxPoints, 				U16			);
		DEFINITION.add (	maxContours,			U16			);
		DEFINITION.add (	maxCompositePoints, 	U16			);
		DEFINITION.add (	maxCompositeContours, 	U16			);
		DEFINITION.add (	maxZones, 				U16			);
		DEFINITION.add (	maxTwilightPoints, 		U16			);
		DEFINITION.add (	maxStorage, 			U16			);
		DEFINITION.add (	maxFunctionDefs, 		U16			);
		DEFINITION.add (	maxInstructionDefs, 	U16			);
		DEFINITION.add (	maxStackElements, 		U16			);
		DEFINITION.add (	maxSizeOfInstructions, 	U16			);
		DEFINITION.add (	maxComponentElements, 	U16			);
		DEFINITION.add (	maxComponentDepth, 		U16			);
	}


	/**
	 * describe table properties
	 */
	public MaxpTable ()
	{
		super (DEFINITION); setBlockName (BLOCK_NAME);
	}


	/**
	 * @param ttf the font description holding this table
	 * @return the head table from the specified font
	 * @throws Exception for any errors
	 */
	public static MaxpTable getTable (TTF ttf) throws Exception
	{
		return ttf.getFileHeader ().getTable (BLOCK_NAME, MaxpTable.class, ttf);
	}
	public static final String BLOCK_NAME = "maxp";


}

