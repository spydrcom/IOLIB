
package net.myorb.gui.components.text;

import javax.swing.text.BadLocationException;
import javax.swing.text.Element;

/**
 * provide tool for searching for line boundaries in text document model
 * @author Michael Druckman
 */
public interface DocumentAlignmentTool
{
	/**
	 * find start of line of interest
	 * - this eliminates the break in tokens
	 * @param startingAt the start of the portion of current interest
	 * @param inDocument the model of the document needing alignment
	 * @return the point closest to the start where EOL was seen
	 * @throws BadLocationException for loading errors
	 */
	int findAlignedStart
		(Element inDocument, int startingAt)
	throws BadLocationException;
}
