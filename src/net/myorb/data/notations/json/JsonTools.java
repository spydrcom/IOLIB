
package net.myorb.data.notations.json;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

/**
 * tools for JSON structure manipulation
 * @author Michael Druckman
 */ 
public class JsonTools
{

	/**
	 * @param numbers list of generic numbers
	 * @return a JSON array of values
	 * @param <T> data type manager
 	 */
	public static <T> JsonSemantics.JsonArray toJsonArrayUsing (List <T> numbers)
	{
		List <Double> list = new ArrayList <Double> ();
		for (T n : numbers) { list.add (Double.parseDouble (n.toString ())); }
		return new JsonSemantics.JsonArray (list);
	}
	public static <T> JsonSemantics.JsonArray toJsonArrayUsing (T[] numbers)
	{
		List <Double> list = new ArrayList <Double> ();
		for (T n : numbers) { list.add (Double.parseDouble (n.toString ())); }
		return new JsonSemantics.JsonArray (list);
	}

	/**
	 * @param numbers array of Double objects
	 * @return JSON array
	 */
	public static JsonSemantics.JsonArray toJsonArray (Double [] numbers)
	{
		List<Number> numberList = new ArrayList<Number>();
		for (Number number : numbers) numberList.add (number);
		return new JsonSemantics.JsonArray (numberList);
	}

	/**
	 * @param numbers array of Number objects
	 * @return JSON array
	 */
	public static JsonSemantics.JsonArray toJsonArray (Number [] numbers)
	{
		List<Number> numberList = new ArrayList<Number>();
		for (Number number : numbers) numberList.add (number);
		return new JsonSemantics.JsonArray (numberList);
	}

	/**
	 * @param value a JSON value expected to be array of String
	 * @return the String array
	 */
	public static String[] toStringArray (JsonSemantics.JsonValue value)
	{
		JsonSemantics.JsonArray array = toArray (value);
		String[] items = new String[array.size ()];
		for (int i = 0; i < items.length; i++)
		{
			JsonSemantics.JsonString
				item = (JsonSemantics.JsonString) array.get (i);
			items[i] = item.getContent ();
		}
		return items;
	}

	/**
	 * @param value a JSON value expected to be array of Number
	 * @return a list of Number objects
	 */
	public static List <Number> toNumbers (JsonSemantics.JsonValue value)
	{
		List <Number> numbers = new ArrayList <Number> ();
		JsonSemantics.JsonArray array = toArray (value);
		for (int i = 0; i < array.size (); i++)
		{
			JsonSemantics.JsonNumber
				item = (JsonSemantics.JsonNumber) array.get (i);
			numbers.add (item.getNumber ());
		}
		return numbers;
	}

	/**
	 * @param value a JSON value expected to be array of Number
	 * @return a list of Double objects
	 */
	public static List <Double> toFloatList (JsonSemantics.JsonValue value)
	{
		List <Double> numbers = new ArrayList <Double> ();
		JsonSemantics.JsonArray array = toArray (value);
		for (int i = 0; i < array.size (); i++)
		{
			JsonSemantics.JsonNumber
				item = (JsonSemantics.JsonNumber) array.get (i);
			numbers.add (item.getNumber ().doubleValue ());
		}
		return numbers;
	}

	/**
	 * @param value a JSON value expected to be array of Number
	 * @return the Number array
	 */
	public static Number[] toNumberArray (JsonSemantics.JsonValue value)
	{
		JsonSemantics.JsonArray array = toArray (value);
		Number[] items = new Number[array.size ()];
		for (int i = 0; i < items.length; i++)
		{
			JsonSemantics.JsonNumber
				item = (JsonSemantics.JsonNumber) array.get (i);
			items[i] = item.getNumber ();
		}
		return items;
	}

	/**
	 * @param value a JSON value expected to be Array
	 * @return the JSON array or exception if array not found
	 */
	public static JsonSemantics.JsonArray toArray (JsonSemantics.JsonValue value)
	{
		if (value.getJsonValueType () != JsonLowLevel.JsonValue.ValueTypes.ARRAY)
		{ throw new RuntimeException ("Array Expected"); }
		return (JsonSemantics.JsonArray) value;
	}

