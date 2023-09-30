
package net.myorb.data.abstractions;

import java.io.File;

/**
 * abstract Directory specific to file system meta-data
 * @author Michael Druckman
 */
public class FileSystemMetadata extends Directory
{

	/**
	 * @param items array of files from directory
	 * @param parent the object to treat as parent collection
	 */
	public void addContents (File [] items, Element parent)
	{
		if (items == null || items.length == 0) return;
		addContents (items, parent.addStructure ());
	}
	public void addContents (File [] items, TypedGroups group)
	{
		for (File file : items) { group.addUnit (examine (file)); }
	}

	/**
	 * collect file meta-data
	 * @param item the file to examine
	 * @return an element holding the meta-data
	 */
	public Element examine (File item)
	{
		Element descriptor = new Element ();

		descriptor.setParent (item.getParent ());
		String simpleName = item.getName ();

		if (item.isDirectory ())
		{
			descriptor.setKey (">>>");
			descriptor.setName (simpleName);
			addContents (item.listFiles (), descriptor);
		}
		else
		{
			int dot = simpleName.lastIndexOf (".");
			descriptor.setKey (simpleName.substring (dot+1).toUpperCase ());
			if (dot >= 0) { descriptor.setName (simpleName.substring (0, dot)); }
			else { descriptor.setName (descriptor.getKey ()); }
			descriptor.setSize (item.length ());
		}

		return descriptor;
	}

}
