
package net.myorb.gui.editor;

/**
 * properties passed from upper layer to independent layer
 * @author Michael Druckman
 */
public interface SnipToolPropertyAccess
{

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

}
