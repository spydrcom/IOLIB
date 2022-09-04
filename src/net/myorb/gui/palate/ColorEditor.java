
package net.myorb.gui.palate;

import net.myorb.gui.components.SimpleScreenIO;
import net.myorb.gui.components.DisplayFrame;

import net.myorb.gui.graphics.ColorNames;

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.JColorChooser;

import java.awt.Color;

/**
 * use chooser to edit palate
 * @author Michael Druckman
 */
public class ColorEditor implements ChangeListener
{


	/**
	 * add a new color to the palate
	 * @param names the current list of color names
	 * @param map the map of name to color in current palate
	 * @param tool the palate tool requesting the edit
	 */
	public static void chooseNewColor
		(
			ColorNames.ColorList names,
			ColorNames.ColorMap map,
			PalateTool tool
		)
	{
		String editing;
	
		try
		{
			editing = SimpleScreenIO.requestTextInput
					(tool.container, "Name for Color", "New Color");
			names.add (0, editing = PalateToolCommands.toConventional (editing));
		} catch (Exception e) { return; }
	
		map.put (editing, Color.BLACK);
		new ColorEditor (editing, map, tool).showChooser ();
	}
	

	ColorEditor
		(
			String colorNamed,
			ColorNames.ColorMap map,
			PalateTool tool
		)
	{
		this ("Add Palate Color", colorNamed, map, tool);
	}


	/**
	 * modify a color currently in the palate
	 * @param called the name of the color to be changed
	 * @param map the map of name to color in current palate
	 * @param tool the palate tool requesting the edit
	 */
	public static void editColor
		(
			String called,
			ColorNames.ColorMap map,
			PalateTool tool
		)
	{
		new ColorEditor (called, map, tool, map.get (called)).showChooser ();
	}


	ColorEditor
		(
			String colorNamed,
			ColorNames.ColorMap map,
			PalateTool tool,
			Color color
		)
	{
		this ("Edit Palate Color", colorNamed, map, tool);
		this.chooser.setColor (color);
	}

	
	/**
	 * common constructor entry
	 * @param title the title for the chooser frame
	 * @param colorNamed the name of the color being edited
	 * @param map the color map of names mapped to color objects
	 * @param tool the palate tool object running commands
	 */
	ColorEditor
		(
			String title,
			String colorNamed,
			ColorNames.ColorMap map,
			PalateTool tool
		)
	{
		this ();
		this.title = title; this.tool = tool;
		this.editing = colorNamed; this.map = map;
	}
	protected ColorNames.ColorMap map;
	protected PalateTool tool;
	protected String editing;


	/**
	 * construct chooser
	 */
	ColorEditor ()
	{
		this.chooser = new JColorChooser ();
		this.chooser.getSelectionModel ().addChangeListener (this);
	}
	protected JColorChooser chooser;

	
	/* (non-Javadoc)
	 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
	 */
	public void stateChanged (ChangeEvent e)
	{
		// color associated with name and display is refreshed
	    Color newColor = this.chooser.getColor ();
		map.put (this.editing, newColor);
		tool.refresh ();
	}
	
	
	/**
	 * create a chooser frame
	 */
	void showChooser ()
	{
		DisplayFrame f =
			new DisplayFrame (this.chooser, this.title);
		this.chooser.setPreferredSize (SimpleScreenIO.wXh (600, 400));
		f.showOrHide ();
	}
	protected String title;


}

