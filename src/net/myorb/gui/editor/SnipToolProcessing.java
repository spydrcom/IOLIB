
package net.myorb.gui.editor;

import net.myorb.gui.components.*;

import java.awt.Component;

import java.io.File;

/**
 * processing methods for execution of editor requests
 * @author Michael Druckman
 */
public class SnipToolProcessing extends SnipToolMenu
{


	public static int W = 900, H = 600, margain = 100;


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
	 * read contents of selected file to tab
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


	/**
	 * expand to full screen
	 * @param properties access to display components
	 */
	static void expand (SnipToolPropertyAccess properties)
	{
		SnipEditor editor =
			properties.newLanguageSensitiveEditor ();
		DisplayFrame frame = makeDisplayFrame (editor);
		editor.setText (getTextContainer ().getText ());
		//setMenuBar (frame);
		show (frame);
	}


	/**
	 * analyze content
	 * @param properties access to display components
	 */
	static void analyze (SnipToolPropertyAccess properties)
	{
		SnipEditor editor =
			properties.newSnipAnalyzer
						(getTextContainer ());
		DisplayFrame frame = makeDisplayFrame (editor);
		//setMenuBar (frame);
		show (frame);
	}


	/**
	 * make frame with widget in scroll bars
	 * @param widget the content component to place in frame
	 * @return the frame with a scrolling component
	 */
	static DisplayFrame makeDisplayFrame (Widget widget)
	{
		return new DisplayFrame
			(
				new Scrolling (widget).toComponent (),
				getName ()
			);
	}


	/**
	 * show content in maximized and sized frame
	 * @param frame the display frame to show
	 */
	static void show (DisplayFrame frame)
	{
		frame.showOrHide (wXh (W, H));
		frame.getSwingComponent ().maximize ();
	}


}

