
package net.myorb.data.abstractions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * utilities for working with ZIP files.
 * @author Michael Druckman
 */
public class ZipUtilities
{


	/**
	 * create index of ZIP file
	 * @param zip the source file to index
	 * @return a map of ZIP contents
	 * @throws Exception for errors
	 */
	public static Map <String, Integer> index (File zip) throws Exception
	{
		int entryNumber = 0;
		ZipSource source = new ZipSource (zip);
		Map <String, Integer> map = new HashMap <> ();
		ZipRecord.Properties p = source.positionToNext ();
		while (p != null)
		{
			map.put (p.getName (), entryNumber++);
			p = source.positionToNext ();
		}
		return map;
	}


	/**
	 * extract from ZIP into specified directory
	 * @param zip the file access for the ZIP source
	 * @param items the index numbers of entries to be extracted
	 * @param toDirectory location for extracted files
	 * @throws Exception for any errors
	 */
	public static void extractFrom (File zip, int[] items, File toDirectory) throws Exception
	{
		ZipSource source = new ZipSource (zip);
		for (int item = 0, at = -1, last = items.length - 1; item <= last; )
		{
			source.positionToNext ();
			if (++at == items[item]) { save (source, toDirectory); item++; }
		}
	}


	/**
	 * copy ZIP source to sink
	 * @param sinkStream a stream for output
	 * @param fromZip the ZIP source file to use
	 * @param itemNumber the offset of items into ZIP source
	 * @throws Exception for any errors
	 */
	public static void copyTo
	(OutputStream sinkStream, File fromZip, int itemNumber)
	throws Exception
	{
		new ZipSource (fromZip, itemNumber).copyTo (sinkStream);
	}


	/**
	 * store source content into file
	 * @param fromSource ZIP text source of entry to save
	 * @param toDirectory the directory to save file to
	 * @throws Exception for any errors
	 */
	public static void save (ZipSource fromSource, File toDirectory) throws Exception
	{
		File saveAs = new File
			(toDirectory, fromSource.getEntryProperties ().getName ());
		fromSource.copyTo (new FileOutputStream (saveAs));
	}


}

