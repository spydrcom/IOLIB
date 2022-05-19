
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
	 * @param <T> the Comparable type being to be treated as Comparator
	 * @return a Comparator that works using the Comparable implementation of a generic type
	 */
	public static <T extends Comparable<T>> Comparator<T> getComparator ()
	{
		return (l, r) -> l.compareTo (r);
	}
	
	/**
	 * @param <T> the Comparable type being sorted
	 * @param list a list of Comparable items to be sorted
	 * @return same list having been sorted
	 */
	public static <T extends Comparable<T>> List<T> sort (List<T> list)
	{
		list.sort (getComparator ());
		return list;
	}

	/**
	 * @param <T> the Comparable type being sorted
	 * @param list the source list of values
	 * @return a sorted copy of the list
	 */
	public static <T extends Comparable<T>> List<T> sortedCopy (List<T> list)
	{
		List<T> temp = new ArrayList<T>();
		temp.addAll (list); List<T> sorted = sort (temp);
		return sorted;
	}

	/**
	 * @param <T> the Comparable type being evaluated
	 * @param list a list of Comparable values
	 * @return the largest in the list
	 */
	public static <T extends Comparable<T>> T max (List<T> list)
	{
		return sortedCopy (list).get (list.size () - 1);
	}

	/**
	 * @param <T> the Comparable type being evaluated
	 * @param list a list of Comparable values
	 * @return the smallest in the list
	 */
	public static <T extends Comparable<T>> T min (List<T> list)
	{
		return sortedCopy (list).get (0);
	}

	/**
	 * @param <T> the Element type being evaluated
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
