
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
	 * mechanism for selection of token interpretation
	 */
	public interface Interpreter
	{
		/**
		 * determine use for a token of a command stream
		 * @param symbol the name assigned to the token value
		 * @param token the token to process
		 */
		void process (String symbol, CommonCommandParser.TokenDescriptor token);
	}

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
	 * pass token pairs to interpreter
	 * @param tokens the list of tokens to process
	 * @param interpreter the interpreter object to use
	 */
	public static void process
		(
			List <CommonCommandParser.TokenDescriptor> tokens,
			Interpreter interpreter
		)
	{
		int n = 0, count = tokens.size () - 1; String sym;
		while (n < count)
		{
			sym = tokens.get (n++).getTokenImage ();
			interpreter.process (sym, tokens.get (n++));
		}
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
		process
		(
			tokens,
			(s, t) ->
				{
					if (t.getTokenType () != CommonCommandParser.TokenType.QOT)
					{ throw new RuntimeException ("Expected string: " + t.getTokenImage ()); }
					addParameterValue (parameters, t, s);
				}
		);
	}

	/**
	 * quoted text to be added to configuration
	 * @param toConfiguration configuration map to add to
	 * @param fromToken the token holding the text value
	 * @param toBeCalled the name of the symbol
	 */
	public static void addParameterValue
		(
			Map<String, Object> toConfiguration,
			CommonCommandParser.TokenDescriptor fromToken,
			String toBeCalled
		)
	{
		toConfiguration.put (toBeCalled, strip (fromToken.getTokenImage ()));
	}
	public static String strip (String text) { return CommonCommandParser.stripQuotes (text); }

}

