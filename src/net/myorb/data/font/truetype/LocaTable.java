
package net.myorb.data.font.truetype;

import net.myorb.utilities.AccessControl.Access;

import net.myorb.data.abstractions.ComparableNumber;
import net.myorb.data.abstractions.BinaryData;

import net.myorb.data.font.TTF;

/**
 * definition of LOCA table
 * @author Michael Druckman
 */
public class LocaTable implements TTF.BlockReader
{


	public LocaTable (TTF ttf)
	{
		this.ttf = ttf;
	}
	TTF ttf;


	/**
	 * @return the size of a loca table entry
	 * @throws Exception for any errors
	 */
	public int bytesPerEntry () throws Exception
	{
		return ttf.getHeadTable ().useLongLocationOffsets () ? 4 : 2;
	}


	/**
	 * @return the number of entries in the loca table
	 * @throws Exception for any errors
	 */
	public int entryCount () throws Exception
	{
		return locaTableLength / bytesPerEntry ();
	}


	/**
	 * @param index the sequence index into the loca table
	 * @return the value found in the table entry
	 * @throws Exception for any errors
	 */
	public int readEntry (int index) throws Exception
	{
		if (tableCache != null)
		{
			return tableCache.get (index).intValue () * indexMultiplier;
		}

		Access access = null; int offset = 0;

		if ((offset = locaTableOffset + bytesInIndex * index) < locaTableEnd)
		{
			try
			{
				access = ttf.getAccess (offset);
				return bytesInIndex==4 ? access.readInt () :
					access.readUnsignedShort () * 2;
			} finally { access.close (); }
		}
		else throw new Exception ("End of LOCA table");
	}


	/**
	 * read complete LOCA table into memory
	 * @throws Exception for any errors
	 */
	public void fullyCacheTable () throws Exception
	{
		if (ttf.getHeadTable ().useLongLocationOffsets ())
		{
			tableElement = BinaryData.Element.UINT;
			indexMultiplier = 1;
		}
		else
		{
			tableElement = BinaryData.Element.USHORT;
			indexMultiplier = 2;
		}

		ttf.readBlock (this, locaTableOffset);
	}


	/**
	 * @return the loca table from the file
	 * @throws Exception for any errors
	 */
	public LocaTable prepare () throws Exception
	{
		OffsetTableDescriptor d =
				ttf.getFileHeader ().find (BLOCK_NAME);
		int offset = d.getOffset (), length = d.getLength ();
		locaTableOffset = offset; locaTableEnd = offset + length;
		bytesInIndex = bytesPerEntry ();
		locaTableLength = length;
		entries = entryCount ();
		return this;
	}
	protected int locaTableOffset, locaTableLength, locaTableEnd, bytesInIndex;


	/* (non-Javadoc)
	 * @see net.myorb.utilities.AccessControl.BlockReader#readBlock(net.myorb.utilities.AccessControl.Access)
	 */
	public void readBlock (Access access) throws Exception
	{ tableCache = TTF.getElementList (access, tableElement, entries); cacheFlag = "CACHED"; }
	protected String cacheFlag = "NOT currently in memory";
	protected ComparableNumber.List tableCache = null;
	protected BinaryData.Element tableElement;
	protected int indexMultiplier, entries;


	/* (non-Javadoc)
	 * @see net.myorb.utilities.AccessControl.BlockReader#setLocation(java.lang.Number)
	 */
	public void setLocation (Number location) {}


	/* (non-Javadoc)
	 * @see net.myorb.data.font.truetype.Boundaries#toString()
	 */
	public String toString ()
	{
		return
		"[loca] @" + Integer.toHexString (locaTableOffset) +
		", entries=" + entries + ", len=" + locaTableLength +
		", bytesInIndex=" + bytesInIndex + 
		", " + cacheFlag;
	}


	public static LocaTable getTable (TTF ttf) throws Exception
	{
		return ttf.getFileHeader ().getTable (BLOCK_NAME, null, null);
	}
	public static void postTable (TTF ttf) throws Exception
	{
		ttf.getFileHeader ().addTable (BLOCK_NAME, new LocaTable (ttf).prepare ());
	}
	public static final String BLOCK_NAME = "loca";


}

