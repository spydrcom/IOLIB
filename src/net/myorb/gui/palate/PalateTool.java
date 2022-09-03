
package net.myorb.gui.palate;

import net.myorb.gui.graphics.ColorNames;
import net.myorb.gui.graphics.ColorDisplays;
import net.myorb.gui.graphics.ColorPropertiesProcessor;

import net.myorb.gui.components.SimpleScreenIO.Scrolling;
import net.myorb.gui.components.SimpleScreenIO.Widget;
import net.myorb.gui.components.SimpleScreenIO.Panel;

import net.myorb.gui.components.MenuListFactory;
import net.myorb.gui.components.SimpleScreenIO;
import net.myorb.gui.components.DisplayFrame;

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.JColorChooser;

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


	/**
	 * list of standard commands for palate edits
	 */
	public enum Commands { Edit, Move_Up, Move_Down, Remove, Add }


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
	 * apply command to specified item
	 * @param command the command to execute
	 * @param item the index into th palate list to change
	 */
	public void editPalate (Commands command, int item)
	{
		switch (command)
		{
			case Move_Down:
				this.exchange (item);
				break;
			case Move_Up:
				this.exchange (item - 1);
				break;
			case Remove:
				this.sourceOrderNames.remove (item);
				break;
			case Add:
				ColorEditor.chooseNewColor
					(
						this.sourceOrderNames,
						this.getColorMap (),
						this
					);
				break;
			case Edit:
				ColorEditor.editColor
					(
						this.sourceOrderNames.get (item),
						this.getColorMap (),
						this
					);
				break;
		}

		this.refresh ();
	}


	/**
	 * exchange identified item with following entry
	 * @param first the first index of list to exchange
	 */
	void exchange (int first)
	{
		ColorNames.ColorList names = this.sourceOrderNames;
		if (first < 0 || first == names.size () - 1) return;

		String name2 = names.get (first + 1);
		String name1 = names.get (first);

		names.set (first + 1, name1);
		names.set (first, name2);
	}


	/**
	 * @return the Standard Palate Pop-up Factory
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
		super (PalateToolCommands.getNames (), new PalatePopupProcessor (tool));
	}
}


/**
 * the processor for the standard factory
 */
class PalatePopupProcessor implements MenuListFactory.Processor
{
	/* (non-Javadoc)
	 * @see net.myorb.gui.components.MenuListFactory.Processor#process(java.lang.String, int)
	 */
	public void process (String command, int item)
	{
		tool.editPalate (PalateToolCommands.recognize (command), item);
	}
	PalatePopupProcessor (PalateTool tool) { this.tool = tool; }
	protected PalateTool tool;
}


/**
 * enumerate and recognize commands
 */
class PalateToolCommands
{

	/**
	 * produce list of commands
	 * @return the list of command names
	 */
	static List <String> getNames ()
	{
		List <String>
			names = new ArrayList <> ();
		for (PalateTool.Commands c : PalateTool.Commands.values ())
		{ names.add (c.name ().replace ('_', ' ')); }
		return names;
	}

	/**
	 * identify command to be used
	 * @param text the name to be recognized
	 * @return the enumeration for the recognized text
	 */
	static PalateTool.Commands recognize (String text)
	{
		return PalateTool.Commands.valueOf
			(text.replace (' ', '_'));
	}

}


/**
 * use chooser to edit palate
 */
class ColorEditor
	implements ChangeListener
{

	/**
	 * add a new color to the palate
	 * @param names the current list of color names
	 * @param map the map of name to color in current palate
	 * @param tool the palate tool requesting the edit
	 */
	static void chooseNewColor
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
			names.add (0, editing);
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
	static void editColor
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
	ColorNames.ColorMap map;
	JColorChooser chooser;
	PalateTool tool;
	String editing;


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
	String title;

}

