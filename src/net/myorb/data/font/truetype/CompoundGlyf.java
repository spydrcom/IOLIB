
package net.myorb.data.font.truetype;

import net.myorb.data.font.TTF;
import net.myorb.utilities.*;

/**
 * the COMPOUND sub-class of the GLYF table entry type
 * @author Michael Druckman
 */
public class CompoundGlyf extends GlyfTableEntry
{

//	do {
//		USHORT flags;
//		USHORT glyphIndex;
//		if ( flags & ARG_1_AND_2_ARE_WORDS) {
//		(SHORT or FWord) argument1;
//		(SHORT or FWord) argument2;
//		} else {
//			USHORT arg1and2; /* (arg1 << 8) | arg2 */
//		}
//		if ( flags & WE_HAVE_A_SCALE ) {
//			F2Dot14  scale;    /* Format 2.14 */
//		} else if ( flags & WE_HAVE_AN_X_AND_Y_SCALE ) {
//			F2Dot14  xscale;    /* Format 2.14 */
//			F2Dot14  yscale;    /* Format 2.14 */
//		} else if ( flags & WE_HAVE_A_TWO_BY_TWO ) {
//			F2Dot14  xscale;    /* Format 2.14 */
//			F2Dot14  scale01;   /* Format 2.14 */
//			F2Dot14  scale10;   /* Format 2.14 */
//			F2Dot14  yscale;    /* Format 2.14 */
//		}
//	} while ( flags & MORE_COMPONENTS ) 
//	if (flags & WE_HAVE_INSTR){
//		USHORT numInstr
//		BYTE instr[numInstr]

	public static final int
	ARG_WORDS			= 1,
	XY_VALUES			= 2,
	ROUND_VALUES		= 4,
	SIMPLE_SCALE		= 8,
	OBSOLETE			= 16,
	MORE_COMPONENTS 	= 32,
	SEPARATE_SCALING	= 64,
	SCALE_2BY2			= 128,
	HAS_INSTRUCTION 	= 256,
	USE_METRICS			= 512,
	OVERLAP				= 1024;

	public static final String
	flags				= "flags",
	glyphIndex			= "glyphIndex",
	argument1			= "argument1",
	argument2			= "argument2",
	scale				= "scale",
	scale01				= "scale01",
	scale10				= "scale10",
	xScale				= "xScale",
	yScale				= "yScale",
	numInstr			= "numInstr";

	public static final BlockDefinition
		HEADER_DEFINITION = new BlockDefinition (),
		BYTE_ARGS_DEFINITION = new BlockDefinition (),
		SHORT_ARGS_DEFINITION = new BlockDefinition (),
		SIMPLE_SCALE_DEFINITION = new BlockDefinition (),
		TWO_BY_TWO_SCALE_DEFINITION = new BlockDefinition (),
		XY_SCALE_DEFINITION = new BlockDefinition (),
		INSTR_DEFINITION = new BlockDefinition ();
	static
	{
		HEADER_DEFINITION.add			(	flags,			U16		);
		HEADER_DEFINITION.add			(	glyphIndex,		U16		);

		BYTE_ARGS_DEFINITION.add		(	argument1,		U8		);
		BYTE_ARGS_DEFINITION.add		(	argument2,		U8		);

		SHORT_ARGS_DEFINITION.add		(	argument1,		U16		);
		SHORT_ARGS_DEFINITION.add		(	argument2,		U16		);

		SIMPLE_SCALE_DEFINITION.add 	(	scale,			U16		);

		XY_SCALE_DEFINITION.add			(	xScale,			S16,	14		);	// FIXED 2.14 so S16 / 2^14
		XY_SCALE_DEFINITION.add			(	yScale,			S16,	14		);

		TWO_BY_TWO_SCALE_DEFINITION.add	(	xScale,			S16,	14		);
		TWO_BY_TWO_SCALE_DEFINITION.add	(	scale01,		S16,	14		);
		TWO_BY_TWO_SCALE_DEFINITION.add	(	scale10,		S16,	14		);
		TWO_BY_TWO_SCALE_DEFINITION.add	(	yScale,			S16,	14		);

		INSTR_DEFINITION.add			(	numInstr, 		U16		);
	}

	/**
	 * @param glyfTable previously read header
	 */
	public CompoundGlyf (GlyfTableEntry glyfTable)
	{
		copyPropertiesFrom (glyfTable);
		this.numberOfContours = -1;
	}

	/**
	 * build definition based on flags
	 * @param flagword the word of flags from the header
	 * @return a block definition for the extended fields
	 */
	public BlockDefinition getSubtableExtendedDefinition (int flagword)
	{
		BlockDefinition def = BYTE_ARGS_DEFINITION;
		if (isSet (flagword, ARG_WORDS)) def = SHORT_ARGS_DEFINITION;

		if (isSet (flagword, SIMPLE_SCALE) ) def = def.plus (SIMPLE_SCALE_DEFINITION);
		else if (isSet (flagword, SEPARATE_SCALING)) def = def.plus (XY_SCALE_DEFINITION);
		else if (isSet (flagword, SCALE_2BY2)) def = def.plus (TWO_BY_TWO_SCALE_DEFINITION);

		return def;
	}

	/**
	 * @param access a reader for the source
	 * @return THIS object populated from the source
	 * @throws Exception for any errors
	 */
	public CompoundGlyf read (TTF.Access access) throws Exception
	{
		int flagword = MORE_COMPONENTS;

		while (isSet (flagword, MORE_COMPONENTS))
		{
			BlockManager subtable = new GenericSubBlock
				(HEADER_DEFINITION, "CGST").read (access);
			flagword = subtable.getProperty (flags).intValue ();
			BlockDefinition extendedDefinition = getSubtableExtendedDefinition (flagword);
			GenericSubBlock.supplement (subtable, extendedDefinition, access);
			//System.out.print (subtable + "  ");
			subtables.add (subtable);
		}

		if (isSet (flagword, HAS_INSTRUCTION))
		{
			GenericSubBlock.supplement (this, INSTR_DEFINITION, access);
			instructions = new byte[getProperty (numInstr).intValue ()];
			access.readFully (instructions);
		}

		return this;
	}
	protected SimleCollection<BlockManager> subtables = new SimleCollection<BlockManager>();
	protected byte[] instructions;

	/**
	 * @return list of index values taken from each sub-table
	 */
	public SimleCollection<Integer> getGlyfIndicies ()
	{
		SimleCollection<Integer> indicies = new SimleCollection<Integer>();
		for (BlockManager b : subtables) indicies.add (b.getProperty (glyphIndex).intValue ());
		return indicies;
	}

	/* (non-Javadoc)
	 * @see net.myorb.data.font.truetype.BoundedTable#toString()
	 */
	public String toString ()
	{
		return super.toString () + "   " + subtables;
	}

}

