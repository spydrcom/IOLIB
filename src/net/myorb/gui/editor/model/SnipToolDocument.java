
package net.myorb.gui.editor.model;

import net.myorb.gui.components.text.LineElementRealization;
import net.myorb.gui.components.text.SingleLineTokenAlignment;
import net.myorb.gui.components.text.DocumentAlignmentTool;
import net.myorb.gui.components.text.TextSource;

import javax.swing.text.Element;
import javax.swing.text.GapContent;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import javax.swing.text.Segment;

//import javax.swing.event.DocumentEvent;

import java.io.IOException;
import java.io.InputStream;

/**
 * Document extension for SnipTool editor
 * - refactor of original JavaKit example from Sun
 * - by Timothy Prinzing version 1.2 05/27/99
 * - refactor done in summer 2022
 * @author Michael Druckman
 */
public class SnipToolDocument extends PlainDocument
	implements LineElementRealization, TextSource
{


	public SnipToolDocument ()
	{
		super (new GapContent (1024));
		prepareDocumentExaminer ();
	}


	// --- AbstractDocument methods ----------------------------

	/**
	 * Updates document structure as a result of text insertion. This will
	 * happen within a write lock. The superclass behavior of updating the line
	 * map is executed followed by marking any comment areas that should
	 * backtracked before scanning.
	 *
	 * @param chng the change event
	 * @param attr the set of attributes
	 */
	protected void insertUpdate (DefaultDocumentEvent chng, AttributeSet attr)
	{
		super.insertUpdate (chng, attr);

//		Element root = getDefaultRootElement ();
//		DocumentEvent.ElementChange ec = chng.getChange (root);

	}


	/**
	 * Updates any document structure as a result of text removal. This will
	 * happen within a write lock. The superclass behavior of updating the line
	 * map is executed followed by placing a lexical update command on the analyzer queue.
	 * @param chng the change event
	 */
	protected void removeUpdate (DefaultDocumentEvent chng)
	{ super.removeUpdate (chng); }


	/**
	 * Class to provide InputStream functionality from a portion of a Document.
	 * This really should be a Reader, but not enough things use it yet.
	 */
	class DocumentInputStream extends InputStream
	{

		public DocumentInputStream (int p0, int p1)
		{
			this.segment = new Segment ();
			this.p0 = p0; this.p1 = Math.min (getLength (), p1);
			this.load ();
		}

		void load ()
		{
			pos = p0;
			try { loadSegment (); }
			catch (IOException ioe) { throw new Error ("unexpected: " + ioe); }
		}

		/**
		 * Reads the next byte of data from this input stream. The value byte is
		 * returned as an <code>int</code> in the range <code>0</code> to
		 * <code>255</code>. If no byte is available because the end of the
		 * stream has been reached, the value <code>-1</code> is returned. This
		 * method blocks until input data is available, the end of the stream is
		 * detected, or an exception is thrown.
		 * <p>
		 * A subclass must provide an implementation of this method.
		 *
		 * @return the next byte of data, or <code>-1</code> if the end of the
		 *         stream is reached.
		 * @exception IOException
		 *                if an I/O error occurs.
		 * @since JDK1.0
		 */
		public int read () throws IOException
		{
			if (index >= segment.offset + segment.count)
			{
				if (pos >= p1) { return -1; } // no more data
				loadSegment ();
			}
			return segment.array[index++];
		}

		void loadSegment () throws IOException
		{
			try
			{
				int n = Math.min (1024, p1 - pos);
				getText (pos, n, segment);
				index = segment.offset;
				pos += n;
			}
			catch (BadLocationException e)
			{
				throw new IOException ("Bad location");
			}
		}

		int p1;		// end position
		int p0;		// start position
		int pos;	// pos in document
		int index;	// index into array of the segment

		Segment segment;
	}



	/*
	 * rewrite of the line start alignment
	 * - these methods isolate the document model
	 */


	/**
	 * find start of line of interest
	 * - this eliminates the break in tokens
	 * @param starting the start of the portion of current interest
	 * @return the point closest to the start where EOL was seen
	 * @throws BadLocationException for loading errors
	 */
	int findAlignedStart (int starting) throws BadLocationException
	{
		return documentExaminer.findAlignedStart (getDefaultRootElement (), starting);
	}


	/**
	 * allocate the examiner object
	 */
	void prepareDocumentExaminer ()
	{ this.documentExaminer = new SingleLineTokenAlignment (this); }
	protected DocumentAlignmentTool documentExaminer;


	/**
	 * get line of text
	 * @param line the element holding the text line
	 * @param segment the Segment object to be filled
	 * @throws BadLocationException for loading errors
	 */
	public void fillSegment
		(Element line, Segment segment)
	throws BadLocationException
	{
		int
			s = line.getStartOffset (),
			e = line.getEndOffset ();
		getText (s, e-s, segment);
	}


	/**
	 * get a string containing text of a line
	 * @param line the element holding the text line
	 * @param segment the Segment object to be filled
	 * @return the segment captured text converted to string
	 * @throws BadLocationException for loading errors
	 */
	public String textFor
		(Element line, Segment segment)
	throws BadLocationException
	{
		fillSegment (line, segment);
		return new String (segment.array);
	}


	/* (non-Javadoc)
	 * @see javax.swing.text.AbstractDocument#getText(int, int)
	 */
	public String getText
	(int starting, int length)
	throws BadLocationException
	{
		Segment text = new Segment ();
		getText (starting, length, text);
		return text.toString ();
	}


	/* (non-Javadoc)
	 * @see net.myorb.gui.components.text.TextSource#buffer(int, int)
	 */
	public StringBuffer buffer (int starting, int length) throws BadLocationException
	{ return new StringBuffer (getText (starting, length)); }


	private static final long serialVersionUID = 9048806203569287533L;

}

