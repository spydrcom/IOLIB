
package net.myorb.gui.editor.model;

import javax.swing.text.Element;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.PlainDocument;
import javax.swing.text.Segment;

import javax.swing.event.DocumentEvent;

import java.io.IOException;
import java.io.InputStream;

/**
 * Document extension for SnipTool editor
 * @author Michael Druckman
 */
public class SnipToolDocument extends PlainDocument
{

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


	private static final long serialVersionUID = 9048806203569287533L;

}
