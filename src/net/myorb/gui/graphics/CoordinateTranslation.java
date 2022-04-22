
package net.myorb.gui.graphics;

import java.awt.event.MouseEvent;
import java.awt.Point;

/**
 * a helper that maps screen position for AWT points
 * @author Michael Druckman
 */
public class CoordinateTranslation
{

	/**
	 * the X/Y scaling for the translation
	 */
	public interface Scaling
	{
		/**
		 * @return x-axis scale
		 */
		float getXScale ();

		/**
		 * @return y-axis scale
		 */
		float getYScale ();
	}

	/**
	 * @param scaling an object that supports scaling
	 */
	public CoordinateTranslation (Scaling scaling)
	{
		this.scaling = scaling;
	}
	Scaling scaling;

	/**
	 * @param event a mouse event to read screen position from
	 * @return the translated point
	 */
	public Point getPoint (MouseEvent event)
	{
		double
		scaledX = event.getX () / scaling.getXScale (),
		scaledY = event.getY () / scaling.getYScale ();
		return getPoint (scaledX, scaledY);
	}

	/**
	 * @param x the x-axis coordinate
	 * @param y the y-axis coordinate
	 * @return the AWT Point
	 */
	public static Point getPoint (double x, double y)
	{
		Point p = new Point ();
		p.setLocation (x, y);
		return p;
	}

}
