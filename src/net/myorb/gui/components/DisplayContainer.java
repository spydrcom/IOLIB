
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

	void setTitle (String title);
	void setIconImage (Image image);
	void setDefaultCloseOperation (int to);
	void setPreferredSize (Dimension preferredSize);
	void setMinimumSize (Dimension preferredSize);
	Component add (Component component);
	void maximize ();
	void setJMenuBar (JMenuBar menuBar);
	void setSize (Dimension size);
	void setVisible (boolean to);
	void setSize (int w, int h);
	void pack ();

}
