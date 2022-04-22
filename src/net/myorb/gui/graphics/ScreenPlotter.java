
package net.myorb.gui.graphics;

import java.awt.image.BufferedImage;

import net.myorb.data.abstractions.Range;

import java.awt.Graphics2D;
import java.awt.Dimension;
import java.awt.Color;

/**
 * an AWT implementation of the plotter interface
 * @author Michael Druckman
 */
public class ScreenPlotter implements Plotter, CoordinateTranslation.Scaling
{


	public ScreenPlotter () {}
	public ScreenPlotter (int perEdge) { setSize (perEdge); }
	public ScreenPlotter (Dimension d) { setSize (d.width, d.height); }
	public ScreenPlotter (int w, int h) { setSize (w, h); }


	/* (non-Javadoc)
	 * @see net.myorb.gui.graphics.Plotter#setSize(int)
	 */
	public void setSize (int perEdge)
	{
		image = DisplayImaging.createBufferedImage (edgeSizeX = edgeSizeY = perEdge);
		g = image.createGraphics (); setColor (Color.white);
	}
	public BufferedImage getImage () { return image; }
	protected BufferedImage image;
	protected Graphics2D g;


	/* (non-Javadoc)
	 * @see net.myorb.gui.graphics.Plotter#setSize(int, int)
	 */
	public void setSize (int width, int height)
	{
		image = DisplayImaging.createBufferedImage (width, height);
		g = image.createGraphics (); setColor (Color.white);
		edgeSizeX = width; edgeSizeY = height;
	}
	protected int edgeSizeX, edgeSizeY;


	/**
	 * @param color selected color for upcoming draw
	 */
	public void setColor (Color color) { g.setColor (color); }


	/* (non-Javadoc)
	 * @see net.myorb.gui.graphics.Plotter#scaleFor(int, int, int, int)
	 */
	public void scaleFor (int xMin, int xMax, int yMin, int yMax)
	{
		xAdj = xMin; yAdj = yMin;
		width = xMax - xMin; height = yMax - yMin;
		xScale = edgeSizeX / (float) width; yScale = edgeSizeY / (float) height;
		if (mustPreserveAspectRatio) preserveAspectRatio ();
	}
	void preserveAspectRatio ()
	{
		// on a square display matrix, this will make the scale square
		// and the aspect ratio of WxH of each GLYF will remain as specified
		if (xScale < yScale) yScale = xScale; else xScale = yScale;
	}
	public void ignoreAspectRatio ()
	{ mustPreserveAspectRatio = false; }
	protected boolean mustPreserveAspectRatio = true;
	protected int xAdj, yAdj, width, height;
	protected float xScale, yScale;

	/**
	 * @param timeUnits number of events
	 * @param pixelsPerUnit x-axis pixels allocated per event
	 * @param range the y-axis range of values
	 * @return scale reduction factor
	 */
	public double scaleForTimeSeries
	(int timeUnits, int pixelsPerUnit, Range range)
	{
		double scaleFactor = 1;
		double lo = range.getLo ().doubleValue (),
			hi = range.getHi ().doubleValue ();
		while
			(
				Math.abs (lo) / scaleFactor > SCALED_MAX ||
				Math.abs (hi) / scaleFactor > SCALED_MAX
			)
		{
			scaleFactor *= 1000;
		}
		scaleFor
		(
			0,
			timeUnits * pixelsPerUnit,
			(int) (lo/scaleFactor),
			(int) (hi/scaleFactor)
		);
		return scaleFactor;
	}
	static final double SCALED_MAX = 1E9;

	/* (non-Javadoc)
	 * @see net.myorb.gui.graphics.CoordinateTranslation.Scaling#getXScale()
	 */
	public float getXScale () { return xScale; }

	/* (non-Javadoc)
	 * @see net.myorb.gui.graphics.CoordinateTranslation.Scaling#getYScale()
	 */
	public float getYScale () { return yScale; }

	/**
	 * @param from X value to be positioned in image
	 * @return the image X coordinate
	 */
	protected int scaleX (int from)
	{
		return (int)(xScale * (from - xAdj));
	}
	protected int scaleX (double from)
	{
		return (int)(xScale * (from - xAdj));
	}


	/**
	 * @param from Y value to be positioned in image
	 * @return the image Y coordinate
	 */
	protected int scaleY (int from)
	{
		return edgeSizeY - (int)(yScale * (from - yAdj));
	}
	protected int scaleY (double from)
	{
		return edgeSizeY - (int)(yScale * (from - yAdj));
	}


	/* (non-Javadoc)
	 * @see net.myorb.gui.graphics.Plotter#drawLine(int, int, int, int)
	 */
	public void drawLine (int fromX, int fromY, int toX, int toY)
	{
		//System.out.print (scaleX (fromX)); System.out.print (" "); System.out.print (scaleY (fromY)); System.out.print (" ");
		//System.out.print (scaleX (toX)); System.out.print (" "); System.out.println (scaleY (toY));
		g.drawLine (scaleX (fromX), scaleY (fromY), scaleX (toX), scaleY (toY));
	}
	public void drawLine (double fromX, double fromY, double toX, double toY)
	{
		g.drawLine (scaleX (fromX), scaleY (fromY), scaleX (toX), scaleY (toY));
	}


	/**
	 * @param title the text to use for frame title
	 */
	public void show (String title)
	{
		DisplayImaging.showImage (image, title, null);
	}


}

