
package net.myorb.gui.editor;

import net.myorb.gui.editor.model.SnipToolToken;

/**
 * the interface for a general token parser.
 * lexical analysis will be done in the implementing layer.
 * @author Michael Druckman
 */
public interface SnipToolScanner
{

	/**
	 * @param source a buffer containing text to be parsed
	 * @param position the position in the model where the content is found
	 */
	void updateSource
	(
		StringBuffer source, int position
	);

	/**
	 * @return the model source position after a token read
	 */
	int getLastSourcePosition ();

	/**
	 * @return the next token in the sequence
	 */
	SnipToolToken getToken ();

	/**
	 * @return a style code for default segment renders
	 */
	int getDefaultStyleCode ();

}
