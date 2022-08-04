
package net.myorb.gui.components.text;

import javax.swing.text.BadLocationException;
import javax.swing.text.Segment;

/**
 * identify source of text container objects
 * @author Michael Druckman
 */
public interface TextSource
{

	/**
	 * read a segment of text
	 * @param starting the starting position in model coordinates
	 * @param length the length of the segment of text to read
	 * @param segment the segment object to fill as a buffer
	 * @throws BadLocationException for coordinate errors
	 */
	void getText
	(int starting, int length, Segment segment)
	throws BadLocationException;

	/**
	 * get a String of the text from the source
	 * @param starting the starting position in model coordinates
	 * @param length the length of the segment of text to read
	 * @return a String holding the identified range of text
	 * @throws BadLocationException for coordinate errors
	 */
	String getText
	(int starting, int length)
	throws BadLocationException;

	/**
	 * get a buffer filled with the text from the source
	 * @param starting the starting position in model coordinates
	 * @param length the length of the segment of text to read
	 * @return a StringBuffer containing the identified range
	 * @throws BadLocationException for coordinate errors
	 */
	StringBuffer buffer
	(int starting, int length)
	throws BadLocationException;

}
