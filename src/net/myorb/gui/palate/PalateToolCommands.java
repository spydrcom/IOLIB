
package net.myorb.gui.palate;

import java.util.ArrayList;
import java.util.List;

/**
 * enumerate and recognize commands
 * @author Michael Druckman
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
		return PalateTool.Commands.valueOf (toUS (text));
	}

	/**
	 * @param text source of conversion
	 * @return spaces changed to underscore
	 */
	static String toUS (String text)
	{
		return text.replace (' ', '_');
	}

}

