
package net.myorb.data.notations.json;

import java.util.HashMap;

/**
 * processing for text escape rules
 * @author Michael Druckman
 */
public class JsonStringRules
{

	/**
	 * replace quote and solidus characters
	 * @param text the string contents to be escaped
	 * @return text with escape sequences translated
	 */
	public static String escapeFor (String text)
	{
		int quote = text.indexOf (QUOTE), sol = text.indexOf (R_SOLIDUS);
		int starting = sol < 0 ? quote: (quote < 0 ? sol : Math.min (sol, quote));
		if (starting >= 0) return escapeFor (text, starting);
		else return text;
	}
	public static String escapeFor (String text, int starting)
	{
		char c;
		StringBuffer buf =
			new StringBuffer (text.substring (0, starting));
		for (int i = starting; i < text.length (); i++)
		{
			if ((c = text.charAt (i)) == R_SOLIDUS)
			{ buf.append (R_SOLIDUS).append (R_SOLIDUS); }
			else if (c == QUOTE) { buf.append (R_SOLIDUS).append (QUOTE); }
			else buf.append (c);
		}
		return buf.toString ();
	}
	public static char R_SOLIDUS = '\\', QUOTE = '"';

	/**
	 * translate escape sequences
	 * @param text the text of the string
	 * @return the escaped content
	 */
	public static String escape (String text)
	{
		if (text.indexOf (R_SOLIDUS) < 0) return text;
		else return escapeTranslated (text);
	}

	/**
	 * translate escaped sequences
	 * @param text the text of the full string
	 * @return the translated string
	 */
	public static String escapeTranslated (String text)
	{
		char c, escaped;
		StringBuffer altered = new StringBuffer ();
		for (int pos = 0; pos < text.length (); pos++)
		{
			//TODO: not fully implemented
			if ((c = text.charAt (pos)) != R_SOLIDUS) { altered.append (c); continue; }
			if (pos > text.length () - 2) { throw new RuntimeException ("Invalid escape sequence"); }
			if ((escaped = text.charAt (pos + 1)) != 'u') { altered.append (translate (escaped)); pos++; continue; }
			if (pos > text.length () - 6) { throw new RuntimeException ("Invalid escape sequence"); }
			altered.append (charFor (text.substring (pos+2, pos+6)));
			pos += 5;
		}
		return altered.toString ();
	}

	/**
	 * unicode escape sequence
	 * @param digits the HEX digits for the unicode character
	 * @return a single unicode character (16 bit)
	 */
	static char charFor (String digits)
	{
		int value = 0;
		try { value = Integer.parseInt (digits, 16); }
		catch (Exception e) { throw new RuntimeException ("Invalid escape sequence: " + digits); }
		return (char) value;
	}

	/**
	 * translation for escape character
	 * @param escaped the character identifying the escape sequence
	 * @return the translation for the sequence
	 */
	static char translate (char escaped)
	{
		if ( ! ESCAPE.containsKey (escaped) )
		{ throw new RuntimeException ("Invalid escape sequence"); }
		return ESCAPE.get (escaped);
	}
	public static HashMap<Character,Character> ESCAPE = new HashMap<> ();

	static
	{
		/*
		 * the recognized escape characters
		 */
		ESCAPE.put ('"', '"');
		ESCAPE.put ('b', '\b');
		ESCAPE.put ('f', '\f');
		ESCAPE.put ('r', '\r');
		ESCAPE.put ('n', '\n');
		ESCAPE.put ('t', '\t');
		ESCAPE.put ('\\', '\\');
		ESCAPE.put ('/', '/');
	}

}
