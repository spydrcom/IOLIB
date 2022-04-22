
package net.myorb.data.abstractions;

import java.util.List;
import java.util.Map;

/**
 * parse configuration parameters from command
 *  add name/value pairs to map
 * @author Michael Druckman
 */
public class ConfigurationParser
{

	/**
	 * parse configuration parameters from a token list
	 * @param tokens a token list from the command line
	 * @param parameters the configuration map to build
	 */
	public static void configure
		(
			List <CommonCommandParser.TokenDescriptor> tokens,
			Map<String, Object> parameters
		)
	{
		configure (new CommonCommandParser.TokenList (tokens), parameters);
	}

	/**
	 * map configuration parameters from the parser
	 * @param tokens a token list from the command line
	 * @param parameters the configuration map to build
	 */
	public static void configure
		(
			CommonCommandParser.TokenList tokens,
			Map<String, Object> parameters
		)
	{
		int n = 0;
		String sym, val;
		while (n+1 < tokens.size())
		{
			sym = tokens.get (n++).getTokenImage ();
			val = strip (tokens.get (n++).getTokenImage ());
			parameters.put (sym, val);
		}
	}
	public static String strip (String text) { return CommonCommandParser.stripQuotes (text); }

}
