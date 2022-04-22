
package net.myorb.gui.graphics;

import net.myorb.data.abstractions.CommonCommandParser;
import net.myorb.data.abstractions.SimplePropertiesManager;
import net.myorb.data.abstractions.SimpleUtilities;

import java.util.ArrayList;

import java.awt.Paint;
import java.awt.Color;

/**
 * tools for conversion between integer values and colors.
 *  codes are RGBA to allows for transparent and opaque and alpha points between
 * @author Michael Druckman
 */
public class ColorTools
{


	/**
	 * set the alpha value of a palette
	 * @param colors the palette of colors
	 * @param alpha the alpha value to be used
	 */
	public static void applyAlpha (Color[] colors, int alpha)
	{
		for (int i = 0; i < colors.length; i++)
		{
			Color c = colors[i];
			colors[i] = new Color (c.getRed (), c.getGreen (), c.getBlue (), alpha);
		}
	}


	/**
	 * list of values is treated as RGB
	 * @param values a list of integer values
	 * @return the translated colors list
	 */
	public static Color[] getColors (Integer[] values)
	{
		int c = 0;
		Color[] colors = new Color[values.length];
		for (int v : values) colors[c++] = new Color (v, alphaIncluded (v));
		return colors;
	}


	/**
	 * RGBA could be opaque or transparent
	 * @param rgba the color code to evaluate
	 * @return TRUE when NOT transparent NOR opaque
	 */
	public static boolean alphaIncluded (int rgba) { return ! (isOpaque (rgba) || isTransparent (rgba)); }


	/**
	 * check alpha channel for transparent
	 * @param rgba the color code to evaluate
	 * @return TRUE when ALPHA=0x00
	 */
	public static boolean isTransparent (int rgba) { return (rgba & ALPHA_MASK) == TRANSPARENT_VALUE; }


	/**
	 * check alpha channel for opaque
	 * @param rgba the color code to evaluate
	 * @return TRUE when ALPHA=0xFF
	 */
	public static boolean isOpaque (int rgba) { return (rgba & ALPHA_MASK) == OPAQUE_ALPHA; }


	/**
	 * constants for alpha channel mask and alpha constants for transparency and opacity
	 */
	public static final int
	OPAQUE_VALUE = 0xFF, TRANSPARENT_VALUE = 0x00,
	OPAQUE_ALPHA = 0xFF000000, ALPHA_MASK = 0xFF000000,
	INVALID_RGB_VALUE = Integer.MIN_VALUE;


	/**
	 * interpret token as color
	 * @param token the token to be interpreted
	 * @return a color object describing interpretation
	 */
	public static Color getColorFor (CommonCommandParser.TokenDescriptor token)
	{
		int rgb; if ((rgb = getRgbFor (token)) == INVALID_RGB_VALUE)
		{ throw new RuntimeException ("Expected color, found: " + token.getTokenImage ()); }
		return new Color (rgb);
	}


	/**
	 * interpret token as color
	 * @param token the token to be interpreted
	 * @return the RGB value for the color
	 */
	public static int getRgbFor (CommonCommandParser.TokenDescriptor token)
	{
		ColorPropertiesProcessor.processColorList ();

		try
		{
			switch (token.getTokenType ())
			{
				case RDX:
				case INT: return token.getTokenValueAsCoded ().intValue ();
				case IDN: return colorFor (token.getTokenImage ()).getRGB ();
				default:  return INVALID_RGB_VALUE;
			}
		}
		catch (Exception e)
		{
			throw new RuntimeException ("Color lookup error: " + token.getTokenImage (), e);
		}
	}


	/**
	 * find colors amongst tokens
	 * @param propertyValue parsed tokens to be interpreted
	 * @param list the list of values found in the tokens
	 */
	public static void parseColorList
		(
			SimplePropertiesManager.PropertyValueList propertyValue,
			ArrayList <Integer> list
		)
	{
		int color;
		for (CommonCommandParser.TokenDescriptor d : propertyValue)
		{ if ((color = getRgbFor (d)) != INVALID_RGB_VALUE) { list.add (color); } }
	}


	/**
	 * @param description name or radix expression
	 * @return the decoded color object
	 */
	public static Color colorFor (String description)
	{
		if (description.startsWith ("0x"))
		{
			return new Color (SimpleUtilities.radixValue (description).intValue ());
		}
		else return Color.getColor (description.toUpperCase ());
	}


	/**
	 * @param description a list of color descriptions
	 * @param paints the list converted to colors
	 */
	public static void paletteFor (String[] description, Paint[] paints)
	{
		for (int i = 0; i < paints.length; i++)
		{ paints[i] = colorFor (description[i]); }
	}


}

