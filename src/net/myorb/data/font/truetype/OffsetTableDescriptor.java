
package net.myorb.data.font.truetype;

import net.myorb.data.font.TTF;
import net.myorb.data.abstractions.BinaryData;

import net.myorb.utilities.*;

/**
 * location and size data for a table.
 *  also included is a checksum for the table
 * @author Michael Druckman
 */
public class OffsetTableDescriptor extends BlockDescriptor
{


//	Type	Name		Description
//	ULONG	tag			4 -byte identifier.
//	ULONG	checkSum	CheckSum for this table.
//	ULONG	offset		Offset from beginning of TrueType font file.
//	ULONG	length		Length of this table.


	public static final BlockDefinition DEFINITION = new BlockDefinition ();

	static
	{
		DEFINITION.add (	CHECKSUM,	U32		);
		DEFINITION.add (	OFFSET,	 	U32		);
		DEFINITION.add (	LENGTH,	 	U32		);
	}

	/**
	 * describe the descriptor properties
	 */
	public OffsetTableDescriptor ()
	{
		super (DEFINITION); setBlockName ("OTDB");
	}
	

	/* (non-Javadoc)
	 * @see net.myorb.utilities.BlockManager#readBlock(net.myorb.utilities.AccessControl.Access)
	 */
	public void readBlock (AccessControl.Access access) throws Exception
	{
		readTag (access); super.readBlock (access);
	}


	/*
	 * core descriptor properties
	 */


	public int getCheckSum ()
	{
		return getCheckSumElement ().intValue ();
	}

	public int getOffset ()
	{
		return getOffsetElement ().intValue ();
	}

	public int getLength ()
	{
		return getLengthElement ().intValue ();
	}


	/*
	 * check sum support
	 */


	/**
	 * @param ttf the TTF object requesting the verify
	 * @throws Exception for any errors
	 */
	public void verifyCheckSum (TTF ttf) throws Exception
	{
		//System.out.println (tag);
		if ("head".equals (tag)) return;
		if (Character.isUpperCase (getBlockName ().charAt (0))) return;
		verifyCheckSum (ttf, BinaryData.Element.SINT);
	}


	/*
	 * read the tag string
	 */


	/**
	 * @param access file access to read with
	 * @throws Exception for any errors
	 */
	public void readTag (TTF.Access access) throws Exception
	{
		tag = access.readString (4);
	}
	public String toString () { return tag; }
	public String getTag () { return tag; }
	private String tag;


}

