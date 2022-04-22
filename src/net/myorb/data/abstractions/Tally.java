
package net.myorb.data.abstractions;

import java.util.HashMap;

/**
 * keep count of item occurrence
 * @author Michael Druckman
 */
public class Tally extends HashMap<String,Integer>
{

	/**
	 * increase count for specified item
	 * @param item the text describing item to be counted
	 */
	public void add (String item)
	{
		int count = 0;
		if (containsKey (item)) count = get (item);
		put (item, count+1);
	}

	/**
	 * @param itemValues array to receive text
	 * @param fromList the key list ordered as required
	 */
	public void fill (String[] itemValues, String[] fromList)
	{
		int col = 0;
		for (String title : fromList)
		{
			Object value = this.get (title);
			String cell = value == null ? "" : value.toString ();
			itemValues[col++] = cell;
		}
	}

	private static final long serialVersionUID = 1L;
}
