
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
			names.add (0, editing = PalateToolCommands.toUS (editing).toUpperCase ());
		} catch (Exception e) { return; }
	
		map.put (editing, Color.BLACK);
		new ColorEditor (editing, map, tool).showChooser ();
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
		this (colorNamed, map, tool);
		this.title = "Edit Palate Color";
		this.chooser.setColor (color);
	}
	
	
	ColorEditor
		(
			String colorNamed,
			ColorNames.ColorMap map,
			PalateTool tool
		)
	{
		this.title = "Add Palate Color";
		this.chooser = new JColorChooser ();
		this.editing = colorNamed; this.map = map;
		this.chooser.getSelectionModel ().addChangeListener (this);
		this.tool = tool;
	}
	protected ColorNames.ColorMap map;
	protected JColorChooser chooser;
	protected PalateTool tool;
	protected String editing;
	
	
	/* (non-Javadoc)
	 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
	 */
	public void stateChanged (ChangeEvent e)
	{
	    Color newColor = this.chooser.getColor ();
		map.put (editing, newColor);
		tool.refresh ();
	}
	
	
	/**
	 * create a chooser frame
	 */
	void showChooser ()
	{
		DisplayFrame f =
			new DisplayFrame (this.chooser, title);
		this.chooser.setPreferredSize (SimpleScreenIO.wXh (600, 400));
		f.showOrHide ();
	}
	protected String title;


}

