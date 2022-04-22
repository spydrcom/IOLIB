
package net.myorb.data.abstractions;

import java.util.function.Consumer;
import java.util.Map.Entry;
import java.util.*;

/**
 * processing for arrays, lists, and sets of integers
 * @author Michael Druckman
 */
public class Numerics
{


	/* collections of integers as objects */


	public static class IntList extends ArrayList<Integer>
	{
		public IntList () {}
		public IntList (Collection <Integer> items) { addAll (items); }
		private static final long serialVersionUID = 7552421357362754694L;
	}

	public static class IntSet extends HashSet<Integer>
	{ private static final long serialVersionUID = 1L; }

	public static class NamedConstants extends HashMap<String,Integer>
	{ private static final long serialVersionUID = 1L; }

	public static class IntMap extends HashMap<Integer,Integer>
	{ private static final long serialVersionUID = 1L; }


	/* useful tracking of numeric lists*/


	/**
	 * count items in numeric lists
	 */
	public static class ItemTally extends IntMap
	{

		/**
		 * @param list the list of items to add to tallys
		 */
		public void addToCounts (IntList list)
		{
			for (Integer item : list)
			{
				if (containsKey (item))
					put (item, get (item) + 1);
				else put (item, 1);
			}
		}

		/**
		 * @return a list ordered by tally count
		 */
		public OrderedTallys getOrderedTallys ()
		{
			OrderedTallys entries = new OrderedTallys ();
			entries.addAll (entrySet ());
			entries.sort (entries);
			return entries;
		}

		private static final long serialVersionUID = 1L;
	}

	/**
	 * use map entries to order pairs by tally counts
	 */
	public static class OrderedTallys
		extends ArrayList <Entry <Integer, Integer>>
		implements Comparator <Entry <Integer, Integer>>
	{
		/**
		 * callback allows use for item with associated tally
		 */
		public interface TallyItemUse
		{
			/**
			 * @param item the value of the item from the original tallied data
			 * @param tally the tally found for that item
			 */
			void noteTallyItem (int item, int tally);
		}

		/* (non-Javadoc)
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public int compare
		(Entry <Integer, Integer> l, Entry <Integer, Integer> r)
		{ return r.getValue () - l.getValue (); }

		/**
		 * @param index the index of the item to send to callback
		 * @param usedBy the callback implementation
		 */
		public void referToTallyItem (int index, TallyItemUse usedBy)
		{
			Entry <Integer, Integer> entry = get (index);
			usedBy.noteTallyItem (entry.getKey (), entry.getValue ());
		}

		private static final long serialVersionUID = 1L;
	}


	/*
	 * parse from text
	 */


	/**
	 * trim text and parse as integer
	 * @param text the text to be parsed
	 * @return the integer value
	 */
	public static int parseNumber (String text)
	{ return Integer.parseInt (text.trim ()); }


	/**
	 * interpret text as integer
	 * @param text the text to interpret
	 * @return the integer value, 0 for empty, -1 for parse error
	 */
	public static int parseInt (String text)
	{
		if (text.isEmpty ()) return 0;

		try
		{
			return parseNumber (text);
		} catch (Exception e) { return -1; }
	}


	/**
	 * parse text of a sequence
	 * @param sequence the values separated by blank space
	 * @return the ordered list of values
	 */
	public static IntList parse (String sequence)
	{
		IntList parsed = new IntList ();
		String[] items = sequence.split (" ");
		for (String s : items) if (!s.isEmpty ())
		{ parsed.add (parseNumber (s)); }
		return parsed;
	}

	/**
	 * allow for decimal point
	 * @param text the text of value with/without decimal point
	 * @return Integer if no decimal found, otherwise double
	 */
	public static Number parseDecimalNumber (String text)
	{
		int dot = text.indexOf ('.');
		if (dot < 0) return parseNumber (text);
		String mantissa = text.substring (dot+1).trim ();
		int characteristic = parseNumber (text.substring (0, dot));
		double scale = Math.pow (10, mantissa.length ());
		double decimal = parseNumber (mantissa) / scale;
		return (double) characteristic + decimal;
	}


