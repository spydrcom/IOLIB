
package net.myorb.data.notations.json;

import net.myorb.data.abstractions.SimpleStreamIO;

import java.io.PrintStream;

import java.util.ArrayList;
import java.util.List;

/**
 * format JSON objects for display
 * @author Michael Druckman
 */
public class JsonPrettyPrinter
{


	/*
	 * import local copies of JsonPrimitives constants
	 */
	public static final JsonPrimitives COLON = JsonPrimitives.COLON, COMMA = JsonPrimitives.COMMA;
	public static final JsonPrimitives START_OBJECT = JsonPrimitives.OPEN_OBJECT, END_OBJECT = JsonPrimitives.CLOSE_OBJECT;
	public static final JsonPrimitives START_ARRAY = JsonPrimitives.OPEN_ARRAY, END_ARRAY = JsonPrimitives.CLOSE_ARRAY;
	public static final String EMPTY_OBJECT = START_OBJECT.getToken () + END_OBJECT.getToken ();


	/**
	 * allow special formatting of nodes
	 */
	public interface SimpleNodeFormatter
	{
		/**
		 * apply formatting rules to values
		 * @param value a JSON Value to be formatted
		 * @return the formatted text
		 */
		String format (JsonSemantics.JsonValue value);
	}


	public JsonPrettyPrinter () {}
	public JsonPrettyPrinter (SimpleNodeFormatter simpleNodeFormatter)
	{ this.simpleNodeFormatter = simpleNodeFormatter; }


	/**
	 * pair text with nesting level
	 */
	public class NestedLine
	{
		/**
		 * @param line text to capture
		 */
		NestedLine (String line)
		{
			this.content = line;
			this.level = nesting;
		}
		String content;
		int level;
	}
	private List<NestedLine> lines = new ArrayList<NestedLine>();


	/**
	 * maintain nesting level
	 */
	public void increase () { nesting++; }
	public void decrease () { nesting--; }
	private int nesting = 0;


	/**
	 * provide a stream source object for formatting output.
	 * text is returned matching order of line number sequence.
	 * indentation is added to each line per nesting level.
	 */
	public class TextSource extends SimpleStreamIO.TextSource
	{
		/* (non-Javadoc)
		 * @see net.myorb.data.abstractions.SimpleStreamIO.TextSource#getString()
		 */
		public String getString () throws SimpleStreamIO.TransferError
		{
			if (lineNumber == lines.size ()) return null;
			format (lines.get (lineNumber++), buffer);
			String string = buffer.toString ();
			buffer.setLength (0);
			return string;
		}
		private StringBuffer buffer = new StringBuffer ();
		private int lineNumber = 0;
	}
	public SimpleStreamIO.TextSource getTextSource () { return new TextSource (); }


	/**
	 * wrapper for string buffer
	 */
	public class TokenBuffer
	{

		/**
		 * add entry into line buffer and clear contents
		 */
		public void flush ()
		{
			if (length () == 0) return;
			lines.add (new NestedLine (buffer.toString ()));
			clear ();
		}

		/**
		 * @param primitive the token ending the line
		 */
		public void endLineWith (JsonPrimitives primitive)
		{
			add (primitive); flush ();
		}

		public void clear () { buffer.setLength (0); }
		public int length () { return buffer.length (); }

		public StringBuffer getBuffer () { return buffer; }
		public String toString () { return buffer.toString (); }

		public TokenBuffer add (JsonPrimitives p) { add (p.getToken ()); return this; }
		public TokenBuffer add (String text) { buffer.append (text); return this; }

		private StringBuffer buffer = new StringBuffer ();
	}
	protected TokenBuffer sequence = new TokenBuffer ();


	/**
	 * format JSON Object with indentation
	 * @param object a JSON Object to be formatted
	 */
	public void postObject (JsonSemantics.JsonObject object)
	{
		int size;
		Object[] strings = object.getOrderedMemberNames ();

		if ((size = strings.length) == 0)
		{
			sequence.add (START_OBJECT).add (END_OBJECT);
		}
		else
		{
			sequence.flush ();
			sequence.endLineWith (START_OBJECT);
			increase ();

			postMember (strings[0], object);
			for (int i = 1; i < size; i++)
			{
				sequence.endLineWith (COMMA);
				postMember (strings[i], object);
			}

			sequence.flush (); decrease ();
			sequence.add (END_OBJECT);
		}
	}


	/**
	 * format a member of an Object with indentation
	 * @param string the identifier for the member to format
	 * @param object the JSON object containing the member to format
	 */
	public void postMember (Object string, JsonSemantics.JsonObject object)
	{
		formatMember (string); post (object.getMember (string));
	}


	/**
	 * @param identifier the identifier for the member to format
	 * @return the identifier portion of the member declaration
	 */
	public TokenBuffer formatMember (Object identifier)
	{
		return formatString (identifier).add (COLON).add (" ");
	}


