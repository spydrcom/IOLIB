
package net.myorb.gui.graphics;

import java.awt.image.BufferedImage;

import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Font;

import java.io.PrintStream;

/**
 * tools for conversion between image and pixel matrix
 * @author Michael Druckman
 */
public class ImageTools
{

	/**
	 * @param image image to be displayed
	 * @param title a title for the display frame
	 */
	public static void show (BufferedImage image, String title)
	{
		DisplayImaging.showImage (image, title, null);
	}

	/**
	 * get pixel matrix for a character of a font
	 * @param c the character to be represented as pixels
	 * @param font the font to use to render the character
	 * @param xAdjust the x-axis offset in the buffer
	 * @param yAdjust the y-axis offset in the buffer
	 * @return a matrix of pixels
	 */
	public static boolean[][] pixelsFor (char c, Font font, int xAdjust, int yAdjust)
	{
		return getPixels (imageFor (c, font, xAdjust, yAdjust));
	}

	/**
	 * get image of a character of a font
	 * @param c the character to be represented in image
	 * @param font the font to use to render the character
	 * @param xAdjust the x-axis offset in the buffer
	 * @param yAdjust the y-axis offset in the buffer
	 * @return an image of the character
	 */
	public static BufferedImage imageFor (char c, Font font, int xAdjust, int yAdjust)
	{
		BufferedImage image;
		Graphics2D g = (image = DisplayImaging.createBufferedImage (100)).createGraphics ();  
		g.setFont (font.deriveFont (100.0f)); g.setColor (Color.white);
		g.drawString (new String (new char[]{c}), xAdjust, yAdjust);
		return image;
	}

	/**
	 * convert pixel matrix to image
	 * @param pixels pixels to be represented
	 * @return image containing the pixels
	 */
	public static BufferedImage imageFor (boolean[][] pixels)
	{
		int h = pixels.length, w = pixels[0].length;
		BufferedImage image = new BufferedImage (h, w, BufferedImage.TYPE_INT_RGB);
		for (int y=0; y<h; y++)
		{
			for (int x=0; x<w; x++)
			{
				if (pixels[x][y])
				{
					image.setRGB (x, y, -1);
				}
			}
		}
		return image;
	}

	/**
	 * build over-strike of 2 characters
	 * @param c the primary character for the composition
	 * @param m the mark to use as over-strike character
	 * @return a pixel matrix of the composition
	 */
	public static boolean[][] compose (boolean[][] c, boolean[][] m)
	{
		int h = c.length, w = c[0].length;
		boolean[][] result = new boolean[h][w];
		for (int y=0; y<h; y++)
		{
			for (int x=0; x<w; x++)
			{
				result[x][y] = c[x][y] | m[x][y];
			}
		}
		return result;
	}

	/**
	 * format a character representation of a pixel matrix
	 * @param pixels a pixel matrix to be displayed
	 * @param toStream the stream to write to
	 */
	public static void showPixels (boolean[][] pixels, PrintStream toStream)
	{
		int h = pixels.length, w = pixels[0].length;
		for (int y = 0; y < h; y++)
		{
			for (int x = 0; x < w; x++)
			{ toStream.print (pixels[x][y] ? "*" : " "); }
			toStream.println ();
		}
	}

	/**
	 * convert image to pixel matrix
	 * @param image the image to be represented
	 * @return the representative pixel matrix
	 */
	public static boolean[][] getPixels (BufferedImage image)
	{
		boolean[][] pixels = new boolean[image.getHeight()][image.getWidth()];
		for (int y=0; y<image.getHeight(); y++)
		{
			for (int x=0; x<image.getWidth(); x++)
			{
				pixels[x][y] = image.getRGB (x,y) == -1;
			}
		}
		return pixels;
	}

}
