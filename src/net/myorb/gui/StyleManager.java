
package net.myorb.gui;

import javax.swing.text.BadLocationException;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.TabExpander;
import javax.swing.text.Utilities;
import javax.swing.text.Segment;
import javax.swing.text.Style;

import java.awt.Color;
import java.awt.Graphics;

import java.awt.font.TextAttribute;
import java.awt.Font;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * a manager for attributes making a style
 * @author Michael Druckman
 */
public class StyleManager extends StyleContext
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
	 * @param name the name to associate with the style
	 * @return a newly created style object
	 */
	public Style addStyle (String name)
	{
		return addStyle (name, defaultStyle);
	}


	/**
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


	public Style getStyleFor (int styleCode) { return styles.get (styleCode); }
	public Color getColorFor (int styleCode) { return colors.get (styleCode); }
	public Font getFontFor (int styleCode) { return fonts.get (styleCode); }


	/**
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
	 * @param g the Graphic being constructed for the render
	 * @param t the tab expander assigned for the render in the caller
	 * @param text a segment of text to be drawn in the Graphic being constructed
	 * @param x the x coordinate within the Graphic to start the draw of the text
	 * @param y the y coordinate within the Graphic to start the draw of the text
	 * @param mark the start point describing the location within the model
	 * @param styleCode the style to use which identifies the font and color
	 * @return the x coordinate within the Graphic ending the draw of the text
	 * @throws BadLocationException for the tabbed text draw layer
	 */
	public int draw
		(
			Graphics g,
			TabExpander t,
			Segment text,
			int x, int y,
			int mark,
			int styleCode
		)
	throws BadLocationException
	{
		attribute (g, styleCode);
		return Utilities.drawTabbedText (text, x, y, g, t, mark);
	}
	
	/**
	 * apply a style code to a Graphic being rendered
	 * @param g the Graphic being constructed for the render
	 * @param styleCode the code that identifies a style
	 */
	public void attribute 
		(
			Graphics g,
			int styleCode
		)
	{
		g.setColor (getColorFor (styleCode));
		g.setFont (getFontFor (styleCode));
	}
	

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

