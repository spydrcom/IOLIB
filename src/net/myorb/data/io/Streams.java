
package net.myorb.data.io;

import net.myorb.data.abstractions.SimpleStreamIO;

import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;

public class Streams
{

	public static class EndStream extends IOException
	{
		private static final long serialVersionUID = -5960383770261332396L;
	}

	public interface TextPrimitives
	{
		String getLine () throws IOException;
		void append (String content);
		void flush ();
	}

	public static TextInputStream
		getTextInputStream (TextPrimitives primitives)
	{ return new TextInputStream (primitives); }

	public static SimpleStreamIO.TextSource getTextSource (TextPrimitives primitives)
	{ return new SimpleStreamIO.TextSource (getTextInputStream (primitives)); }

	public static TextOutputStream
		getTextOutputStream (TextPrimitives primitives)
	{ return new TextOutputStream (primitives); }

	public static SimpleStreamIO.TextSink getTextSink (TextPrimitives primitives)
	{ return new SimpleStreamIO.TextSink (getTextOutputStream (primitives)); }

}

class TextInputStream extends InputStream
{

	TextInputStream (Streams.TextPrimitives text)
	{
		this.text = text;
	}
	Streams.TextPrimitives text;

	StringBuffer buffer = new StringBuffer ();
	int position = 1;

	String nextLine () throws IOException
	{
		String line;
		if ((line = text.getLine ()) == null)
		{ throw new Streams.EndStream (); }
		return line;
	}

	char getc () throws IOException
	{
		if (position > buffer.length ())
		{
			buffer.setLength (0);
			buffer.append (nextLine ());
			position = 0;
		}
		return buffer.charAt (position++);
	}

	/* (non-Javadoc)
	 * @see java.io.InputStream#read()
	 */
	public int read () throws IOException
	{
		return getc ();
	}
	
}

class TextOutputStream extends OutputStream
{

	TextOutputStream (Streams.TextPrimitives text)
	{
		this.text = text;
	}
	Streams.TextPrimitives text;
	
	/* (non-Javadoc)
	 * @see java.io.OutputStream#write(byte[])
	 */
	public void write (byte[] bytes) throws IOException
	{
		text.append (new String (bytes));
	}

	/* (non-Javadoc)
	 * @see java.io.OutputStream#flush()
	 */
	public void flush () throws IOException
	{
		super.flush ();
		text.flush ();
	}

	/* (non-Javadoc)
	 * @see java.io.OutputStream#write(int)
	 */
	public void write (int b) throws IOException
	{
		text.append (new String (new byte[]{(byte)b}));
	}

}

