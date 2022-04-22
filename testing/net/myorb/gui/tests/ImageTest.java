
package net.myorb.gui.tests;

import net.myorb.gui.components.*;

public class ImageTest extends SimpleScreenIO
{
	public static void main (String... args)
	{
		WidgetFrame f = new WidgetFrame (new Image ("data/test.png"), "test");
		f.showAndExit();
	}
}
