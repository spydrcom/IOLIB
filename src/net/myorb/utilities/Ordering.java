
package net.myorb.utilities;

import java.util.Comparator;
import java.util.Collection;

import java.util.ArrayList;
import java.util.List;

/**
 * a mechanism for generating
 *  Comparator objects for elements of objects needing ordering
 * @author Michael Druckman
 */
public class Ordering
{

	/**
	 * description of an element of the group
	 */
	public interface Element
	{
		/**
		 * @param other another element of the group
		 * @return a number representing the difference between elements
		 */
		Number relativeTo (Element other);
	}

	/**
	 * an implementation of Comparator for Element implementers
	 */
	public interface Mechanism 
		extends Comparator <Element>
	{}

	/**
	 * a mechanism instance for use with elements
	 */
	public static final
	Mechanism MANAGER = (e1, e2) -> 
	(int) Math.signum
	(
		e1.relativeTo (e2).doubleValue ()
	);

	/**
	 * @param elements a collection of elements
	 * @return a List with the sorted elements
	 */
	public static < T extends Element > List <T>
		aSortedList (Collection <T> elements)
	{
		List <T>
		list = new ArrayList<T> ();
		list.addAll (elements);
		list.sort (MANAGER);
		return list;
	}

}
