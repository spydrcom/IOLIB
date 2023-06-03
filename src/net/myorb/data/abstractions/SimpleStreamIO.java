
package net.myorb.data.abstractions;

import java.util.Arrays;

import java.io.*;

/**
 * stream IO primitives encapsulating java.io object references
 * @author Michael Druckman
 */
public class SimpleStreamIO
{


	public static int BLOCK_SIZE = 1024;


	/**
	 * error condition returned for exception during transfer
	 */
	@SuppressWarnings ("serial")
	public static class TransferError extends Exception
	{
		public TransferError (Exception cause)
		{ super (TITLE_MESSAGE); this.initCause (cause); }
		public static final String TITLE_MESSAGE =
		"Exception encountered during transfer";
	}


	/**
	 * identify source of a stream
	 */
	public interface Source
	{
		/**
		 * @return a block of bytes from the stream
		 * @throws TransferError for any errors
		 */
		byte[] get () throws TransferError;

		/**
		 * @return bytes of source processed
		 */
		long totalRead ();
	}


	/**
	 * identify sink for contents
	 */
	public interface Sink
	{
		/**
		 * @param block bytes to be sent to sink
		 * @throws TransferError for any errors
		 */
		void put (byte[] block) throws TransferError;
	}


	/**
	 * source for reader of binary formats
	 */
	public static class BinarySource implements Source
	{

		public BinarySource (InputStream stream)
		{
			this.stream = stream;
		}
		InputStream stream;

		/* (non-Javadoc)
		 * @see net.myorb.data.abstractions.SimpleStreamIO.Source#get()
		 */
		@Override
		public byte[] get () throws TransferError
		{
			try
			{
				int n;
				byte[] block = new byte[BLOCK_SIZE];
				bytesRead += (n = stream.read (block));
				if (n == BLOCK_SIZE) return block;

				if (n >= 0)
					return Arrays.copyOf (block, n);
				else return null;
			}
			catch (IOException e) { throw new TransferError (e); }
		}

		/* (non-Javadoc)
		 * @see net.myorb.data.abstractions.SimpleStreamIO.Source#totalRead()
		 */
		public long totalRead ()
		{
			return bytesRead;
		}
		long bytesRead = 0;

	}


	/**
	 * sink for binary content
	 */
	public static class BinarySink implements Sink
	{

		public BinarySink (OutputStream stream)
		{ this.stream = stream; }
		OutputStream stream;

		/* (non-Javadoc)
		 * @see net.myorb.data.abstractions.SimpleStreamIO.Sink#put(byte[])
		 */
		@Override
		public void put (byte[] block) throws TransferError
		{
			try
			{
				if (block == null)
				{ stream.flush (); stream.close (); }
				else stream.write (block);
			}
			catch (IOException e) { throw new TransferError (e); }
		}
		
	}


	/**
	 * source for reader that observes text new line records
	 */
	public static class TextSource implements Source
	{

		/**
		 * the reader will be set post-construction
		 */
		public TextSource () {}

		/**
		 * use file reader for source
		 * @param file stream source as file
		 * @throws FileNotFoundException for missing file
		 */
		public TextSource (File file) throws FileNotFoundException
		{ setBufferedReader (new FileReader (file)); }

		/**
		 * generic reader identifies source stream
		 * @param reader the reader for the source stream
		 */
		public TextSource (Reader reader) { setBufferedReader (reader); }
		public TextSource (InputStream in) { this (new InputStreamReader (in)); }

		/* (non-Javadoc)
		 * @see net.myorb.lotto.support.SimpleStreamIO.Source#get()
		 */
		public byte[] get () throws TransferError { return getFrom (getString ()); }
		public byte[] getFrom (String s) { return s != null ? s.getBytes () : null; }

		/**
		 * @return a line of text based on buffered reader record algorithm
		 * @throws TransferError for IO errors
		 */
		public String getString () throws TransferError
		{
			try
			{
				String line =
				bufferedReader.readLine ();
				if (line == null) return null;
				bytesRead += line.length ();
				return line;
			}
			catch (IOException e) { throw new TransferError (e); }
		}

		/* (non-Javadoc)
		 * @see net.myorb.data.abstractions.SimpleStreamIO.Source#totalRead()
		 */
		public long totalRead ()
		{
			return bytesRead;
		}
		long bytesRead = 0;

		/**
		 * @param reader identify how source is to be read
		 */
		public void setBufferedReader (Reader reader)
		{ this.bufferedReader = new BufferedReader (reader); }
		protected BufferedReader bufferedReader;

	}


