
package net.myorb.data.abstractions;

import java.util.Comparator;
import java.util.Collection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * simple methods for common needs
 * @author Michael Druckman
 */
public class SimpleUtilities
{


	/**
	 * implementer is container of other object
	 */
	public interface Container
	{

		/**
		 * @param c the class sought
		 * @return TRUE implies sought type found
		 */
		boolean contains (Class<?> c);

		/**
		 * @param c class of type sought
		 * @return contents cast to sought type
		 * @param <T> type of data contained
		 */
		<T> T getContents (Class<T> c);

	}


	public static class ListOfNames extends ArrayList <String>
	{ private static final long serialVersionUID = 7292323251671167371L; }


	/**
	 * provide ability to change property values
	 */
	public interface PropertySetting
	{
		/**
		 * @param named the name of the property
		 * @param to the value for the property
		 */
		void setProperty (String named, Object to);
	}


	/**
	 * named properties mapped to generic objects
	 */
	public static class PropertiesMap
			extends HashMap <String, Object>
			implements PropertySetting
	{
		/* (non-Javadoc)
		 * @see net.myorb.data.abstractions.SimpleUtilities.PropertySetting#setProperty(java.lang.String, java.lang.Object)
		 */
		public void setProperty (String named, Object to) { this.put (named, to); }
		private static final long serialVersionUID = 4861056622657533026L;
	}

	/**
	 * cast object to type
	 * @param object any Java object
	 * @param expectedClass the class of object expected
	 * @return the object cast to the class, null if not appropriate
	 * @param <C> the class the object will be cast to
	 */
	public static <C> C verifyClass (Object object, Class<C> expectedClass)
	{
		if (object == null) return null;
		else if (expectedClass.isInstance (object))
		{ return expectedClass.cast (object); }
		else if (object instanceof Container)
		{
			Container c = (Container) object;
			// checking to see if container has sought object type
			if (c.contains (expectedClass))
			{
				// return found contents
				return (C) c.getContents (expectedClass);
			}
		}
		return null;
	}

	/**
	 * provide map from key to list of element
	 * @param <Key> the type of keys in the map
	 * @param <Element> the element type
	 */
	public static class MapToList <Key, Element> extends HashMap <Key, List <Element>>
	{

		/**
		 * add to end of list for key
		 * @param k the value of the key
		 * @param e the element to add
		 */
		public void add (Key k, Element e)
		{
			List<Element> l;
			if (containsKey (k)) l = get (k);
			else put (k, l = new ArrayList<Element>());
			l.add (e);
		}

		private static final long serialVersionUID = 1563112713652696697L;
	}

	/**
	 * collect lists of text items
	 */
	public static class TextItems extends ArrayList<String>
	{
		public TextItems () {}
		public TextItems (Collection <String> items) { addAll (items); }
		
		public TextItems applyNaturalOrdering ()
		{
			TextItems copy = new TextItems (this);
			copy.sort (null);
			return copy;
		}

		private static final long serialVersionUID = -4633804835986601445L;
	}

