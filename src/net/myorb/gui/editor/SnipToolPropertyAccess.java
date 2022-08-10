
package net.myorb.gui.editor;

import net.myorb.gui.components.*;
import net.myorb.gui.editor.model.*;

import javax.swing.text.JTextComponent;

import java.awt.Font;

/**
 * properties passed from upper layer to independent layer
 * @author Michael Druckman
 */
public interface SnipToolPropertyAccess
{

	/**
	 * @return default font size
	 */
	int getFontSize ();

	/**
	 * @return default font family name
	 */
	String getFontFamily ();

	/**
	 * @param withStyle the style for the font
	 * @return a Font object with Snip configuration
	 */
	Font getFont (int withStyle);

	/**
	 * @return a newly constructed SnipEditor
	 */
	SnipToolEditor newEditor ();

	/**
	 * @return a newly constructed analyzer for snip content
	 */
	SnipAnalyzer newSnipAnalyzer (JTextComponent component);

	/**
	 * @return a newly constructed SnipEditor with LSE kit
	 */
	SnipToolEditor newLanguageSensitiveEditor ();

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

