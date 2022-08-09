
package net.myorb.gui.editor.model;

import net.myorb.gui.ScribeEngine;
import net.myorb.gui.editor.SnipToolScanner;

import javax.swing.text.WrappedPlainView;
import javax.swing.text.BadLocationException;

import javax.swing.text.Element;

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
		scanner.updateSource
		(doc.buffer (starting, ending-starting), starting);
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
		ScribeEngine scribe =
			new ScribeEngine (x, y, this, g, context);
		// the marked point is the start of the token at line start
		// - note that this may be before p0 to allow correct first token render
		int mark = alignScannerAround (p0, p1);

		// empty lines will return null for the EOL character
		if (lastTokenread == null) return x;

		// p0 now becomes the end of the first token
		p0 = scanner.getLastSourcePosition ();

		// the text can now be drawn knowing the correct first token
		return drawText (scribe, p0, p1, mark);
	}


	/**
	 * draw the adjusted text range starting with the full first token
	 * @param scribe the text draw engine wrapping the graphics object
	 * @param p0 the adjusted model starting point in the document
	 * @param p1 the original end point in the model for this draw
	 * @param mark the point found at the start of the first token
	 * @return the x coordinate of end point within the Graphic
	 * @throws BadLocationException for the text draw layer
	 */
	protected int drawText
	(ScribeEngine scribe, int p0, int p1, int mark)
	throws BadLocationException
	{
		int styleCode =
			lastTokenread.getStyleCode ();
		for ( int nextStyleCode, last ; p0 < p1 ; p0 = last )
		{
			lastTokenread = scanner.getToken ();
			last = scanner.getLastSourcePosition ();

			if (lastTokenread == null) break;
			nextStyleCode = lastTokenread.getStyleCode ();

			if (nextStyleCode != styleCode)
			{
				if (isSophisticatedStyle (styleCode))
				{
					doc.replace
					(
						mark, lastTokenread.representation,
						context.getStyleFor (styleCode)
					);
				}
				else
				{
					// color change, flush what we have
					scribe.draw (doc, mark, p0, styleCode);
				}
				styleCode = nextStyleCode;
				mark = p0;
			}
		}
		return scribe.draw (doc, mark, p1, styleCode);	// flush remaining
	}
	boolean isSophisticatedStyle (int styleCode)
	{
		//return context.isSophisticatedStyle (styleCode);
		return false;
	}


}

