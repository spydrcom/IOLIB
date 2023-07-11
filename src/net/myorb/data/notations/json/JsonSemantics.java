
package net.myorb.data.notations.json;

import net.myorb.data.abstractions.SimpleUtilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * representations for JSON structures
 * @author Michael Druckman
 */
public class JsonSemantics extends JsonLowLevel
{


	static
	{
		/*
		 * simple number and text primitives
		 */
		NUMBER = JsonPrimitives.NUMBER;
		STRING = JsonPrimitives.STRING;
	}
	public static final JsonPrimitives NUMBER, STRING;


	/*
	 * primitive methods associated with keywords
	 */

	/**
	 * @return JSON null object
	 */
	public static JsonValue getNull () { return getKeywordFor (NULL); }

	/**
	 * check Value for NULL keyword
	 * @param value the value object to check
	 * @return TRUE = keyword is NULL
	 */
	public static boolean isNull (JsonValue value)
	{
		return value == null || value.indicates (NULL);
	}

	/**
	 * @param value the JSON value to check
	 * @return JSON null for null value
	 */
	public static JsonValue valueOrNull (JsonValue value)
	{
		return value==null? getNull (): value;
	}

	/**
	 * @param value the String object to check
	 * @return JSON null for null value or JsonString otherwise
	 */
	public static JsonValue stringOrNull (String value)
	{
		return value==null? getNull (): new JsonString (value);
	}

	/**
	 * @param value a JsonValue object
	 * @return null or string depending on value
	 */
	public static String getStringOrNull (JsonSemantics.JsonValue value)
	{
		if (isNull (value)) return null;
		JsonString string = SimpleUtilities.verifyClass (value, JsonString.class);
		if (string == null) throw new RuntimeException ("String expected but not found");
		else return string.getContent ();
	}

	/**
	 * @return JSON false object
	 */
	public static JsonValue getFalse () { return getKeywordFor (FALSE); }

	/**
	 * @return JSON true object
	 */
	public static JsonValue getTrue () { return getKeywordFor (TRUE); }

	/**
	 * @param value the boolean value to represent
	 * @return true or false depending on parameter
	 */
	public static JsonValue getBoolean (boolean value)
	{ return value? getTrue (): getFalse (); }

	/**
	 * check Value for boolean TRUE
	 * @param value the value object to check
	 * @return TRUE = keyword is TRUE
	 */
	public static boolean isTrue (JsonValue value)
	{
		return value.indicates (TRUE);
	}


	/*
	 * structures describing literals
	 */

	/**
	 * base class for literals
	 */
	public static abstract class JsonLiteral
		extends JsonConstruct implements JsonValue
	{ JsonLiteral (JsonPrimitives indication) { super (indication); } }

	/**
	 * description of string literal
	 */
	public static class JsonString extends JsonLiteral
	{
		public JsonString (String value)
		{ super (STRING); setFormattedText (value); }
		public String getContent () { return JsonStringRules.escape (value); }
		public ValueTypes getJsonValueType () { return ValueTypes.TEXT; }
		public String toString () { return formattedText; }

		/**
		 * retain both content value and formatted version
		 * @param value the content of the string
		 */
		public void setFormattedText (String value)
		{
			formattedText = JsonObjectFormatter.formatJsonString (this.value = value);
		}
		private String formattedText, value;
	}

	/**
	 * description of numeric literal
	 */
	public static class JsonNumber extends JsonLiteral
	{
		/**
		 * throw exception if expected number not found
		 * @param v a JSON value expected to be a number
		 * @return value as a number
		 */
		public static JsonNumber verify (JsonValue v)
		{
			if (v.getJsonValueType () != ValueTypes.NUMERIC)
			{ throw new RuntimeException ("JSON number expected"); }
			return (JsonNumber) v;
		}

		public JsonNumber (Number value) { super (NUMBER); this.value = value; }
		public ValueTypes getJsonValueType () { return ValueTypes.NUMERIC; }
		public String toString () { return value.toString (); }
		public Number getNumber () { return value; }
		private Number value;
	}


	/*
	 * Java structures describing JSON structures
	 */


	/**
	 * representation of JSON Array
	 */
	public static class JsonArray
		extends ArrayList <JsonValue>
		implements JsonValue
	{
		public JsonArray () {}

		public <N extends Number> JsonArray
		(List <N> numbers) { includingNumbers (numbers); }

		public void addElement (JsonValue element) { add (element); }
		public ValueTypes getJsonValueType () { return ValueTypes.ARRAY; }
		public boolean indicates (JsonPrimitives specifically) { return false; }

		/**
		 * add list of text items
		 * @param items a list of items to be included
		 * @return the Array structure for chain calls
		 * @param <E> the type of list elements
		 */
		public <E> JsonArray includingStrings (List <E> items)
		{
			for (E e : items)
			{ add (new JsonString (e.toString ())); }
			return this;
		}

		/**
		 * add list of numeric items
		 * @param items a list of items to be included
		 * @return the Array structure for chain calls
		 * @param <N> the type of Numbers used
		 */
		public <N extends Number> JsonArray includingNumbers (List <N> items)
		{
			for (N n : items)
			{ add (new JsonNumber (n)); }
			return this;
		}

		/**
		 * throw exception if expected array not found
		 * @param v a JSON value expected to be an array
		 * @return value as an array
		 */
		public static JsonArray verify (JsonValue v)
		{
			if (v.getJsonValueType () != ValueTypes.ARRAY)
			{ throw new RuntimeException ("JSON array expected"); }
			return (JsonArray) v;
		}

		/**
		 * select a segment of elements
		 * @param from the index of the segment start
		 * @param to the index of the segment end
		 * @return the segment as new array
		 */
		public JsonArray section (int from, int to)
		{
			JsonArray A = new JsonArray ();
			A.addAll (this.subList (from, to));
			return A;
		}

		private static final long serialVersionUID = -5991990239117935625L;
	}


