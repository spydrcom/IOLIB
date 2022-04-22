
package net.myorb.data.conventional;

import java.io.File;

/**
 * Tab Delimited File format
 * @author Michael Druckman
 */
public class TDF extends CharacterDelimited
	implements AbstractNameValueProcessor.ParserAccess
{

	/**
	 * change parser to use tab
	 */
	public static class TabDelimitedParser extends Parser
	{

		/* (non-Javadoc)
		 * @see net.myorb.data.conventional.CharacterDelimited.Parser#getDelimiter()
		 */
		public String getDelimiter () { return "\t"; }

		/* (non-Javadoc)
		 * @see net.myorb.data.conventional.CharacterDelimited.Parser#getDelimiterCode()
		 */
		public String getDelimiterCode () { return "0009"; }

		public TabDelimitedParser (Processor processor, DateFormat dateManager)
		{ super (processor, dateManager); }

	}

	/* (non-Javadoc)
	 * @see net.myorb.data.conventional.CharacterDelimited#parse(java.io.File, net.myorb.data.conventional.CharacterDelimited.Processor)
	 */
	public void parse (File file, Processor processor) throws Exception
	{
		parse (file, parser = new TabDelimitedParser (processor, dateManager));
	}

	/* (non-Javadoc)
	 * @see net.myorb.data.conventional.CharacterDelimited.CommonProcessing#getParser()
	 */
	public Parser getParser () { return parser; }
	protected CharacterDelimited.Parser parser;

	public TDF () {}
	public TDF (DateFormat dateManager)
	{ super (dateManager); }


}
