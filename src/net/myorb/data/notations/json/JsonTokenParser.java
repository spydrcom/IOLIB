
package net.myorb.data.notations.json;

import net.myorb.data.abstractions.CommonCommandParser;

import java.util.List;

/**
 * token parser specific to JSON
 * @author Michael Druckman
 */
public class JsonTokenParser extends CommonCommandParser
		implements CommonCommandParser.SpecialTokenSegments
{


	/**
	 * exception for unexpected token sequences
	 */
	public static class Unexpected extends Exception
	{
		Unexpected (String message, Object object) { super (message + object.toString ()); }
		Unexpected (Object object) { this ("Unexpected element: ", object); }
		private static final long serialVersionUID = 3838601205002392893L;
	}


	public JsonTokenParser ()
	{
		MULTI_CHARACTER_OPERATOR = "";
		OPERATOR_EXTENDED = MULTI_CHARACTER_OPERATOR;
		OPERATOR = JsonPrimitives.getDelimiters ();									// the delimiters list according to the Primitives class
	}


	public final String WHITE_SPACE = " \t\r\n_";
	public final String OPERATOR, MULTI_CHARACTER_OPERATOR, OPERATOR_EXTENDED;
	public final String IDN_LEAD = LETTER, IDN_BODY = LETTER;

	public String getIdnLead () { return IDN_LEAD; }
	public String getWhiteSpace () { return WHITE_SPACE; }
	public String getMultiCharacterOperator () { return MULTI_CHARACTER_OPERATOR; }
	public String getExtendedOperator () { return OPERATOR_EXTENDED; }
	public String getOperator () { return OPERATOR; }
	public String getIdnBody () { return IDN_BODY; }


	/**
	 * @param buffer text buffer to be parsed
	 * @return list of tokens parsed from source
	 */
	public static List<TokenDescriptor> parse (StringBuffer buffer)
	{
		return parseCommon (buffer, new JsonTokenParser ());
	}

}
