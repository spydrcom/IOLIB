
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
	}
	protected JTextComponent component;
	protected boolean useSelected;
	protected char eol;


	String parse (String source) throws IOException
	{
		throw new Streams.EndStream ();
	}

	/* (non-Javadoc)
	 * @see net.myorb.data.io.Streams.TextPrimitives#getLine()
	 */
	public String getLine () throws IOException
	{
		return parse (useSelected? component.getSelectedText (): component.getText ());
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

