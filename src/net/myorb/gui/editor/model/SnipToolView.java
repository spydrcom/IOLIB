
package net.myorb.gui.editor.model;

import javax.swing.text.Utilities;
import javax.swing.text.WrappedPlainView;
import javax.swing.text.BadLocationException;

import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.Segment;

import java.awt.Graphics;
import java.awt.Color;

public class SnipToolView extends WrappedPlainView
{

	public SnipToolView (Element elem)
	{
		super(elem);
		doc = (SnipToolDocument) getDocument ();
	}
	SnipToolDocument doc;


	/* (non-Javadoc)
	 * @see javax.swing.text.WrappedPlainView#drawUnselectedText(java.awt.Graphics, int, int, int, int)
	 */
	protected int drawUnselectedText
	(Graphics g, int x, int y, int p0, int p1)
	throws BadLocationException
	{
		Document doc = getDocument();
		Color last = null;
		int mark = p0;
		for (; p0 < p1;) {

			Color fg = null;	//TODO
			int p = 0;

			updateScanner(p0);
//			int p = Math.min(lexer.getEndOffset(), p1);
//			p = (p <= p0) ? p1 : p;
//			Color fg = getForeground(lexer.token);

			if (fg != last && last != null) {
				// color change, flush what we have
				g.setColor(last);
				Segment text = getLineBuffer();
				doc.getText(mark, p0 - mark, text);
				x = Utilities.drawTabbedText(text, x, y, g, this, mark);
				mark = p0;
			}
			last = fg;
			p0 = p;
		}

		// flush remaining
		g.setColor(last);
		Segment text = getLineBuffer();
		doc.getText(mark, p1 - mark, text);
		x = Utilities.drawTabbedText(text, x, y, g, this, mark);
		return x;
	}

	/**
	 * Update the scanner (if necessary) to point to the appropriate token
	 * for the given start position needed for rendering.
	 */
	void updateScanner(int p)	//TODO
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

}
