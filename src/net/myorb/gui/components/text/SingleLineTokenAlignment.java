
package net.myorb.gui.components.text;

import javax.swing.text.Element;
import javax.swing.text.BadLocationException;
import javax.swing.text.Segment;

/**
 * provide document line boundary alignment
 * - document model ranges passed to the view object are not aligned
 * - failing alignment means a token could be split at the start point
 * - the logic of the examiner steps backwards thru document lines
 * - finding an end-of-line marker provides an alignment point
 * - this logic fails for tokens that cross line boundaries
 * @author Michael Druckman
 */
public class SingleLineTokenAlignment implements DocumentAlignmentTool
{


	public SingleLineTokenAlignment
	(LineElementRealization lineRealization) { this.lineRealization = lineRealization; }
	protected LineElementRealization lineRealization;


	/**
	 * find start of line of interest
	 * - this eliminates the break in tokens
	 * @param startingAt the start of the portion of current interest
	 * @param inDocument the model of the document needing alignment
	 * @return the point closest to the start where EOL was seen
	 * @throws BadLocationException for loading errors
	 */
	public int findAlignedStart
		(Element inDocument, int startingAt)
	throws BadLocationException
	{
		int lineNum = inDocument.getElementIndex (startingAt);

		return findLineStartInPriorLine
			(
				inDocument,				// the documentRoot element of the model
				--lineNum, startingAt,	// the line number just before the requested line
				new Segment ()			// a workspace buffer for rendering text
			);
	}


	/**
	 * search back thru lines of the document model for EOL
	 * @param documentRoot the root element in the document model
	 * @param lineNum the line number just before the requested line
	 * @param requestedStart the document model coordinate for line start
	 * @param segment a workspace buffer for rendering text in searches
	 * @return the point closest to the start where EOL was seen
	 * @throws BadLocationException for loading errors
	 */
	int findLineStartInPriorLine
		(Element documentRoot, int lineNum, int requestedStart, Segment segment)
	throws BadLocationException
	{
		for (int eolLocation; lineNum > 0; lineNum--)
		{
			// get text from line number
			String text = lineRealization.textFor (documentRoot.getElement (lineNum), segment);
			if ((eolLocation = checkForEol (text, requestedStart)) >= 0) return eolLocation;
		}
		return 0; // got back to start of document
	}


	/**
	 * check text for EOL
	 * @param text the source to search for EOL
	 * @param around the model index close to start
	 * @return the index of the closest EOL or -1 for none
	 */
	int checkForEol (String text, int around)
	{
		int eolIndex;
		if ((eolIndex = text.indexOf (EOL)) >= 0)
		{ return closestEOL (around, eolIndex, text); }
		else return -1;
	}


	/**
	 * find start of line of interest
	 * - this eliminates the break in tokens
	 * @param to target for finding the closest EOL
	 * @param from the starting point for the search
	 * @param in the text being searched
	 * @return the closest EOL found
	 */
	int closestEOL (int to, int from, String in)
	{
		for (int next = 0, prev = from ; ; prev = next)
		{
			// check for closer EOL
			next = in.indexOf (EOL, prev + 1);

			// EOL must come before starting point requested
			if (next < 0 || next >= to) return prev + 1;
		}
	}
	static final String EOL = "\n";


}

