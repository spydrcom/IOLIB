
package net.myorb.gui.components;

import java.util.HashMap;

/**
 * tabulate pairs of DisplayFrame components
 * @author Michael Druckman
 */
public class SideBySide extends SimpleScreenIO
{


	/**
	 * @param name the name given to the panel
	 * @return the named panel
	 */
	public static Panel getPanel (String name)
	{
		Panel p = panelCalled.get (name);

		if (p == null)
		{
			p = startGridPanel (null, 0, 1);
			Widget scroll = new Scrolling (p);
			setPreferredSize (scroll, SCROLL_SIZE);
			DisplayFrame frame = new DisplayFrame (scroll, name);
			panelCalled.put (name, p);
			frame.show ();
		}

		return p;
	}
	public static HashMap <String, Panel> panelCalled = new HashMap <> ();
	public static final Dimensions SCROLL_SIZE = wXh (1000, 290);


	/**
	 * @return component found in last display frame
	 */
	public static Widget getWidget ()
	{
		Widget widget = getLastFrameAsWidget ();
		setPreferredSize (widget, WIDGET_SIZE);
		return widget;
	}
	public static final Dimensions WIDGET_SIZE = wXh (475, 260);


	/**
	 * add grid holding last 2 display frame components into named panel
	 * @param named the name of the panel showing side-by-side
	 */
	public static void addToPanel (String named)
	{
		Widget last = getWidget (), previous = getWidget ();
		Panel section = wsBordered (sideBySidePanel (previous, last), 5);
		postToScrollingPanel (section, getPanel (named));
	}


}

