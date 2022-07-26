
package net.myorb.data.io;

import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import java.io.IOException;
import java.io.Writer;

/**
 * provide for streams to text area component.
 * - implementation of Streams.TextPrimitives for JTextArea
 * @author Michael Druckman
 */
public class TextAreaStreams
	implements Streams.TextPrimitives
{

	public TextAreaStreams
	(JTextArea area, JScrollPane scrollPane)
	{
		this.bar = scrollPane.getVerticalScrollBar ();
		this.area = area;
	}
	protected JTextArea area; protected JScrollBar bar;

	/* (non-Javadoc)
	 * @see net.myorb.data.io.Streams.TextPrimitives#append(java.lang.String)
	 */
	public void append (String content) {
		area.append (content);
	}

	/* (non-Javadoc)
	 * @see net.myorb.data.io.Streams.TextPrimitives#flush()
	 */
	public void flush () {
		bar.setValue (bar.getMaximum ());
		area.repaint ();
	}

	/* (non-Javadoc)
	 * @see net.myorb.data.io.Streams.TextPrimitives#getLine()
	 */
	public String getLine () throws IOException {
		return null;
	}

	/**
	 * @return OutputStream for connected area
	 */
	public TextOutputStream getOutputStream ()
	{ return new TextOutputStream (this); }

	/**
	 * @return Writer for connected area
	 */
	public Writer getOutputWriter ()
	{ return new TextOutputWriter (this); }

}
