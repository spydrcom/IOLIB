
package net.myorb.gui.editor.model;

import net.myorb.gui.StyleManager;

import net.myorb.gui.editor.SnipToolScanner;
import net.myorb.gui.editor.SnipToolPropertyAccess;

import javax.swing.text.Style;
import javax.swing.text.Element;
import javax.swing.text.ViewFactory;
import javax.swing.text.View;

import java.awt.Color;
import java.awt.Font;

/**
 * StyleContext extension for SnipTool editor
 * - refactor of original JavaKit example from Sun
 * - by Timothy Prinzing version 1.2 05/27/99
 * - refactor done in summer 2022
 * @author Michael Druckman
 */
public class SnipToolContext  extends StyleManager
		implements ViewFactory
{


	/*
	 * redesign done on 8/1/2022 to change from the Prinzing architecture 
	 * to use the StyleManager architecture built in the IOlib GUI package
	 */


	/**
	 * establish content for tokens of upper layer
	 * @param properties access to properties of upper layer
	 */
	public SnipToolContext (SnipToolPropertyAccess properties)
	{
		super (); this.properties = properties;
	}
	protected SnipToolPropertyAccess properties;


	/**
	 * assign a style code for a style which is just a font and color
	 * @param font the font to associate with the new style
	 * @param color the foreground color to assign
	 * @return the style code for the new style
	 */
	public int postAnonymousStyle (Font font, Color color)
	{
		Style s;
		int assigned = nextStyleCode++;
		styles.add (s = addStyle (null));
		fonts.add (font); colors.add (color);
		s.addAttribute (STYLE_CODE, assigned);
		return assigned;
	}


	/* (non-Javadoc)
	 * @see javax.swing.text.ViewFactory#create(javax.swing.text.Element)
	 */
	public View create (Element element)
	{
		return new SnipToolView (element, this);
	}


	/**
	 * @return the scanner allocated for this editor
	 */
	public SnipToolScanner getScanner ()
	{
		return properties.getScanner ();
	}


	private static final long serialVersionUID = -7292184372169423008L;

}

