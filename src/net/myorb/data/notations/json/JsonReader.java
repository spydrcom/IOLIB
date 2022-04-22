
package net.myorb.data.notations.json;

import net.myorb.data.notations.json.JsonTokenParser.Unexpected;

import net.myorb.data.abstractions.CommonCommandParser.TokenDescriptor;
import net.myorb.data.abstractions.SimpleStreamIO;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

/**
 * text reader that parses JSON source
 * @author Michael Druckman
 */
public class JsonReader extends SimpleStreamIO
{


	/**
	 * SimpleStreamIO text sink for token processing
	 */
	public static class TokenSink extends TextSink
	{

		/* (non-Javadoc)
		 * @see net.myorb.data.abstractions.SimpleStreamIO.TextSink#endOfProcessing()
		 */
		public void endOfProcessing () throws IOException {}

		/* (non-Javadoc)
		 * @see net.myorb.data.abstractions.SimpleStreamIO.TextSink#putString(java.lang.String)
		 */
		public void putString (String text) throws IOException
		{
			StringBuffer textBuffer = new StringBuffer (text);
			tokenBuffer.addAll (JsonTokenParser.parse (textBuffer));
		}

		private List<TokenDescriptor> tokenBuffer = new ArrayList<>();
	}


	/**
	 * parse a full text source 
	 * @param source the SimpleStreamIO text source to parse
	 * @return a list of token descriptors produced by parser
	 * @throws Exception for IO errors
	 */
	public static List<TokenDescriptor> parse
		(TextSource source)
	throws Exception
	{
		TokenSink sink = new TokenSink ();
		processTextStream (source, sink);
		return sink.tokenBuffer;
	}


	/**
	 * build JSON constructs from tokens
	 * @param tokens the list of parsed tokens
	 * @param constructs a list of constructs being built
	 * @throws Unexpected for unexpected token sequences
	 */
	public static void convert
		(List<TokenDescriptor> tokens, List<JsonSemantics.JsonConstruct> constructs)
	throws Unexpected
	{
		JsonSemantics.JsonConstruct newConstruct = null;

		for (TokenDescriptor t : tokens)
		{
			switch (t.getTokenType ())
			{
				case IDN:
					newConstruct = JsonSemantics.getKeywordFor (t.getTokenImage ());
				break;

				case QOT:
					newConstruct = new JsonSemantics.JsonString (JsonTokenParser.stripQuotes (t.getTokenImage ()));
				break;

				case OPR:
					newConstruct = JsonSemantics.getDelimiterFor (t.getTokenImage ());
				break;

				default:
					newConstruct = new JsonSemantics.JsonNumber (t.getTokenValue ());
				break;
			}
			constructs.add (newConstruct);
		}
	}


	/**
	 * a buffer for collection of JSON constructs as they are parsed
	 */
	public static class ConstructBuffer
	{

		/**
		 * allocate list of constructs
		 */
		ConstructBuffer ()
		{
			constructs = new ArrayList<JsonSemantics.JsonConstruct>();
			nextConstruct = 0; anticipationMet = false;
		}
		protected List<JsonSemantics.JsonConstruct> constructs;
		protected int nextConstruct;

		/**
		 * step to next construct in list checking for end of buffer
		 * @return next construct from buffer incrementing index
		 */
		public JsonSemantics.JsonConstruct getNextConstruct ()
		{
			if (nextConstruct >= constructs.size ())
			{ throw new RuntimeException ("Attempt to read past end of source"); }
			return constructs.get (nextConstruct++);
		}

		/**
		 * perform getNextConstruct and set anticipationMet flag
		 * @param anticipating an indication that may or may not be next in sequence
		 * @return the next construct
		 */
		public JsonSemantics.JsonConstruct getNextConstruct (JsonPrimitives anticipating)
		{
			JsonSemantics.JsonConstruct construct = getNextConstruct ();
			anticipationMet = construct.getIndication () == anticipating;
			return construct;
		}

		/**
		 * @return the indication specified from anticipating was present in last get
		 */
		public boolean anticipationWasMet () { return anticipationMet; }
		protected boolean anticipationMet;

		/**
		 * @param source the SimpleStreamIO text source to read from
		 * @return the construct buffer build from token list read
		 * @throws Unexpected for token parser errors
		 * @throws IOException for IO errors
		 */
		public static ConstructBuffer
				process (TextSource source)
		throws Unexpected, Exception
		{
			ConstructBuffer buffer = new ConstructBuffer ();
			convert (parse (source), buffer.constructs);
			return buffer;
		}

	}


	/**
	 * read construct from buffer
	 *  and check for expected indication
	 * @param indication the indication expected
	 * @param buffer the buffer of constructs being read
	 * @return the construct being checked for appropriate indication value
	 * @throws Unexpected for specified indication not found
	 */
	public static JsonSemantics.JsonConstruct expect
		(JsonPrimitives indication, ConstructBuffer buffer)
	throws Unexpected
	{
		return expect (indication, buffer.getNextConstruct ());
	}


