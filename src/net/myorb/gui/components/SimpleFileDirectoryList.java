
package net.myorb.gui.components;

import net.myorb.data.abstractions.ZipTextSink;
import net.myorb.utilities.ApplicationShell;

import java.io.FilenameFilter;
import java.io.File;

/**
 * GUI for directory contents display
 * @author Michael Druckman
 */
public class SimpleFileDirectoryList extends SimpleFileList
{

	/**
	 * @param title the title for the display
	 */
	public void show (String title)
	{
		show (title, new DirectoryTable ());
	}

	/**
	 * implementation of table for file system directory display
	 */
	public class DirectoryTable extends Table implements FilenameFilter
	{

		/**
		 * menu item for ZIP of selected directory items
		 */
		public class ZipBundle extends MenuItem
		{
			/* (non-Javadoc)
			 * @see net.myorb.gui.components.SimpleCallback.Adapter#executeAction()
			 */
			public void executeAction () throws Exception
			{ bundle (files, getSelectedItems ()); }
			ZipBundle () { super ("Bundle"); }
		}

		/**
		 * menu item for ZIP of selected directory items to named file
		 */
		public class ZipBundleTo extends MenuItem
		{
			/* (non-Javadoc)
			 * @see net.myorb.gui.components.SimpleCallback.Adapter#executeAction()
			 */
			public void executeAction () throws Exception
			{ bundleTo (files, getSelectedItems ()); }
			ZipBundleTo () { super ("Bundle To"); }
		}

		/**
		 * @param directory the directory to be displayed
		 */
		public void setEntriesFrom (File directory)
		{
			files = directory.listFiles (this);

			for (File f : files)
			{
				setRow (f.getName (), f.length ());
			}

			resizeTo (nextRow);
		}
		protected File[] files;

		/**
		 * @return array of files selected in GUI
		 * @throws Exception for any errors
		 */
		public File[] getSelectedSourceFiles () throws Exception
		{
			int[] items = getSelectedItems ();
			File[] selected = new File[items.length];
			for (int i = 0; i < items.length; i++) selected[i] = files[items[i]];
			return selected;
		}

		/* (non-Javadoc)
		 * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
		 */
		public boolean accept (File directory, String name)
		{
			if (type == null) return true;
			return name.toLowerCase ().endsWith (type);
		}

		/* (non-Javadoc)
		 * @see net.myorb.lotto.gui.components.TableProperties#doDoubleClick(int)
		 */
		public void doDoubleClick (int forRowNumber)
		{
			if (files[forRowNumber].isDirectory ())
			{
				try { main (files[forRowNumber].getAbsolutePath (), type); }
				catch (Exception e) { e.printStackTrace (); }
				return;
			}

			try { ApplicationShell.showForType (getPathFor (forRowNumber)); }
			catch (ApplicationShell.ShellError s) { s.changeToNotification (); }
			catch (Exception e) { AppAccessMenuManager.unexpectedError (e); }
		}

		/* (non-Javadoc)
		 * @see net.myorb.gui.components.SimpleFileList.Table#getPathFor(int)
		 */
		public String getPathFor (int selected) throws Exception
		{
			return files[selected].getAbsolutePath ();
		}

		/* (non-Javadoc)
		 * @see net.myorb.gui.components.SimpleFileList.Table#getMenu()
		 */
		public Menu getMenu ()
		{
			Menu m = super.getMenu ();
			m.add (new ZipBundleTo ());
			m.add (new ZipBundle ());
			return m;
		}

		/**
		 * @param extraMenuItem extra menu item to add
		 */
		public void addMenuItem (MenuItem extraMenuItem)
		{
			Menu m = getMenu ();
			m.add (0, extraMenuItem);
			m.addAsPopup (displayTable);
		}

		/**
		 * @param directory the directory to be displayed
		 * @param type the type of files to be shown
		 */
		public void setEntriesFrom (File directory, String type)
		{ this.type = type; setEntriesFrom (directory); }
		protected String type = null;

	}


	/**
	 * perform bundle to ZIP.
	 *  ZIP file named after directory
	 * @param files the files to be bundled
	 * @param items the index numbers of entries to be bundled
	 * @throws Exception for any errors
	 */
	public static void bundle (File[] files, int[] items) throws Exception
	{ bundleTo (files[0].getParentFile ().getName (), files, items); }


	/**
	 * perform bundle to ZIP.
	 *  ZIP file named by user prompt
	 * @param files the files to be bundled
	 * @param items the index numbers of entries to be bundled
	 * @throws Exception for any errors
	 */
	public static void bundleTo (File[] files, int[] items) throws Exception
	{
		String name = SimpleScreenIO.requestTextInput
		(null, "Name for ZIP", "Create ZIP File");
		bundleTo (name, files, items);
	}


	/**
	 * perform bundle to named ZIP
	 * @param name the name for the ZIP file
	 * @param files the files to be bundled into ZIP
	 * @param items the index numbers of entries to be bundled
	 * @throws Exception for any errors
	 */
	public static void bundleTo
	(String name, File[] files, int[] items)
	throws Exception
	{
		File dir = files[0].getParentFile ();
		File zipFile = new File (dir, name + ".zip");
		ZipTextSink.Stream stream = ZipTextSink.newZipFileStream (zipFile);
		for (int item : items) stream.addTextFileAsEntry (files[item]);
		stream.close ();
	}


	/**
	 * @param file the directory to be displayed
	 * @param type the type of files to be shown
	 */
	public void setEntriesFrom (File file, String type)
	{
		((DirectoryTable) getTable ()).setEntriesFrom (file, type);
	}


	/**
	 * command line driver
	 * @param args 0 = directory, 1 = file type
	 * @throws Exception for any errors
	 */
	public static void main (String... args) throws Exception
	{
		SimpleFileDirectoryList list;
		String dir = args[0], type = null;
		if (args.length > 1) type = args[1];
		(list = new SimpleFileDirectoryList ()).show (dir);
		list.setEntriesFrom (new File (dir), type);
		list.getFrame ().done ();
	}


}

