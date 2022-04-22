
package net.myorb.gui.components;

import javax.swing.JPanel;
import javax.swing.JComponent;

import java.awt.GridLayout;
import java.awt.Dimension;

/**
 * prepare panel to display in frame
 * @author Michael Druckman
 */
public class DisplayComponents
{


	/**
	 * for square components
	 * @param component the item to be displayed
	 * @param title the title for the frame
	 * @param size the size of the panel
	 */
	public static void showComponent
		(JComponent component, String title, int size)
	{
		showComponent (component, title, size, size);
	}


	/**
	 * add table to panel and display frame
	 * @param component the table to be displayed
	 * @param title the title for the frame
	 * @param width of the displayed panel
	 * @param height of the panel
	 */
	public static void showComponent
		(JComponent component, String title, int width, int height)
	{
		JPanel panel = new JPanel ();
		addToPanel (component, width, height, panel);
		showInFrame (panel, title);
	}


	/**
	 * set component into sized panel
	 * @param component the table object
	 * @param width the width preferred
	 * @param height the height
	 * @param panel to add to
	 */
	public static void addToPanel
	(JComponent component, int width, int height, JPanel panel)
	{
		panel.setLayout (new GridLayout ());
		int W = width + DisplayFrame.MARGIN, H = height + DisplayFrame.MARGIN;
		panel.setPreferredSize (new Dimension (W, H));
		panel.add (component);
	}


	/**
	 * @param panel compiled content to be shown in frame
	 * @param title the title to display on frame
	 */
	public static void showInFrame (JPanel panel, String title)
	{
		new DisplayFrame (panel, title).show ();
	}


}
