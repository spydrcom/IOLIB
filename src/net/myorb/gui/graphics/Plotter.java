
package net.myorb.gui.graphics;

/**
 * primitives for plotting on an abstract canvas
 * @author Michael Druckman
 */
public interface Plotter
{

	/**
	 * construct square image with given edge size
	 * @param perEdge square canvas edge size
	 */
	void setSize (int perEdge);

	/**
	 * construct rectangular image with given edge sizes
	 * @param width the pixel count for the x-axis (horizontal edge)
	 * @param height the pixel count for the y-axis
	 */
	void setSize (int width, int height);

	/**
	 * prepare scaling
	 *  for coordinate ranges
	 * @param xMin minimum value of X
	 * @param xMax maximum value of X
	 * @param yMin minimum value of Y
	 * @param yMax maximum value of Y
	 */
	void scaleFor (int xMin, int xMax, int yMin, int yMax);

	/**
	 * draw line from point to point
	 * @param fromX X coordinate of from
	 * @param fromY Y coordinate of from
	 * @param toX X coordinate of to
	 * @param toY Y coordinate of to
	 */
	void drawLine (int fromX, int fromY, int toX, int toY);

}
