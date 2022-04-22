
package net.myorb.gui.components;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JMenuBar;

import java.awt.event.ActionEvent;
import javax.swing.KeyStroke;

import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

/**
 * a set of helper methods for constructing menu bars
 * @author Michael Druckman
 */
public class SimpleMenuBar
{


	public static class MenuItem extends JMenuItem
	{

		/**
		 * @param name the text of the menu item
		 */
		public MenuItem (String name) { super (name); }

		/**
		 * @param text the text of the tip for the item
		 */
		public void setTip (String text)
		{
			this.setToolTipText (text);
		}

		/**
		 * @param key the letter to use as a Mnemonic
		 */
		public void setItemMnemonic (String key)
		{
			this.setMnemonic (key.charAt (0));
		}

		/**
		 * @param key the key value (VK) to use
		 */
		public void setHotKey (int key)
		{
			this.setAccelerator (KeyStroke.getKeyStroke (this.keyCode = key, modifiers));
		}
		int keyCode = KeyEvent.VK_ENTER;

		/**
		 * @param modifiers the modifiers to use
		 */
		public void setModifiers (int modifiers)
		{
			this.setAccelerator (KeyStroke.getKeyStroke (keyCode, this.modifiers = modifiers));
		}
		int modifiers = ActionEvent.ALT_MASK;

		/**
		 * @param listener then listener to assign to the item
		 */
		public void setActionForItem (ActionListener listener)
		{
			this.addActionListener (listener);
		}

		private static final long serialVersionUID = -3200581606079481350L;
	}


	JMenuBar menuBar = new JMenuBar ();

	/**
	 * @return the object holding the constructed result
	 */
	public JMenuBar getMenuBar () { return menuBar; }
	
	/**
	 * @param name the name for the menu
	 * @return the menu item added
	 */
	public JMenu addMenu (String name)
	{
		JMenu menu = new JMenu (name);
		menuBar.add (menu);
		return menu;
	}

	/**
	 * @param name the name of the menu item
	 * @param menu the menu being built
	 * @return the item constructed
	 */
	public MenuItem addMenuItem (String name, JMenu menu)
	{
		MenuItem item = new MenuItem (name);
		menu.add (item);
		return item;
	}

	/**
	 * @param key identification for the key to use
	 * @param modifiers the modifiers (ALT, CTL, ...) to use
	 * @param item the menu item being built
	 */
	public void setHotKey (int key, int modifiers, JMenuItem item)
	{
		item.setAccelerator (KeyStroke.getKeyStroke (key, modifiers));
	}

	/**
	 * @param key the letter to use as a Mnemonic
	 * @param item the menu item being built
	 */
	public void setMnemonic (String key, JMenuItem item)
	{
		item.setMnemonic (key.charAt (0));
	}

	/**
	 * @param listener then listener to assign to the item
	 * @param item the menu item being built
	 */
	public void setActionListener (ActionListener listener, JMenuItem item)
	{
		item.addActionListener (listener);
	}

}
