
package net.myorb.gui.editor;

import net.myorb.gui.editor.model.*;

import net.myorb.gui.components.SimpleScreenIO;

/**
 * properties passed from upper layer to independent layer
 * @author Michael Druckman
 */
public interface SnipToolPropertyAccess
{

	/**
	 * @return a newly constructed SnipEditor
	 */
	SimpleScreenIO.SnipEditor newEditor ();

	/**
	 * @return path to JXR configuration file for menus
	 */
	String getConfigurationPath ();

	/**
	 * @return access to text component of screen display
	 */
	Object getDisplay ();

	/**
	 * @param source text of command to execute
	 */
	void execute (String source);

	/**
	 * @return name of file selected in GUI
	 */
	String getSelectedFileName ();

	/**
	 * @return path to source files
	 */
	String getDirectoryName ();


	/*
	 * factories for model objects
	 */

	/**
	 * @return a Document object to be used as model of the editor source
	 */
	SnipToolDocument newDocument ();

	/**
	 * @return a style context management object
	 */
	SnipToolContext newContext ();

	/**
	 * @return an extended editor kit for implementing snip tool
	 */
	SnipToolKit newKit ();

	/**
	 * @return a token parser that will supply lexical analysis
	 */
	SnipToolScanner getScanner ();

	/**
	 * @return the code that provides a default style in the editor
	 */
	int getDefaultStyleCode ();

}

