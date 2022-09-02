
package net.myorb.gui.palate;

import net.myorb.gui.graphics.ColorDisplays;
import net.myorb.gui.graphics.ColorPropertiesProcessor;

import net.myorb.gui.components.SimpleScreenIO;
import net.myorb.gui.components.SimpleScreenIO.Scrolling;

import java.awt.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * the palate display manager for application GUI 
 * @author Michael Druckman
 */
public class PalateTool
{

	public PalateTool ()
	{
		this.palate = new PalateEnumeration ();
	}

	/**
	 * @return display widget for palate
	 */
	public SimpleScreenIO.Widget getPalatePanel ()
	{
		return new Scrolling
			(
				ColorDisplays.paletteColumnPanel
				(
					palate.sourceOrderNames,
					palate.getColorMap ()
				)
			);
	}

	/**
	 * @return the current color list from the palate
	 */
	public Color[] getPalateColors ()
	{
		return this.palate.getPalateColors ();
	}
	protected PalateEnumeration palate;

}

/**
 * monitor for the color properties
 */
class PalateEnumeration extends ColorPropertiesProcessor
{

	PalateEnumeration () { processStandardColorList (); }

	Color[] getPalateColors ()
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
	protected List <String> sourceOrderNames = new ArrayList <String> ();

}

