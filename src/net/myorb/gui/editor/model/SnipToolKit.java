
package net.myorb.gui.editor.model;

import net.myorb.gui.editor.SnipToolPropertyAccess;

import net.myorb.gui.components.SimpleScreenIO;

import javax.swing.text.Document;
import javax.swing.text.ViewFactory;
import javax.swing.text.Style;

import java.awt.Color;
import java.awt.Font;

import java.util.Collection;

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


	/**
	 * set the attributes for a token
	 * @param token the token to be configured
	 * @param font the font to be used in render
	 * @param color the color to be used
	 */
	public void setAttributesFor (SnipToolToken token, Font font, Color color)
	{
		SnipToolContext context = getStylePreferences ();
		Style s = context.getStyleFor (token.getScanValue ());
		context.setAttributesFor (s, font, color);
	}


	/**
	 * set the attributes for a style
	 * @param tokens the tokens to be configured
	 * @param font the font to be used in render
	 * @param color the color to be used
	 */
	public void setAttributesFor (Collection<SnipToolToken> tokens, Font font, Color color)
	{
		SnipToolContext context = getStylePreferences ();
		for (SnipToolToken token : tokens)
		{
			Style s = context.getStyleFor (token.getScanValue ());
			context.setAttributesFor (s, font, color);
		}
	}


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

