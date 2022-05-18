
package net.myorb.data.font.truetype;

import net.myorb.utilities.AccessControl;
import net.myorb.utilities.BlockManager;

import net.myorb.data.font.TTF;

/**
 * definition of NAME table
 * @author Michael Druckman
 */
public class NameTable extends BlockManager
{


//	The Naming Table is organized as follows:
//
//		Type	Description
//		USHORT	Format selector (=0). 
//		USHORT	Number of NameRecords that follow n.
//		USHORT	Offset to start of string storage (from start of table).
//		n  NameRecords	The NameRecords.
//		(Variable)	Storage for the actual string data.


	public static final String
	formatSelector		= "formatSelector",
	nameRecordCount		= "nameRecordCount",
	stringStorageOffset = "stringStorageOffset";

	public static final BlockDefinition DEFINITION = new BlockDefinition ();

	static
	{
		DEFINITION.add (	formatSelector,			U16		);
		DEFINITION.add (	nameRecordCount, 		U16		);
		DEFINITION.add (	stringStorageOffset, 	U16		);
	}


	/**
	 * describe table properties
	 */
	public NameTable ()
	{
		super (DEFINITION); setBlockName (BLOCK_NAME);
	}


	/* (non-Javadoc)
	 * @see net.myorb.utilities.BlockManager#readExtendedTableProperties(net.myorb.utilities.AccessControl.Access)
	 */
	public void readExtendedTableProperties (AccessControl.Access access) throws Exception
	{
		parseCollection
		(
			getProperty (nameRecordCount).intValue (),
			NameRecord.class, nameRecords, access, this
		);
		for (NameRecord nr : nameRecords) nr.readString (access);
	}
	protected SimleCollection<NameRecord> nameRecords = new SimleCollection<NameRecord>();


	/**
	 * description of name table entry
	 */
	public interface Entry
	{
		/**
		 * @return the text of the entry
		 */
		String getText ();

		/**
		 * @return the language of the text
		 */
		String getLanguage ();

		/**
		 * @return the type of name table entry
		 */
		String getContext ();
	}


	/**
	 * @return list sorted by platform and then by name type
	 */
	protected SimleCollection<NameRecord> sortedNameRecords ()
	{
		SimleCollection<NameRecord> sorted = new SimleCollection<NameRecord>();
		sorted.addAll (nameRecords);
		sorted.sort
		(
			(l, r) ->
			{
				int lp, rp;
				if ((lp=l.getPlatformId()) == (rp=r.getPlatformId()))
				{ return l.getNameId() < r.getNameId() ? -1 : 1; }
				else return lp < rp ? -1 : 1;
			}
		);
		return sorted;
	}


	/**
	 * print full list of parsed name records
	 */
	public void dump ()
	{
		for (NameRecord nr : nameRecords)
		{
			System.out.println (nr);
		}
	}


	/**
	 * print text of parsed name records
	 */
	public void print ()
	{
		int platform = -1, id = 0;
		if (nameRecords.size () == 0) return;

		for (NameRecord nr : sortedNameRecords ())
		{
			//System.out.println (nr);

			if ((id = nr.getPlatformId ()) != platform)
			{
				System.out.println ();
				System.out.println ("===");
				System.out.println (nr.formattedHeader ());
				headerLanguage = nr.getLanguageId ();
				System.out.println ("===");
				platform = id;
			}

			System.out.print (nr.formattedRecord (headerLanguage));
			System.out.println ("  " + nr.getStringText ());
		}

		System.out.println ();
	}
	int headerLanguage = -1;


	/**
	 * @param type the name ID value to find
	 * @return a list of Entry objects
	 */
	public SimleCollection<Entry> getRecordsOfType (int type)
	{
		SimleCollection<Entry> list =
			new SimleCollection<Entry>();
		for (NameRecord record : sortedNameRecords ())
		{ if (record.getNameId () == type) list.add (record); }
		return list;
	}


	/**
	 * @param ttf the font description holding this table
	 * @return the name table from the specified font
	 * @throws Exception for any errors
	 */
	public static NameTable getTable (TTF ttf) throws Exception
	{
		return ttf.getFileHeader ().getTable (BLOCK_NAME, NameTable.class, ttf);
	}
	public static final String BLOCK_NAME = "name";


}

