
package net.myorb.gui.editor;

import net.myorb.gui.components.FileDrop;

import javax.swing.JEditorPane;
import javax.swing.JScrollPane;

import java.awt.Component;

import java.io.File;

/**
 * processing methods for execution of editor requests
 * @author Michael Druckman
 */
public class SnipToolProcessing extends SnipToolMenu
{


	public static int W = 800, H = 500, margain = 100;


	/**
	 * @return JEditorPane with copied source text in scroll bars
	 */
	static JScrollPane buildEditor ()
	{
		JEditorPane editor = new SnipEditor ();
		editor.setText (actions.getSource ().getSelectedText ());
		JScrollPane s = new JScrollPane (editor);
		s.setPreferredSize
		(
			wXh (W - margain, H - margain)
		);
		addEditorToList (editor);
		return s;
	}


	/**
	 * add a tab with a name
	 * @param name a name for the tab
	 */
	static void addTab (String name)
	{
		add (name).add (buildEditor ());
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
	 */
	static void process (File file)
	{
		String name =
			SnipToolSupport.shortNameFor (file.getName ());
		addTab (name); SnipToolComponents.copy (file);
	}


	/**
	 * attach a drop target to specified component
	 * @param component related component
	 */
	static void connectDrop (Component component)
	{
		FileDrop.simpleFileDrop (component, (f) -> { process (f); });
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
		process (new File (name));
	}


	/**
	 * save contents of selected tab to script file with tab name
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

