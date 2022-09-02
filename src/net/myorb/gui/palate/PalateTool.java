
package net.myorb.gui.palate;

import net.myorb.gui.graphics.ColorDisplays;
import net.myorb.gui.graphics.ColorPropertiesProcessor;
import net.myorb.gui.components.SimpleScreenIO;

import java.awt.Color;

import java.io.File;

import java.util.ArrayList;

import java.util.List;
import java.util.Map;

/**
 * the palate display manager for application GUI 
 * @author Michael Druckman
 */
public class PalateTool extends ColorPropertiesProcessor
{


	public PalateTool (String filePath)
	{
		processColorCSV (new File (filePath));
	}


	/**
	 * @return display widget for palate
	 */
	public SimpleScreenIO.Widget getPalatePanel ()
	{
		return new SimpleScreenIO.Scrolling
			(
				ColorDisplays.paletteColumnPanel
				(
					this.sourceOrderNames,
					this.getColorMap ()
				)
			);
	}


	/**
	 * @return the current color list from the palate
	 */
	public Color[] getPalateColors ()
	{
		int n = 0;
		Map <String, Color> map = this.getColorMap ();
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
	protected List <String> sourceOrderNames = new ArrayList <> ();


}

