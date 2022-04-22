
package net.myorb.data.abstractions;

import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.File;

/**
 * treat file as source
 * @author Michael Druckman
 */
public class FileSource extends SimpleStreamIO
{


	/**
	 * @param file the file to read from
	 * @throws FileNotFoundException for file not found
	 */
	public FileSource (File file) throws FileNotFoundException
	{ this (new FileInputStream (file), file.getAbsolutePath ()); }

	protected FileSource (InputStream fileReader, String filePath)
	{ this.fileReader = fileReader; this.filePath = filePath; }
	protected InputStream fileReader;
	protected String filePath;


	/**
	 * @return a stream source for the current entry
	 */
	public SimpleStreamIO.Source getSource ()
	{
		return getSourceFor (fileReader, filePath);
	}


	/**
	 * @param sinkStream the stream to send output to
	 * @throws Exception for any errors
	 */
	public void copyTo (OutputStream sinkStream) throws Exception
	{
		sendTo (sinkStream, getSource ());
	}


	/**
	 * @return stream as a TextSource
	 */
	public SimpleStreamIO.TextSource getTextSource ()
	{
		return new SimpleStreamIO.TextSource (fileReader);
	}


	/**
	 * @param sink destination for text content
	 * @throws Exception for any errors
	 */
	public void copyTo (SimpleStreamIO.TextSink sink) throws Exception
	{
		processTextStream (getTextSource (), sink);
	}


}
