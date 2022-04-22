
package net.myorb.data.unicode;

import net.myorb.data.abstractions.SimpleUtilities;

import net.myorb.data.abstractions.SimpleXmlHash.Node;
import net.myorb.data.abstractions.SimpleXmlHash.Document;

import net.myorb.gui.components.UnicodeCharacterBlock;

import java.awt.Font;

import java.util.HashMap;
import java.util.Map;

/**
 * index blocks of Unicode characters with multiple granularity choices
 *  - index by assigned character groupings (BMP) in the Unicode specification
 *  - index 256 character blocks
 * @author Michael Druckman
 */
public class BlockIndex
{


	/**
	 * internal representation of the master index
	 */
	public static class Entry
	{
		int first, last;
		String description;

		public Entry (Node n)
		{
			description = n.get ("Description");
			first = Toolkit.parseHex (n.get ("PageBase"));
			last = Toolkit.parseHex (n.get ("PageEnd"));
		}
	}


	/**
	 * index file is read on first reference
	 * @throws Exception for IO errors
	 */
	public static void readBMPlaneIndexFile () throws Exception
	{
		if (master != null) return;
		master = new HashMap<Integer,Entry>();
		Document index = Document.read ("UnicodeIndex");
		for (Node n : index.getNodeList ())
		{
			Entry e = new Entry (n);
			master.put (e.first, e);
		}
	}
	static Map<Integer,Entry> master = null;


	/**
	 * lazy init of the index
	 * @return the map holding the index entries
	 * @throws Exception for IO error
	 */
	public static Map<Integer,Entry> getIndex () throws Exception
	{
		if (master == null) readBMPlaneIndexFile ();
		return master;
	}


	/**
	 * look for value as base of block
	 * @param base the value to be sought in index
	 * @return the description of the block, null if not found
	 * @throws Exception for IO error
	 */
	public static String getBlockDescription (int base) throws Exception
	{
		Entry item = getIndex ().get (base);
		if (item != null) return item.description;
		return null;
	}


	/**
	 * dump the BMP index
	 * @throws Exception for IO errors
	 */
	public static void dumpBMPlaneIndex () throws Exception
	{
		int last = -1;
		Map<Integer,Entry> index = getIndex ();
		for (Integer page: SimpleUtilities.orderedKeys (index))
		{
			int next = last + 1;
			Entry e = index.get (page);

			if (e.first != next)
			{ Toolkit.trace ("Break detected at " + Toolkit.hex4 (next) + " vs " + Toolkit.hex4 (e.first)); }
			else last = e.last;

			System.out.print (Toolkit.hex4 (page)); System.out.print (" - ");
			System.out.print (Toolkit.hex4 (e.last)); System.out.print (": ");
			System.out.println (e.description);
		}
	}


	/**
	 * display formatted index
	 * @param font the font used to build the index
	 * @param index the map holding the built index
	 * @throws Exception for IO errors
	 */
	public static void showIndex (Font font, Map<Integer,Integer> index) throws Exception
	{
		String description;
		System.out.println ();
		System.out.println (font.getName ());

		for (Integer page: SimpleUtilities.orderedKeys (index))
		{
			System.out.print (Toolkit.hex4 (page));
			System.out.print (": "); System.out.print (index.get (page));

			if ((description = getBlockDescription (page)) != null)
			{
				System.out.print ("\t\t");
				System.out.print (description);
			}

			System.out.println ();
		}
	}


	/**
	 * show BMP (Basic Multilingual Plane) index of font.
	 *  BMP is first Plane of Unicode standard, first 16 bits, basically UTF-16
	 * @param font the font used to build the index
	 * @throws Exception for IO errors
	 */
	public static void showBMPlaneIndex (Font font) throws Exception
	{
		showIndex (font, buildBMPlaneIndex (font));
	}


	/**
	 * build BMP index map for font
	 * @param font the font used to build the index
	 * @return map of block to population
	 * @throws Exception for IO errors
	 */
	public static Map<Integer,Integer> buildBMPlaneIndex (Font font) throws Exception
	{
		Map<Integer,Entry> index = getIndex ();
		Map<Integer,Integer> blockMap = new HashMap<Integer,Integer>();

		for (Integer block : SimpleUtilities.orderedKeys (index))
		{
			int count = 0;
			Entry e = index.get (block);
			for (int c = e.first; c <= e.last; c++)
			{ if (font.canDisplay (c)) { count++; } }
			if (count > 0) blockMap.put (block, count);
		}

		return blockMap;
	}


	/**
	 * show block population index
	 * @param font the font being indexed
	 */
	public static void showBlockIndex (Font font)
	{
		try
		{
			showIndex (font, buildBlockIndex (font));
		}
		catch (Exception e) { e.printStackTrace(); }
	}


	/**
	 * build block population index
	 * @param font the font being indexed
	 * @return map of page to population
	 */
	public static Map<Integer,Integer> buildBlockIndex (Font font)
	{
		Map<Integer,Integer> pageMap = new HashMap<Integer,Integer>();
		for (int page = 0; page < 0x10000; page += 0x100)
		{
			int count = 0;
			for (int c = 0; c < 0x100; c++)
			{ if (font.canDisplay (page+c)) count++; }
			if (count > 0) pageMap.put (page, count);
		}
		return pageMap;
	}


	/**
	 * a table display for an index
	 */
	public static class Table extends CrossReference
	{

		static final String COLUMNS[] = new String[]
		{
			"Symbols", "Block Starting", "Block Ending", "Block Description"
		};
		public Table (Font font, UnicodeCharacterBlock block) { super (font, COLUMNS, block); }

		/* (non-Javadoc)
		 * @see net.myorb.data.unicode.CrossReference#loadCrossReference()
		 */
		public void loadCrossReference ()
		{
			try
			{
				Map<Integer,Integer> index = buildBMPlaneIndex (font);
				for (Integer block : SimpleUtilities.orderedKeys (index))
				{ addToRowCount (formatRow (master.get (block), index.get (block))); }
				releaseUnusedRows ();
			}
			catch (Exception e) { e.printStackTrace (); }
		}
		Object[] formatRow (Entry e, int symbols)
		{
			return rowFor
			(
				symbols, Toolkit.hex4 (e.first), Toolkit.hex4 (e.last), e.description
			);
		}

	}


	/**
	 * unit test index
	 * @param p not used
	 * @throws Exception for errors
	 */
	public static void main (String... p) throws Exception
	{
		dumpBMPlaneIndex ();
	}


}

