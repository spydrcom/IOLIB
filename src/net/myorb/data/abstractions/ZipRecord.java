
package net.myorb.data.abstractions;

import java.nio.file.attribute.FileTime;

import java.util.zip.ZipEntry;

/**
 * wrapper for ZIP entry object
 * @author Michael Druckman
 */
public class ZipRecord
{


	/**
	 * identify properties available on entry records
	 */
	public interface Properties
	{

		/**
		 * @return the name of the entry
		 */
		String getName ();

		/**
		 * @return time of creation
		 */
		FileTime getCreationTime ();

		/**
		 * @return time of last access
		 */
		FileTime getLastAccessTime ();

		/**
		 * @return time of last modification
		 */
		FileTime getLastModifiedTime ();

		/**
		 * @return the modification time stamp on entry
		 */
		long getTime ();

		/**
		 * @return size of the entry
		 */
		long getSize ();

	}


	/**
	 * expose properties of ZIP entry
	 */
	public static class Access extends ZipEntry implements Properties
	{
		public Access (String name) { super (name); }
	}


	/**
	 * wrapper for ZIP entry exposing properties of entries in ZIP files
	 */
	public static class EntryWrapper implements ZipRecord.Properties
	{

		/**
		 * @param entry the entry read from ZIP input stream
		 */
		public void setEntry (ZipEntry entry)
		{
			this.entry = entry;
		}
		protected ZipEntry entry;

		/* (non-Javadoc)
		 * @see net.myorb.data.abstractions.ZipRecord.Properties#getName()
		 */
		public String getName ()
		{
			return entry.getName ();
		}

		/* (non-Javadoc)
		 * @see net.myorb.data.abstractions.ZipRecord.Properties#getTime()
		 */
		public long getTime ()
		{
			return entry.getTime ();
		}

		/* (non-Javadoc)
		 * @see net.myorb.data.abstractions.ZipRecord.Properties#getCreationTime()
		 */
		public FileTime getCreationTime ()
		{
			return entry.getCreationTime ();
		}

		/* (non-Javadoc)
		 * @see net.myorb.data.abstractions.ZipRecord.Properties#getLastAccessTime()
		 */
		public FileTime getLastAccessTime ()
		{
			return entry.getLastAccessTime ();
		}

		/* (non-Javadoc)
		 * @see net.myorb.data.abstractions.ZipRecord.Properties#getLastModifiedTime()
		 */
		public FileTime getLastModifiedTime ()
		{
			return entry.getLastModifiedTime ();
		}

		/* (non-Javadoc)
		 * @see net.myorb.data.abstractions.ZipRecord.Properties#getSize()
		 */
		public long getSize ()
		{
			return entry.getSize ();
		}
		
	}


}

