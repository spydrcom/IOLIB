
package net.myorb.data.io;

import java.io.IOException;
import java.io.Writer;

/**
 * a Writer implementation based on primitive interface
 * @author Michael Druckman
 */
public class TextOutputWriter extends Writer
{

	public TextOutputWriter (Streams.TextPrimitives text)
	{
		this.text = text;
	}
	protected Streams.TextPrimitives text;

	/* (non-Javadoc)
	 * @see java.io.Writer#close()
	 */
	public void close () throws IOException {}

	/* (non-Javadoc)
	 * @see java.io.Writer#flush()
	 */
	public void flush () throws IOException { text.flush (); }

	/* (non-Javadoc)
	 * @see java.io.Writer#write(char[], int, int)
	 */
	public void write (char[] chars, int off, int len) throws IOException
	{
		text.append (new String (chars, off, len));
	}

}
