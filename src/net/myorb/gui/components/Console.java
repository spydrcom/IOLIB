
package net.myorb.gui.components;

import net.myorb.gui.components.SimpleScreenIO.Panel;
import net.myorb.gui.components.SimpleScreenIO.Scrolling;
import net.myorb.gui.components.SimpleScreenIO;

import javax.swing.text.JTextComponent;
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

		JTextArea area = new JTextArea ();

		textArea = area;
		textArea.setBackground (color);
		textArea.setSize (width, height);
		((JTextArea)textArea).setColumns (columns);
		panel.add (textArea);

		buffer =
		new TextLineInputListener (this, textArea);
		buffer.setParent (area);
	}

	/**
	 * @param text data to be appended to line
	 */
	public void append (String text)
	{
		buffer.append (text);
	}
	protected TextLineInputListener buffer;

	/**
	 * @return the text area of the display
	 */
	public JTextComponent getTextArea () { return textArea; }
	protected JTextComponent textArea;

	/**
	 * @return the component inside scroll bars
	 */
	public Scrolling getScrollingPanel ()
	{ return new Scrolling (panel); }

	/**
	 * @return the containing panel
	 */
	public Panel getPanel () { return panel; }
	protected Panel panel;

	/**
	 * @param title a frame title
	 * @param width the width of the display
	 * @param height the height of the display
	 * @return the frame used to display the panel
	 */
	public SimpleScreenIO.Frame show
		(
			String title,
			int width, int height
		)
	{
		return SimpleScreenIO.show
		(
			getScrollingPanel (),
			title, width, height
		);
	}

}
