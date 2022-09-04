
package net.myorb.gui.palate;

import java.util.ArrayList;
import java.util.List;

/**
 * enumerate and recognize commands
 * @author Michael Druckman
 */
public class PalateToolCommands
{

	/**
	 * produce list of commands
	 * @return the list of command names
	 */
	public static List <String> getNames ()
	{
		List <String>
			names = new ArrayList <> ();
		for (PalateTool.Commands c : PalateTool.Commands.values ())
		{ names.add (toBS (c.name ())); }
		return names;
	}

	/**
	 * identify command to be used
	 * @param text the name to be recognized
	 * @return the enumeration for the recognized text
	 */
	public static PalateTool.Commands recognize (String text)
	{
		return PalateTool.Commands.valueOf (toUS (text));
	}

	/**
	 * force name convention 
	 *  - fully upper case text
	 *  - remove leading and trailing whitespace
	 *  - blank space within text changed to underscore
	 * @param text source of conversion to the conventions
	 * @return text changed to conform with conventions
	 */
	public static String toConventional (String text)
	{
		return toUS (text.trim ().toUpperCase ());
	}

	/**
	 * blank space within text changed to underscore
	 * @param text source of conversion to the conventions
	 * @return text changed to conform with conventions
	 */
	public static String toUS (String text)
	{
		return text.trim ().replaceAll (" ", "_");
	}

	/**
	 * underscore within text changed to blank space
	 * @param text source of conversion to the conventions
	 * @return text changed to conform with conventions
	 */
	public static String toBS (String text)
	{
		return text.trim ().replaceAll ("_", " ");
	}

}

