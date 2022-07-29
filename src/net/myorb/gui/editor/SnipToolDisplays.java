
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
	 * @param properties access to display components
	 */
	protected static void buildPanel (SnipToolPropertyAccess properties)
	{
		connectDrop (buildTabbedPanel (), properties);
	}


	/**
	 * add a text panel with an index count as name
	 * @param properties access to display components
	 */
	protected static void add (SnipToolPropertyAccess properties)
	{
		addTab (Integer.toString (tabCount++), properties);
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

