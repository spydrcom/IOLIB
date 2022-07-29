
package net.myorb.gui.javaedit;

import java.io.Reader;
import java.io.Writer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.swing.Action;

import javax.swing.text.Caret;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import javax.swing.text.ViewFactory;
import javax.swing.text.BadLocationException;

public class SnipToolKit extends EditorKit
{


	/* (non-Javadoc)
	 * @see javax.swing.text.EditorKit#createCaret()
	 */
	public Caret createCaret ()
	{
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.swing.text.EditorKit#createDefaultDocument()
	 */
	public Document createDefaultDocument ()
	{
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.swing.text.EditorKit#getActions()
	 */
	public Action[] getActions ()
	{
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.swing.text.EditorKit#getContentType()
	 */
	public String getContentType ()
	{
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.swing.text.EditorKit#getViewFactory()
	 */
	public ViewFactory getViewFactory ()
	{
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.swing.text.EditorKit#read(java.io.InputStream, javax.swing.text.Document, int)
	 */
	public void read
		(InputStream in, Document doc, int pos)
	throws IOException, BadLocationException
	{
	}

	/* (non-Javadoc)
	 * @see javax.swing.text.EditorKit#read(java.io.Reader, javax.swing.text.Document, int)
	 */
	public void read
		(Reader in, Document doc, int pos)
	throws IOException, BadLocationException
	{
	}

	/* (non-Javadoc)
	 * @see javax.swing.text.EditorKit#write(java.io.OutputStream, javax.swing.text.Document, int, int)
	 */
	public void write
		(OutputStream out, Document doc, int pos, int len)
	throws IOException, BadLocationException
	{
	}

	/* (non-Javadoc)
	 * @see javax.swing.text.EditorKit#write(java.io.Writer, javax.swing.text.Document, int, int)
	 */
	public void write
		(Writer out, Document doc, int pos, int len)
	throws IOException, BadLocationException
	{
	}

	private static final long serialVersionUID = -2014366548974171111L;

}
