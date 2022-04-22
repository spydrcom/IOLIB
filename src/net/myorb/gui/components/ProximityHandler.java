
package net.myorb.gui.components;

import javax.swing.JPopupMenu;
import javax.swing.JComponent;
import javax.swing.JMenuItem;

/**
 * show menu on mouse-over event
 * @author Michael Druckman
 */
public class ProximityHandler extends SimpleMouseEventCallback.Adapter
{

	/**
	 * @param component the component being modified
	 * @param items the list of options
	 */
	public ProximityHandler (JComponent component, JMenuItem[] items)
	{
		super (SimpleMouseEventCallback.EventType.ENTERED);
		for (JMenuItem item : items) menu.add (item);
		this.component = component;
	}
	private JComponent component;

	/* (non-Javadoc)
	 * @see net.myorb.gui.components.SimpleCallback.Adapter#executeAction()
	 */
	public void executeAction () throws Exception { menu.show (component, 0, 0); }
	private JPopupMenu menu = new JPopupMenu ();

}
