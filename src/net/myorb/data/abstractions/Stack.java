
package net.myorb.data.abstractions;

import java.util.ArrayList;

/**
 * typical last in first out stack
 * @param <T> type of items found on the stack
 * @author Michael Druckman
 */
public class Stack<T> extends ArrayList<T>
{

	/**
	 * @return index of top of stack
	 */
	public int last ()
	{ return size () - 1; }

	/**
	 * @param t item to push on stack
	 */
	public void push (T t) { add (t); }

	/**
	 * @return item at top of stack
	 */
	public T tos () { return get (last ()); }

	/**
	 * @return item removed from top of stack
	 */
	public T pop () { return remove (last ()); }

	private static final long serialVersionUID = -2268712127058733768L;

}