	/**
	 * raise issue if current construct
	 *  fails to match expected indication
	 * @param indication the indication expected
	 * @param from the construct object being checked
	 * @return the same construct object to allow for call chain
	 * @throws Unexpected for specified indication not found
	 */
	public static JsonSemantics.JsonConstruct expect
		(JsonPrimitives indication, JsonSemantics.JsonConstruct from)
	throws Unexpected
	{
		if (from.getIndication () != indication)
		{ throw new Unexpected (from); }
		return from;
	}


	/**
	 * expect a JSON Value
	 * @param from the construct object being checked
	 * @return the same construct object cast to a JSON Value object
	 * @throws Unexpected for constructs not of JsonValue type
	 */
	public static JsonSemantics.JsonValue expectValue
		(JsonSemantics.JsonConstruct from)
	throws Unexpected
	{
		if (from instanceof JsonSemantics.JsonValue)
			return (JsonSemantics.JsonValue) from;
		throw new Unexpected (from);
	}


	/**
	 * @param next the current construct
	 * @param buffer the construct buffer at current construct point
	 * @return the next construct if expectation met
	 * @throws Unexpected for comma not found
	 */
	public static JsonSemantics.JsonConstruct expectComma
		(JsonSemantics.JsonConstruct next, ConstructBuffer buffer)
	throws Unexpected
	{
		expect (COMMA, next);
		return buffer.getNextConstruct ();
	}
	public static final JsonPrimitives COMMA = JsonPrimitives.COMMA;


	/**
	 * pull constructs from buffer to build structure
	 */
	public static abstract class StructureBuilder
	{

		/**
		 * call anticipate mechanism with end-of-structure primitive
		 * @param buffer the buffer of constructs
		 * @return next construct from buffer
		 */
		abstract JsonSemantics.JsonConstruct anticipateStructureEnd (ConstructBuffer buffer);

		/**
		 * add an element to the structure
		 * @param current the current construct being processed
		 * @param buffer the buffer of constructs being processed
		 * @throws Unexpected for construct out of sequence
		 */
		abstract void addElement (JsonSemantics.JsonConstruct current, ConstructBuffer buffer) throws Unexpected;

		/**
		 * anticipate an indication in next construct
		 * @param indication the indication being anticipated
		 * @param from the buffer being parsed
		 * @return the next construct found
		 */
		public static JsonSemantics.JsonConstruct anticipate
				(JsonPrimitives indication, ConstructBuffer from)
		{
			JsonSemantics.JsonConstruct next = from.getNextConstruct (indication);
			if (from.anticipationWasMet ()) return null;
			else return next;
		}

		/**
		 * either anticipation Was Met
		 *  on structure end or comma must be found
		 * @param next the current examined construct
		 * @param buffer the buffer holding the construct sequence
		 * @return the next construct or NULL if anticipation Was Met
		 * @throws Unexpected for failure on comma expectation
		 */
		public static JsonSemantics.JsonConstruct processEndLine
				(JsonSemantics.JsonConstruct next, ConstructBuffer buffer)
		throws Unexpected
		{
			if (buffer.anticipationWasMet ()) return null;
			else return expectComma (next, buffer);
		}

		/**
		 * pull sequence of constructs belonging to structure
		 * @param buffer the sequence of constructs being processed
		 * @throws Unexpected for tokens out of proper sequence
		 */
		public void processStructure (ConstructBuffer buffer) throws Unexpected
		{
			for
				(
					JsonSemantics.JsonConstruct next = anticipateStructureEnd (buffer);
					next != null; next = processEndLine (anticipateStructureEnd (buffer), buffer)
				)
			{
				addElement (next, buffer);
			}
		}

	}


	/**
	 * build an array out of construct buffer
	 */
	public static class ArrayBuilder extends StructureBuilder
	{

		/* (non-Javadoc)
		 * @see net.myorb.data.notations.json.JsonReader.StructureBuilder#anticipateStructureEnd(net.myorb.data.notations.json.JsonReader.ConstructBuffer)
		 */
		JsonSemantics.JsonConstruct
			anticipateStructureEnd (ConstructBuffer from)
		{ return anticipate (JsonPrimitives.CLOSE_ARRAY, from); }

		/* (non-Javadoc)
		 * @see net.myorb.data.notations.json.JsonReader.StructureBuilder#addElement(net.myorb.data.notations.json.JsonSemantics.JsonConstruct, net.myorb.data.notations.json.JsonReader.ConstructBuffer)
		 */
		void addElement (JsonSemantics.JsonConstruct current, ConstructBuffer buffer) throws Unexpected
		{
			array.addElement (getElement (buffer, current));
		}
		private JsonSemantics.JsonArray array = new JsonSemantics.JsonArray ();

