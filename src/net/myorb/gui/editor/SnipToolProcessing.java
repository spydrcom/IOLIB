
package net.myorb.gui.editor;

import net.myorb.gui.components.FileDrop;

import java.awt.Component;

import java.io.File;

/**
 * processing methods for execution of editor requests
 * @author Michael Druckman
 */
public class SnipToolProcessing extends SnipToolMenu
{


	public static int W = 1000, H = 600, margain = 100;


	/**
	 * @param editor the editor to be displayed
	 * @return JEditorPane with copied source text in scroll bars
	 */
	static Component buildEditor (SnipEditor editor)
	{
		addEditorToList (editor);
		Scrolling s = new Scrolling (editor);
		editor.setText (actions.getSource ().getSelectedText ());
		s.configure (W - margain, H - margain);
		return s;
	}


	/**
	 * add a tab with a name
	 * @param name a name for the tab
	 * @param using properties of this tool
	 */
	static void addTab (String name, SnipToolPropertyAccess using)
	{
		add (name).add (buildEditor (using.newEditor ()));
	}


	/**
	 * trap exceptions as NULL for tab name requests.
	 * - user cancel will cause an exception which needs processing
	 * @return new name for tab, null for exceptions
	 */
	static String requestForName ()
	{
		try { return requestName (); }
		catch (Exception x)
		{ return null; }
	}


	/**
	 * request name for tab
	 * @return TRUE success FALSE canceled
	 */
	static boolean setToRequestedName ()
	{
		String name = requestForName ();
		if (name == null) return false;
		setName (name);
		return true;
	}


	/**
	 * add tab for file contents
	 * @param files content to read
	 * @param using properties of this tool
	 */
	static void process (File file, SnipToolPropertyAccess using)
	{
		String name =
			SnipToolSupport.shortNameFor (file.getName ());
		addTab (name, using); SnipToolComponents.copy (file);
	}


	/**
	 * attach a drop target to specified component
	 * @param component related component for the drop target
	 * @param using properties of this tool
	 */
	static void connectDrop (Component component, SnipToolPropertyAccess using)
	{
		FileDrop.simpleFileDrop (component, (f) -> { process (f, using); });
	}


	/**
	 * save contents of selected tab to file with tab name
	 * @param properties access to display components
	 */
	static void open (SnipToolPropertyAccess properties)
	{
		String name = properties.getSelectedFileName ();
		if (name == null && (name = requestForName ()) == null) return;
		else name = SnipToolSupport.getFilePath (properties.getDirectoryName (), name);
		process (new File (name), properties);
	}


	/**
	 * save contents of selected tab to script file with tab name
	 * @param properties properties of this tool
	 */
	static void save (SnipToolPropertyAccess properties)
	{
		saveTo (SnipToolSupport.getFileAccess (properties.getDirectoryName (), getName ()));
	}


	/**
	 * save contents of selected tab to script file with tab name
	 * @param properties access to display components
	 */
	static void saveAs (SnipToolPropertyAccess properties)
	{
		if (setToRequestedName ()) save (properties);
	}


}

