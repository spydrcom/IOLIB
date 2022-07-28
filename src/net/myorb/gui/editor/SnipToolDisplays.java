
package net.myorb.gui.editor;

import net.myorb.gui.components.DisplayFrame;

/**
 * GUI components for Snip edit display
 * @author Michael Druckman
 */
public class SnipToolDisplays extends SnipToolProcessing
{


	/**
	 * construct tab panel with left side index
	 */
	protected static void buildPanel ()
	{
		connectDrop (buildTabbedPanel ());
	}


	/**
	 * add a text panel with an index count as name
	 */
	protected static void add ()
	{
		addTab (Integer.toString (tabCount++));
	}
	protected static int tabCount = 1;


	/**
	 * @return DisplayFrame holding tabs and menu bar
	 */
	protected static DisplayFrame buildFrame ()
	{
		frame = new DisplayFrame
			(getTabbedPanel (), "Snip Editor");
		setMenuBar (frame);
		return frame;
	}
	protected static DisplayFrame frame = null;


}

