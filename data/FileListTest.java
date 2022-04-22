
package net.myorb.gui.tests;

import net.myorb.gui.components.SimpleFileDirectoryList;

import java.io.File;

public class FileListTest
{
	public static void main(String[] args) throws Exception
	{
		SimpleFileDirectoryList list;
		(list = new SimpleFileDirectoryList ()).show ("TESTING File List");
		list.setEntriesFrom (new File ("data"), "json");
		list.getFrame ().done ();
	}
}
