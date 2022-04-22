
package net.myorb.gui.components;

import javax.swing.JMenuItem;

import java.util.List;

/**
 * provide for menu selection options for Field components
 * @param <T> the type of menu item data
 * @author Michael Druckman
 */
public class FieldMenu<T> extends SimpleMenu<T>
	implements SimpleMenu.SelectionSetting<T>
{

	/**
	 * @param field the field being modified
	 * @param items the list of options to be presented
	 */
	public FieldMenu (SimpleScreenIO.Field field, List<T> items)
	{
		super (items); setConsumer (this);
		(handler = new ProximityHandler (field, optionsList (items)))
		.addMouseListenerTo (this.field = field);
	}
	private SimpleScreenIO.Field field;

	/**
	 * show menu without waiting for mouse event
	 */
	public void doPopup ()
	{
		try
		{ Thread.sleep (500); handler.executeAction (); }
		catch (Exception e) { e.printStackTrace ();}
	}
	private ProximityHandler handler;

	/* (non-Javadoc)
	 * @see net.myorb.lotto.gui.components.SimpleMenu#acceptSelection(java.lang.Object)
	 */
	public void acceptSelection (T selected) { field.setText (selected.toString ()); }
	public String getTitleForErrorDialog () { return "Menu manager failed"; }
	public Object getAssociatedComponent () { return field; }

	/**
	 * @param item name of an options
	 * @return the menu item for that option
	 */
	private JMenuItem newMenuItem (T item)
	{
		JMenuItem menuItem = new JMenuItem (item.toString ());
		menuItem.addActionListener (new SimpleItem (item));
		return menuItem;
	}

	/**
	 * @param options list of option names
	 * @return the options as menu items
	 */
	private JMenuItem[] optionsList (List<T> options)
	{
		JMenuItem[] items = new JMenuItem[options.size()];
		for (int i=0; i<items.length; i++) items[i] = newMenuItem (options.get (i));
		return items;
	}

	private static final long serialVersionUID = 1L;
}

