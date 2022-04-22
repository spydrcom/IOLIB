
package net.myorb.gui.tests;

import net.myorb.gui.components.SimpleFileDirectoryList;
import net.myorb.gui.components.SimpleFileZipEntryList;

import java.io.File;

public class FileListTest
{
	public static void main(String[] args) throws Exception
	{
		SimpleFileZipEntryList.main ("/workspace/pix.zip");

		SimpleFileDirectoryList list;
		(list = new SimpleFileDirectoryList ()).show ("TESTING File List");
		list.setEntriesFrom (new File ("/workspace/"), null);
		list.getFrame ().done ();
	}
}
