
package net.myorb.gui.graphics;

import net.myorb.data.conventional.CharacterDelimited.Processor;
import net.myorb.data.conventional.CharacterDelimited.Reader;

import net.myorb.data.conventional.CSV;

import java.awt.Color;
import java.io.File;

import java.util.List;
import java.util.ArrayList;

import java.util.Collection;

import java.util.HashMap;
import java.util.Map;

/**
 * set color palate in system properties
 * @author Michael Druckman
 */
public class ColorPropertiesProcessor implements Processor
{

	/* (non-Javadoc)
	 * @see net.myorb.data.conventional.CharacterDelimited.Processor#process(net.myorb.data.conventional.CharacterDelimited.Reader)
	 */
	public void process (Reader reader)
	{
		process
		(
			reader.getText ("Name").toUpperCase ().split ("/"),
			"0x" + reader.getText ("Value").substring (1)
		);
	}

	/**
	 * @param name the name for a value
	 * @param value a hex representation for the color
	 */
	public void addToList (String name, String value)
	{
		String n = name.trim ().replaceAll (" ", "_");
		colorMap.put (n, Color.decode (value));
		System.setProperty (n, value);
	}

	/**
	 * @param names one or more names for a RGB value
	 * @param value the RGB value in hex representation
	 */
	public void process (String[] names, String value)
	{
		for (String n : names) addToList (n, value);
	}

	/**
	 * @param source CSV file to read into properties
	 * @return this processor
	 */
	public ColorPropertiesProcessor processColorCSV (File source)
	{
		try { new CSV ().parse (source, this); }
		catch (Exception e) { throw new RuntimeException ("Color properties file processing failed"); }
		return this;
	}
	public void processColorCsvOnce (File source)
	{
		if (alreadyProcessed) return;
		processColorCSV (source);
		alreadyProcessed = true;
	}
	public static boolean alreadyProcessed = false;

	/**
	 * full list taken from properties/colorlist.csv
	 * @return this processor
	 */
	public ColorPropertiesProcessor processFullColorList ()
	{
		return processColorCSV (new File ("properties/colorlist.csv"));
	}

	/**
	 * standard file taken from properties/standard.csv
	 * @return this processor
	 */
	public ColorPropertiesProcessor processStandardColorList ()
	{
		return processColorCSV (new File ("properties/standard.csv"));
	}

	/**
	 * read properties/colorlist into system properties
	 * @return this processor
	 */
	public static ColorPropertiesProcessor processColorList ()
	{
		return new ColorPropertiesProcessor ().processFullColorList ();
	}

	/**
	 * @return the set of colors
	 */
	public Collection <Color> getColors ()
	{
		return colorMap.values ();
	}

	/**
	 * @return the ordered list of color names
	 */
	public List <String> getColorNames ()
	{
		ArrayList <String>
			names = new ArrayList <String> ();
		names.addAll (colorMap.keySet ()); names.sort (null);
		return names;
	}

	/**
	 * @return a map of names to colors
	 */
	public Map <String, Color> getColorMap () { return colorMap; }
	protected Map <String, Color> colorMap = new HashMap <> ();

}
