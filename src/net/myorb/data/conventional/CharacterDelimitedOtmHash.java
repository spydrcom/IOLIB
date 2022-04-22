
package net.myorb.data.conventional;

import java.util.List;

/**
 * CharacterDelimited parser enabled for
 *  production of name/value hash tables in indexed map.
 *  this version supports One-To-Many mappings
 * @author Michael Druckman
 */
public class CharacterDelimitedOtmHash extends CharacterDelimitedHash
{


	/**
	 * map of index to list of name/value pairs sets
	 */
	public static class OtmIndex extends Util.MapToList <String, TextMap>
	{ private static final long serialVersionUID = -6146990202388736691L; }


	/**
	 * default CharacterDelimited.CommonProcessing object is CSV
	 * @param indexedBy name of field to use as index of entries
	 */
	public CharacterDelimitedOtmHash (String indexedBy) { this (new CSV (), indexedBy); }


	/**
	 * @param commonParser an implementer of CharacterDelimited.CommonProcessing
	 * @param indexedBy name of field to use as index of entries
	 */
	public CharacterDelimitedOtmHash (ParserAccess commonParser, String indexedBy) { super (commonParser, indexedBy); }


	/* (non-Javadoc)
	 * @see net.myorb.data.conventional.AbstractNameValueProcessor#addToStructure(net.myorb.data.conventional.AbstractNameValueProcessor.TextMap)
	 */
	public void addToStructure (TextMap map) { index.add (map.get (indexedBy), map); }


	/* (non-Javadoc)
	 * @see net.myorb.data.conventional.CharacterDelimitedHash#getOrderedKeys()
	 */
	public Util.TextItems getOrderedKeys ()
	{ return new Util.TextItems (Util.orderedKeys (index)); }

	/* (non-Javadoc)
	 * @see net.myorb.data.conventional.CharacterDelimitedHash#getKeys()
	 */
	public Util.TextItems getKeys () { return new Util.TextItems (index.keySet ()); }


	/**
	 * @param item the text of the key to find
	 * @return a list of the name/value pairs associated with that key
	 */
	public List<TextMap> getRecordsFor (String item) { return index.get (item); }


	/**
	 * @return the map of all keys
	 */
	public OtmIndex getOtmIndex () { return index; }
	protected OtmIndex index = new OtmIndex ();


}

