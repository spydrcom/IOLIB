
package net.myorb.data.io;

import net.myorb.data.abstractions.SimpleStreamIO;

import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;

/**
 * enable text stream management with simple interfaces
 * @author Michael Druckman
 */
public class Streams
{

	/**
	 * exception which indicates end of stream
	 */
	public static class EndStream extends IOException
	{
		private static final long serialVersionUID = -5960383770261332396L;
	}

	/**
	 * Primitive interface to build stream interactions
	 */
	public interface TextPrimitives
	{

		/**
		 * @return a line of text from the source
		 * @throws IOException for end of stream
		 */
		String getLine () throws IOException;

		/**
		 * @param content the text added to the stream
		 */
		void append (String content);

		/**
		 * ensure last of content is written
		 */
		void flush ();
	}

	/**
	 * @param primitives implementation of primitive interface
	 * @return a text input stream
	 */
	public static TextInputStream
		getTextInputStream (TextPrimitives primitives)
	{ return new TextInputStream (primitives); }

	/**
	 * @param primitives implementation of primitive interface
	 * @return a text source implementation
	 */
	public static SimpleStreamIO.TextSource getTextSource (TextPrimitives primitives)
	{ return new SimpleStreamIO.TextSource (getTextInputStream (primitives)); }

	/**
	 * @param primitives implementation of primitive interface
	 * @return a text output stream
	 */
	public static TextOutputStream
		getTextOutputStream (TextPrimitives primitives)
	{ return new TextOutputStream (primitives); }

	/**
	 * @param primitives implementation of primitive interface
	 * @return a text sink implementation
	 */
	public static SimpleStreamIO.TextSink getTextSink (TextPrimitives primitives)
	{ return new SimpleStreamIO.TextSink (getTextOutputStream (primitives)); }

}

/**
 * an input stream implementation based on primitive interface
 */
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

/**
 * an output stream implementation based on primitive interface
 */
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

