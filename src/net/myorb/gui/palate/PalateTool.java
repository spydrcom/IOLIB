
package net.myorb.gui.palate;

import net.myorb.gui.graphics.ColorNames;
import net.myorb.gui.graphics.ColorDisplays;
import net.myorb.gui.graphics.ColorPropertiesProcessor;

import net.myorb.gui.components.MenuListFactory;
import net.myorb.gui.components.SimpleScreenIO;

import java.awt.Color;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * the palate display manager for application GUI 
 * @author Michael Druckman
 */
public class PalateTool extends ColorPropertiesProcessor
{


	public enum Commands { Edit, Move_Up, Move_Down, Remove }


	public PalateTool (String filePath)
	{
		processColorCSV (new File (filePath));
	}


	/**
	 * get a display widget for the palate
	 * @param menuFactory the factory for pop-up menu generation
	 * @return display widget for palate
	 */
	public SimpleScreenIO.Widget getPalatePanel (MenuListFactory menuFactory)
	{
		return new SimpleScreenIO.Scrolling
			(
				ColorDisplays.paletteColumnPanel
				(
					this.sourceOrderNames,
					menuFactory, this.getColorMap ()
				)
			);
	}


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
	protected ColorNames.ColorList sourceOrderNames = new ColorNames.ColorList ();


	/**
	 * @param command the command to execute
	 * @param item the index into th palate list to change
	 */
	public void editPalate (Commands command, int item)
	{
		System.out.println ("TOOL: " + command + " " + item);
	}


	/**
	 * @return the Standard Palate Popup Factory
	 */
	public MenuListFactory getStandardPalatePopupFactory ()
	{
		return new PalatePopupFactory (this);
	}


}


/**
 * an implementation of the standard factory
 */
class PalatePopupFactory extends MenuListFactory
{
	PalatePopupFactory (PalateTool tool)
	{
		super (PalatePopupProcessor.getNames (), new PalatePopupProcessor (tool));
	}
}


/**
 * the processor for the standard factory
 */
class PalatePopupProcessor implements MenuListFactory.Processor
{

	PalatePopupProcessor (PalateTool tool)
	{
		this.tool = tool;
	}
	PalateTool tool;

	/**
	 * @return the list f command names
	 */
	static List <String> getNames ()
	{
		List <String>
			names = new ArrayList <> ();
		for (PalateTool.Commands c : PalateTool.Commands.values ())
		{ names.add (c.name ().replace ('_', ' ')); }
		return names;
	}

	/* (non-Javadoc)
	 * @see net.myorb.gui.components.MenuListFactory.Processor#process(java.lang.String, int)
	 */
	public void process (String command, int item)
	{
		PalateTool.Commands c =
			PalateTool.Commands.valueOf (command.replace (' ', '_'));
		tool.editPalate (c, item);
	}

}

