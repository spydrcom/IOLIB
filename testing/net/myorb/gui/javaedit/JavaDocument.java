
package net.myorb.gui.javaedit;

import java.io.*;
//import java.util.Vector;
//import java.awt.Color;

import javax.swing.event.*;
import javax.swing.text.*;

/**
 * A document to represent text in the form of the java programming language.
 * This is quite primitive in that it simply provides support for lexically
 * analyzing the text.
 * 
 * @author Timothy Prinzing
 * @version 1.2 05/27/99
 */
@SuppressWarnings("serial")
public class JavaDocument extends PlainDocument {

	public JavaDocument() {
		super(new GapContent(1024));
	}

	/**
	 * Create a lexical analyzer for this document.
	 * @return new scanner object
	 */
	public Scanner createScanner() {
		Scanner s;
		try {
			s = new Scanner();
		} catch (IOException e) {
			s = null;
		}
		return s;
	}

	/**
	 * Fetch a reasonable location to start scanning given the desired start
	 * location. This allows for adjustments needed to accommodate multi-line
	 * comments.
	 * @param p value to translate
	 * @return getStartOffset
	 */
	public int getScannerStart(int p) {
		Element elem = getDefaultRootElement();
		int lineNum = elem.getElementIndex(p);
		Element line = elem.getElement(lineNum);
		AttributeSet a = line.getAttributes();
		while (a.isDefined(CommentAttribute) && lineNum > 0) {
			lineNum -= 1;
			line = elem.getElement(lineNum);
			a = line.getAttributes();
		}
		return line.getStartOffset();
	}

	// --- AbstractDocument methods ----------------------------

	/**
	 * Updates document structure as a result of text insertion. This will
	 * happen within a write lock. The superclass behavior of updating the line
	 * map is executed followed by marking any comment areas that should
	 * backtracked before scanning.
	 *
	 * @param chng
	 *            the change event
	 * @param attr
	 *            the set of attributes
	 */
	protected void insertUpdate(DefaultDocumentEvent chng, AttributeSet attr) {
		super.insertUpdate(chng, attr);

		// update comment marks
		Element root = getDefaultRootElement();
		DocumentEvent.ElementChange ec = chng.getChange(root);
		if (ec != null) {
			Element[] added = ec.getChildrenAdded();
			boolean inComment = false;
			for (int i = 0; i < added.length; i++) {
				Element elem = added[i];
				int p0 = elem.getStartOffset();
				int p1 = elem.getEndOffset();
				String s;
				try {
					s = getText(p0, p1 - p0);
				} catch (BadLocationException bl) {
					s = null;
				}
				if (inComment) {
					MutableAttributeSet a = (MutableAttributeSet) elem.getAttributes();
					a.addAttribute(CommentAttribute, CommentAttribute);
					int index = s.indexOf("*/");
					if (index >= 0) {
						// found an end of comment, turn off marks
						inComment = false;
					}
				} else {
					// scan for multiline comment
					int index = s.indexOf("/*");
					if (index >= 0) {
						// found a start of comment, see if it spans lines
						index = s.indexOf("*/", index);
						if (index < 0) {
							// it spans lines
							inComment = true;
						}
					}
				}
			}
		}
	}

	/**
	 * Updates any document structure as a result of text removal. This will
	 * happen within a write lock. The superclass behavior of updating the line
	 * map is executed followed by placing a lexical update command on the
	 * analyzer queue.
	 *
	 * @param chng
	 *            the change event
	 */
	protected void removeUpdate(DefaultDocumentEvent chng) {
		super.removeUpdate(chng);

		// update comment marks
	}

	// --- variables ------------------------------------------------

	/**
	 * Key to be used in AttributeSet's holding a value of Token.
	 */
	static final Object CommentAttribute = new AttributeKey();

	static class AttributeKey {

		private AttributeKey() {
		}

		public String toString() {
			return "comment";
		}

	}

	public class Scanner extends sun.tools.java.Scanner {

		Scanner() throws IOException {
			super(new LocalEnvironment(), new DocumentInputStream(0, getLength()));
			scanComments = true;
		}

		/**
		 * Sets the range of the scanner. This should be called to reinitialize
		 * the scanner to the desired range of coverage.
		 * @param p0 start of range
		 * @param p1 end of range
		 * @throws IOException for errors
		 */
		public void setRange(int p0, int p1) throws IOException {
			useInputStream(new DocumentInputStream(p0, p1));
			this.p0 = p0;
		}

		/**
		 * This fetches the starting location of the current token in the
		 * document.
		 * @return point plus offset
		 */
		public final int getStartOffset() {
			int begOffs = (int) (pos & MAXFILESIZE);
			return p0 + begOffs;
		}

		/**
		 * This fetches the ending location of the current token in the
		 * document.
		 * @return point plus offset
		 */
		public final int getEndOffset() {
			int endOffs = (int) (getEndPos() & MAXFILESIZE);
			return p0 + endOffs;
		}

		int p0;
	}

	/**
	 * Class to provide InputStream functionality from a portion of a Document.
	 * This really should be a Reader, but not enough things use it yet.
	 */
	class DocumentInputStream extends InputStream {

		public DocumentInputStream(int p0, int p1) {
			this.segment = new Segment();
			this.p0 = p0;
			this.p1 = Math.min(getLength(), p1);
			pos = p0;
			try {
				loadSegment();
			} catch (IOException ioe) {
				throw new Error("unexpected: " + ioe);
			}
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
		public int read() throws IOException {
			if (index >= segment.offset + segment.count) {
				if (pos >= p1) {
					// no more data
					return -1;
				}
				loadSegment();
			}
			return segment.array[index++];
		}

		void loadSegment() throws IOException {
			try {
				int n = Math.min(1024, p1 - pos);
				getText(pos, n, segment);
				pos += n;
				index = segment.offset;
			} catch (BadLocationException e) {
				throw new IOException("Bad location");
			}
		}

		Segment segment;
		int p0; // start position
		int p1; // end position
		int pos; // pos in document
		int index; // index into array of the segment
	}

	static class LocalEnvironment extends sun.tools.java.Environment {

		public void error(Object source, int where, String err, Object arg1, Object arg2, Object arg3) {
			// should do something useful...
			System.err.println(err);
			System.err.println("location: " + where);
		}
	}

}
