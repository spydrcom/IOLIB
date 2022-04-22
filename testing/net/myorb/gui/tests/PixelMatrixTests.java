
package net.myorb.gui.tests;

import net.myorb.gui.components.SimpleScreenIO;
import net.myorb.gui.graphics.DisplayImaging;
import net.myorb.gui.graphics.ImageTools;

import java.awt.image.BufferedImage;
import java.awt.event.MouseListener;

import java.awt.Font;

public class PixelMatrixTests extends ImageTools
{

	static char C1 = 0x2282, C2 = 0x2283;
	static char M1 = 0x002F, M2 = 0x005C, M3 = 0x0305;
	
	public static void main (String... args)
	{
		DisplayImaging.PixelMatrixPanel p; MouseListener l;
		Font f = new Font ("Dialog.Plain", Font.PLAIN, 20);

		boolean[][] cim = show ('X', f, 15, 85);
		boolean[][] mim = show (M3, f, 40, 90);

		boolean[][] cmp = compose (cim, mim);
		showPixels (cmp, System.out);

		show (imageFor (cmp), "Composed");

		l = new DisplayImaging.PixelToggle ();
		p = DisplayImaging.getPixelMatrixPanel (cmp, l);
		SimpleScreenIO.showAndExit (p, "matrix", 500, 500);
		showPixels (p.getPixels (), System.out);
	}

	public static boolean[][] show (char c, Font font, int x, int y)
	{
		BufferedImage image; boolean[][] p;
		show (image = imageFor (c, font, x, y), "Test");
		showPixels (p = getPixels (image), System.out);
		return p;
	}

}
