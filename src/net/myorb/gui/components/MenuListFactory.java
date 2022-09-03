
package net.myorb.gui.components;

import net.myorb.gui.components.MenuManager.ActionList;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.util.List;

/**
 * a factory for processing pop-up action items on displayed lists
 * @author Michael Druckman
 */
public class MenuListFactory
{

	/**
	 * interface to a processor object
	 * that will process a list item
	 * with the requested action
	 */
	public interface Processor
	{
		/**
		 * @param command the name of the command as show in the menu item
		 * @param item the index into the list the command applies to
		 */
		void process (String command, int item);
	}

	/**
	 * @param itemNames the names of the actions to offer
	 * @param processor an implementation of the Processor contract
	 */
	public MenuListFactory
		(
			List <String> itemNames,
			MenuListFactory.Processor processor
		)
	{
		this.itemNames = itemNames;
		this.processor = processor;
	}
	MenuListFactory.Processor processor;
	List <String> itemNames;

	/**
	 * @param forItem the item of the list being built
	 * @return the action item list for the pop-up
	 */
	public ActionList getActionList (int forItem)
	{
		return new ItemActionList (forItem, itemNames, processor);
	}

}

/**
 * the action list for one item of the displayed list
 */
@SuppressWarnings("serial")
class ItemActionList extends ActionList
{

	ItemActionList
		(
			int forItem, List <String> itemNames,
			MenuListFactory.Processor processor
		)
	{
		this.processor = processor;
		this.forItemNumber = forItem;
		this.buildList (itemNames);
	}
	MenuListFactory.Processor processor;
	int forItemNumber;

	/**
	 * @param itemNames the names of the menu items
	 */
	void buildList (List <String> itemNames)
	{
		for (String name : itemNames)
		{
			this.add (new ItemAction (name, this));
		}
	}

	/**
	 * @param command the text of the command from an action item
	 */
	void takeAction (String command)
	{
		processor.process (command, forItemNumber);
	}

}

/**
 * action listener for one menu item
 *  connected to one item of the displayed list
 */
class ItemAction implements ActionListener
{

	ItemAction (String itemName, ItemActionList action)
	{
		this.itemName = itemName;
		this.action = action;
	}
	ItemActionList action;
	String itemName;

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed (ActionEvent e)
	{
		action.takeAction (e.getActionCommand ());
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString () { return itemName; }

}

