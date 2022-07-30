
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
 * - refactor of original JavaKit example from Sun
 * - by Timothy Prinzing version 1.2 05/27/99
 * - refactor done in summer 2022
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
	 * establish content for tokens of upper layer
	 * @param properties access to properties of upper layer
	 */
	public SnipToolContext (SnipToolPropertyAccess properties)
	{ super(); process (properties.getAll ()); }


	/**
	 * construct the token styles.
	 * - assign unique category parent styles
	 * @param tokens the list of tokens in the system
	 */
	public void process (SnipToolToken.SystemTokens tokens)
	{
		this.allocate (tokens.size ());
		Style root = getStyle (DEFAULT_STYLE);
		for (SnipToolToken t : tokens) { link (t, root); }
	}
	void link (SnipToolToken token, Style root)
	{
		Style style = addStyle
			(null, parentFor (token.getCategory (), root));
		style.addAttribute (SnipToolToken.TokenAttribute, token);
		tokenStyles[token.getScanValue ()] = style;
	}
	Style parentFor (String cat, Style root)
	{
		Style parent = getStyle (cat);
		if (parent != null) return parent;
		else return addStyle (cat, root);
	}
	void allocate (int n)
	{
		this.tokenCount = n;
		this.tokenStyles = new Style[n];
		this.tokenColors = new Color[n];
		this.tokenFonts = new Font[n];
	}
	protected int tokenCount;


	/**
	 * verify code is legal
	 * @param code the code to check
	 * @return TRUE for legal
	 */
	public boolean isInRange (int code)
	{
		return (code < 0) || (code >= tokenCount);
	}


	/**
	 * Fetch the foreground color to use
	 * for a lexical token with the given value.
	 * 
	 * @param code
	 *            attribute set from a token element
	 *            that has a Token in the set.
	 * @return color from code
	 */
	public Color getForeground (int code)
	{
		Color colorFromCode;
		if ( ! isInRange (code) ) return Color.black;
		if ((colorFromCode = tokenColors[code]) != null) return colorFromCode;
		return tokenColors[code] = StyleConstants.getForeground (tokenStyles[code]);
	}

	public Color getForeground (SnipToolToken token)
	{
		if (token == null) return Color.black;
		else return getForeground (token.getScanValue ());
	}


	/**
	 * Fetch the font to use for a
	 *  lexical token with the given scan value.
	 * @param code element code
	 * @return font for code
	 */
	public Font getFont (int code)
	{
		Font f;
		if ( ! isInRange (code) ) return null;
		if ((f = tokenFonts[code]) != null) return f;
		return tokenFonts[code] = getFont (tokenStyles[code]);
	}


	/**
	 * Fetches the attribute set to use for the given scan code.
	 * The set is stored in a table to facilitate relatively fast
	 * access to use in conjunction with the scanner.
	 * @param code assigned to token
	 * @return style for code
	 */
	public Style getStyleForScanValue (int code)
	{
		if ( ! isInRange (code) ) return null;
		else return tokenStyles[code];
	}


	/* (non-Javadoc)
	 * @see javax.swing.text.ViewFactory#create(javax.swing.text.Element)
	 */
	public View create (Element element)
	{
		return new SnipToolView (element, this);
	}


	private static final long serialVersionUID = -7292184372169423008L;

}

