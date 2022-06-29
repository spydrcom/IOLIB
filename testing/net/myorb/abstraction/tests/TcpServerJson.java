
package net.myorb.abstraction.tests;

import net.myorb.data.abstractions.ServerConventions;
import net.myorb.data.notations.json.JsonLowLevel.JsonValue;
import net.myorb.data.notations.json.JsonPrettyPrinter;

/**
 * test class for JSON RPC service
 */
public class TcpServerJson
{

	public static void main (String[] s) throws Exception
	{
		System.out.println ("test starts");
		ServerConventions.provideService (8081, new JsonProcessor (), "\f");
		System.out.println ("test ends");
	}

}

class JsonProcessor implements ServerConventions.JsonProcessor
{

	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.ServerConventions.JsonProcessor#process(net.myorb.data.notations.json.JsonLowLevel.JsonValue)
	 */
	public String process (JsonValue value)
	{
		try { JsonPrettyPrinter.sendTo (value, System.out); } catch (Exception e) {}
		return "OK, response incomplete";
	}

}
