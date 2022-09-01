
package net.myorb.gui.tests;

import net.myorb.gui.graphics.ColorPropertiesProcessor;

import java.awt.Color;

import java.util.List;
import java.util.ArrayList;

import java.util.Map;

public class BasicPalateTest extends ColorPropertiesProcessor
{

	public void addToList (String name, String value)
	{
		super.addToList (name, value);
		sourceOrderNames.add (name);
	}
	List <String> sourceOrderNames = new ArrayList <String> ();

	public static void main
		(String[] args)
	throws Exception
	{
		BasicPalateTest palate;
		Map <String, Color> map =
			(palate = new BasicPalateTest ())
			.processStandardColorList ()
			.getColorMap ();

		for (String name : palate.sourceOrderNames)
		{
			Color c = map.get (name);

			System.out.println
			(
				name + " : x" +
				Integer.toHexString (c.getRGB ())
			);
		}
	}

}
