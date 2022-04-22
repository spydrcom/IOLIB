
package net.myorb.utilities;

import java.util.HashMap;

/**
 * connect ENABLED state to generic item list
 * @param <T> description of items to be flagged
 * @author Michael Druckman
 */
public class FlagManager<T> extends HashMap<T,Boolean>
{

	/**
	 * @param t item to become enabled
	 */
	public void enable (T t) { this.put (t, true); }
	public void disable (T t) { this.put (t, false); }

	/**
	 * @param t item changing state
	 * @param to TRUE = enabled
	 */
	public void setEnabled (T t, boolean to) { this.put (t, to); }

	/**
	 * @param t item being queried
	 * @return TRUE = enabled
	 */
	public boolean isEnabled (T t)
	{
		return this.containsKey (t)? this.get (t): false;
	}

	private static final long serialVersionUID = 1L;
}

