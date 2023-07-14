
package net.myorb.gui.graphics;

import java.awt.event.MouseMotionListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;

import java.awt.Point;

/**
 * a helper that translates screen position using scaling interface
 * @author Michael Druckman
 */
public class MouseMotionHandler implements MouseMotionListener, MouseListener
{


	/**
	 * @param scaling the scaling to apply to points of mouse events
	 */
	public MouseMotionHandler (CoordinateTranslation.Scaling scaling)
	{ this.translator = new CoordinateTranslation (scaling); }
	protected CoordinateTranslation translator;


	/*
	 * mouse motion trap
	 */

	/* (non-Javadoc)
	 * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
	 */
	public void mouseMoved (MouseEvent event)
	{
//		System.out.print
//		(
//			"X: " + event.getX () + " Y: " + event.getY () + " "
//		);

		processEvent (translator.getPoint (event));
	}

	/**
	 * default displays coordinates.
	 *  override provides point coordinates to replacing routine
	 * @param point the point at which event occurred
	 */
	public void processEvent (Point point)
	{
		System.out.print
		(
			"X (scaled) : " + point.getX() + " Y (scaled) : " + point.getY()
		);
		System.out.println ();
	}


	/*
	 * mouse-over trap
	 */

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	public void mouseExited  (MouseEvent event)
	{
		processEnterExitEvent (translator.getPoint (event), false);
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	public void mouseEntered (MouseEvent event)
	{
		processEnterExitEvent (translator.getPoint (event), true);
	}

	/**
	 * default displays coordinates.
	 *  override provides point coordinates to replacing routine
	 * @param point the point at which event occurred (component entered or exited)
	 * @param entering TRUE : mouse entering component
	 */
	public void processEnterExitEvent (Point point, boolean entering)
	{
		System.out.print
		(entering? "Entering @ ": "Exiting @ ");
		System.out.print
		(
			"X (scaled) : " + point.getX() + " Y (scaled) : " + point.getY()
		);
		System.out.println ();
	}


	/*
	 * mouse click trap
	 */

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	public void mouseClicked (MouseEvent event)
	{
		processClick (event.getClickCount (), translator.getPoint (event));
	}

	/**
	 * default displays coordinates and click count.
	 *  override provides point coordinates to replacing routine
	 * @param count number of clicks received from mouse
	 * @param point the scaled location of click
	 */
	public void processClick (int count, Point point)
	{
		System.out.println ("CLICK " + count);

		System.out.print
		(
			"X (scaled) : " + point.getX() + " Y (scaled) : " + point.getY()
		);
		System.out.println ();
	}


	/*
	 * range selection using Press/Drag/Release
	 */

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	public void mousePressed (MouseEvent event) { from = translator.getPoint (event); to = null; }

	/* (non-Javadoc)
	 * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
	 */
	public void mouseDragged (MouseEvent event) { to = translator.getPoint (event); }

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	public void mouseReleased (MouseEvent event)
	{
		if (from != null && to != null)
			processDrag (from, to);
		from = null;
	}
	protected Point from, to;

	/**
	 * default displays coordinates.
	 *  override provides from/to coordinates to replacing routine
	 * @param from point where drag started
	 * @param to point where drag ended
	 */
	public void processDrag (Point from, Point to)
	{
		System.out.print
		(
			"DRAG : " + from.x + " - " + to.x
		);
		System.out.println ();
	}


}

