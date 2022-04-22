
package net.myorb.gui.components;

import net.myorb.data.abstractions.Status;

import java.util.Collection;

/**
 * build menu from generic list of items
 * @param <T> type of menu items
 * @author Michael Druckman
 */
public class SimpleMenu<T> extends Menu
{

	/**
	 * identify recipient of item selections
	 * @param <T> type of menu items
	 */
	public interface SelectionSetting<T> extends Status.Localization
	{
		/**
		 * @param selected the selected item is passed back
		 * @throws Exception for generated errors
		 */
		void acceptSelection (T selected) throws Exception;
	}

	/**
	 * @param items collection of items to be used in menu
	 */
	public SimpleMenu (Collection<T> items) 
	{
		for (T item : items) add (new SimpleItem (item));
	}

	/**
	 * @param forConsumptionBy identification of consumer
	 */
	public void setConsumer (SelectionSetting<T> forConsumptionBy)
	{
		for (MenuItem item : this)
		{ item.setProperties (forConsumptionBy); }
		this.forConsumptionBy = forConsumptionBy;		
	}
	private SelectionSetting<T> forConsumptionBy;

	/**
	 * menu item wrapper
	 */
	public class SimpleItem extends MenuItem
	{
		public SimpleItem (T item) { super (item.toString ()); this.item = item; }
		public void executeAction () throws Exception { forConsumptionBy.acceptSelection (item); }
		private T item;
	}

	private static final long serialVersionUID = 1L;
}