	/**
	 * sink for text that observes new line as record delimiter
	 */
	public static class TextSink implements Sink
	{

		/**
		 * effect close of sink
		 * @throws IOException for IO errors
		 */
		public void endOfProcessing () throws IOException
		{
			out.close ();
		}

		/**
		 * @param text put string to sink as record
		 * @throws IOException for IO errors
		 */
		public void putString (String text) throws IOException
		{
			out.println (text);
		}

		/* (non-Javadoc)
		 * @see net.myorb.lotto.support.SimpleStreamIO.Sink#put(byte[])
		 */
		public void put (byte[] block) throws TransferError
		{
			try
			{
				if (block == null) endOfProcessing ();
				else putString (new String (block));
			}
			catch (IOException e) { throw new TransferError (e); }
		}

		/**
		 * @param file the file to use for sink
		 * @throws FileNotFoundException for inability to create file
		 */
		public TextSink (File file) throws FileNotFoundException
		{
			this (new PrintStream (file));
		}

		/**
		 * @param stream the stream to use for sink
		 */
		public TextSink (OutputStream stream)
		{
			this (new PrintStream (stream));
		}

		/**
		 * @param stream the stream to use for sink
		 */
		public TextSink (PrintStream stream)
		{
			out = stream;
		}
		protected PrintStream out;

		/**
		 * sink will be override of put method
		 */
		public TextSink () { out = null; }

	}


	/**
	 * a sink that accepts text line-by-line
	 */
	public interface TextLineSink
	{
		/**
		 * @param line a line of text
		 */
		void putLine (String line);
	}


	/**
	 * @param sink a line-by-line sink object
	 * @param source the text source to be copied
	 * @throws TransferError for any errors
	 */
	public static void copyTo (TextLineSink sink, TextSource source) throws TransferError
	{
		String line ;
		while ((line = source.getString ()) != null)
		{ sink.putLine (line); }
	}


	/**
	 * binary version of the stream processing
	 * @param source the source of content to process
	 * @param sink the sink for content to process
	 * @throws Exception for any errors
	 */
	public static void processStream
	(Source source, Sink sink)
	throws Exception
	{
		/*
		 * 1/8/19 refactor combines processing stream types
		 * and new SimpleStreamManagement encapsulates this
		 * implementation feature set
		 */
		SimpleStreamManagement.processStream (source, sink);
	}


	/**
	 * text version of the stream processing
	 * @param source the source of content to process
	 * @param sink the sink for content to process
	 * @throws IOException for IO errors
	 */
	public static void processTextStream
	(TextSource source, TextSink sink)
	throws Exception
	{
		/*
		 * 1/8/19 refactor combines processing stream types
		 * and new SimpleStreamManagement encapsulates this
		 * implementation feature set
		 */
		SimpleStreamManagement.processStream (source, sink);
	}


	/**
	 * force stream close.
	 * trap exceptions with standard trace.
	 * @param stream the stream object to close
	 */
	public static void forceClosed (Closeable stream)
	{
		try { stream.close (); }
		catch (IOException ioe)
		{
			System.err.println
			("Error attempting stream close: " + ioe.getMessage ());
			//ioe.printStackTrace ();
		}
	}


	/**
	 * stream processing for generic source
	 * @param sinkStream the IO stream to write to
	 * @param fromSource the source of content to process
	 * @throws Exception for any errors
	 */
	public static void sendTo
	(OutputStream sinkStream, Source fromSource)
	throws Exception
	{
		if (fromSource instanceof TextSource)
		{
			// these are now identical...
			//  as long as processTextStream references exist, keep this way
			processTextStream ((TextSource) fromSource, new TextSink (sinkStream));
			//processStream ((TextSource) fromSource, new TextSink (sinkStream));
		}
		else if (fromSource instanceof BinarySource)
		{
			processStream ((BinarySource) fromSource, new BinarySink (sinkStream));
		}
		else
		{
			throw new SimpleStreamManagement.StreamMisMatchException ("Type of source could not be determined");
		}
		forceClosed (sinkStream);
	}


	/**
	 * read text file into sink
	 * @param filepath name of file to read
	 * @param sink the sink for the file contents
	 * @throws Exception for any errors
	 */
	public static void processTextFile
	(String filepath, TextSink sink)
	throws Exception
	{
		processTextStream (getFileSource (filepath), sink);
	}


	/**
	 * read text file into sink
	 * @param file access to file to read
	 * @param sink the sink for the file contents
	 * @throws Exception for any errors
	 */
	public static void processTextFile
	(File file, TextSink sink)
	throws Exception
	{
		processTextStream (getFileSource (file), sink);
	}


