
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
 * - 8/1/22 is essentially a rewrite
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


	/**
	 * align scanner with an EOL indicator
	 * @param starting start position of range of interest
	 * @param ending the end position for the range of interest
	 * @return the location at the end of the token starting the range
	 * @throws BadLocationException for segment errors
	 */
	int alignScannerAround (int starting, int ending) throws BadLocationException
	{
		int alignedStart, endToken;
		// position may lie inside a token
		// - no EOL conventions are able to be assumed
		loadBuffer (alignedStart = doc.findAlignedStart (starting), ending);

		while (true)
		{
			endToken = scanner.getLastSourcePosition ();
			if (alignmentFound (starting, endToken)) return alignedStart;
			lastTokenread = scanner.getToken ();
			alignedStart = endToken;
		}
	}
	boolean alignmentFound (int startOfSegment, int tokenEndPosition)
	{ return tokenEndPosition >= startOfSegment || lastTokenread == null; }
	

	/**
	 * parse tokens across a segment requested
	 * @param starting start position of range of interest
	 * @param ending the end position for the range of interest
	 * @throws BadLocationException for segment errors
	 */
	void loadBuffer (int starting, int ending) throws BadLocationException
	{
		Segment segment = getSegment (starting, ending);
		scanner.updateSource (new StringBuffer (segment.toString ()), starting);
		lastTokenread = scanner.getToken ();
	}
	protected SnipToolToken lastTokenread;


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
		int nextStyleCode, last, mark;
		mark = alignScannerAround (p0, p1);
		p0 = scanner.getLastSourcePosition ();

		if (lastTokenread == null) return x;
		int styleCode = lastTokenread.getStyleCode ();

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
				styleCode = nextStyleCode;
				mark = p0;
			}

			p0 = last;
		}

		// flush remaining
		return draw (g, x, y, p1, mark, styleCode);
	}


	/**
	 * draw a range in model coordinates
	 * @param g Graphics object being constructed
	 * @param x the X coordinate in the view architecture
	 * @param y the Y coordinate in the view architecture
	 * @param end the end of the range in model coordinates
	 * @param mark the start of the range in model coordinates
	 * @param styleCode the code describing the style to use for draw
	 * @return the x coordinate of end point within the Graphic
	 * @throws BadLocationException for the text draw layer
	 */
	int draw
		(
			Graphics g,
			int x, int y,			// AWT graphics coordinates
			int end, int mark,		// document model coordinates
			int styleCode			// context assigned code
		)
	throws BadLocationException
	{
		Segment text = getSegment (mark, end);
		return context.draw (g, this, text, x, y, mark, styleCode);
	}


	/**
	 * get document text found in range
	 * @param from start position of range of interest
	 * @param to the end position for the range of interest
	 * @return Segment holding content found in specified range
	 * @throws BadLocationException for segment errors
	 */
	Segment getSegment
		(int from, int to)
	throws BadLocationException
	{
		Segment text = getLineBuffer ();
		doc.getText (from, to - from, text);
		return text;
	}


}

