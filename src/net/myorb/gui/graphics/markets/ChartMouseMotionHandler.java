
package net.myorb.gui.graphics.markets;

import net.myorb.gui.graphics.MouseMotionHandler;

import javax.swing.JComponent;

import java.awt.Point;

/**
 * feedback screen events to chart object
 * @author Michael Druckman
 */
public class ChartMouseMotionHandler extends MouseMotionHandler
{

	static final boolean TRACE = false;

	/**
	 * @param chart the feedback destination
	 */
	public ChartMouseMotionHandler (MarketChart chart)
	{ super (chart); this.chart = chart; }
	protected MarketChart chart;

	/* (non-Javadoc)
	 * @see net.myorb.gui.graphics.MouseMotionHandler#processEvent(java.awt.Point)
	 */
	public void processEvent (Point point)
	{
		chart.showDateTip ( (int) point.getX () );
	}

	/* (non-Javadoc)
	 * @see net.myorb.gui.graphics.MouseMotionHandler#processDrag(java.awt.Point, java.awt.Point)
	 */
	public void processDrag (Point from, Point to)
	{
		chart.zoom ( (int) from.getX (), (int) to.getX () );
	}

	/* (non-Javadoc)
	 * @see net.myorb.gui.graphics.MouseMotionHandler#processClick(int, java.awt.Point)
	 */
	public void processClick (int count, Point point)
	{
		if (TRACE) super.processClick (count, point);
	}

	/* (non-Javadoc)
	 * @see net.myorb.gui.graphics.MouseMotionHandler#processEnterExitEvent(java.awt.Point, boolean)
	 */
	public void processEnterExitEvent (Point point, boolean entering)
	{
		if (TRACE) super.processEnterExitEvent (point, entering);
	}

	/**
	 * @param component the component to monitor
	 */
	public void addTo (JComponent component)
	{
		component.addMouseMotionListener (this);
		component.addMouseListener (this);
	}

}
