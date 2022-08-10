
package net.myorb.gui.components;

import net.myorb.gui.components.SimpleScreenIO.*;
import net.myorb.gui.components.DisplayFrame;

import javax.swing.text.JTextComponent;
import javax.swing.JMenuBar;

import java.awt.Dimension;

/**
 * wrap EditPane in containers for display
 * @author Michael Druckman
 */
public class SnipFrame extends SnipEditor
{

	public SnipFrame () {}

	/**
	 * @param source the text components with source of text to edit
	 */
	public SnipFrame (JTextComponent source)
	{
		this.setText (source.getSelectedText ());
		this.source = source;
	}
	protected JTextComponent source;


	/**
	 * @param menu a menu bar for the frame
	 */
	public void setMenu (JMenuBar menu)
	{
		this.menu = menu;
	}
	protected JMenuBar menu = null;


	/**
	 * @return component in scroll bars
	 */
	public Scrolling getScrolling ()
	{ return new Scrolling (this); }


	/**
	 * build frame and show on screen
	 * @param title the title to place on the frame
	 * @param size the dimension of the frame
	 */
	public void show (String title, Dimension size)
	{
		DisplayFrame frame = new DisplayFrame
			(getScrolling ().toComponent (), title);
		frame.menuBar = menu;
		frame.show (size);
	}


	private static final long serialVersionUID = 109759403897436L;

}
