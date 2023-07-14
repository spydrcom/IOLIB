
package net.myorb.gui.graphics;

import net.myorb.gui.components.SimpleScreenIO;
import net.myorb.gui.components.SimpleScreenIO.Widget;

import java.awt.event.MouseEvent;
import java.awt.Point;

/**
 * a handler specific to generation of tool tip events
 * @author Michael Druckman
 */
public abstract class ToolTipHandler extends MouseMotionHandler
{

	/**
	 * generate the tip text for the event
	 * @param event the description of the mouse event
	 * @return the tip to be displayed
	 */
	public abstract String formatTip (MouseEvent event);

	/* (non-Javadoc)
	 * @see net.myorb.gui.graphics.MouseMotionHandler#mouseMoved(java.awt.event.MouseEvent)
	 */
	public void mouseMoved (MouseEvent event)
	{
		SimpleScreenIO.setToolTipText ( widget, formatTip (event) );
	}

	/* (non-Javadoc)
	 * @see net.myorb.gui.graphics.MouseMotionHandler#processEnterExitEvent(java.awt.Point, boolean)
	 */
	public void processEnterExitEvent (Point point, boolean entering) {}

	/**
	 * @param widget the widget that should display the tip
	 */
	public void connect (Widget widget)
	{
		this.widget = widget;
	}
	protected Widget widget;

	public ToolTipHandler (CoordinateTranslation.Scaling scaling)
	{
		super (scaling);
	}

}
