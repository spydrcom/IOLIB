
package net.myorb.data.conventional;

import java.util.HashMap;
import java.io.File;

/**
 * abstract processor which reads name/value pairs
 * @author Michael Druckman
 */
public abstract class AbstractNameValueProcessor
	extends CharacterDelimited implements CharacterDelimited.Processor
{


	/**
	 * the parser is required to provide the header names
	 */
	public interface ParserAccess extends CommonProcessing {}


	/**
	 * map of name/value pairs
	 */
	public static class TextMap extends HashMap <String, String>
	{
		/**
		 * @param name the key to find the value
		 * @return the value interpreted as a number
		 */
		public Number getNumberFor (String name) { return Double.parseDouble (get (name)); }
		public TextMap (HashMap <String, String> contents) { if (contents != null) putAll (contents); }
		private static final long serialVersionUID = 8296635724601777316L;
	}


	/**
	 * default CharacterDelimited.CommonProcessing object is CSV
	 */
	public AbstractNameValueProcessor () { this (new CSV ()); }


	/**
	 * @param parserAccess a CharacterDelimited.CommonProcessing object suited to name/value pair objects
	 */
	public AbstractNameValueProcessor (ParserAccess parserAccess)
	{
		this.referencedParser = parserAccess;
	}
	protected ParserAccess referencedParser;


	/**
	 * @param file the file to be parsed
	 * @throws Exception for any errors
	 */
	public void parse (File file) throws Exception
	{
		referencedParser.parse (file, this);
	}


	/* (non-Javadoc)
	 * @see net.myorb.data.conventional.CharacterDelimited.Processor#process(net.myorb.data.conventional.CharacterDelimited.Reader)
	 */
	public void process (Reader reader)
	{
		TextMap mapOfRecord = new TextMap (null);
		if (columnHeaders == null) columnHeaders = referencedParser.getParser ().getHeaders ();
		mapRecord (columnHeaders, reader, mapOfRecord);
		addToStructure (mapOfRecord);
	}
	protected String[] columnHeaders = null;

	/**
	 * @param mapOfRecord the name/value pairs of a processed record
	 */
	public abstract void addToStructure (TextMap mapOfRecord);


}

