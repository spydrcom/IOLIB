
package net.myorb.gui.graphics;

import net.myorb.data.conventional.CharacterDelimited;
import net.myorb.data.conventional.CSV;

import java.awt.Color;
import java.io.File;

import java.util.Collection;

/**
 * set color palate in system properties
 * @author Michael Druckman
 */
public class ColorPropertiesProcessor
	implements CharacterDelimited.Processor
{


	/* (non-Javadoc)
	 * @see net.myorb.data.conventional.CharacterDelimited.Processor#process(net.myorb.data.conventional.CharacterDelimited.Reader)
	 */
	public void process (CharacterDelimited.Reader reader)
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
	public ColorNames.ColorList getColorNames ()
	{
		ColorNames.ColorList
			names = new ColorNames.ColorList ();
		names.addAll (colorMap.keySet ());
		names.sort (null);
		return names;
	}


	/**
	 * @return a map of names to colors
	 */
	public ColorNames.ColorMap getColorMap () { return colorMap; }
	protected ColorNames.ColorMap colorMap = new ColorNames.ColorMap ();


	/**
	 * read master color list into system properties
	 * @return this processor
	 */
	public static ColorPropertiesProcessor getMasterColorList ()
	{
		if (masterList == null)
		{
			return (masterList = new ColorPropertiesProcessor ())
				.processColorCSV (new File (masterColorListFilePath));
		}
		return masterList;
	}
	static ColorPropertiesProcessor masterList = null;


	/**
	 * identify source of master color list
	 * @param filePath the path to the source file
	 */
	public static void setMasterColorSource (String filePath)
	{
		masterColorListFilePath = filePath;
	}
	static String masterColorListFilePath = "properties/colorlist.csv";


}