		JsonSemantics.JsonArray processArray (ConstructBuffer buffer) throws Unexpected
		{ processStructure (buffer); return array; }

	}

	/**
	 * parse representation of JSON Array
	 * @param buffer the buffer being parsed
	 * @return the new JSON Array built by parser
	 * @throws Unexpected for unexpected token sequences
	 */
	public static JsonSemantics.JsonValue
		getArray (ConstructBuffer buffer) throws Unexpected
	{ return new ArrayBuilder ().processArray (buffer); }


	/**
	 * build an object out of construct buffer
	 */
	public static class ObjectBuilder extends StructureBuilder
	{

		/**
		 * @param construct the JSON construct holding the member name string
		 * @return the content of the string which names the parsed member
		 * @throws Unexpected for construct failing to contain string
		 */
		String getMemberName (JsonSemantics.JsonConstruct construct) throws Unexpected
		{ return ((JsonSemantics.JsonString) expect (JsonPrimitives.STRING, construct)).getContent (); }

		/* (non-Javadoc)
		 * @see net.myorb.data.notations.json.JsonReader.StructureBuilder#anticipateStructureEnd(net.myorb.data.notations.json.JsonReader.ConstructBuffer)
		 */
		JsonSemantics.JsonConstruct anticipateStructureEnd (ConstructBuffer from) { return anticipate (JsonPrimitives.CLOSE_OBJECT, from); }

		/* (non-Javadoc)
		 * @see net.myorb.data.notations.json.JsonReader.StructureBuilder#addElement(net.myorb.data.notations.json.JsonSemantics.JsonConstruct, net.myorb.data.notations.json.JsonReader.ConstructBuffer)
		 */
		void addElement (JsonSemantics.JsonConstruct current, ConstructBuffer buffer) throws Unexpected
		{
			String name = getMemberName (current); expect (JsonPrimitives.COLON, buffer);
			JsonSemantics.JsonConstruct elementStart = buffer.getNextConstruct ();
			object.addMember (name, getElement (buffer, elementStart));
		}
		private JsonSemantics.JsonObject object = new JsonSemantics.JsonObject ();

		JsonSemantics.JsonObject processObject (ConstructBuffer buffer) throws Unexpected
		{ processStructure (buffer); return object; }

	}

	/**
	 * parse representation of JSON Object
	 * @param buffer the buffer being parsed
	 * @return the new JSON Object built by parser
	 * @throws Unexpected for unexpected token sequences
	 */
	public static JsonSemantics.JsonValue
		getObject (ConstructBuffer buffer) throws Unexpected
	{ return new ObjectBuilder ().processObject (buffer); }


	/**
	 * read next JSON Element
	 * @param buffer the buffer being parsed
	 * @param next the construct starting the sequence
	 * @return a JSON Value object newly parsed from the buffer
	 * @throws Unexpected for unexpected token sequences
	 */
	public static JsonSemantics.JsonValue getElement
		(ConstructBuffer buffer, JsonSemantics.JsonConstruct next)
	throws Unexpected
	{
		if (next instanceof JsonSemantics.JsonValue)
		{ return (JsonSemantics.JsonValue) next; }

		switch (next.getIndication ())
		{
			case OPEN_OBJECT:	return getObject (buffer);
			case OPEN_ARRAY:	return getArray (buffer);
			default:			break;
		}
		throw new Unexpected (next);
	}


	/**
	 * read next JSON Element
	 * @param buffer the buffer being parsed
	 * @return the JSON Element read from the construct buffer
	 * @throws Unexpected for unexpected token sequences
	 */
	public static JsonSemantics.JsonValue
			getElement (ConstructBuffer buffer)
	throws Unexpected
	{
		JsonSemantics.JsonConstruct
			next = buffer.getNextConstruct ();
		return getElement (buffer, next);
	}


	/**
	 * read source to produce JSON value
	 * @param source a text source object to read from
	 * @return the root node of the JSON constructs read from source
	 * @throws Unexpected for token parser errors
	 * @throws Exception for IO errors
	 */
	public static JsonSemantics.JsonValue
		readFrom (TextSource source)
	throws Unexpected, Exception
	{
		return getElement (ConstructBuffer.process (source));
	}


	/**
	 * unit test reading file
	 * @param args not used for test
	 * @throws Exception for all errors
	 */
	public static void main (String[] args) throws Exception
	{
		JsonSemantics.JsonValue v;
		TextSource source = getFileSource ("data/expr.json");
		//ConstructBuffer buffer = ConstructBuffer.process (getFileSource ("data/test.json"));
		//ConstructBuffer buffer = ConstructBuffer.process (getFileSource ("data/expr.json"));
		//System.out.println (v = getElement (buffer));
		System.out.println (v = readFrom (source));
		JsonPrettyPrinter.sendTo (v, System.out);
	}


}