	/**
	 * @param text the text to be included in the string
	 * @return the buffer object for chain calls
	 */
	public TokenBuffer formatString (Object text)
	{
		formatString (text, sequence.getBuffer ()); return sequence;
	}


	/**
	 * @param text the contents of the string to format
	 * @param buffer a StringBuffer collecting text
	 * @return the buffer object for chain calls
	 */
	public static StringBuffer
		formatString (Object text, StringBuffer buffer)
	{
		return buffer.append ('"').append (text).append ('"');
	}


	/**
	 * @param array a JSON Array to be formatted
	 */
	public void postArray (JsonSemantics.JsonArray array)
	{
		int size;
		if ((size = array.size ()) == 0)
		{
			sequence.add (START_ARRAY).add (END_ARRAY);
		}
		else
		{
			sequence.flush ();
			sequence.endLineWith (START_ARRAY);
			increase ();

			int lastElement = size - 1;
			for (int i = 0; i < lastElement; i++)
			{
				post (array.get (i)); sequence.endLineWith (COMMA);
			}
			post (array.get (lastElement));

			sequence.flush (); decrease ();
			sequence.add (END_ARRAY);
		}
	}


	/**
	 * @param value a JSON Value to be formatted
	 */
	public void post (JsonSemantics.JsonValue value)
	{
		if (value == null)
			sequence.add (JsonSemantics.NULL);
		else
		{
			switch (value.getJsonValueType ())
			{
				case OBJECT:	postObject ((JsonSemantics.JsonObject) value); break;
				case ARRAY:		postArray ((JsonSemantics.JsonArray) value); break;
				default:		formatSimpleNode (value); break;
			}
		}
	}


	/**
	 * @param value the JSON value to be formatted
	 */
	public void formatSimpleNode (JsonSemantics.JsonValue value)
	{
		if (simpleNodeFormatter == null) sequence.add (value.toString ());
		else sequence.add (simpleNodeFormatter.format (value));
	}
	private SimpleNodeFormatter simpleNodeFormatter = null;


	/**
	 * main entry point
	 * @param value the JSON root value to format
	 * @return THIS PrettyPrinter instance for cascade calls
	 */
	public JsonPrettyPrinter format (JsonSemantics.JsonValue value)
	{
		post (value);
		sequence.flush ();
		return this;
	}


	/**
	 * use SimpleStreamIO as output object
	 * @param out a SimpleStreamIO TextSink object for output
	 * @throws Exception for IO errors
	 */
	public void sinkTo (SimpleStreamIO.TextSink out) throws Exception
	{
		SimpleStreamIO.processTextStream (getTextSource (), out);
	}


	/**
	 * use java.io stream for output
	 * @param out a PrintStream to use for output
	 */
	public void sendTo (PrintStream out)
	{
		StringBuffer
		buffer = new StringBuffer ();
		for (NestedLine line : lines)
		{
			format (line, buffer);
			out.println (buffer);
			buffer.setLength (0);
		}
	}
	public static final String WS =			// white space to use for indentation
			"                                                                                                                       " +
			"                                                                                                                       " +
			"                                                                                                                       " +
			"                                                                                                                       ";
	public void format (NestedLine line, StringBuffer buffer)
	{
		int spaceCount = 3 * line.level, spaceAvailable = WS.length ();
		while (spaceCount > spaceAvailable) { buffer.append (WS); spaceCount -= spaceAvailable; }
		if ((spaceCount > 0)) buffer.append (WS, 0, spaceCount);
		buffer.append (line.content);
	}


	/**
	 * @param value a JSON Value to be formatted
	 * @param out a PrintStream to use for output
	 * @throws Exception for any errors
	 */
	public static void sendTo
		(JsonSemantics.JsonValue value, PrintStream out)
	throws Exception
	{
		new JsonPrettyPrinter ().format (value).sendTo (out);
	}
	public static void sendTo
		(JsonSemantics.JsonValue value, PrintStream out, SimpleNodeFormatter using)
	throws Exception
	{
		new JsonPrettyPrinter (using).format (value).sendTo (out);
	}


	/**
	 * @param value a JSON Value to be formatted
	 * @param out a SimpleStreamIO TextSink object for output
	 * @throws Exception for any errors
	 */
	public static void sinkTo
		(JsonSemantics.JsonValue value, SimpleStreamIO.TextSink out)
	throws Exception
	{
		new JsonPrettyPrinter ().format (value).sinkTo (out);
	}
	public static void sinkTo
		(
			JsonSemantics.JsonValue value,
			SimpleStreamIO.TextSink out,
			SimpleNodeFormatter using
		)
	throws Exception
	{
		new JsonPrettyPrinter (using).format (value).sinkTo (out);
	}


}

