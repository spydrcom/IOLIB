
package net.myorb.gui.graphics;

import net.myorb.data.conventional.CharacterDelimited.Processor;
import net.myorb.data.conventional.CharacterDelimited.Reader;
import net.myorb.data.conventional.CSV;

import java.io.File;

/**
 * set color palette in system properties
 * @author Michael Druckman
 */
public class ColorPropertiesProcessor implements Processor
{

	/* (non-Javadoc)
	 * @see net.myorb.data.conventional.CharacterDelimited.Processor#process(net.myorb.data.conventional.CharacterDelimited.Reader)
	 */
	@Override
	public void process (Reader reader)
	{
		process
		(
			reader.getText ("Name").toUpperCase ().split ("/"),
			"0x" + reader.getText ("Value").substring (1)
		);
	}

	/**
	 * @param names one or more names for a RGB value
	 * @param value the RGB value in hex representation
	 */
	public void process (String[] names, String value)
	{
		String name;
		for (String n : names)
		{
			name = n.trim ().replaceAll (" ", "_");
			//System.out.println (name + "=" + value);
			//System.out.println ("\"" + name + "\",");
			System.setProperty (name, value);
		}
	}

	/**
	 * read properties/colorlist into system properties
	 */
	public static void processColorList ()
	{
		if (alreadyProcessed) return;
		try { new CSV ().parse (new File ("properties/colorlist.csv"), new ColorPropertiesProcessor ()); }
		catch (Exception e) { throw new RuntimeException ("Color properties file processing failed"); }
		alreadyProcessed = true;
	}
	public static boolean alreadyProcessed = false;

}