	/**
	 * @param <T> the Comparable type being to be treated as Comparator
	 * @return a Comparator that works using the Comparable implementation of a generic type
	 */
	public static <T extends Comparable<T>> Comparator<T> getComparator ()
	{
		return new Comparator<T>()
		{
			public int compare (T left, T right)
			{ return left.compareTo (right); }
		};
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
	 * @param <T> the Comparable type being evaluated
	 * @param numbers a list of Number objects
	 * @return the largest in the list
	 */
	public static <T extends Number> T largestOf (List<T> numbers)
	{
		return searching (numbers, 1, getRealNumberComparator (), getFirstEntry (numbers));
	}
	public static <T extends Number> T largestWholeNumber (List<T> fromList)
	{
		return searching (fromList, 1, getWholeNumberComparator (), getFirstEntry (fromList));
	}

	/**
	 * @param fromList a list of numbers
	 * @return first item in list, RuntimeException if empty
	 * @param <T> the Number type being sampled
	 */
	public static <T extends Number> T getFirstEntry (List<T> fromList)
	{
		if (fromList.size() < 1)
			throw new RuntimeException ("List is empty");
		return fromList.get (0);
	}

	/**
	 * @param <T> the Comparable type being evaluated
	 * @param numbers a list of Number objects
	 * @return the smallest in the list
	 */
	public static <T extends Number> T smallestOf (List<T> numbers)
	{
		return searching (numbers, -1, getRealNumberComparator (), getFirstEntry (numbers));
	}
	public static <T extends Number> T smallestWholeNumber (List<T> fromList)
	{
		return searching (fromList, -1, getWholeNumberComparator (), getFirstEntry (fromList));
	}

	/**
	 * @param <T> the type being evaluated
	 * @param list a list of items to evaluate
	 * @param lookingFor 1 = greatest, -1 = smallest
	 * @param test the comparator that supplies the ordering
	 * @param soFar a value considered best candidate so far
	 * @return the largest or smallest as specified
	 */
	public static <T> T searching (List<T> list, int lookingFor, Comparator<T> test, T soFar)
	{ for (T t : list) if (test.compare (t, soFar) == lookingFor) soFar = t; return soFar; }

	/**
	 * @param <T> the type being evaluated
	 * @return comparator for number type
	 */
	public static <T extends Number> Comparator<T> getRealNumberComparator ()
	{
		return new Comparator<T>()
		{
			public int compare (T left, T right)
			{
				return compareRealNumbers (left, right);
			}
		};
	}
	public static <T extends Number> Comparator<T> getWholeNumberComparator ()
	{
		return new Comparator<T>()
		{
			public int compare (T left, T right)
			{
				return compareWholeNumbers (left, right);
			}
		};
	}
	public static int compareRealNumbers (Number left, Number right)
	{
		Double l = left.doubleValue (), r = right.doubleValue (); return l.compareTo (r);
	}
	public static int compareWholeNumbers (Number left, Number right)
	{
		Long l = left.longValue (), r = right.longValue (); return l.compareTo (r);
	}

	/**
	 * add items of collection to list
	 * @param <T> the type of items being added to collection
	 * @param items the collection of items being added
	 * @param to the destination collection
	 */
	public static <T> void addUnordered (Collection<T> items, Collection<T> to)
	{
		to.addAll (items);
	}

	/**
	 * list items of collection
	 * @param <Element> the type of items being added to list
	 * @param items the collection of items
	 * @return a list of the items
	 */
	public static <Element> List<Element> unordered (Collection<Element> items)
	{
		List<Element> into = new ArrayList<Element>();
		addUnordered (items, into);
		return into;
	}

	/**
	 * @param <Element> the Comparable type being sorted
	 * @param items a Collection of items to be sorted into a list
	 * @return the sorted list
	 */
	public static
		<Element extends Comparable<Element>> List<Element>
		ordered (Collection<Element> items)
	{
		return sort (unordered (items));
	}

	/**
	 * @param <Key> the Comparable type being sorted
	 * @param <Element> the type of values found in the map
	 * @param map a map containing comparable keys to be ordered
	 * @return a list of the keys naturally ordered
	 */
	public static
		<Key extends Comparable<Key>,Element> List<Key>
		orderedKeys (Map<Key,Element> map)
	{
		return ordered (map.keySet ());
	}

	/**
	 * @param <Key> the Key type used in the map
	 * @param <Element> the Comparable type being ordered
	 * @param map a map containing comparable values to be sorted
	 * @return a list of the values naturally ordered
	 */
	public static
		<Element extends Comparable<Element>,Key> List<Element>
		orderedValues (Map<Key,Element> map)
	{
		return ordered (map.values ());
	}

	/**
	 * allocate a new item of a collection
	 * @param type the class of the item to be added
	 * @param collection the collection used to hold allocated items
	 * @param <T> the Comparable type being added to collection
	 * @return a new instance of the item type
	 * @throws Exception for any errors
	 */
	public static <T> T allocateCollectedItem
	(Class<T> type, Collection<T> collection) throws Exception
	{ T item = type.newInstance (); collection.add (item); return item; }


	/*
	 * list operations
	 */

	/**
	 * get index of last list entry
	 * @param list a list object of any element type
	 * @return index of last entry
	 */
	public static int lastEntryOf (List <?> list) { return list.size () - 1; }

	/**
	 * remove last entry added to list
	 * @param list the list to be changed
	 */
	public static void discardLastEntryOf (List <?> list) { list.remove (lastEntryOf (list)); }

	/**
	 * add items in array to generic collection
	 * @param <T> the Comparable type being added to collection
	 * @param items elements to be added to collection
	 * @param to the collection receiving items
	 */
	public static <T> void add (T [] items, Collection <T> to)
	{
		for (T t : items) to.add (t);
	}

	/**
	 * @param <T> the type of items in list
	 * @param items array holding contents for list
	 * @return a list of the array contents
	 */
	public static <T> List<T> toList (T[] items)
	{
		List<T> list = new ArrayList<T>();
		add (items, list);
		return list;
	}
	@SafeVarargs public static <T> List<T> listOf (T... items)
	{
		List<T> list = new ArrayList<T>();
		add (items, list);
		return list;
	}

	/**
	 * @param items array of double values
	 * @param list the list to populate
	 */
	public static void toList (double[] items, List <Double> list)
	{
		for (double d : items) list.add (d);
	}
	public static List<Double> toList (double[] items)
	{
		List <Double> list = new ArrayList <> ();
		toList (items, list);
		return list;
	}

	/**
	 * convert numbers to integers
	 * @param numbers the list of numbers
	 * @return the list of integers
	 */
	public static List<Integer> toIntegers (List <Number> numbers)
	{
		List<Integer> list = new ArrayList<>();
		for (Number n : numbers) list.add (n.intValue ());
		return list;
	}

	/**
	 * convert numbers to floats
	 * @param numbers the list of numbers
	 * @return the list of floats
	 */
	public static List <Double> toFloats (List <Number> numbers)
	{
		List<Double> list = new ArrayList<>();
		for (Number n : numbers) list.add (n.doubleValue ());
		return list;
	}

	/**
	 * save redundant allocation of empty array for conversion
	 * @param numbers a list of double objects
	 * @return array of the contents
	 */
	public static Double []
	arrayOf (List <Double> numbers) { return numbers.toArray (EMPTY_LIST); }
	static final Double[] EMPTY_LIST = new Double[]{};

	/**
	 * @param numbers list of Number objects
	 * @param floats array of floats to receive values
	 */
	public static void toFloats (Number [] numbers, double [] floats)
	{
		for (int i = 0; i < floats.length; i++) floats[i] = numbers[i].doubleValue ();
	}

	/**
	 * parse radix syntax with sign extension
	 * @param image the text of the radix value
	 * @return the Number representation of the value
	 */
	public static Number radixValue (String image)
	{
		int rdx;
		switch (image.charAt (1))
		{
			case 'b': rdx = 2;  break;
			case 'o': rdx = 8;  break;
			case 'x': rdx = 16; break;
			default: return 0;
		}
		long value = Long.parseLong (image.substring (2), rdx);
		if ((value & SIGN) != 0) value |= MASK;
		return value;
	}
	static long SIGN = 0x80000000l, MASK = 0xFFFFFFFF00000000l;

}