	/**
	 * source identified by file path
	 * @param filepath path to file to use for source
	 * @return a source object that reads a file
	 * @throws Exception for any errors
	 */
	public static TextSource
		getFileSource (String filepath) throws Exception
	{ return getFileSource (new File (filepath)); }


	/**
	 * source identified by file access object
	 * @param file the file access object to use for source
	 * @return a source object that reads a file
	 * @throws Exception for any errors
	 */
	public static TextSource getFileSource (File file) throws Exception
	{ return new TextSource (new FileReader (file)); }


	/**
	 * write contents of source to file by path
	 * @param source the source for the file contents
	 * @param filepath name of file to generated
	 * @throws Exception for any errors
	 */
	public static void generateTextFile
	(TextSource source, String filepath) throws Exception
	{ generateTextFile (source, new File (filepath)); }


	/**
	 * write contents of source to file
	 * @param source the source for the file contents
	 * @param file file object to be generated
	 * @throws Exception for any errors
	 */
	public static void generateTextFile
	(TextSource source, File file) throws Exception
	{ processTextStream (source, getFileSink (file)); }


	/**
	 * sink identified by file path
	 * @param filepath path to file to use for sink
	 * @return a sink object that writes to a file
	 * @throws Exception for any errors
	 */
	public static TextSink getFileSink
		(String filepath) throws Exception
	{ return getFileSink (new File (filepath)); }


	/**
	 * sink identified by file access object
	 * @param file the file access object to use for sink
	 * @return a sink object that writes a file
	 * @throws Exception for any errors
	 */
	public static TextSink getFileSink (File file) throws Exception
	{ return new TextSink (new PrintStream (file)); }


	/**
	 * System.out is used for sink
	 * @param source text source to be printed
	 * @throws Exception for any errors
	 */
	public static void printToConsole (TextSource source) throws Exception
	{ processStream (source, new TextSink (System.out)); }


	/**
	 * simple source for single line of text
	 */
	public static class SingleLineSource extends TextSource
	{
		/* (non-Javadoc)
		 * @see net.myorb.data.abstractions.SimpleStreamIO.TextSource#getString()
		 */
		public String getString () throws TransferError
		{ String temp = contents; contents = null; return temp; }
		public SingleLineSource (String contents) { this.contents = contents; }
		String contents;
	}


	/**
	 * prepare a TEMP file
	 * @param name the filename base
	 * @param type the type of file to be implied
	 * @return the newly created file
	 * @throws Exception for errors
	 */
	public static File createTempFile (String name, String type) throws Exception
	{
		File temp = File.createTempFile (name, type);
		temp.deleteOnExit ();
		return temp;
	}


	/**
	 * save (single line) text to file
	 * @param source the contents to be saved
	 * @param file the file to be written
	 * @return the path to the file used
	 * @throws Exception for errors
	 */
	public static String saveToFile (String source, File file) throws Exception
	{
		processTextStream (new SingleLineSource (source), getFileSink (file));
		return file.getAbsolutePath ();
	}


	/**
	 * save text source to temp file
	 * @param source the content stream to be saved
	 * @param name filename to be used in temp file creation
	 * @param type filetype to be used in temp file creation
	 * @return the file that has captured the source stream
	 * @throws Exception for errors
	 */
	public static File saveToTempFile (TextSource source, String name, String type) throws Exception
	{ File f; processTextStream (source, getFileSink (f = createTempFile (name, type))); return f; }
	public static File saveSourceToTempFile (Source source, String name, String type) throws Exception
	{ File f; sendTo (new FileOutputStream (f = createTempFile (name, type)), source); return f; }


	/**
	 * save text line to temp file
	 * @param source the text to be saved
	 * @param name filename to be used in temp file creation
	 * @param type filetype to be used in temp file creation
	 * @return the file that has captured the source stream
	 * @throws Exception for errors
	 */
	public static File saveToTempFile (String source, String name, String type) throws Exception
	{
		return saveToTempFile (new SingleLineSource (source), name, type);
	}


	/**
	 * @param in stream for reading content
	 * @param named the name of the resource
	 * @return a source for the resource
	 */
	public Source getSourceFor (InputStream in, String named)
	{
		if (ContentTypes.isBinaryRequest (named))
		{ return new BinarySource (in); }
		else return new TextSource (in);
	}


	/**
	 * treat string as text source
	 * @param text a block of text to use as source
	 * @return a text source access object
	 */
	public static TextSource sourceFor (String text)
	{
		return new TextSource (new StringReader (text));
	}


}

