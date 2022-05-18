
package net.myorb.gui.graphics;

import net.myorb.gui.components.SimpleScreenIO;

import java.util.ArrayList;
import java.util.HashMap;
import java.awt.Color;

/**
 * organized lists of color names
 * @author Michael Druckman
 */
public class ColorNames extends SimpleScreenIO
{


	/**
	 * list of all names supplied to System properties
	 */
	public static final String[] LIST = 
		{	
				"ALICE_BLUE",
				"ANTIQUE_WHITE",
				"AQUA",
				"AQUA_MARINE",
				"AZURE",
				"BEIGE",
				"BISQUE",
				"BLACK",
				"BLANCHED_ALMOND",
				"BLUE",
				"BLUE_VIOLET",
				"BROWN",
				"BURLY_WOOD",
				"CADET_BLUE",
				"CHARTREUSE",
				"CHOCOLATE",
				"CORAL",
				"CORN_FLOWER_BLUE",
				"CORN_SILK",
				"CRIMSON",
				"CYAN",
				"DARK_BLUE",
				"DARK_CYAN",
				"DARK_GOLDEN_ROD",
				"DARK_GRAY",
				"DARK_GREY",
				"DARK_GREEN",
				"DARK_KHAKI",
				"DARK_MAGENTA",
				"DARK_OLIVE_GREEN",
				"DARK_ORANGE",
				"DARK_ORCHID",
				"DARK_RED",
				"DARK_SALMON",
				"DARK_SEA_GREEN",
				"DARK_SLATE_BLUE",
				"DARK_SLATE_GRAY",
				"DARK_TURQUOISE",
				"DARK_VIOLET",
				"DEEP_PINK",
				"DEEP_SKY_BLUE",
				"DIM_GRAY",
				"DIM_GREY",
				"DODGER_BLUE",
				"FIREBRICK",
				"FLORAL_WHITE",
				"FOREST_GREEN",
				"GAINSBORO",
				"GHOST_WHITE",
				"GOLD",
				"GOLDEN_ROD",
				"GRAY",
				"GREY",
				"GREEN",
				"GREEN_YELLOW",
				"HONEYDEW",
				"HOT_PINK",
				"INDIAN_RED",
				"INDIGO",
				"IVORY",
				"KHAKI",
				"LAVENDER",
				"LAVENDER_BLUSH",
				"LAWN_GREEN",
				"LEMON_CHIFFON",
				"LIGHT_BLUE",
				"LIGHT_CORAL",
				"LIGHT_CYAN",
				"LIGHT_GOLDEN_ROD_YELLOW",
				"LIGHT_GRAY",
				"LIGHT_GREY",
				"LIGHT_GREEN",
				"LIGHT_PINK",
				"LIGHT_SALMON",
				"LIGHT_SEA_GREEN",
				"LIGHT_SKY_BLUE",
				"LIGHT_SLATE_GRAY",
				"LIGHT_STEEL_BLUE",
				"LIGHT_YELLOW",
				"LIME",
				"LIME_GREEN",
				"LINEN",
				"MAGENTA",
				"FUCHSIA",
				"MAROON",
				"MEDIUM_AQUA_MARINE",
				"MEDIUM_BLUE",
				"MEDIUM_ORCHID",
				"MEDIUM_PURPLE",
				"MEDIUM_SEA_GREEN",
				"MEDIUM_SLATE_BLUE",
				"MEDIUM_SPRING_GREEN",
				"MEDIUM_TURQUOISE",
				"MEDIUM_VIOLET_RED",
				"MIDNIGHT_BLUE",
				"MINT_CREAM",
				"MISTY_ROSE",
				"MOCCASIN",
				"NAVAJO_WHITE",
				"NAVY",
				"OLD_LACE",
				"OLIVE",
				"OLIVE_DRAB",
				"ORANGE",
				"ORANGE_RED",
				"ORCHID",
				"PALE_GOLDEN_ROD",
				"PALE_GREEN",
				"PALE_TURQUOISE",
				"PALE_VIOLET_RED",
				"PAPAYA_WHIP",
				"PEACH_PUFF",
				"PERU",
				"PINK",
				"PLUM",
				"POWDER_BLUE",
				"PURPLE",
				"RED",
				"ROSY_BROWN",
				"ROYAL_BLUE",
				"SADDLE_BROWN",
				"SALMON",
				"SANDY_BROWN",
				"SEA_GREEN",
				"SEA_SHELL",
				"SIENNA",
				"SILVER",
				"SKY_BLUE",
				"SLATE_BLUE",
				"SLATE_GRAY",
				"SNOW",
				"SPRING_GREEN",
				"STEEL_BLUE",
				"TAN",
				"TEAL",
				"THISTLE",
				"TOMATO",
				"TURQUOISE",
				"VIOLET",
				"WHEAT",
				"WHITE",
				"WHITE_SMOKE",
				"YELLOW",
				"YELLOW_GREEN"
		};

	@SuppressWarnings("serial")
	public static class ColorList extends ArrayList <String> {}

	public static HashMap <String, Color> MAP = new HashMap <> ();

	static
	{
		for (String name : LIST)
		{
			MAP.put (name, Colour.getColor (name));
		}
	}


	/**
	 * @return list of unsorted names
	 */
	public static ColorList unsorted ()
	{
		ColorList list = new ColorList ();

		for (String name : LIST)
		{
			list.add (name);
		}

		return list;
	}


	/**
	 * @return sorted by name
	 */
	public static ColorList byName ()
	{
		ColorList list = unsorted ();
		list.sort (null);
		return list;
	}


	/**
	 * @return sorted by RGB code
	 */
	public static ColorList byCode ()
	{
		ColorList list = unsorted ();
		list.sort
		(
			(l, r) -> (MAP.get (l).getRGB () & RGB_MASK) < (MAP.get (r).getRGB () & RGB_MASK) ? -1 : 1
		);
		return list;
	}
	public static int RGB_MASK = 0xFFFFFF;


	/**
	 * 8x8 matrix of green/blue.
	 *  each element can hold 32 colors
	 * @return the green/blue matrix
	 */
	public static ColorList[][] gbMatrix ()
	{
		ColorList[][] gb = new ColorList[8][8];

		for (int i = 0; i < 8; i++)
		{
			for (int j = 0; j < 8; j++)
			{
				gb[i][j] = new ColorList ();
			}
		}

		for (String name : byCode ())
		{
			Color color = MAP.get (name);
			int g = color.getGreen () / 32, b = color.getBlue () / 32;
			gb[g][b].add (name);
		}

		return gb;
	}


}