	/**
	 * representation of JSON Object
	 */
	public static class JsonObject
		implements JsonValue
	{

		/* (non-Javadoc)
		 * @see net.myorb.data.notations.json.JsonSemantics.JsonValue#getJsonValueType()
		 */
		public ValueTypes getJsonValueType () { return ValueTypes.OBJECT; }

		/* (non-Javadoc)
		 * @see net.myorb.data.notations.json.JsonSemantics.JsonValue#indicates(net.myorb.data.notations.json.JsonPrimitives)
		 */
		public boolean indicates (JsonPrimitives specifically) { return false; }

		/**
		 * add element with named association
		 * @param name the identifier for the member
		 * @param element the element the name maps to
		 */
		public void addMemberNamed (String name, JsonValue element) { members.put (name, element); }

		/**
		 * add the member and associate with specified identifier
		 * @param identifier the identifier to associate with the member in this JSON object
		 * @param element the element mapped to this identifier in this object
		 * @param <I> the type of identifiers used
		 */
		public <I> void addMember (I identifier, JsonValue element) { members.put (identifier.toString (), element); }

		/**
		 * get the member associated with specified identifier
		 * @param identifier the identifier for the member to find in this JSON object
		 * @return the member if found, otherwise null
		 * @param <I> the type of identifiers used
		 */
		public <I> JsonValue getMember (I identifier) { return members.get (identifier.toString ()); }

		/**
		 * copy all members from specified object
		 * @param from the JSON object containing members to copy
		 */
		public void copyMembers (JsonObject from) { members.putAll (from.members); }

		/**
		 * get list of member identifiers
		 * @return the identifier objects present in this JSON object
		 */
		public Object[] getMembers () { return members.keySet ().toArray (); }

		/**
		 * get the contents of the JsonString mapped to member name
		 * @param memberName name of the Object Member
		 * @return the content of the string
		 */
		public String getMemberString (String memberName)
		{
			return getStringOrNull (members.get (memberName));
		}

		/**
		 * get the contents of the 
		 *  JsonString mapped to member identifier
		 * @param identifier the name of the Object Member
		 * @return the content of the string from member
		 * @param <I> the type of identifiers used
		 */
		public <I> String getMemberString (I identifier)
		{
			return getMemberString (identifier.toString ());
		}

		/* (non-Javadoc)
		 * @see java.util.AbstractMap#toString()
		 */
		public String toString ()
		{
			Object[] members = getMembers ();
			if (members.length == 0) return JsonObjectFormatter.EMPTY_OBJECT;
			return new JsonObjectFormatter ().toString (members, this);
		}

		/**
		 * throw exception if expected object not found
		 * @param v a JSON value expected to be an object
		 * @return value as an object
		 */
		public static JsonObject verify (JsonValue v)
		{
			if (v.getJsonValueType () != ValueTypes.OBJECT)
			{ throw new RuntimeException ("JSON object expected"); }
			return (JsonObject) v;
		}

		/**
		 * @param name the name of a member
		 * @return the value associated with that name
		 */
		public JsonValue getMemberCalled (String name)
		{
			return members.get (name);
		}


		/**
		 * @return list of members as specified
		 */
		public String[] getOrderedMemberNames ()
		{ return orderedMembers!=null? orderedMembers: members.keySet ().toArray (new String[]{}); }
		public void setOrderedMembersList (String[] orderedMembers) { this.orderedMembers = orderedMembers; }
		private String[] orderedMembers = null;

		/**
		 * @return Set of names of the members
		 */
		public Set<String> getMemberNames () { return members.keySet (); }
		private HashMap<String,JsonValue> members = new HashMap<>();
	}


}


/**
 * format a JSON object as a flat string
 */
class JsonObjectFormatter extends JsonPrettyPrinter
{

	/**
	 * construct String representation of JsonObject
	 * @param strings the names of the members of the Object
	 * @param object the Object being formatted
	 * @return the text version of the Object
	 */
	public String toString
	(Object[] strings, JsonSemantics.JsonObject object)
	{
		int size = strings.length;
		sequence.add (START_OBJECT).add (" ");
		formatMemberTo (strings[0], object);

		for (int i = 1; i < size; i++)
		{
			sequence.add (COMMA).add (" ");
			formatMemberTo (strings[i], object);
		}

		sequence.add (" ").add (END_OBJECT);
		return sequence.toString ();
	}

	/**
	 * format a member of an Object with NO indentation
	 * @param string the identifier for the member to format
	 * @param object the JSON object containing the member to format
	 */
	public void formatMemberTo (Object string, JsonSemantics.JsonObject object)
	{
		formatMember (string).add (object.getMember (string).toString ());
	}

	/**
	 * @param value the text to format
	 * @return text formatted as JsonString
	 */
	public static String formatJsonString (String value)
	{
		return formatString (value, new StringBuffer ()).toString ();
	}

}

