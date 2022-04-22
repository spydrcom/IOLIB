
package net.myorb.data.abstractions;

/**
 * the optional properties that control how values are to be displayed
 * @author Michael Druckman
 */
public class ValueDisplayProperties
{

	/**
	 * the name of the mode of formatting to be used
	 */
	public static String DEFAULT_DISPLAY_MODE = "Decimal";

	/**
	 * the count of digits to be displayed in a value
	 */
	public static int DEFAULT_DISPLAY_PRECISION = 16;


	/**
	 * format decimal value for display
	 * @param x the value to be formatted
	 * @param precision number of decimal places
	 * @return the formatted value
	 */
	public static String formatDecimalString (Double x, int precision)
	{
		int intVal = x.intValue ();
		if (x == intVal) return Integer.toString (intVal);
		if (precision > 0) return trim (String.format ("%."+precision+"f", x));
		else return Double.toString (x);
	}
	private static String trim (String source)
	{
		for (int i = source.length()-1; i > 0; i--)
		{
			if (source.charAt(i) == '0') continue;
			if (source.charAt(i) == '.') return source.substring (0, i);
			return source.substring (0, i+1);
		}
		return "0";
	}

}
