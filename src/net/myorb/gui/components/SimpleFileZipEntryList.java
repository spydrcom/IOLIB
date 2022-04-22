
package net.myorb.gui.components;

import net.myorb.data.abstractions.ZipUtilities;
import net.myorb.data.abstractions.SimpleStreamIO;
import net.myorb.data.abstractions.ZipRecord;
import net.myorb.data.abstractions.ZipSource;

import net.myorb.utilities.ApplicationShell;

import java.io.IOException;
import java.io.File;

/**
 * GUI for ZIP file contents display
 * @author Michael Druckman
 */
public class SimpleFileZipEntryList extends SimpleFileList
{

	/**
	 * @param title the title for the display
	 */
	public void show (String title)
	{
		show (title, new ZipTable ());
	}

	/**
	 * implementation of table for ZIP file contents display
	 */
	public class ZipTable extends Table
	{

		/**
		 * menu item for extract of selected ZIP file entries
		 */
		public class ZipExtract extends MenuItem
		{
			public void executeAction () throws Exception
			{   extractFrom (file, getSelectedItems ());   }
			ZipExtract () { super ("Extract"); }
		}

		/**
		 * menu item for extract of selected ZIP file entries
		 */
		public class ZipExtractTo extends MenuItem
		{
			public void executeAction () throws Exception
			{    extractTo (file, getSelectedItems ());    }
			ZipExtractTo () { super ("Extract To"); }
		}

		/**
		 * @param file the ZIP file to be displayed
		 * @throws IOException for IO errors
		 */
		public void setEntriesFrom (File file) throws Exception
		{
			ZipRecord.Properties p;
			this.file = file; ZipSource source = new ZipSource (file);
			while ((p = source.positionToNext ()) != null)
			{ setRow (p); checkSpace (); }
			resizeTo (nextRow);
		}
		public void setRow (ZipRecord.Properties properties) throws IOException
		{ setRow (properties.getName (), properties.getSize ()); }

		/**
		 * adjust size before space error occurs
		 */
		public void checkSpace ()
		{
			if (nextRow+10 > data.length) resizeTo (data.length+100);
		}

		/* (non-Javadoc)
		 * @see net.myorb.gui.components.SimpleTableAdapter#doDoubleClick(int)
		 */
		public void doDoubleClick (int forRowNumber)
		{
			try { ApplicationShell.showForType (getTempFile (forRowNumber).getAbsolutePath ()); }
			catch (ApplicationShell.ShellError s) { s.changeToNotification (); }
			catch (Exception e) { e.printStackTrace (); }
		}

		/**
		 * extract ZIP entry to temp file
		 * @param forEntryNumber the entry offset count to the chosen one
		 * @return a file access object that refers to generated temp
		 * @throws Exception for any errors
		 */
		public File getTempFile (int forEntryNumber) throws Exception
		{
			ZipSource zipEntry = new ZipSource (file, forEntryNumber);
			String name = get (forEntryNumber, 0).toString () + "_", type = "." + get (forEntryNumber, 1).toString ();
			File tempFile = SimpleStreamIO.saveSourceToTempFile (zipEntry.getSource (), name, type);
			return tempFile;
		}
		protected File file;

		/* (non-Javadoc)
		 * @see net.myorb.gui.components.SimpleFileList.Table#getPathFor(int)
		 */
		public String getPathFor (int selected) throws Exception
		{
			return getTempFile (selected).getAbsolutePath ();
		}

		/* (non-Javadoc)
		 * @see net.myorb.gui.components.SimpleFileList.Table#getMenu()
		 */
		public Menu getMenu ()
		{
			ApplicationShell.exclude ("ZipDisplay");
			Menu m = super.getMenu ();
			m.add (new ZipExtractTo ());
			m.add (new ZipExtract ());
			return m;
		}

	}

	/**
	 * @param file the ZIP file to be displayed
	 * @throws IOException for IO errors
	 */
	public void setEntriesFrom (File file) throws Exception
	{
		((ZipTable) getTable ()).setEntriesFrom (file);
	}


	/**
	 * perform extract from ZIP
	 * @param zip the file access for the ZIP source
	 * @param items the index numbers of entries to be extracted
	 * @throws Exception for any errors
	 */
	public static void extractFrom (File zip, int[] items) throws Exception
	{
		ZipUtilities.extractFrom (zip, items, zip.getParentFile ());
	}


	/**
	 * extract from ZIP into identified directory
	 * @param zip the file access for the ZIP source
	 * @param items the index numbers of entries to be extracted
	 * @throws Exception for any errors
	 */
	public static void extractTo (File zip, int[] items) throws Exception
	{
		String path = SimpleScreenIO.requestTextInput
				(null, "Name for folder", "Create ZIP Extract folder");
		File directory = new File (zip.getParentFile (), path);
		if ( ! directory.exists () ) { directory.mkdirs (); }
		ZipUtilities.extractFrom (zip, items, directory);
	}


	/**
	 * command line driver
	 * @param args single argument must be ZIP file path
	 * @throws Exception for any errors
	 */
	public static void main (String... args) throws Exception
	{
		String zip = args[0];
		SimpleFileZipEntryList list;
		(list = new SimpleFileZipEntryList ()).show (zip);
		list.setEntriesFrom (new File (zip));
		list.getFrame ().done ();
	}


}

