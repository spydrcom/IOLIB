
package net.myorb.data.conventional;

import net.myorb.data.abstractions.SimpleUtilities;

import java.util.HashMap;

/**
 * CharacterDelimited parser enabled for
 *  production of name/value hash tables in indexed map
 * @author Michael Druckman
 */
public class CharacterDelimitedHash extends AbstractNameValueProcessor
{


	public static class Util extends SimpleUtilities {}


	/**
	 * map of index to name/value pairs set
	 */
	public static class RecordIndex extends HashMap <String, TextMap>
	{ private static final long serialVersionUID = 5807046175687785625L; }


	/**
	 * default CharacterDelimited.CommonProcessing object is CSV
	 * @param indexedBy name of field to use as index of entries
	 */
	public CharacterDelimitedHash (String indexedBy) { this (new CSV (), indexedBy); }


	/**
	 * @param commonParser an implementer of CharacterDelimited.CommonProcessing
	 * @param indexedBy name of field to use as index of entries
	 */
	public CharacterDelimitedHash (ParserAccess commonParser, String indexedBy)
	{ super (commonParser); this.indexedBy = indexedBy; }
	protected String indexedBy;


	/* (non-Javadoc)
	 * @see net.myorb.data.conventional.CharacterDelimitedProcessor#addToStructure(net.myorb.data.conventional.CharacterDelimitedProcessor.TextMap)
	 */
	public void addToStructure (TextMap map)
	{
		index.put (map.get (indexedBy), map);
	}


	/**
	 * @return the index values of the hash in a sorted list
	 */
	public Util.TextItems getOrderedKeys ()
	{ return new Util.TextItems (Util.orderedKeys (index)); }

	/**
	 * @return the index values of the hash as a Set (no ordering applied)
	 */
	public Util.TextItems getKeys () { return new Util.TextItems (index.keySet ()); }


	/**
	 * @param item the text of the key to find
	 * @return the name/value pairs associated with that key
	 */
	public TextMap getRecordFor (String item) { return index.get (item); }


	/**
	 * @return the map of all keys
	 */
	public RecordIndex getRecordIndex () { return index; }
	protected RecordIndex index = new RecordIndex ();


}

