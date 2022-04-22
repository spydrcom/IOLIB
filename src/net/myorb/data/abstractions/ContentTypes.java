
package net.myorb.data.abstractions;

import net.myorb.data.notations.json.JsonReader;
import net.myorb.data.notations.json.JsonSemantics;

import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;

/**
 * map file types to content types
 * @author Michael Druckman
 */
public class ContentTypes
{


	// the known file type map
	static Map <String, String> TYPES = new HashMap <> ();
	static Set <String> BINARY = new HashSet <> ();


	// initialize map

	static
	{
		try
		{
			JsonSemantics.JsonObject cfg;
			SimpleStreamIO.TextSource source = JsonReader.getFileSource ("/links/mimetypes.json");
			cfg = (JsonSemantics.JsonObject) JsonReader.readFrom (source);

			JsonSemantics.JsonArray mime =
				(JsonSemantics.JsonArray) cfg.getMember ("MIME");
			for (JsonSemantics.JsonValue v : mime) { config (v); }
		}
		catch (Exception e) { e.printStackTrace (); }
	}

	/**
	 * @param v configuration association from mimetypes file
	 */
	public static void config
		(
			JsonSemantics.JsonValue v
		)
	{
		JsonSemantics.JsonObject o = (JsonSemantics.JsonObject) v;
		String n = o.getMemberString ("Name"), t = o.getMemberString ("Type");
		boolean isText = JsonSemantics.isTrue (o.getMember ("Text"));
		if (!isText) BINARY.add (t);
		TYPES.put (n, t);
	}


	/**
	 * given requested address provide content type
	 * @param requested the text of the requested address
	 * @return the content-type to be used
	 */
	public static String getTypeFor (String requested)
	{
		String ft = "";
		String type = "text/plain";
		int dot = requested.lastIndexOf ('.');
		if (dot >= 0) { ft = requested.substring (dot + 1).toUpperCase (); }
		if (TYPES.containsKey (ft)) type = TYPES.get (ft);
		return type;
	}


	/**
	 * as opposed to text
	 * @param contentType the name of the type
	 * @return TRUE = binary format
	 */
	public static boolean isBinary (String contentType)
	{
		return BINARY.contains (contentType);
	}


	/**
	 * @param requested the text of the requested address
	 * @return TRUE = binary format
	 */
	public static boolean isBinaryRequest (String requested)
	{
		return isBinary (getTypeFor (requested));
	}


}

