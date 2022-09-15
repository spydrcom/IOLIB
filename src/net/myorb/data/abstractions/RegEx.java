
package net.myorb.data.abstractions;

import java.util.Collection;
import java.util.ArrayList;

import java.util.regex.*;

/**
 * regular expression searches for lists
 * @author Michael Druckman
 */
public class RegEx extends ArrayList <String>
{


	/*
	 * Summary of regular-expression constructs
	 * https://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html
	 */


	/**
	 * @param source the collection of items to be searched
	 */
	public void setSource (Collection <String> source)
	{
		this.source = source;
	}
	protected Collection <String> source;


	/**
	 * start a new empty list
	 */
	public void startList ()
	{
		this.source = new ArrayList <String> ();
	}


	/**
	 * add to the list of this component
	 * @param item the item to add to list
	 */
	public void addToList (String item)
	{
		this.source.add (item);
	}


	/**
	 * compile the pattern that will define the list
	 * @param pattern the reg-ex text to use for the search
	 */
	public void setExpression (String pattern)
	{
		this.pattern = Pattern.compile (pattern);
	}
	protected Pattern pattern;


	/**
	 * replace this list with the source items that match the pattern
	 */
	public void refresh ()
	{
		this.clear ();

		for (String item : source)
		{
			if  (
					pattern.matcher
						(
							ignoreCase ? item.toLowerCase () : item
						)
					.matches ()
				)
			{
				this.add (item);
			}
		}
	}


	/**
	 * force comparisons to lower case versions
	 * @param to new value for the flag
	 */
	public void setIgnoreCase (boolean to)
	{
		this.ignoreCase = to;
	}
	protected boolean ignoreCase = false;


	private static final long serialVersionUID = -6548786250751323411L;
}

