
package net.myorb.data.io;

import java.io.OutputStream;
import java.io.IOException;

/**
 * an output stream implementation based on primitive interface
 * @author Michael Druckman
 */
public class TextOutputStream extends OutputStream
{

	public TextOutputStream (Streams.TextPrimitives text)
	{
		this.text = text;
	}
	protected Streams.TextPrimitives text;
	
	/* (non-Javadoc)
	 * @see java.io.OutputStream#write(byte[])
	 */
	public void write (byte[] bytes) throws IOException
	{
		text.append (new String (bytes));
	}

	/* (non-Javadoc)
	 * @see java.io.OutputStream#write(int)
	 */
	public void write (int b) throws IOException
	{ write (new byte[]{(byte)b}); }

	/* (non-Javadoc)
	 * @see java.io.OutputStream#flush()
	 */
	public void flush () throws IOException
	{
		super.flush ();
		text.flush ();
	}

}

