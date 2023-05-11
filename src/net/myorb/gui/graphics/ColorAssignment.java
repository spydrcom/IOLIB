
package net.myorb.gui.graphics;

import java.awt.Color;

/**
 * implementation of temperature color model
 * - colors selected by algorithm centered on hue ranges
 * @author Michael Druckman
 */
public class ColorAssignment extends ColorNames
{


	// temperature sequence of colors low to high
	// >> Blue Green Yellow Orange Red


	/**
	 * get color based on value range
	 * @param value a value in the range lo..hi
	 * @param lo the low end of the value domain
	 * @param hi the high end of the value domain
	 * @return the Color object for given value
	 */
	public static Color getTemperatureColorFrom (double value, double lo, double hi)
	{
		double range = hi - lo;
		double adjustedValue = value - lo;
		return getTemperatureColorFor (adjustedValue / range);
	}


	/**
	 * get color for standard 0..1 range
	 * @param value the requested entry specified as float range 0..1
	 * @return the Color object for given value
	 */
	public static Color getTemperatureColorFor (double value)
	{
		if ( value < 0.0 || value > 1.0 )
		{ throw new RuntimeException ("Temperature Color request out of range 0..1"); }
		Number index = 100.0 * value; int entryNumber = index.intValue ();
		return TemperatureColorIndex.get (entryNumber);
	}


}

