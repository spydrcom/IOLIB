
package net.myorb.data.notations.json;

/**
 * convert JSON primitives to/from Java primitive types
 * @author Michael Druckman
 */
public class JsonConversions
{

	//
	// JsonString to/from String
	//

	public static JsonLowLevel.JsonValue toText (String value)
	{
		if (value == null)
			return JsonSemantics.getNull ();
		return new JsonSemantics.JsonString (value);
	}

	public static String textOf
	(JsonSemantics.JsonObject source, String member)
	{
		JsonLowLevel.JsonValue value =
			source.getMemberCalled (member);
		return textOf (value);
	}

	public static String textOf (JsonLowLevel.JsonValue value)
	{
		if (JsonSemantics.isNull (value)) return null;
		JsonSemantics.JsonString text = (JsonSemantics.JsonString) value;
		return text.toString ();
	}

	//
	// JsonNumber to/from Long
	//

	public static JsonLowLevel.JsonValue toNumber (Long value)
	{
		if (value == null)
			return JsonSemantics.getNull ();
		return new JsonSemantics.JsonNumber (value);
	}

	public static Long valueOf (JsonSemantics.JsonObject source, String member)
	{
		return valueOf (source.getMemberCalled (member));
	}

	public static Long valueOf (JsonLowLevel.JsonValue value)
	{
		if (JsonSemantics.isNull (value)) return null;
		JsonSemantics.JsonNumber n = (JsonSemantics.JsonNumber) value;
		return n.getNumber ().longValue ();
	}

}
