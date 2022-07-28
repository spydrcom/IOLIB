
package net.myorb.gui.editor;

import net.myorb.gui.components.SimpleScreenIO;

import java.awt.Component;
import java.io.File;

/**
 * helper class for Snip tool features
 * @author Michael Druckman
 */
public class SnipToolSupport extends SimpleScreenIO
{


	/**
	 * user request for tab name
	 * @param forWidget related Component
	 * @return the name provided by screen request
	 * @throws Exception for errors in the input request
	 */
	public static String requestName (Component forWidget) throws Exception
	{
		return requestTextInput (forWidget, "Name for Tab", "", "NA");
	}


	/**
	 * file type extension is removed for brevity
	 * @param fullName files name with file type extended
	 * @return name with type removed
	 */
	public static String shortNameFor (String fullName)
	{		
		int dot = fullName.lastIndexOf ('.');
		if (dot > 0) return fullName.substring (0, dot);
		return fullName;
	}


	/**
	 * dump text string to console
	 * @param text content to dump
	 */
	public static void dump (String text)
	{
		for (int i = 0; i < text.length (); i++)
		{
			System.out.println (Integer.toHexString (text.charAt (i)));
		}
	}


	/**
	 * refer to the file specified
	 * @param directoryName the source of files
	 * @param named the simple file name
	 * @return the file descriptor
	 */
	public static File getFileAccess (String directoryName, String named)
	{
		return new File (getFilePath (directoryName, named + ".txt"));
	}


	/**
	 * refer to the file specified
	 * @param directoryName the source of files
	 * @param named the simple file name
	 * @return the file path
	 */
	public static String getFilePath (String directoryName, String named)
	{
		return directoryName + named;
	}


}

