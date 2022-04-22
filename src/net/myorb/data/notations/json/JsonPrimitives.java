
package net.myorb.data.notations.json;

/**
 * enumeration of JSON tokens
 * @author Michael Druckman
 */
public enum JsonPrimitives
{

	COLON (":"),			// delimiters
	COMMA (","),
	NULL ("null"),			// keywords
	TRUE ("true"),
	FALSE ("false"),
	OPEN_OBJECT ("{"),		// open/close object
	CLOSE_OBJECT ("}"),
	OPEN_ARRAY ("["),		// open/close array
	CLOSE_ARRAY ("]"),
	NUMBER ("#"),			// numeric literal	(not used as token)
	STRING ("$")			// text literal		(not used as token)
	;

	/**
	 * the token parser requires the list of delimiters
	 * @return a String containing all delimiters
	 */
	public static String getDelimiters ()
	{
		return
				COMMA.token +
				OPEN_ARRAY.token +
				OPEN_OBJECT.token +
				CLOSE_OBJECT.token +
				CLOSE_ARRAY.token +
				COLON.token
				;						// ",[{}]:"
	}

	/**
	 * @param token the text of the token being identified
	 */
	JsonPrimitives (String token) { this.token = token; }

	/**
	 * @return the token identified by this symbol
	 */
	public String getToken () { return token; }
	private final String token;

}
