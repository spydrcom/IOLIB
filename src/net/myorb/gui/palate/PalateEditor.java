
package net.myorb.gui.palate;

import net.myorb.gui.palate.PalateTool.Commands;
import net.myorb.gui.components.SimpleScreenIO;
import net.myorb.gui.graphics.ColorNames;

import java.awt.Color;

/**
 * an editor that executes the palate command list
 * @author Michael Druckman
 */
public class PalateEditor
{


	PalateEditor
		(
			PalateTool tool
		)
	{
		this.tool = tool;
	}
	protected PalateTool tool;


	/**
	 * apply command to specified item
	 * @param command the command to execute
	 * @param item the index into the palate list to change
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
				tool.getCurrentNameList ().remove (item);
				break;
			case Add:
				ColorEditor.chooseNewColor
					(
						tool.getCurrentNameList (),
						tool.getColorMap (),
						tool
					);
				break;
			case Edit:
				ColorEditor.editColor
					(
						tool.getCurrentNameList ().get (item),
						tool.getColorMap (),
						tool
					);
				break;
			case Rename:
				rename (item);
				break;
			case Top:
				top (item);
				break;
		}

		tool.refresh ();
	}


	/**
	 * change the name of the list entry
	 * @param index the entry number for the command
	 */
	void rename (int index)
	{
		ColorNames.ColorList names = tool.getCurrentNameList ();
		String oldName = names.get (index);
		String newName;

		try
		{
			newName = SimpleScreenIO.requestTextInput
					(tool.container, "New name for Color", "New Name");
			names.set (index, newName = PalateToolCommands.toUS (newName).toUpperCase ());
		} catch (Exception e) { return; }

		ColorNames.ColorMap map = tool.getColorMap ();
		Color value = map.get (oldName);

		map.put (newName, value);
		map.remove (oldName);
	}


	/**
	 * move selected entry to top of list
	 * @param index the entry number for the command
	 */
	void top (int index)
	{
		ColorNames.ColorList names = tool.getCurrentNameList ();
		String name = names.get (index);
		names.remove (index);
		names.add (0, name);
	}


	/**
	 * exchange identified item with following entry
	 * @param first the first index of list to exchange
	 */
	void exchange (int first)
	{
		ColorNames.ColorList names = tool.getCurrentNameList ();
		if (first < 0 || first == names.size () - 1) return;

		String name2 = names.get (first + 1);
		String name1 = names.get (first);

		names.set (first + 1, name1);
		names.set (first, name2);
	}


}

