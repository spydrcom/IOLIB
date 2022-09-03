
package net.myorb.gui.tests;

import net.myorb.gui.graphics.ColorPropertiesProcessor;
import net.myorb.gui.components.MenuListFactory;
import net.myorb.gui.graphics.ColorDisplays;
import net.myorb.gui.graphics.ColorNames;

import java.awt.Color;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BasicPalateTest extends ColorPropertiesProcessor
{


	public void addToList (String name, String value)
	{
		super.addToList (name, value);
		sourceOrderNames.add (name);
	}
	ColorNames.ColorList sourceOrderNames = new ColorNames.ColorList ();


	public static void main
		(String[] args)
	throws Exception
	{
		BasicPalateTest palate;

		ColorNames.ColorMap map =
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
		ColorDisplays.showPaletteColumn (palate.sourceOrderNames, new PalatePopupFactory (), map);
	}


	/**
	 * standard file taken from properties/standard.csv
	 * @return this processor
	 */
	public ColorPropertiesProcessor processStandardColorList ()
	{
		return processColorCSV (new File ("properties/standard.csv"));
	}


}


class PalatePopupFactory extends MenuListFactory
{
	PalatePopupFactory ()
	{
		super (PalatePopupProcessor.getNames (), new PalatePopupProcessor ());
	}
}

class PalatePopupProcessor implements MenuListFactory.Processor
{

	enum Commands { Edit, Move_Up, Move_Down, Rempve }

	static List <String> getNames ()
	{
		List <String>
			names = new ArrayList <> ();
		for (Commands c : Commands.values ())
		{ names.add (c.name ().replace ('_', ' ')); }
		return names;
	}

	/* (non-Javadoc)
	 * @see net.myorb.gui.components.MenuListFactory.Processor#process(java.lang.String, int)
	 */
	public void process (String command, int item)
	{
		System.out.println (command + " " + item);
	}

}



