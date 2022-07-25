
package net.myorb.data.io;

import net.myorb.data.abstractions.SimpleStreamIO;

import java.io.IOException;

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

