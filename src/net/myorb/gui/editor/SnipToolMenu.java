
package net.myorb.gui.editor;

import net.myorb.gui.components.SimpleMenuBar;
import net.myorb.gui.components.DisplayFrame;

import net.myorb.jxr.JxrParser;

/**
 * menu manager for Snip tool
 * @author Michael Druckman
 */
public class SnipToolMenu extends SnipToolComponents
{


	/**
	 * provide source environment component to menu functions
	 * @param properties access to display components
	 */
	public static void setSource (SnipToolPropertyAccess properties)
	{
		actions.setSource (asTextComponent (properties.getDisplay ()));
	}


	/**
	 * use JXR to provide the menus
	 * @param properties access to display components
	 */
	public static void prepareSnipToolActions (SnipToolPropertyAccess properties)
	{
		try { JxrParser.read (properties.getConfigurationPath (), null); }
		catch (Exception e) { e.printStackTrace (); }
		actions.provideAccess (properties);
		setSource (properties);
	}


	/**
	 * @return the action listeners for the menu items
	 */
	public static SnipToolActions getSnipActions ()
	{ return actions = new SnipToolActions (); }
	static SnipToolActions actions;


	/**
	 * @param menu the menu prepared by JXR ready for display
	 */
	public static void setMenuBar (SimpleMenuBar menu)
	{
		snipToolMenu = menu;
	}


	/**
	 * @param frame the display to have the menu
	 */
	public static void setMenuBar (DisplayFrame frame)
	{ frame.setMenuBar (snipToolMenu.getMenuBar ()); }
	static SimpleMenuBar snipToolMenu;


}

