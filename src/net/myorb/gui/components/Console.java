
package net.myorb.gui.components;

import net.myorb.gui.components.SimpleScreenIO.Panel;
import net.myorb.gui.components.SimpleScreenIO.Scrolling;
import net.myorb.gui.components.SimpleScreenIO;

import javax.swing.JTextArea;
import java.awt.Color;

/**
 * provide a component that will act as a GUI console
 * @author Michael Druckman
 */
public class Console implements TextLineInputListener.TextLineProcessor
{

	/* (non-Javadoc)
	 * @see net.myorb.gui.components.TextLineInputListener.TextLineProcessor#process(java.lang.String)
	 */
	@Override public void process (String line) {}

	/**
	 * @param color BG color for components
	 * @param width the width of the display
	 * @param height the height of the display
	 * @param columns the max number of columns 
	 */
	public Console
		(
			Color color,
			int width, int height,
			int columns
		)
	{
		panel = new Panel ();
		panel.setBackground (color);
		panel.setSize (width, height);

		textArea = new JTextArea ();
		textArea.setBackground (color);
		textArea.setSize (width, height);
		textArea.setColumns (columns);
		panel.add (textArea);

		new TextLineInputListener (this, textArea);
	}

	/**
	 * @return the text area of the display
	 */
	public JTextArea getTextArea () { return textArea; }
	JTextArea textArea;

	/**
	 * @return the component inside scroll bars
	 */
	public Scrolling getScrollingPanel ()
	{ return new Scrolling (panel); }

	/**
	 * @return the containing panel
	 */
	public Panel getPanel ()
	{ return panel; }
	Panel panel;

	/**
	 * @param title a frame title
	 * @param width the width of the display
	 * @param height the height of the display
	 */
	public void show
		(
			String title,
			int width, int height
		)
	{
		SimpleScreenIO.show
		(
			getScrollingPanel (),
			title, width, height
		);
	}

}
