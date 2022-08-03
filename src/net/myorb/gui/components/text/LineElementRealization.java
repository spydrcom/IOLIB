
package net.myorb.gui.components.text;

import javax.swing.text.Element;
import javax.swing.text.BadLocationException;
import javax.swing.text.Segment;

/**
 * model to view conversion for swing text document model
 * @author Michael Druckman
 */
public interface LineElementRealization
{

	/**
	 * get line of text
	 * @param line the element holding the text line
	 * @param segment the Segment object to be filled
	 * @throws BadLocationException for loading errors
	 */
	void fillSegment
		(Element line, Segment segment)
	throws BadLocationException;

	/**
	 * get a string containing text of a line
	 * @param line the element holding the text line
	 * @param segment the Segment object to be filled
	 * @return the segment captured text converted to string
	 * @throws BadLocationException for loading errors
	 */
	String textFor
		(Element line, Segment segment)
	throws BadLocationException;

}