	/**
	 * take a member from an object and treat as an array
	 * @param descriptor a JSON object expected to be holding a field
	 * @param called the name of the member to be treated as an array
	 * @return the array in the member called as specified
	 */
	public static JsonSemantics.JsonArray getArrayFrom
		(JsonSemantics.JsonObject descriptor, String called)
	{ return toArray (descriptor.getMemberCalled (called)); }

	/**
	 * take a member from an object and treat as a number
	 * @param descriptor a JSON object expected to be holding a field
	 * @param called the name of the member to be treated as a number
	 * @return the number in the member called as specified
	 */
	public static JsonSemantics.JsonNumber getNumberFrom
		(JsonSemantics.JsonObject descriptor, String called)
	{ return (JsonSemantics.JsonNumber) descriptor.getMemberCalled (called); }

	/**
	 * get the numeric value of a named member of a JSON object
	 * @param descriptor the object being parsed
	 * @param called name of the member being read
	 * @return the value of the named member
	 */
	public static double getValueFrom (JsonSemantics.JsonObject descriptor, String called)
	{ return getNumberFrom (descriptor, called).getNumber ().doubleValue (); }

	/**
	 * convert array of numbers to double float
	 * @param numbers the numbers from the JSON tools
	 * @return Numbers array converted to Double array
	 */
	public static Double [] toDoubleArray (Number [] numbers)
	{
		Double [] doubles = new Double [numbers.length];
		for (int i = 0; i < numbers.length; i++) doubles [i] = numbers [i].doubleValue ();
		return doubles;
	}

	/**
	 * treat value as array of floating numbers
	 * @param value a JSON value expected to be array of float
	 * @return the extracted array of floating values
	 */
	public static Double [] toDoubleArray (JsonSemantics.JsonValue value)
	{
		return toDoubleArray (toNumberArray (toArray (value)));
	}

	/**
	 * construct JSON version for Map
	 * @param map the Java Map object to represent
	 * @return the JSON representation for the map
	 * @param <K> key data type for map
	 * @param <V> value data type
	 */
	public static <K,V> JsonSemantics.JsonObject toJsonObject (Map<K,V> map)
	{
		JsonSemantics.JsonObject
			object = new JsonSemantics.JsonObject ();
		for (K key : map.keySet ())
		{
			object.addMemberNamed
			(
				key.toString (),
				new JsonSemantics.JsonString
				(
					map.get (key).toString ()
				)
			);
		}
		return object;
	}

	/**
	 * build a Java Map version for a JSON object
	 * @param value a JSON object to translate to Map
	 * @return a String map holding the data
	 */
	public static Map<String,String> toTextMap (JsonSemantics.JsonObject value)
	{
		Map<String,String> map =
				new HashMap<String,String>();
		for (String name : value.getMemberNames ())
		{ map.put (name, value.getMemberString (name)); }
		return map;
	}

	/**
	 * build a Java Map version for a JSON object
	 * @param value a JSON object to translate to Map
	 * @return a String map holding the data
	 */
	public static Map<String,Object> toObjectMap (JsonSemantics.JsonObject value)
	{
		Map<String,Object>
			map = new HashMap<String,Object>();
		for (String name : value.getMemberNames ())
		{ map.put (name, value.getMemberString (name)); }
		return map;
	}

	/**
	 * build JSON hash for a symbol table
	 * @param symbols the symbols to be included
	 * @return a JSON object holding the symbols
	 */
	public static JsonSemantics.JsonObject symbolHash (Map<String,Map<String,String>> symbols)
	{
		JsonSemantics.JsonObject
			importHash = new JsonSemantics.JsonObject ();
		for (String item : symbols.keySet ())
		{
			importHash.addMemberNamed
			(
				item, toJsonObject (symbols.get (item))
			);
		}
		return importHash;
	}

}
