
package net.myorb.gui.editor.model;

import javax.swing.text.Utilities;
import javax.swing.text.WrappedPlainView;
import javax.swing.text.BadLocationException;

//import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.Segment;

import java.awt.Graphics;
import java.awt.Color;

/**
 * View specific to SnipTool functionality
 * - refactor of original JavaKit example from Sun
 * - by Timothy Prinzing version 1.2 05/27/99
 * - refactor done in summer 2022
 * @author Michael Druckman
 */
public class SnipToolView extends WrappedPlainView
{

	public SnipToolView (Element elem, SnipToolContext context)
	{ super (elem); this.context = context; this.doc = (SnipToolDocument) getDocument (); }
	protected SnipToolContext context;
	protected SnipToolDocument doc;


	/* (non-Javadoc)
	 * @see javax.swing.text.WrappedPlainView#drawUnselectedText(java.awt.Graphics, int, int, int, int)
	 */
	protected int drawUnselectedText
		(
			Graphics g,
			int x, int y,			// X/Y coordinates in graphics
			int p0, int p1			// starting/ending point in model
		)							// returns X location at end of range
	throws BadLocationException
	{
		Color last = null;
		int mark = p0;

		for (; p0 < p1 ;)
		{

			updateScanner (p0);
			int p = adjustedPosition (p0, p1);
			Color fg = context.getForeground (lastTokenread);

			if (fg != last && last != null)
			{
				// color change, flush what we have
				x = draw (g, x, y, p0, mark, last);
				mark = p0;
			}

			last = fg;
			p0 = p;

		}

		// flush remaining
		return draw (g, x, y, p1, mark, last);
	}


	int draw
		(
			Graphics g,
			int x, int y,
			int end, int mark,
			Color color
		)
	throws BadLocationException
	{
		g.setColor (color);
		Segment text = getLineBuffer ();
		doc.getText (mark, end - mark, text);
		return Utilities.drawTabbedText (text, x, y, g, this, mark);
	}


	/**
	 * Update the scanner (if necessary) to point to the appropriate token
	 * for the given start position needed for rendering.
	 * @param p point to read up to in document
	 */
	void updateScanner (int p)
	{
//		try {
//			if (!lexerValid) {
//				JavaDocument doc = (JavaDocument) getDocument();
//				lexer.setRange(doc.getScannerStart(p), doc.getLength());
//				lexerValid = true;
//			}
//			while (lexer.getEndOffset() <= p) {
//				lexer.scan();
//			}
//		} catch (Throwable e) {
//			// can't adjust scanner... calling logic
//			// will simply render the remaining text.
//			e.printStackTrace();
//		}
	}

	int adjustedPosition (int p0, int p1)
	{
		int p = Math.min (getTokenEndOffset (), p1);
		p = (p <= p0) ? p1 : p;
		return p;
	}
	int getTokenEndOffset () { return 0; }
	SnipToolToken lastTokenread;

}
