
package net.myorb.gui.tests;

import net.myorb.gui.components.SimpleScreenIO;
import net.myorb.gui.graphics.NAV;

import java.io.File;

public class ImageDisplay extends SimpleScreenIO
{

	public static void main (String[] args) throws Exception
	{
		File dir = new File ("images/");
		File[] list = dir.listFiles (); Image[] images = new Image[9];
		for (int i=0; i<9; i++) images[i] = new Image (list[i].getAbsolutePath ());
		new NAV ().show ("NAV Images", images);
	}

}