	/**
	 * parse decimal value but return truncated integer
	 * @param text the text of value with/without decimal point
	 * @return Integer value if decimal found, otherwise characteristic of value
	 */
	public static int parseTruncatedDecimalNumber (String text)
	{
		return parseDecimalNumber (text).intValue ();
	}


	/**
	 * list of text integers converted to list if integers
	 * @param text list of text number representations
	 * @return list of the integer values
	 */
	public static IntList parseIntegers (List<String> text)
	{
		String sequence = "";
		for (String item : text) sequence += " " + item;
		return parse (sequence);
	}


	/**
	 * parse both integer and decimal values
	 * @param text list of text number representations (with/without decimal point)
	 * @return list of the Number values
	 */
	public static List<Number> parseNumbers (List<String> text)
	{
		List<Number> numbers = new ArrayList<Number>();
		for (String item : text) numbers.add (parseDecimalNumber (item));
		return numbers;
	}


	/**
	 * parse sub-expression within square ([]) brackets
	 * @param text string containing [...contents...]
	 * @return substring contents of brackets
	 */
	public static String parseBracketed (String text)
	{
		int start = text.indexOf ('[');
		if (start < 0) return null;

		int end = text.indexOf (']', ++start);
		if (end < 0) return null;

		return text.substring (start, end);
	}


	/**
	 * parse numeric sub-expression within square ([]) brackets
	 * @param text string containing integers [1,2,3...] (comma ',' or space ' ' delimited)
	 * @return list of integer contents of brackets
	 */
	public static IntList parseBracketedIntegers (String text)
	{
		try
		{
			String enclosed, sequence = text.replace (',', ' ');
			return ((enclosed = parseBracketed (sequence)) == null)? null: parse (enclosed);
		} catch (Exception e) { return null; }
	}


	/*
	 * initialize new value objects
	 */


	/**
	 * build new tabulation object to register hits
	 * @return a new tabulation object initialized for no hits
	 */
	public static int[] newHitTabulation ()
	{
		return new int[]{0, 0, 0, 0, 0, 0};
	}


	/*
	 * duplicate clone or copy
	 */


	/**
	 * make copy of list
	 * @param <T> type of items in source array
	 * @param list the list to copy
	 * @return the copy
	 */
	public static <T> List<T> copy (List<T> list)
	{
		return SimpleUtilities.unordered (list);
	}


	/*
	 * array conversion
	 */


	/**
	 * convert discrete integer array to object array
	 * @param values the array of integer values
	 * @return object array of numbers
	 */
	public static Number[] toNumbers (int[] values)
	{
		int j = 0;
		Number[] n = new Number[values.length];
		for (int i: values) n[j++] = i;
		return n;
	}


	/**
	 * convert Number array to Integer array
	 * @param values the array of Numbers
	 * @return object array of Integers
	 */
	public static Integer[] toIntegers (Number[] values)
	{
		int j = 0;
		Integer[] n = new Integer[values.length];
		for (Number item : values) n[j++] = item.intValue ();
		return n;
	}


	/**
	 * build list from array
	 * @param <T> type of elements in source array
	 * @param array the array of values
	 * @return the ordered list
	 */
	public static <T> List<T> toList (T[] array)
	{
		List<T> items = new ArrayList<T>();
		SimpleUtilities.add (array, items);
		return items;
	}


	/**
	 * construct text from items
	 * @param <T> type of items in source array
	 * @param items the objects to be converted
	 * @param delimiter the text separating items
	 * @param starting the starting position in the array
	 * @param ending the last item
	 * @return the text sequence
	 */
	public static <T> String asText (T[] items, String delimiter, int starting, int ending)
	{
		String buffer = items[starting].toString ();

		for (int index = starting+1; index <= ending; index++)
		{
			String next = items[index].toString ();
			buffer += delimiter + next;
		}

		return buffer;
	}


