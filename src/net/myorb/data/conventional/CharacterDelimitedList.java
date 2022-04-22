
package net.myorb.data.conventional;

import java.util.ArrayList;

/**
 * CharacterDelimited parser enabled for
 *  production of name/value hash tables in sequenced list
 * @author Michael Druckman
 */
public class CharacterDelimitedList extends AbstractNameValueProcessor
{


	/**
	 * use list to preserve record sequence
	 */
	public static class RecordList extends ArrayList <TextMap>
	{ private static final long serialVersionUID = -4398826855391633775L; }


	/**
	 * default CharacterDelimited.CommonProcessing object is CSV
	 */
	public CharacterDelimitedList () { this (new CSV ()); }


	/**
	 * @param commonParser an implementer of CharacterDelimited.CommonProcessing
	 */
	public CharacterDelimitedList (ParserAccess commonParser)
	{
		super (commonParser);
	}


	/* (non-Javadoc)
	 * @see net.myorb.data.conventional.CharacterDelimitedProcessor#addToStructure(net.myorb.data.conventional.CharacterDelimitedProcessor.TextMap)
	 */
	public void addToStructure (TextMap map)
	{
		recordList.add (map);
	}
	protected RecordList recordList = new RecordList ();


	/**
	 * @return count of record in processed list
	 */
	public int getRecordCount () { return recordList.size (); }


	/**
	 * @param index the index into records read
	 * @return a hash of the fields of the record
	 */
	public TextMap getRecordFor (int index) { return recordList.get (index); }


	/**
	 * @return the full list of all records read
	 */
	public RecordList getRecordList () { return recordList; }


}

