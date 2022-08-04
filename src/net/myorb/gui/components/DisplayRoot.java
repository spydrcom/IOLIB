
package net.myorb.gui.components;

import javax.swing.JFrame;

/**
 * JFrame extended as a DisplayContainer
 * @author Michael
 */
public class DisplayRoot extends JFrame
	implements DisplayContainer
{

	public DisplayRoot () {}
	public DisplayRoot (String title) { super (title); }
	private static final long serialVersionUID = -1615325280046605362L;

	public void maximize () {}

}
