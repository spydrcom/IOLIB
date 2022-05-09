
package net.myorb.gui.components;

import java.awt.Image;

import javax.swing.JInternalFrame;

/**
 * JInternalFrame extended as a DisplayContainer
 * @author Michael Druckman
 */
public class DisplayChild extends JInternalFrame
	implements DisplayContainer
{

	public DisplayChild (String title)
	{ super (title, true, true, true, true); }

	/* (non-Javadoc)
	 * @see net.myorb.gui.components.DisplayContainer#setIconImage(java.awt.Image)
	 */
	public void setIconImage (Image image) {}

	private static final long serialVersionUID = -1366686439227157871L;

}