	/*
	 * Collection conversion
	 */


	/**
	 * dump collection of items into set
	 * @param <T> type of items in source collection
	 * @param items the Collection of values
	 * @return Collection as a set
	 */
	public static <T> Set<T> toSet (Collection<T> items)
	{
		Set<T> set = new HashSet<T>();
		SimpleUtilities.addUnordered (items, set);
		return set;
	}


	/**
	 * dump collection of items into list
	 * @param <T> type of items in source collection
	 * @param items the Collection of values
	 * @return Collection as a list
	 */
	public static <T> List<T> toList (Collection<T> items)
	{
		List<T> list = new ArrayList<T>();
		SimpleUtilities.addUnordered (items, list);
		return list;
	}


	/*
	 * set operations
	 */


	/**
	 * compute set intersection
	 * @param <T> type of items in sets
	 * @param left left operand of operator
	 * @param right right operand of operator
	 * @return set of elements in both operands
	 */
	public static <T> Set<T> intersect (Set<T> left, Set<T> right)
	{
		Set<T> common = new HashSet<T>();
		right.forEach
		(
			new Consumer<T> ()
			{
				public void accept (T element)
				{
					if (left.contains (element)) common.add (element);
				}
			}
		);
		return common;
	}
	public static <T> int intersectionSize (Set<T> left, Set<T> right)
	{ return intersect (left, right).size (); }


	/*
	 * array, list, and set sorting / summing
	 */


	/**
	 * sort array of integers
	 * @param items the array to be sorted
	 * @return the array after sorting
	 */
	public static Integer[] sort (Integer[] items)
	{ Arrays.sort (items); return items; }


	/**
	 * compute sum of list
	 * @param values the list of values
	 * @return total
	 */
	public static int sum (List<Integer> values)
	{
		int total = 0;
		for (int value : values) total += value;
		return total;
	}
	public static List<Integer> times (List<Integer> values, int[] weights)
	{
		List<Integer> products = new ArrayList<Integer>();
		for (int i = 0; i < values.size (); i++) products.add (values.get(i) * weights[i]);
		return products;
	}
	public static int sum (List<Integer> values, int[] weights)
	{ return sum (times (values, weights)); }


	/**
	 * get first entries up to maximum
	 * @param list the list being evaluated
	 * @param maxSize the maximum entries to be included
	 * @return entries of list up to maximum
	 */
	public static IntList first (List<Integer> list, int maxSize)
	{ return new IntList (list.subList (0, max (list, maxSize))); }


	/**
	 * compute largest count allowed
	 * @param list the list being evaluated
	 * @param maxSize the maximum entries to be included
	 * @return smaller of list size or maximum
	 */
	public static int max (List<Integer> list, int maxSize)
	{ return Math.min (maxSize, list.size ()); }


	/**
	 * pad single digit value with leading space
	 * @param v the value to be formatted
	 * @return the formatted text
	 */
	public static String pad (int v) { return padWith (v, " "); }
	public static String padZero (int v) { return padWith (v, "0"); }

	public static String padWith (int v, String leading)
	{
		return v<10? leading + Integer.toString(v): Integer.toString(v);
	}


	/*
	 * formatting of values
	 */


	/**
	 * format list separated by space
	 * @param values the list of values to format
	 * @param buffer the buffer collecting output
	 */
	public static void formatSequence (Object[] values, StringBuffer buffer)
	{
		for (Object n : values)
		{
			buffer.append (formatRestricted (n)).append (" | ");
		}
	}


	/**
	 * restrict display size to 6 characters
	 * @param o the object being displayed
	 * @return the text representation
	 */
	public static String formatRestricted (Object o)
	{
		String s = o.toString ();
		if (s.length() > 4) s = s.substring (0, 4);
		return s;
	}


	/*
	 * random operations
	 */


	/**
	 * get a random integer
	 * @param hi the excluded highest value
	 * @return the chosen value
	 */
	public static int choose (int hi)
	{ return Randomizer.choose (hi); }


}

