
package net.myorb.gui.components;

import javax.swing.KeyStroke;

import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;
import javax.swing.JMenuBar;
import javax.swing.JMenu;

import java.awt.Component;
import java.awt.event.MouseListener;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.KeyEvent;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

/**
 * implementation of basic popup menu using swing components
 * @author Michael Druckman
 */
public class MenuManager
{

	/**
	 * list of actions that comprise a menu
	 */
	public static class ActionList extends ArrayList <ActionListener>
	{
		private static final long serialVersionUID = 8177238268397423916L;
	}

	/**
	 * description of component that can contain menu items
	 */
	public interface MenuList
	{
		/**
		 * @param item the item to add to menu list
		 */
		void add (JMenuItem item);
	}

	/**
	 * provide hot key for menu item
	 */
	public interface HotKeyAvailable
	{
		/**
		 * @return key stroke to use as hot key
		 */
		KeyStroke getHotKey ();
	}

	/**
	 * provide mneumonic for menu item
	 */
	public interface MnemonicAvailable
	{
		/**
		 * @return key used as Mnemonic
		 */
		char getMnemonic ();
	}

	/**
	 * add menu to menu bar
	 * @param items the items of the menu list
	 * @param menuName the name of the menu to add to bar
	 * @param toBar the menu bar to add to
	 */
	public static void addToMenuBar
	(ActionList items, String menuName, JMenuBar toBar)
	{
		MenuWrapper toMenu = null;
		if (MENU_MAP.containsKey (menuName)) toMenu = MENU_MAP.get (menuName);
		else toMenu = newMenuWrapper (menuName, items);
		toBar.add (toMenu.menu);
	}
	static MenuWrapper newMenuWrapper (String menuName, ActionList items)
	{
		MenuWrapper toMenu = new MenuWrapper (menuName);
		toMenu.menu.setMnemonic (TOP[nextTopMneumonic++]);
		MENU_MAP.put (menuName, toMenu);
		addItems (items, toMenu);
		return toMenu;
	}
	static final int[] TOP = new int[]
	{KeyEvent.VK_F1, KeyEvent.VK_F2, KeyEvent.VK_F3, KeyEvent.VK_F4, KeyEvent.VK_F5, KeyEvent.VK_F6, KeyEvent.VK_F7, KeyEvent.VK_F8};
	static final Map<String,MenuWrapper> MENU_MAP = new HashMap<String,MenuWrapper>();
	static int nextTopMneumonic = 0;

	/**
	 * get mouse adapter for popup menu
	 * @param items the items of the menu list
	 * @return the mouse adapter that provides popup menu
	 */
	public static MouseListener getMenu (ActionList items)
	{ PopupAdapter p = new PopupAdapter (); addItems (items, p); return p; }

	/**
	 * build a sub-menu for a pop-up
	 * @param items the items of the menu list
	 * @param name the name to be given to the sub-menu
	 * @return the mouse adapter that provides popup menu
	 */
	public static JMenu getMenuOf (List<ActionListener> items, String name)
	{
		JMenu m = new JMenu ();
		for (ActionListener item : items) addItem (item, m);
		m.setMnemonic (name.charAt (0));
		m.setText (name);
		return m;
	}

	/**
	 * @param items the items of the menu
	 * @return the popup to use as a mouse listener
	 */
	public static MouseListener getMenu (JMenu[] items)
	{
		PopupAdapter p = new PopupAdapter ();
		for (JMenu item : items) p.add (item);
		return p;
	}

	/**
	 * add a popup menu to a component
	 * @param items the actions for the menu items
	 * @param to the component that will have popup
	 */
	public static void addPopup (ActionList items, Component to)
	{ to.addMouseListener (getMenu (items)); }

	/**
	 * process menu items
	 * @param items list of actions to identify as menu items
	 * @param toList menu list collecting items
	 */
	public static void addItems (List<ActionListener> items, MenuList toList)
	{ for (ActionListener item : items) addItem (item, toList); }

	/**
	 * add item to menu list
	 * @param item action to identify as menu item
	 * @param toList menu list collecting items
	 */
	public static void addItem (ActionListener item, MenuList toList)
	{
		JMenuItem menuItem;
		checkForHotKey (menuItem = new JMenuItem (item.toString ()), item);
		toList.add (menuItem);
	}
	static void addItem (ActionListener item, JMenu toList)
	{
		JMenuItem menuItem;
		checkForHotKey (menuItem = new JMenuItem (item.toString ()), item);
		toList.add (menuItem);
	}
	static void checkForHotKey (JMenuItem menuItem, ActionListener item)
	{
		if (item instanceof HotKeyAvailable)
		{ menuItem.setAccelerator (((HotKeyAvailable)item).getHotKey ()); }
		else if (item instanceof MnemonicAvailable)
		{ menuItem.setMnemonic (((MnemonicAvailable)item).getMnemonic ()); }
		menuItem.addActionListener (item);
	}


	/**
	 * formal list of menu items
	 */
	public static class MenuContainer extends ArrayList<MenuItem>
	{
		/**
		 * @param toComponent the component to have menu added
		 */
		public void addAsPopup (Component toComponent)
		{
			ActionList items = new ActionList ();
			for (MenuItem item : this) items.add (item);
			addPopup (items, toComponent);
		}
		public void addAsPopupToWidget (SimpleScreenIO.Widget theWidget)
		{
			addAsPopup (theWidget.toComponent ());
		}
		private static final long serialVersionUID = -7373008332919173147L;
	}


}


/**
 * construct menu object and collect items
 */
class MenuWrapper implements MenuManager.MenuList
{
	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.MenuManager.MenuList#add(javax.swing.JMenuItem)
	 */
	public void add (JMenuItem item) { menu.add (item); }
	public MenuWrapper (String menuName) { this.menu = new JMenu (menuName); }
	JMenu menu;
}


/**
 * construct popup object and collect items
 */
class PopupAdapter extends MouseAdapter implements MenuManager.MenuList
{

	public PopupAdapter ()
	{ popup = new JPopupMenu (); }
	JPopupMenu popup;

	/**
	 * check mouse event for popup trigger
	 * @param e the mouse event descriptor
	 */
	void processMouseEvent (MouseEvent e)
	{
		if (e.isPopupTrigger ())
		{
            popup.show (e.getComponent (), e.getX (), e.getY ());
        }
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.MenuManager.MenuList#add(javax.swing.JMenuItem)
	 */
	public void add (JMenuItem item) { popup.add (item); }

	/* (non-Javadoc)
	 * @see java.awt.event.MouseAdapter#mousePressed(java.awt.event.MouseEvent)
	 */
	public void mousePressed(MouseEvent e) { processMouseEvent (e); }

	/* (non-Javadoc)
	 * @see java.awt.event.MouseAdapter#mouseReleased(java.awt.event.MouseEvent)
	 */
	public void mouseReleased(MouseEvent e) { processMouseEvent (e); }

}


