
package net.myorb.gui.editor.model;

import net.myorb.gui.editor.SnipToolPropertyAccess;

import net.myorb.gui.components.SimpleScreenIO;

import javax.swing.text.ViewFactory;
import javax.swing.text.Document;

/**
 * EditorKit extension for SnipTool editor.
 * - refactor of original JavaKit example from Sun
 * - by Timothy Prinzing version 1.2 05/27/99
 * - refactor done in summer 2022
 * @author Michael Druckman
 */
public class SnipToolKit extends SimpleScreenIO.SnipEditor.SnipEditorModel
{


	public SnipToolKit (SnipToolPropertyAccess properties)
	{
		super (); this.properties = properties;
	}
	protected SnipToolPropertyAccess properties;


	/**
	 * @return context object
	 */
	public SnipToolContext getStylePreferences ()
	{
		if (preferences == null)
		{ preferences = properties.newContext (); }
		return preferences;
	}


	/**
	 * @param prefs updated preferences to use
	 */
	public void setStylePreferences (SnipToolContext prefs)
	{
		preferences = prefs;
	}
	protected SnipToolContext preferences;


	// --- EditorKit methods -------------------------


	/**
	 * Create a copy of the editor kit. This allows an implementation to serve
	 * as a prototype for others, so that they can be quickly created.
	 */
	public Object clone ()
	{
		SnipToolKit kit = properties.newKit ();
		kit.setStylePreferences (preferences);
		return kit;
	}


	/**
	 * Creates an uninitialized text storage model
	 * that is appropriate for this type of editor.
	 * @return the model
	 */
	public Document createDefaultDocument ()
	{
		return properties.newDocument ();
	}


	/**
	 * Fetches a factory that is suitable for producing views of any models that
	 * are produced by this kit. The default is to have the UI produce the
	 * factory, so this method has no implementation.
	 * @return the view factory
	 */
	public final ViewFactory getViewFactory ()
	{
		return getStylePreferences ();
	}


//	/**
//	 * Get the MIME type of the data that this kit represents support for. This
//	 * kit supports the type <code>text/java</code>.
//	 * this is now FINAL in JEditorPane
//	 */
//	public String getContentType() {
//		return "text/snip";
//	}


	private static final long serialVersionUID = -4828767015101435716L;

}

