
package net.myorb.gui.components;

import javax.swing.RootPaneContainer;
import javax.swing.JMenuBar;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;

/**
 * common interface between root and internal frame types
 * @author Michael
 */
public interface DisplayContainer
	extends RootPaneContainer
{

	/**
	 * @param title text of title for frame
	 */
	void setTitle (String title);

	/**
	 * @param image icon to use on frame
	 */
	void setIconImage (Image image);

	/**
	 * @param to action to use for close
	 */
	void setDefaultCloseOperation (int to);

	/**
	 * @param preferredSize size for preference
	 */
	void setPreferredSize (Dimension preferredSize);

	/**
	 * @param preferredSize minimum size allowed
	 */
	void setMinimumSize (Dimension preferredSize);

	/**
	 * @param component object to display in frame
	 * @return same component
	 */
	Component add (Component component);

	/**
	 * @param menuBar menu bar for frame
	 */
	void setJMenuBar (JMenuBar menuBar);

	/**
	 * @param size size for initial display
	 */
	void setSize (Dimension size);

	/**
	 * @param w the width
	 * @param h the height
	 */
	void setSize (int w, int h);

	/**
	 * @param to TRUE for display request
	 */
	void setVisible (boolean to);

	/**
	 * force internal frame to maximum
	 */
	void maximize ();

	/**
	 * standard frame pack
	 */
	void pack ();

}
