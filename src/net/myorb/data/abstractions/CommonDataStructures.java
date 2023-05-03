
package net.myorb.data.abstractions;

import java.util.*;

/**
 * shared atomic data structure types
 * @author Michael Druckman
 */
public class CommonDataStructures
{


	/**
	 * list of items of specified object type
	 * @param <T> type of items
	 */
	public static class ItemList <T> extends ArrayList <T>
	{
		public ItemList () {}
		public ItemList (T item) { this.add (item); }
		public ItemList (Collection <T> items) { this.addAll (items); }
		public ItemList (T [] items) { CommonDataStructures.addAll (items, this); }
		private static final long serialVersionUID = -4619972438389917002L;
	}


	/**
	 * add all items to list
	 * @param items an array of items to add
	 * @param list the list being appended
	 */
	public static <T> void addAll
		( T [] items, Collection <T> toList )
	{ for ( T t : items ) { toList.add (t); } }


	/**
	 * lists of symbols
	 */
	public static class TextItems extends ItemList <String>
	{
		public TextItems () {}
		public TextItems (String item) { super (item); }
		public TextItems (String [] items) { super (items); }
		public TextItems (Collection <String> items) { super (items); }
		private static final long serialVersionUID = -58202141273735090L;
	}


	/**
	 * set of items of specified object type
	 * @param <T> type of items
	 */
	public static class PrimitiveSet <T> extends HashSet <T>
	{
		public PrimitiveSet () {}
		public PrimitiveSet (T item) { this.add (item); }
		public PrimitiveSet (Collection <T> items) { this.addAll (items); }
		public PrimitiveSet (T [] items) { CommonDataStructures.addAll (items, this); }
		private static final long serialVersionUID = 3039467719588772607L;
	}


	/**
	 * hash map with ordering of KEY values
	 * @param <KEY> the key type for the mapping
	 * @param <TARGET> the object type of the mapping
	 */
	public static class OrderedMap <KEY,TARGET> extends HashMap <KEY, TARGET>
	{
		/**
		 * @return ordered list of KEY values
		 */
		public ItemList <KEY> getOrderedValues ()
		{
			ItemList <KEY> values = new ItemList <> ();
			values.addAll ( this.keySet () ); values.sort (null);
			return values;
		}
		private static final long serialVersionUID = 2941846059003887184L;
	}


	/**
	 * symbol table translation
	 * @param <TARGET> the object type of the mapping
	 */
	public static class SymbolicMap <TARGET>
				extends OrderedMap <String, TARGET>
	{ private static final long serialVersionUID = -58202141273735090L; }


	/**
	 * text Identifier with generic value
	 * @param <V> type of values being mapped
	 */
	public static class NamedValue <V>
	{
		/**
		 * constructor for name value pair
		 * @param name the name part of the pair
		 * @param value the value part of the pair
		 */
		public NamedValue (String name, V value)
		{this.nameOfSymbol = name; this.namedValue = value; }
		public String getIdentifier () { return nameOfSymbol; }
		public String toString () { return namedValue.toString (); }
		public V getIdentifiedContent () { return namedValue; }
		private String nameOfSymbol; private V namedValue;
	}


	/**
	 * lookup table for named values
	 * @param <V> type of values being mapped
	 */
	public static class IdentifiedValue <V> extends SymbolicMap < NamedValue < V > >
	{
		/**
		 * @param name symbol to be assigned
		 * @param value the generic value to assign to symbol
		 */
		public void include (String name, V value)
		{ this.put ( name, new NamedValue < V > ( name, value ) ); }

		/**
		 * lookup value directly from name
		 * @param name the symbol that was assigned
		 * @return the generic value assigned to the symbol
		 */
		public V getIdentifiedContent (String name)
		{ return this.get (name).getIdentifiedContent (); }

		private static final long serialVersionUID = -7725017701187451480L;
	}


}

