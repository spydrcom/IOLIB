
package net.myorb.gui.tests;

import net.myorb.gui.graphics.ColorNames;
import net.myorb.gui.graphics.ColorDisplays;
import net.myorb.gui.graphics.ColorPropertiesProcessor;
import net.myorb.gui.components.SimpleScreenIO;

import java.awt.Color;

public class ColorTest extends SimpleScreenIO
{

	public ColorTest ()
	{
		ColorPropertiesProcessor.processColorList ();
	}

	public Panel getMatrixPanel ()
	{
		return ColorDisplays.getGBmatrixPanel ();
	}

	public Panel getPalettePanel ()
	{
		return ColorDisplays.fullPalettePanel ();
	}

	public static void main(String[] args) throws Exception
	{
		ColorPropertiesProcessor.processColorList ();

		System.out.println ("Names: ");
		for (String name : ColorNames.LIST)
		{
			System.out.print (name);
			System.out.print (": ");
			System.out.print (Integer.toHexString (Color.getColor (name).getRGB ()));
			System.out.println ();
		}
		System.out.println ();

		System.out.println ("By Name:");
		System.out.println (ColorNames.byName ());
		System.out.println ();

		System.out.println ("By Code:");
		System.out.println (ColorNames.byCode ());
		System.out.println ();

		ColorDisplays.showFullPalette ();
		ColorDisplays.showGBmatrix ();
	}


}
