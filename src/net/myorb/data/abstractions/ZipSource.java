
package net.myorb.data.abstractions;

import java.util.zip.ZipInputStream;
import java.util.zip.ZipEntry;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.File;

/**
 * ZIP file as stream source
 * @author Michael Druckman
 */
public class ZipSource extends FileSource
{


	/**
	 * @param file the ZIP file
	 * @param entryNumber the entry number
	 * @throws Exception for errors
	 */
	public ZipSource (File file, int entryNumber) throws Exception
	{ this (file); positionTo (entryNumber); }


	/**
	 * @param file the ZIP file
	 * @throws Exception for errors
	 */
	public ZipSource (File file) throws Exception
	{ this (new FileInputStream (file)); }


	/**
	 * @param in a file stream for the ZIP source
	 * @throws Exception for any errors
	 */
	public ZipSource (FileInputStream in) throws Exception
	{ this (new ZipInputStream (in)); }


	/**
	 * @param reader a ZIP stream reader
	 * @throws Exception for any errors
	 */
	public ZipSource (ZipInputStream reader) throws Exception
	{ super (reader, null); }


	/**
	 * current position advances
	 * @return properties of ZIP entry
	 * @throws IOException for IO errors
	 */
	public ZipRecord.Properties positionToNext () throws IOException
	{
		ZipEntry e; ZipRecord.Properties p = null;
		if ((e = ((ZipInputStream) fileReader).getNextEntry ()) != null)
		{ properties.setEntry (e); filePath = (p = getEntryProperties ()).getName (); }
		else properties = null;
		return p;
	}


	/**
	 * @param entryNumber number of positions to advance
	 * @return the properties of the new entry
	 * @throws IOException for IO errors
	 */
	public ZipRecord.Properties positionTo (int entryNumber) throws IOException
	{
		ZipRecord.Properties properties = positionToNext ();
		for (int n = 0; n < entryNumber; n++) properties = positionToNext ();
		return properties;
	}


	/**
	 * @return the properties of the current entry
	 */
	public ZipRecord.Properties getEntryProperties () { return properties; }
	protected ZipRecord.EntryWrapper properties = new ZipRecord.EntryWrapper ();


}

