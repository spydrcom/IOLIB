
package net.myorb.data.unicode;

import net.myorb.data.abstractions.Status;
import net.myorb.gui.components.SimpleScreenIO;

import java.awt.Font;

/**
 * commonly used static methods
 * @author Michael Druckman
 */
public class Toolkit extends SimpleScreenIO
{

	/*
	 * hexadecimal parse/format
	 */

	/**
	 * hex display string for a value
	 * @param v the value to be displayed
	 * @return the text version
	 */
	public static String toHex (int v)
	{
		return Integer.toHexString (v).toUpperCase ();
	}

	/**
	 * @param value number to be displayed
	 * @return hex display of value forced to 16 bits width (4 hex digits)
	 */
	public static String hex4 (int value)
	{
		String digits;
		if (value >= 0x1000) digits = toHex (value);
		else digits = toHex (0x10000 + value).substring (1);
		return "0x" + digits;
	}

	/**
	 * parse text as hex value
	 * @param v the display text to be parsed
	 * @return the parsed value
	 */
	public static int parseHex (String v) { return Integer.parseInt (v, 16); }

	/*
	 * label formatting
	 */

	/**
	 * make a label with an arbitrary Unicode character
	 * @param value the Unicode value for a character to display
	 * @return label for character represented by value
	 */
	public static Label labelFor (int value)
	{
		return new Label (stringFor (value));
	}

	/**
	 * change label text to be hex display of value
	 * @param l the label to update
	 * @param v the update value
	 */
	public static void setHexText (Label l, int v)
	{
		l.setText (toHex (v));
	}

	/**
	 * make a label holding a hex value as text display
	 * @param value the Unicode value for a character
	 * @return a label with the value as hex text
	 */
	public static Label hexLabelFor (int value)
	{
		return new Label (toHex (value));
	}

	/*
	 * Java structure conversion
	 */

	/**
	 * make string version of character
	 * @param value the Unicode value for a character
	 * @return a string with the character in first position
	 */
	public static String stringFor (int value)
	{
		return new String (new char[]{(char) value});
	}

	/**
	 * get the character enlargement label
	 * @param frame the frame holding the enlargement
	 * @return the label in the display
	 */
	public static Label labelFrom (Object frame)
	{
		Panel p = (Panel) Frame.getFrameRoot (frame).getComponent (0);
		return (Label) p.getComponent (0);
	}

	/**
	 * get first character of label
	 * @param label a label holding a one character text string
	 * @return the character from the text string
	 */
	public static char charFrom (Label label)
	{
		return label.getText ().charAt (0);
	}

	/*
	 * font manipulation
	 */

	/**
	 * @param label set font size of label
	 * @param toSize the new point size
	 */
	public static void resizeFont (Label label, int toSize)
	{ label.setFont (label.getFont ().deriveFont ((float) toSize)); }

	/**
	 * @param l the label to be modified
	 * @param f font to use in place of currently found in label
	 * @param ps the new point size
	 */
	public static void useAvailableFontOrResize (Label l, Font f, int ps)
	{ if (f != null) l.setFont (f); else resizeFont (l, ps); }


	/*
	 * error handling
	 */


	/**
	 * build status for benign notifications
	 * @param relatedTo the panel generating the request
	 * @return Status object for INFO
	 */
	public static Status localInfo (Panel relatedTo)
	{
		return Status.newInstance (Status.Level.INFO).setComponent (relatedTo);
	}

	/**
	 * format Invalid Unicode Value error
	 * @param relatedTo the panel generating the request
	 * @throws Alert for illegal value
	 */
	public static void parserAlert (Panel relatedTo) throws Alert
	{ alert (localError ("Invalid Unicode Value", relatedTo)); }


	/**
	 * establish context for local error
	 * @param relatedTo the panel generating the request
	 * @return Status object containing context
	 */
	public static Status localError (Panel relatedTo)
	{ return Status.newInstance (Status.Level.ERROR).setComponent (relatedTo); }
	public static Status localError (String message, Panel relatedTo)
	{ return localError (relatedTo).setMessage (message); }

	/**
	 * build status for Symbol Processing Error
	 * @param message the message to include with status
	 * @param relatedTo the panel generating the request
	 * @return the status for the error condition
	 */
	public static Status symbolError (String message, Panel relatedTo)
	{
		return localError (message, relatedTo).setTitle ("Symbol Processing Error");
	}

	/**
	 * error generated by
	 *  exception during symbol search
	 * @param name the symbol not found in search
	 * @param relatedTo the panel generating the request
	 * @param cause the exception that caused the failure
	 * @throws Alert the alert for notification
	 */
	public static void symbolLookupFailed
	(String name, Panel relatedTo, Exception cause) throws Alert
	{
		alert (symbolError ("Symbol lookup failed: " + name, relatedTo).setCause (cause));
	}

	/**
	 * error for symbol not found
	 * @param name the symbol not found in search
	 * @param relatedTo the panel generating the request
	 * @throws Alert the alert for notification
	 */
	public static void symbolNotFound (String name, Panel relatedTo) throws Alert
	{
		alert (symbolError ("Symbol Not Found: " + name, relatedTo));
	}

	/**
	 * error for code support failure in font
	 * @param code the code not found supported by font
	 * @param relatedTo the panel generating the request
	 * @throws Alert the alert for notification
	 */
	public static void codeNotSupported (String code, Panel relatedTo) throws Alert
	{
		alert (symbolError ("Not supported in font: " + code, relatedTo));
	}

	/**
	 * error for non-BMP character reference
	 * @param relatedTo the panel generating the request
	 * @throws Alert the alert for notification
	 */
	public static void useBMPlaneOnly (Panel relatedTo) throws Alert
	{
		alert (localError ("BMP (U-16, 16 bit Unicode) only", relatedTo).setTitle ("Out of Range"));
	}


	/*
	 * sundry
	 */


	/**
	 * check for font support for code
	 * @param code the code not found supported by font
	 * @param font the font expected to support specified code
	 * @param relatedTo the panel generating the request
	 * @throws Alert the alert for notification
	 */
	public static void checkCodeSupported (int code, Font font, Panel relatedTo) throws Alert
	{
		if (!font.canDisplay (code)) alert (symbolError ("Not supported in font: " + hex4 (code), relatedTo));
	}

	/**
	 * decomposed code reference format
	 * @param primaryCode code for primary character
	 * @param markCode code for mark character
	 * @return formatted decomposed code
	 */
	public static String formatDecomposedCode (int primaryCode, int markCode)
	{
		return hex4 (primaryCode) + "-" + hex4 (markCode);
	}

	/**
	 * debug trace on/off
	 * @param message text for user display
	 * @throws Exception for terminal condition
	 */
	public static void trace (String message) throws Exception
	{
		if (ignore) return;
		if (terminal) throw new Exception (message);
		else System.out.println (message);
	}
	static boolean terminal = false, ignore = false;

}

