
package net.myorb.gui.palate;

import net.myorb.gui.graphics.ColorNames;
import net.myorb.gui.graphics.ColorDisplays;
import net.myorb.gui.graphics.ColorPropertiesProcessor;

import net.myorb.gui.components.SimpleScreenIO.Scrolling;
import net.myorb.gui.components.SimpleScreenIO.Widget;
import net.myorb.gui.components.SimpleScreenIO.Panel;

import net.myorb.gui.components.MenuListFactory;

import java.awt.Color;
import java.io.File;

/**
 * the palate display manager for application GUI 
 * @author Michael Druckman
 */
public class PalateTool extends ColorPropertiesProcessor
{


	/**
	 * list of standard commands for palate edits
	 */
	public enum Commands { Add, Edit, Move_Up, Move_Down, Rename, Remove, Top }


	public PalateTool (String filePath)
	{
		processColorCSV (new File (filePath));
	}


	/**
	 * get a display widget for the palate
	 * @param menuFactory the factory for pop-up menu generation
	 * @return display widget for palate
	 */
	public Widget getPalatePanel (MenuListFactory menuFactory)
	{
		this.menuFactory = menuFactory;
		this.container = new Scrolling (content ());
		return this.container;
	}
	protected MenuListFactory menuFactory;
	protected Scrolling container;


	/**
	 * build a palate of anonymous colors
	 * @param colors a set of colors to be used
	 */
	public void setPalateColors (Color[] colors)
	{
		int n = 0;
		ColorNames.ColorMap
			map = this.getColorMap ();
		map.clear (); sourceOrderNames.clear ();
		for (Color c : colors)
		{
			String name = "ANON_" + Integer.toString (n);
			sourceOrderNames.add (name);
			map.put (name, c); n++;
		}
	}


	/**
	 * get a copy of the palate as an array of colors
	 * @return the current color list from the palate
	 */
	public Color[] getPalateColors ()
	{
		int n = 0;
		ColorNames.ColorMap map = this.getColorMap ();
		Color [] colors = new Color [this.sourceOrderNames.size ()];
		for (String name : this.sourceOrderNames)
		{ colors [n++] = map.get (name); }
		return colors;
	}


	/* (non-Javadoc)
	 * @see net.myorb.gui.graphics.ColorPropertiesProcessor#addToList(java.lang.String, java.lang.String)
	 */
	public void addToList (String name, String value)
	{
		super.addToList (name, value);
		this.sourceOrderNames.add (name);
	}


	/**
	 * @return the current list of color names
	 */
	public ColorNames.ColorList getCurrentNameList () { return sourceOrderNames; }
	protected ColorNames.ColorList sourceOrderNames = new ColorNames.ColorList ();


	/**
	 * @return the Standard Palate Pop-up Factory
	 */
	public MenuListFactory getStandardPalatePopupFactory ()
	{
		PalateEditor editor = new PalateEditor (this);
		return new PalatePopupFactory (editor);
	}


	/**
	 * @return a generated palate column for name list
	 */
	Panel content ()
	{
		return ColorDisplays.paletteColumnPanel
			(
				this.sourceOrderNames, this.menuFactory,
				this.getColorMap ()
			);
	}


	/**
	 * replace container view port with fresh column display
	 */
	void refresh ()
	{
		container.setViewportView
		(
			content ()
		);
	}


}


