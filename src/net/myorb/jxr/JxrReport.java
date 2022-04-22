
package net.myorb.jxr;

import net.myorb.data.abstractions.Stack;

import java.util.HashMap;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * format information found in symbols
 * @author Michael Druckman
 */
public class JxrReport
{


	/*
	 * flow trace for debugging assistance
	 */
	public static boolean FILE_TRACE = false, BEAN_TRACE = false;

	/**
	 * @param flag TRUE implies trace enabled
	 * @param tag the text of a tag to identify type of trace
	 * @param enter TRUE implies entering a structure
	 * @param details some context description
	 */
	public static void trace
	(boolean flag, String tag, boolean enter, String details)
	{
		if (!flag) return;
		System.out.print (tag);
		System.out.print (enter? "> ": "< ");
		System.out.print (details);
		System.out.println ();
	}
	/*
	 * end of debugging code
	 */


	public static final boolean
	VERBOSE_ERROR_TRACE = true,
	RAISE_AS_EXCEPTION = true,
	TERSE = false;


	/**
	 * @param e the exception being reported
	 * @param element the name of the element causing the error
	 * @param a the block of attributes on the element
	 */
	public static void processError
	(Exception e, String element, String a)
	{
		System.out.println (); System.out.println ("===");
		System.out.println ("Error in node: " + element);
		System.out.println (a); System.out.println (e);
		System.out.println (SOURCE_STACK);
		System.out.println (NODE_STACK);
		System.out.println ("===");
	}


	/*
	 * source and node stacks for debugging reports
	 */

	public static void pushSource
	(String path, HashMap<String,Object> symbols)
	{
		String parm = symbols==null? "NULL": symbols.toString ();
		trace (FILE_TRACE, path, true, parm);
		SOURCE_STACK.push (path);
	}

	public static void popSource (String path)
	{
		trace (FILE_TRACE, path, false, "");
		SOURCE_STACK.pop ();
	}

	public static void popNode () { NODE_STACK.pop (); }
	public static void pushNode (String name) { NODE_STACK.push (name); }
	static Stack<String> SOURCE_STACK = new Stack<>(), NODE_STACK = new Stack<>();


	/**
	 * @param symbols a symbol table to display on sys out
	 */
	public static void dump (HashMap<String,Object> symbols)
	{
		System.out.println ();
		System.out.println ("******************************");
		System.out.println ("* Symbol Collection Compiled *");
		System.out.println ("******************************");
		System.out.println ();

		Object[] names =
			sortedNames (symbols);
		if (TERSE) dump (names, symbols);
		else tabulate (names, symbols);
	}


	/**
	 * build array of symbol names
	 * @param symbols the symbol table to be sorted
	 * @return array of the symbol names
	 */
	public static Object[] sortedNames
	(HashMap<String,Object> symbols)
	{
		if (symbols == null)
		{
			System.out.println ("Symbol table is empty");
			return null;
		}
		Object[] names = symbols.keySet ().toArray ();
		Arrays.sort (names);
		return names;
	}


	/**
	 * simple display dump
	 * @param names the names of the symbols
	 * @param symbols the hash of the symbols
	 */
	public static void dump
	(Object[] names, HashMap<String,Object> symbols)
	{
		for (Object name : names)
		{
			display (name.toString (), symbols);
		}
	}


	/**
	 * simple text dump
	 * @param name the name of the symbol
	 * @param symbols the hash of the symbols
	 */
	public static void display
	(String name, HashMap<String,Object> symbols)
	{
		String description = symbols.get (name).toString ();
		System.out.println (name + ": " + description);
	}


	/**
	 * format table of symbols
	 * @param names the names of the symbols
	 * @param symbols the hash of the symbols
	 */
	public static void tabulate
	(Object[] names, HashMap<String,Object> symbols)
	{
		if (names == null) return;

		for (Object name : names)
		{
			String nameText = name.toString ();
			Object item = symbols.get (nameText);

			if (item instanceof String)
			{
				display (nameText, symbols);
			}
			else if (item instanceof Collection<?>)
			{
				itemize (nameText, (Collection<?>) item);
			}
		}
	}


	/**
	 * @param name the name of the collection
	 * @param c the collection containing symbols
	 */
	public static void itemize (String name, Collection<?> c)
	{
		int size = c.size ();
		String label = size == 1? " symbol": " symbols";
		System.out.println (); System.out.println (name + " (" + size + label +"):");
		for (Object o : organized (c)) System.out.println ("\t" + o.toString ());
		System.out.println ();
	}


	/**
	 * @param c the collection containing symbols
	 * @return the object of the table ordered as available
	 */
	public static Object[] organized (Collection<?> c)
	{ return sortable (c)? sorted (c): c.toArray (); }


	/**
	 * @param c the collection containing symbols
	 * @return TRUE implies sortable and not empty
	 */
	public static boolean sortable (Collection<?> c)
	{
		Object item;
		if (c instanceof HashMap) return keyChk (c);
		if ( ! (c instanceof ArrayList) ) return false;
		@SuppressWarnings("rawtypes") ArrayList a = (ArrayList) c;
		return (item = firstArrayItem (a)) != null && item instanceof Comparable;
	}


	/**
	 * @param a collection of objects as array list
	 * @return first item of the array
	 */
	public static Object firstArrayItem (ArrayList<?> a)
	{ return a.size () < 2? null: a.get (0); }


	/**
	 * @param c the collection containing symbols
	 * @return TRUE for organizable hash key set
	 */
	public static boolean keyChk (Collection<?> c)
	{
		Object item;
		@SuppressWarnings("rawtypes") HashMap hash = (HashMap) c;
		return (item = firstKey (hash)) != null && item instanceof Comparable;
	}


	/**
	 * @param hash a hash of unsorted objects
	 * @return the lowest sorted value of hash keys
	 */
	public static Object firstKey (HashMap<?,?> hash)
	{ return hash.size () < 2? null: hash.keySet ().toArray()[0]; }


	/**
	 * @param c the collection containing symbols
	 * @return the symbols sorted using Comparable mechanism
	 */
	public static Object[] sorted (Collection<?> c)
	{
		if (c instanceof HashMap) return sortedHash (c);
		return sortedArray (c);
	}


	/**
	 * @param c collection of objects to be sorted
	 * @return sorted list as array
	 */
	public static Object[] sortedArray (Collection<?> c)
	{
		@SuppressWarnings("unchecked")
		ArrayList<Object> hash = (ArrayList<Object>) c;
		Object[] items = hash.toArray ();
		Arrays.sort (items);
		return items;
	}


	/**
	 * @param c collection of objects to be sorted
	 * @return sorted list as array
	 */
	public static Object[] sortedHash (Collection<?> c)
	{
		@SuppressWarnings("unchecked")
		HashMap<Object,Object> hash = (HashMap<Object,Object>) c;
		Object[] keys = hash.keySet ().toArray ();
		return sortedHash (hash, keys);
	}


	/**
	 * @param hash the full hash of unsorted objects
	 * @param keys the keys of the hash to sort
	 * @return sorted list as array
	 */
	public static Object[] sortedHash
	(HashMap<Object,Object> hash, Object[] keys)
	{
		Arrays.sort (keys);
		ArrayList<Object> a = new ArrayList<Object>();
		for (Object key : keys) a.add (hash.get (key));
		return a.toArray ();
	}


}

