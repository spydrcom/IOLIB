
package net.myorb.gui.graphics;

import net.myorb.gui.components.DisplayFrame;
import net.myorb.gui.components.SimpleScreenIO;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.GridLayout;

import java.awt.image.BufferedImage;

import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * layer above BufferedImage for pixel manipulation
 * @author Michael Druckman
 */
public class DisplayImaging extends SimpleScreenIO
{


	/**
	 * black background with no axis lines
	 * @param edgeSize the pixel edge size of the area
	 * @return a buffered image object
	 */
	public static BufferedImage createBufferedImage (int edgeSize)
	{
	    return new BufferedImage (edgeSize, edgeSize, BufferedImage.TYPE_INT_RGB);
	}
	public static BufferedImage createBufferedImage (int width, int height)
	{
	    return new BufferedImage (width, height, BufferedImage.TYPE_INT_RGB);
	}


	/**
	 * prepare a chart image as a four quadrant area
	 * @param edgeSize the pixel edge size of the area
	 * @return a buffered image object
	 */
	public static BufferedImage chartBufferedImage (int edgeSize)
	{
	    int full = edgeSize - MARGIN, half = full / 2;
		BufferedImage image = createBufferedImage (full);
	    Graphics2D g = image.createGraphics ();

	    g.setColor (Colour.gray);
	    g.fillRect (0, 0, full, full);

	    g.setColor (Colour.black);
	    g.drawLine (0, half, full, half);
	    g.drawLine (half, 0, half, full);

	    g.dispose ();
	    return image;
	}


	/**
	 * a panel with a grid layout for representing a pixel matrix
	 */
	public static class PixelMatrixPanel extends Panel
	{

		public PixelMatrixPanel (Grid layout)
		{ super (layout); layout.setHgap (1); layout.setVgap (1); }
		public PixelMatrixPanel (int w, int h) { this (new Grid (w, h)); this.w = w; this.h = h; }
		public PixelMatrixPanel (boolean[][] forPixels, MouseListener listener)
		{
			this (forPixels[0].length, forPixels.length);
			populate (forPixels, listener);
		}
		int w, h;

		public void populate (boolean[][] forPixels, MouseListener listener)
		{
			for (int y=0; y<h; y++)
			{
				for (int x=0; x<w; x++)
				{
					Label l = new Label ("");
					if (forPixels[x][y])
					{
						l.setBackground (Colour.WHITE);
					} else l.setBackground (Colour.BLACK);
					if (listener != null) l.addMouseListener (listener);
					l.setOpaque (true);
					this.add (l);
				}
			}
		}

		public boolean[][] getPixels ()
		{
			int n = 0;
			boolean[][] pixels = new boolean[w][h];
			for (int y=0; y<h; y++)
			{
				for (int x=0; x<w; x++)
				{
					pixels[x][y] = getComponent (n++).getBackground () == Colour.WHITE;
				}
			}
			return pixels;
		}

		private static final long serialVersionUID = 2685764426689226846L;
	}


	/**
	 * mouse clicks in PixelMatrixPanel will toggle bit value
	 */
	public static class PixelToggle extends MouseAdapter
	{
		/* (non-Javadoc)
		 * @see java.awt.event.MouseAdapter#mouseClicked(java.awt.event.MouseEvent)
		 */
		public void mouseClicked (MouseEvent e)
		{
			Component c = e.getComponent ();
			if (c.getBackground() == Colour.WHITE)
				c.setBackground (Colour.BLACK);
			else c.setBackground (Colour.WHITE);
		}
	}


	/**
	 * build a panel with a pixel matrix of labels
	 * @param forPixels the pixel matrix to be displayed
	 * @param listener mouse listener for click events
	 * @return a panel holding a label matrix (grid)
	 */
	public static PixelMatrixPanel
	getPixelMatrixPanel (boolean[][] forPixels, MouseListener listener)
	{ return new PixelMatrixPanel (forPixels, listener); }


	/**
	 * convert image to pixel matrix
	 * @param forImage the image to be converted
	 * @param listener mouse listener for click events
	 * @return a panel holding a pixel matrix
	 */
	public static PixelMatrixPanel
	getPixelMatrixPanel (BufferedImage forImage, MouseListener listener)
	{ return getPixelMatrixPanel (ImageTools.getPixels (forImage), listener); }


	/**
	 * prepare display of an image
	 * @param image a buffered image for display
	 * @param listener mouse listener
	 * @return access to display
	 */
	public static Widget constructDisplayWidget
		(BufferedImage image, MouseMotionListener listener)
	{
		Panel panel = new Panel ();
		panel.setLayout (new GridLayout ());
		buildImagePanel (panel, image, listener);
		return (Widget) panel;
	}


	/**
	 * prepare display of an image
	 * @param image a buffered image for display
	 * @param title the title for the frame
	 * @param listener mouse listener
	 * @return access to display
	 */
	public static JComponent showImage
	(BufferedImage image, String title, MouseMotionListener listener)
	{
		Panel panel = new Panel ();
		panel.setLayout (new GridLayout ());
		JLabel label = buildImagePanel (panel, image, listener);
		new DisplayFrame ((Widget) panel, title).show ();
		return label;
	}


	/**
	 * @param panel the panel being built
	 * @param image the image to be included
	 * @param listener a mouse listener for the image
	 * @return the label used to display image
	 */
	public static Label buildImagePanel
	(Panel panel, BufferedImage image, MouseMotionListener listener)
	{
		Label label = new Label (new ImageIcon (image));
		panel.setPreferredSize (imageFrameDimensions (image));
		if (listener != null) add (listener, label);
		panel.add (label);
		return label;
	}


	/**
	 * @param plotter the plot to add
	 * @param plotPanel the panel being built
	 * @param listener a mouse listener for the image
	 * @return the label used to display image
	 */
	public static Label buildPlotPanel
	(ScreenPlotter plotter, Panel plotPanel, MouseMotionListener listener)
	{
		return buildImagePanel (plotPanel, plotter.getImage (), listener);
	}


	/**
	 * @param image the image being framed
	 * @return dimensions of frame including margins
	 */
	public static Dimensions imageFrameDimensions (BufferedImage image)
	{
		return wXh (image.getWidth () + MARGIN, image.getHeight () + MARGIN);
	}
	public static final int MARGIN = DisplayFrame.MARGIN;


	/**
	 * present a frame
	 * @param frameTitle the title for the frame
	 * @param component the component to be displayed
	 */
	public static void showFrame (String frameTitle, JComponent component)
	{
		DisplayFrame f =
			new DisplayFrame (component, frameTitle);
		component.setPreferredSize (wXh (900, 500));
		f.show ();
	}


	/**
	 * connect mouse handlers to the plot component
	 * @param listener the mouse listener to be used to field events
	 * @param to the component that will contain the plot area
	 */
	public static void add (MouseMotionListener listener, JComponent to)
	{
		to.addMouseMotionListener (listener);
		if (listener instanceof MouseListener)
		{
			to.addMouseListener ((MouseListener)listener);
		}
	}


}

