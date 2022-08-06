
package net.myorb.gui;

import net.myorb.gui.ScribeEngine.Attribution;
import net.myorb.jxr.JxrParser;

import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.Style;

import java.awt.Color;
import java.awt.font.TextAttribute;
import java.awt.Font;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import java.io.FileInputStream;
import java.io.File;

/**
 * a manager for attributes making a style
 * @author Michael Druckman
 */
public class StyleManager extends StyleContext implements Attribution
{


	/**
	 * unique objects which can be used to identify attributes
	 */
	public interface Attribute {}
	public static Attribute getStyleAttributeFor (String name)
	{ return new StyleAttribute (name); }

	public static final Attribute STYLE_CODE = getStyleAttributeFor ("StyleCode");


	public StyleManager ()
	{
		super ();
		this.defaultStyle = getStyle (DEFAULT_STYLE);
		this.styles = new ArrayList<Style>();
		this.colors = new ArrayList<Color>();
		this.fonts = new ArrayList<Font>();
	}
	protected Style defaultStyle;


	/**
	 * add a new style to the StyleContext structure
	 * @param name the name to associate with the style
	 * @return a newly created style object
	 */
	public Style addStyle (String name)
	{
		return addStyle (name, defaultStyle);
	}


	/**
	 * add a style to the cache at the current style code
	 * @param style the style object being cached
	 */
	public void cacheStyle (Style style)
	{
		colors.add (StyleConstants.getForeground (style));
		fonts.add (getFont (style)); styles.add (style);
	}
	protected List<Style> styles;
	protected List<Color> colors;
	protected List<Font> fonts;


	/*
	 * ScribeEngine.Attribution implementation
	 */

	/* (non-Javadoc)
	 * @see net.myorb.gui.ScribeEngine.Attribution#getStyleFor(int)
	 */
	public Style getStyleFor (int styleCode) { return styles.get (styleCode); }

	/* (non-Javadoc)
	 * @see net.myorb.gui.ScribeEngine.Attribution#getColorFor(int)
	 */
	public Color getColorFor (int styleCode) { return colors.get (styleCode); }

	/* (non-Javadoc)
	 * @see net.myorb.gui.ScribeEngine.Attribution#getFontFor(int)
	 */
	public Font getFontFor (int styleCode) { return fonts.get (styleCode); }


	/**
	 * add new style to system and return code
	 * @param style the style object being posted
	 * @return the code which identifies the style
	 */
	public int assignStyleCode (Style style)
	{
		int assigned = nextStyleCode++;
		style.addAttribute (STYLE_CODE, assigned);
		cacheStyle (style);
		return assigned;
	}
	protected int nextStyleCode = 0;


	/**
	 * retrieve the code value for the style
	 * @param style the style object being queried
	 * @return the code which identifies the style
	 */
	public int getStyleCodeFor (Style style)
	{
		Number code =
			(Number) style.getAttribute (STYLE_CODE);
		return code.intValue ();
	}


	/**
	 * set the attributes for a style
	 * @param s the style to be given the attributes
	 * @param a the map of TextAttribute taken from the font
	 */
	public void setAttributesFor (Style s, Map<TextAttribute,?> a)
	{
		StyleConstants.setFontSize (s, valueOf (a.get (TextAttribute.SIZE)));
		StyleConstants.setFontFamily (s, a.get (TextAttribute.FAMILY).toString ());
		StyleConstants.setStrikeThrough (s, (Boolean) (a.get (TextAttribute.STRIKETHROUGH)));
		StyleConstants.setUnderline (s, (Boolean) (a.get (TextAttribute.UNDERLINE)));
	}
	public int valueOf (Object value) { return ((Number) value).intValue (); }


	/**
	 * set the attributes for a style
	 * @param s the style to be given the attributes
	 * @param font the font to be used in render
	 * @param color the foreground color
	 */
	public void setAttributesFor (Style s, Font font, Color color)
	{
		StyleConstants.setForeground (s, color);
		StyleConstants.setBold (s, font.isBold ());
		StyleConstants.setItalic (s, font.isItalic ());
		setAttributesFor (s, font.getAttributes ());
	}


	/**
	 * use JXR to read configuration script
	 */
	public void readScript (String scriptPath)
	{
		try
		{
			FileInputStream
			source = new FileInputStream (new File (scriptPath));
			JxrParser.read (source, SCRIPT_PARAMETER, this);
		}
		catch (Exception e) { e.printStackTrace (); }
	}
	public static final String SCRIPT_PARAMETER = "StyleManager";


	private static final long serialVersionUID = -6051870418619697463L;

}


/**
 * a unique marker for identifying a style attribute
 */
class StyleAttribute implements StyleManager.Attribute
{
	public String toString () { return name; }
	public StyleAttribute (String name) { this.name = name; }
	protected String name;
}

