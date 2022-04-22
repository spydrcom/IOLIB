
package net.myorb.gui.components;

import net.myorb.data.abstractions.Status;

import java.awt.event.ActionListener;

/**
 * special case for Callback objects working as menu item actions.
 * implemented as simple callback object, addition of name is used to complete menu item display.
 * the executeAction method is still invoked for the action and error dialog is generated for exceptions
 * @author Michael Druckman
 */
public class MenuItem extends SimpleCallback.Adapter
	implements ActionListener
{

	/**
	 * @param name the name to display on the menu item
	 * @param title the title for the error handling dialog frame
	 * @param component the component to be associated with the error dialog
	 */
	public MenuItem (String name, String title, Object component)
	{ super (); setName (name); setProperties (title, component); }
	public MenuItem (String name, Status.Localization localization)
	{ super (); setName (name); setProperties (localization); }
	public MenuItem (String name) { setName (name); }


	/**
	 * @param menuItemName the name to display on the menu item
	 */
	public void setName (String menuItemName)
	{ this.menuItemName = menuItemName; }
	protected String menuItemName;


	/* (non-Javadoc)
	 * @see net.myorb.lotto.gui.components.SimpleCallback.Adapter#unimplementedErrorMessage()
	 */
	public String unimplementedErrorMessage () { return "Unimplemented Menu Item"; }


	// this is the method that must be overridden to install an action implementation.
	// in absence of an override the above message will display as an error when the menu item is invoked.
	// public void executeAction () throws Exception { throw new Exception (unimplementedErrorMessage ()); }
	// see SimpleCallback and SimpleCallback.Adapter to understand the chain of declarations implementing this logic.


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString () // menu item component is constructed using toString
	{ return menuItemName; }


}

