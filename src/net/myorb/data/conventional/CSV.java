
package net.myorb.data.conventional;

import java.io.File;

/**
 * Comma Separated Value data format
 * @author Michael Druckman
 */
public class CSV extends CharacterDelimited
	implements AbstractNameValueProcessor.ParserAccess
{

	public static final String DEL = ",", CODE = "002C";

	/**
	 * change parser to use comma
	 */
	public static class CommaSeparatedValuesParser extends Parser
	{

		/* (non-Javadoc)
		 * @see net.myorb.data.conventional.CharacterDelimited.Parser#getDelimiterCode()
		 */
		public String getDelimiterCode () { return CODE; }

		/* (non-Javadoc)
		 * @see net.myorb.data.conventional.CharacterDelimited.Parser#getDelimiter()
		 */
		public String getDelimiter () { return DEL; }

		public CommaSeparatedValuesParser
		(Processor processor, DateFormat dateManager)
		{ super (processor, dateManager); }

	}

	/* (non-Javadoc)
	 * @see net.myorb.data.conventional.CharacterDelimited#parse(java.io.File, net.myorb.data.conventional.CharacterDelimited.Processor)
	 */
	public void parse (File file, Processor processor) throws Exception
	{
		parse (file, parser = new CommaSeparatedValuesParser (processor, dateManager));
	}

	/* (non-Javadoc)
	 * @see net.myorb.data.conventional.CharacterDelimited.CommonProcessing#getParser()
	 */
	public Parser getParser () { return parser; }
	protected CharacterDelimited.Parser parser;

	public CSV (DateFormat dateManager)
	{ super (dateManager); }
	public CSV () {}

}
