
package net.myorb.data.abstractions;

import java.util.zip.ZipOutputStream;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.io.File;

/**
 * create ZIP text file entries using SimpleStreamIO sink concepts.
 *  this provides a complete wrapper around the runtime ZIP functionality
 * @author Michael Druckman
 */
public class ZipTextSink extends SimpleStreamIO.TextSink
{

	/**
	 * encapsulate ZIP runtime layer
	 */
	public static class Stream
	{

		/**
		 * ZIP output stream is protected
		 */
		protected ZipOutputStream link;
		protected PrintStream out;

		/**
		 * only allows output stream to file
		 * @param out the file output stream to use
		 */
		protected Stream (FileOutputStream out)
		{
			this.link = new ZipOutputStream (out);
			this.out = new PrintStream (link);
		}

		/**
		 * position to next entry and prepare TextSink
		 * @param newEntryName text of a name for the entry
		 * @return SimpleStreamIO Text Sink for ZIP
		 * @throws IOException for IO errors
		 */
		public ZipTextSink createSinkFor (String newEntryName) throws IOException
		{
			return createNewEntry (this, newEntryName);
		}

		/**
		 * release resources when processing is complete
		 * @throws IOException for IO errors
		 */
		public void close () throws IOException { link.close (); }

		/**
		 * build the stream object for a ZIP file
		 * @param file the ZIP file access object to use
		 * @return a Stream object for the ZIP file identified
		 * @throws FileNotFoundException for failed File access
		 */
		public static Stream
			createStreamFor (File file) throws FileNotFoundException
		{ return new Stream (new FileOutputStream (file)); }

		/**
		 * add entry with same name as file
		 * @param file the file to be added to ZIP
		 * @throws Exception for any errors
		 */
		public void addTextFileAsEntry (File file) throws Exception
		{
			createSinkFor (file.getName ()).addTextFile (file);
		}

		/**
		 * add entry with same name as file (taken from path)
		 * @param path the path to the file to be added to ZIP
		 * @throws Exception for any errors
		 */
		public void addTextFileAsEntry (String path) throws Exception
		{
			addTextFileAsEntry (new File (path));
		}

	}


	/**
	 * build text source for ZIP entry
	 * @param writer stream sink for current ZIP entry
	 * @param entry the currently positioned entry
	 * @throws IOException for IO errors
	 */
	protected ZipTextSink
		(Stream stream, ZipRecord.Access entry)
	throws IOException
	{
		super (stream.out);
		this.stream = stream; this.entry = entry;
		stream.link.putNextEntry (entry);
	}
	protected Stream stream;


	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.SimpleStreamIO.TextSink#endOfProcessing()
	 */
	public void endOfProcessing () throws IOException
	{
		stream.link.closeEntry ();
	}


	/**
	 * create ZIP file to use as sink (file object version).
	 *  this is the encapsulated Stream version hiding the ZipOutputStream.
	 * @param file the file access object for the ZIP sink for the stream
	 * @return the output stream with ZIP entry marker management
	 * @throws FileNotFoundException for file not created
	 */
	public static Stream newZipFileStream (File file) throws FileNotFoundException
	{
		return Stream.createStreamFor (file);
	}


	/**
	 * create ZIP file to use as sink (file path version).
	 *  this is the encapsulated Stream version hiding the ZipOutputStream.
	 * @param path the path to the file to use as ZIP sink, will over-write
	 * @return the output stream with ZIP entry marker management
	 * @throws FileNotFoundException for file not created
	 */
	public static Stream newZipFileStream (String path) throws FileNotFoundException
	{
		return Stream.createStreamFor (new File (path));
	}


	/**
	 * @param stream stream access to ZIP sink
	 * @param name text of name for the entry
	 * @return the sink to use for this entry
	 * @throws IOException for IO errors
	 */
	public static ZipTextSink createNewEntry (Stream stream, String name) throws IOException
	{
		return new ZipTextSink (stream, new ZipRecord.Access (name));
	}


	/**
	 * read text file into this sink
	 * @param filepath name of file to read
	 * @throws Exception for any errors
	 */
	public void addTextFile (String filepath) throws Exception
	{
		SimpleStreamIO.processTextFile (filepath, this);
	}


	/**
	 * read text file into this sink
	 * @param file access to file to read
	 * @throws Exception for any errors
	 */
	public void addTextFile (File file) throws Exception
	{
		SimpleStreamIO.processTextFile (file, this);
	}


	/**
	 * @return access to entry properties
	 */
	public ZipRecord.Properties getEntryProperties () { return entry; }
	protected ZipRecord.Access entry;


}

