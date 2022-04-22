
package net.myorb.data.abstractions;

import java.util.HashMap;

/**
 * simple parse and hash for name/value pairs.
 *  syntax expected is name=value;name=value for any number of pairs
 * @author Michael Druckman
 */
public class NameValueHash extends HashMap<String,String>
{

	public NameValueHash () {}

	/**
	 * @param initialParameters the text of a set of pairs
	 */
	public NameValueHash (String initialParameters)
	{
		parse (initialParameters);
	}

	/**
	 * @param item a single pair defined by name=value syntax
	 */
	public void add (String item)
	{
		String [] pair = item.split ("=");
		if (pair.length != 2) throw new RuntimeException ("Name/Value pair not found: " + item);
		this.put (pair[0], pair[1]);
	}

	/**
	 * @param parameters an array of pairs defined by name=value syntax
	 */
	public void addAll (String [] parameters)
	{
		for (String parameter : parameters) add (parameter);
	}

	/**
	 * @param parameters an arbitrary number of pairs defined by name=value;name=value syntax
	 */
	public void parse (String parameters)
	{
		if (parameters == null) return;
		String [] multi = parameters.split (";");
		if (multi.length > 1) addAll (multi);
		else add (multi[0]);
	}

	private static final long serialVersionUID = 4253855075154772854L;

}
