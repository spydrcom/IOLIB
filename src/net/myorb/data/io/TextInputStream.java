
package net.myorb.data.io;

import java.io.InputStream;
import java.io.IOException;

/**
 * an input stream implementation based on primitive interface
 * @author Michael Druckman
 */
public class TextInputStream extends InputStream
{

	public TextInputStream (Streams.TextPrimitives text)
	{
		this.text = text;
	}
	protected Streams.TextPrimitives text;

	StringBuffer buffer = new StringBuffer ();
	int position = 1;

	String nextLine () throws IOException
	{
		String line;
		if ((line = text.getLine ()) == null)
		{ throw new Streams.EndStream (); }
		return line;
	}

	int getc () throws IOException
	{
		try
		{
			if (position >= buffer.length ())
			{
				buffer.setLength (0);
				buffer.append (nextLine ());
				position = 0;
			}
			return buffer.charAt (position++);
		}
		catch (Streams.EndStream eos)
		{
			return -1;
		}
	}

	/* (non-Javadoc)
	 * @see java.io.InputStream#read()
	 */
	public int read () throws IOException
	{
		try { return getc (); }
		catch (Exception e) { return -1; }
	}
	
}

