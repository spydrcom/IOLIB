
package net.myorb.data.notations.json;

import net.myorb.data.notations.json.JsonTokenParser.Unexpected;

import java.util.HashMap;
import java.util.Map;

/**
 * definition of the root classes of JSON Value types.
 *  the concepts of JSON keywords and delimiters are defined.
 *  static mappings for keyword and delimiter tokens are provided.
 * @author Michael Druckman
 */
public class JsonLowLevel
{


	/**
	 * root interface for JSON values
	 */
	public interface JsonValue
	{

		/**
		 * the recognized types of JSON Values
		 */
		public enum ValueTypes
		{KEYWORD, NUMERIC, TEXT, ARRAY, OBJECT}

		/**
		 * @param specifically the primitive in question
		 * @return TRUE = specifically IS indicated
		 */
		boolean indicates (JsonPrimitives specifically);

		/**
		 * @return type of this value
		 */
		ValueTypes getJsonValueType ();

	}


	/**
	 * the common implementation for constructs
	 */
	public static class JsonConstruct
	{

		/**
		 * @param indication the indication associated with this construct
		 */
		JsonConstruct (JsonPrimitives indication) { this.indication = indication; }

		/**
		 * @return the indication associated with this construct (as specified in constructor)
		 */
		public JsonPrimitives getIndication () { return indication; }

		/**
		 * does this construct specifically indicate identified primitive
		 * @param specifically the primitive in question
		 * @return TRUE = specifically IS indicated
		 */
		public boolean indicates (JsonPrimitives specifically)
		{ return getIndication () == specifically; }
		protected JsonPrimitives indication;

	}


	/**
	 * common base for keywords and delimiters
	 */
	public static class JsonConstant extends JsonConstruct
	{
		/**
		 * @param primitive indication associated with this construct
		 */
		JsonConstant (JsonPrimitives primitive)
		{ super (primitive); this.name = primitive.getToken (); }

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		public String toString () { return name; }
		private String name;
	}


	/*
	 * definition of keyword
	 */

	/**
	 * a class holding keyword data
	 */
	public static class JsonKeyword extends JsonConstant implements JsonValue
	{
		JsonKeyword (JsonPrimitives primitive) { super (primitive); }
		public ValueTypes getJsonValueType ()
		{ return ValueTypes.KEYWORD; }
	}

	/**
	 * map keyword to its class
	 * @param primitive a primitive for a keyword
	 */
	public static void addKeyword (JsonPrimitives primitive)
	{ keywords.put (primitive.getToken (), new JsonKeyword (primitive)); }
	public static final Map<String,JsonKeyword> keywords = new HashMap<> ();
	public static JsonKeyword getKeywordFor (JsonPrimitives primitive)
	{ return keywords.get (primitive.getToken ()); }

	static
	{
		/*
		 * build map of keywords
		 */
		addKeyword (NULL = JsonPrimitives.NULL);
		addKeyword (FALSE = JsonPrimitives.FALSE);
		addKeyword (TRUE = JsonPrimitives.TRUE);
	}
	public static final JsonPrimitives NULL, TRUE, FALSE;

	/**
	 * @param token the text of a token to look for
	 * @return a JsonKeyword representation of the construct
	 * @throws Unexpected for keyword not recognized
	 */
	public static JsonKeyword getKeywordFor (String token) throws Unexpected
	{
		JsonKeyword keyword = keywords.get (token);
		if (keyword == null) throw new Unexpected ("Keyword not recognized: " + token);
		return keyword;
	}


	/*
	 * definition of delimiter
	 */

	/**
	 * a class holding delimiter data
	 */
	public static class JsonDelimiter extends JsonConstant
	{
		JsonDelimiter (JsonPrimitives primitive)
		{ super (primitive); }
	}

	/**
	 * map delimiter to its class
	 * @param primitive a primitive for a delimiter
	 */
	public static void addDelimiter
	(JsonPrimitives primitive) { delimiters.put (primitive.getToken (), new JsonDelimiter (primitive)); }
	public static final Map<String,JsonDelimiter> delimiters = new HashMap<>();

	static
	{
		/*
		 * build map of delimiters
		 */
		addDelimiter (JsonPrimitives.COMMA);
		addDelimiter (JsonPrimitives.OPEN_OBJECT);
		addDelimiter (JsonPrimitives.CLOSE_OBJECT);
		addDelimiter (JsonPrimitives.CLOSE_ARRAY);
		addDelimiter (JsonPrimitives.OPEN_ARRAY);
		addDelimiter (JsonPrimitives.COLON);
	}

	/**
	 * @param token the text of a token to look for
	 * @return a JsonDelimiter representation of the construct
	 * @throws Unexpected for delimiter not recognized
	 */
	public static JsonDelimiter getDelimiterFor (String token) throws Unexpected
	{
		JsonDelimiter delimiter = delimiters.get (token);
		if (delimiter == null) throw new Unexpected ("Delimiter not recognized: " + token);
		return delimiter;
	}


}


