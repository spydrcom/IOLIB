
package net.myorb.data.conventional;

import net.myorb.data.abstractions.SimpleDateManager;
import net.myorb.data.abstractions.SimpleStreamIO;

import java.util.HashMap;
import java.util.Date;

import java.io.IOException;
import java.io.File;

/**
 * generic Separated Value data format
 * @author Michael Druckman
 */
public class CharacterDelimited extends SimpleStreamIO
{

	/**
	 * make parser available to higher layer(s)
	 */
	public interface CommonProcessing
	{
		/**
		 * @param file the file to parse
		 * @param processor the processor for the columns of the record
		 * @throws Exception for any errors
		 */
		void parse (File file, Processor processor) throws Exception;

		/**
		 * @return access to CharacterDelimited layer parser
		 */
		Parser getParser ();
	}

	/**
	 * read values with assigned data type
	 */
	public interface Reader
	{
		/**
		 * @param forName name of the column
		 * @return a text string
		 */
		String getText (String forName);

		/**
		 * @param forName name of the column
		 * @return a Number a lang.Number object
		 */
		Number getNumber (String forName);

		/**
		 * @param forName name of the column
		 * @return a Date in the util.Date representation
		 */
		Date getDate (String forName);

		/**
		 * @param forName name of the column
		 * @return a Date in text representation
		 */
		String getFormattedDate (String forName);
	}

	/**
	 * implementer becomes enabled to process parsed data
	 */
	public interface Processor
	{
		/**
		 * @param reader a column data parser
		 */
		void process (Reader reader);
	}

	/**
	 * define date formats specific to these types
	 */
	public static class DateFormat extends SimpleDateManager
	{
		/**
		 * @param order specification of order
		 */
		public DateFormat (Order order) { super (order); }

		/**
		 * default order is MDY
		 */
		public DateFormat () {}
	}

	/**
	 * data parsed from stream will be passed to this parser
	 */
	public static class Parser extends TextSink implements Reader
	{

		/**
		 * describe processing for types of rows of file
		 */
		interface RowProcessor { void processRow (); }

		/* (non-Javadoc)
		 * @see net.myorb.data.abstractions.SimpleStreamIO.TextSink#endOfProcessing()
		 */
		public void endOfProcessing () throws IOException {}

		/**
		 * @param processor a record processor for the column data
		 * @param dateManager a manager for the date types in the file
		 */
		public Parser (Processor processor, DateFormat dateManager)
		{
			this.currentProcessor = new TitleProcessor ();
			this.recordProcessor = new RecordProcessor (this, processor);
			this.dateManager = dateManager==null? new DateFormat (): dateManager;
		}
		protected RowProcessor recordProcessor = null, currentProcessor = null;
		protected DateFormat dateManager = null;

		/* (non-Javadoc)
		 * @see net.myorb.data.conventional.CSV.Reader#getText(java.lang.String)
		 */
		public String getText (String forName)
		{
			Integer col;
			if ((col = titles.get (forName)) == null) return null;
			return substituteEscape (row[col]);
		}

		/* (non-Javadoc)
		 * @see net.myorb.data.conventional.CSV.Reader#getNumber(java.lang.String)
		 */
		public Number getNumber (String forName)
		{
			Integer col;
			if ((col = titles.get (forName)) == null) return null;
			return Double.parseDouble (getCol (col).replaceAll (",", ""));
		}

		/* (non-Javadoc)
		 * @see net.myorb.data.conventional.CharacterDelimited.Reader#getDate(java.lang.String)
		 */
		public Date getDate (String forName)
		{
			Integer col;
			if ((col = titles.get (forName)) == null) return null;
			return parseDate (getCol (col));
		}

		/* (non-Javadoc)
		 * @see net.myorb.data.conventional.CharacterDelimited.Reader#getFormattedDate(java.lang.String)
		 */
		public String getFormattedDate (String forName)
		{
			return dateManager.format (getDate (forName));
		}

		/**
		 * @param text the source text for the date
		 * @return the util.Date representation
		 */
		public Date parseDate (String text) { return dateManager.parse (text); }

		/**
		 * @param col column number
		 * @return current column of row
		 */
		public String getCol (int col) { return row[col]; }

		/**
		 * @return the character used to delimit columns
		 */
		public String getDelimiter () { return " "; }

		/**
		 * @return the unicode digits for the delimiter character
		 */
		public String getDelimiterCode () { return "0020"; }
		public String getDelimiterEscape () { return "&" + getDelimiterCode (); }
		public String substituteEscape (String text)
		{
			return text.replaceAll (getDelimiterEscape (), getDelimiter ());
		}

		/**
		 * @param text text line to be parsed
		 */
		public void parseRow (String text) { row = text.split (getDelimiter ()); }

		/* (non-Javadoc)
		 * @see net.myorb.data.abstractions.SimpleStreamIO.TextSink#putString(java.lang.String)
		 */
		public void putString (String text) throws IOException
		{
			if (!text.isEmpty ()) { parseRow (text); currentProcessor.processRow (); }
		}

		/**
		 * processor just for the titles record
		 */
		public class TitleProcessor implements RowProcessor
		{
			/* (non-Javadoc)
			 * @see net.myorb.data.conventional.CharacterDelimited.Parser.RowProcessor#processRow()
			 */
			public void processRow ()
			{
				// generate map of titled columns
				for (int i=0; i<row.length; i++) { titles.put (row[i], i); }

				// keep array of titles read from file for use by extended classes
				headers = titles.keySet ().toArray (new String[]{});

				// change processor for subsequent records
				currentProcessor = recordProcessor;
			}
		}

		/**
		 * processor for records following the titles
		 */
		public static class RecordProcessor implements RowProcessor
		{
			RecordProcessor (Reader reader, Processor processor)
			{ this.reader = reader; this.processor = processor; }

			/* (non-Javadoc)
			 * @see net.myorb.data.conventional.CharacterDelimited.Parser.RowProcessor#processRow()
			 */
			public void processRow () { processor.process (reader); }
			Reader reader; Processor processor;
		}

		protected String[] headers;
		public String[] getHeaders () { return headers; }

		protected HashMap <String, Integer> titles = new HashMap <String, Integer> ();
		protected String[] row;
	}

	/**
	 * @param parser the parser being used
	 * @param reader the reader for the records being built
	 * @param intoMap the map to be structured by the reader
	 */
	public static void mapRecord
	(Parser parser, Reader reader, HashMap <String, String> intoMap) { mapRecord (parser.getHeaders (), reader, intoMap); }
	public static void mapRecord (String[] headers, Reader reader, HashMap <String, String> intoMap)
	{ for (String header : headers) { intoMap.put (header, reader.getText (header)); } }

	/**
	 * @param file a WSV (Whitespace Separated Values) file to be parsed
	 * @param processor the processor for the columns of the record
	 * @throws Exception for any IO errors
	 */
	public void parse (File file, Processor processor) throws Exception
	{
		parse (file, new Parser (processor, dateManager));
	}
	public void parse (File file, TextSink sink) throws Exception
	{
		processTextStream (new TextSource (file), sink);
	}

	public CharacterDelimited () {}
	public CharacterDelimited (DateFormat dateManager) { this.dateManager = dateManager; }
	protected DateFormat dateManager = null;

}
