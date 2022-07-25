
package net.myorb.gui.components;

import net.myorb.data.io.Streams;
import net.myorb.data.abstractions.SimpleStreamIO;

import javax.swing.text.JTextComponent;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

/**
 * wrapper for JTextComponent providing stream access
 * @author Michael Druckman
 */
public class TextComponentStreams implements Streams.TextPrimitives
{


	public TextComponentStreams
		(
			JTextComponent component,
			boolean useSelected,
			char eol
		)
	{
		this.component = component;
		this.useSelected = useSelected;
		this.eol = "" + eol;
	}
	protected JTextComponent component;
	protected boolean useSelected;
	protected String eol;


	/**
	 * separate lines at EOL points
	 * @throws IOException for internal error
	 */
	void parse () throws IOException
	{
		String source =
				useSelected?
					component.getSelectedText ():
					component.getText ();
		buffer = source.split (eol);
		current = 0;
	}
	boolean reused = false;
	String[] buffer = null;
	int current;


	/* (non-Javadoc)
	 * @see net.myorb.data.io.Streams.TextPrimitives#getLine()
	 */
	public String getLine () throws IOException
	{
		if (reused)
			throw new IOException ("Attempted reuse");
		else if (buffer == null) parse ();

		if (current >= buffer.length)
		{
			reused = true;
			buffer = null;
			return null;
		}

		return buffer[current++];
	}


	/* (non-Javadoc)
	 * @see net.myorb.data.io.Streams.TextPrimitives#append(java.lang.String)
	 */
	public void append (String content)
	{
		component.setText (component.getText () + content);
	}


	/* (non-Javadoc)
	 * @see net.myorb.data.io.Streams.TextPrimitives#flush()
	 */
	public void flush () {}


	public InputStream getInputStream ()
	{ return Streams.getTextInputStream (this); }

	public SimpleStreamIO.TextSource getTextSource ()
	{ return Streams.getTextSource (this); }

	public OutputStream getOutputStream ()
	{ return Streams.getTextOutputStream (this); }

	public SimpleStreamIO.TextSink getTextSink ()
	{ return Streams.getTextSink (this); }


}

