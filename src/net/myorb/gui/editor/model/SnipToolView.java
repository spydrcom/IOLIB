
package net.myorb.gui.editor.model;

import net.myorb.gui.editor.SnipToolScanner;

import javax.swing.text.WrappedPlainView;
import javax.swing.text.BadLocationException;

import javax.swing.text.Element;
import javax.swing.text.Segment;

import java.awt.Graphics;

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
	{
		super (elem); this.context = context;
		this.doc = (SnipToolDocument) getDocument ();
		this.scanner = context.getScanner ();
	}
	protected SnipToolScanner scanner;
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
		SnipToolToken lastTokenread;
		int nextStyleCode, last, mark = p0;
		int styleCode = scanner.getDefaultStyleCode ();

		scanner.updateSource (new StringBuffer (getText (p0, p1)), p0);

		for (; p0 < p1 ;)
		{

			lastTokenread = scanner.getToken ();
			last = scanner.getLastSourcePosition ();

			if (lastTokenread == null) break;
			nextStyleCode = lastTokenread.getStyleCode ();

			if (nextStyleCode != styleCode)
			{
				// color change, flush what we have
				x = draw (g, x, y, p0, mark, styleCode);
				mark = p0;
			}
			styleCode = nextStyleCode;

			p0 = last;

		}

		// flush remaining
		return draw (g, x, y, p1, mark, styleCode);
	}


	int draw
		(
			Graphics g,
			int x, int y,
			int end, int mark,
			int styleCode
		)
	throws BadLocationException
	{
		Segment text = getLineBuffer ();
		doc.getText (mark, end - mark, text);
		return context.draw (g, this, text, x, y, mark, styleCode);
	}
	
	String getText
	(int from, int to)
	throws BadLocationException
	{
		Segment text = getLineBuffer ();
		doc.getText (from, to - from, text);
		return text.toString ();
	}


}

