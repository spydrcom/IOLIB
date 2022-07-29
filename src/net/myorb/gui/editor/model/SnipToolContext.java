
package net.myorb.gui.editor.model;

import net.myorb.gui.editor.SnipToolPropertyAccess;

import javax.swing.text.Style;
import javax.swing.text.Element;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.ViewFactory;
import javax.swing.text.View;

import java.awt.Color;
import java.awt.Font;

/**
 * StyleContext extension for SnipTool editor
 * @author Michael Druckman
 */
public class SnipToolContext  extends StyleContext implements ViewFactory
{


	/**
	 * The styles representing the actual token types.
	 */
	Style[] tokenStyles;

	/**
	 * Cache of foreground colors to represent the various tokens.
	 */
	transient Color[] tokenColors;

	/**
	 * Cache of fonts to represent the various tokens.
	 */
	transient Font[] tokenFonts;

	/**
	 * Key to be used in AttributeSet's holding a value of Token.
	 */
	public static final Object TokenAttribute = new SnipToolToken.AttributeKey ();


	public SnipToolContext (SnipToolPropertyAccess properties)
	{
		super();

		this.maximumScanValue = properties.getMaximumScanValue ();

		tokenStyles = new Style[maximumScanValue + 1];

		process (properties.getAll (), getStyle (DEFAULT_STYLE));
	}
	protected int maximumScanValue;


	public void process (SnipToolToken[] tokens, Style root)
	{
		int n = tokens.length;

		for (int i = 0; i < n; i++)
		{
			SnipToolToken t = tokens[i];
			Style parent = getStyle (t.getCategory());

			if (parent == null)
			{
				parent = addStyle (t.getCategory (), root);
			}

			Style s = addStyle (null, parent);
			s.addAttribute (SnipToolToken.TokenAttribute, t);
			tokenStyles[t.getScanValue()] = s;
		}
	}


	/**
	 * Fetch the foreground color to use for a lexical token with the given
	 * value.
	 * 
	 * @param code
	 *            attribute set from a token element that has a Token in the
	 *            set.
	 * @return color
	 */
	public Color getForeground (int code)
	{
		if (tokenColors == null)
		{
			tokenColors = new Color[maximumScanValue + 1];
		}

		if ((code >= 0) && (code < tokenColors.length))
		{
			Color c = tokenColors[code];

			if (c == null)
			{
				Style s = tokenStyles[code];
				c = StyleConstants.getForeground (s);
			}

			return c;
		}

		return Color.black;
	}


	/**
	 * Fetch the font to use for a lexical token with the given scan value.
	 * @param code element code
	 * @return font
	 */
	public Font getFont (int code)
	{
		if (tokenFonts == null) {
			tokenFonts = new Font[maximumScanValue + 1];
		}
		if (code < tokenFonts.length) {
			Font f = tokenFonts[code];
			if (f == null) {
				Style s = tokenStyles[code];
				f = getFont(s);
			}
			return f;
		}
		return null;
	}

	/**
	 * Fetches the attribute set to use for the given scan code. The set is
	 * stored in a table to facilitate relatively fast access to use in
	 * conjunction with the scanner.
	 * @param code element code
	 * @return style to use
	 */
	public Style getStyleForScanValue (int code)
	{
		if (code < tokenStyles.length)
		{
			return tokenStyles[code];
		}
		return null;
	}


	/* (non-Javadoc)
	 * @see javax.swing.text.ViewFactory#create(javax.swing.text.Element)
	 */
	public View create (Element element)
	{
		return new SnipToolView (element);
	}


	private static final long serialVersionUID = -7292184372169423008L;

}

