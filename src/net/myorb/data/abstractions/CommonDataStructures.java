
package net.myorb.data.abstractions;

import net.myorb.data.abstractions.SpaceDescription;

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
	 * @param toList the list being appended
	 * @param <T> data type being used
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
	 * SymbolicMap for numeric values
	 */
	public static class NamedValueMap extends SymbolicMap <Number>
	{

		public NamedValueMap () {}
		public NamedValueMap (String format) { this.format = format; }
		protected String format;

		/**
		 * get value of name formatted as specified
		 * @param name the symbol to find in map
		 * @return the formatted value
		 */
		public String formatted (String name)
		{
			return String.format (format, get (name));
		}

		/**
		 * @return a list of the names
		 */
		public TextItems names ()
		{
			return new TextItems ( keySet () );
		}

		/**
		 * get value sorted symbol references
		 * @return sorted names from value map
		 */
		public TextItems ordered ()
		{
			TextItems enumeration = names ();
			enumeration.sort ( (i1, i2) -> ordered ( i1, i2 ) );
			return enumeration;
		}

		/**
		 * comparator for value map entries
		 * @param item1 name of one map entry
		 * @param item2 name of another map entry
		 * @return 1 for LT and -1 for GT
		 */
		public int ordered (String item1, String item2)
		{
			double
				i1 = get (item1).doubleValue (),
				i2 = get (item2).doubleValue ();
			return i1 < i2 ? 1 : -1;
		}

		private static final long serialVersionUID = 2460668451896898561L;
	}


	/**
	 * name-value pairs forming a symbol table
	 */
	public static class NumericParameterization extends SymbolicMap <Number>
	{
		/**
		 * @param name a symbol name to evaluate
		 * @param defaultValue value to use if not mapped
		 * @return the value determined for the symbol given
		 */
		public Number evaluate (String name, Number defaultValue)
		{
			Number found;
			if ( (found = this.get (name)) == null ) return defaultValue;
			else return found;
		}

		/**
		 * @param name symbol to be assigned
		 * @param value the value to give the symbol
		 */
		public void assign (String name, Number value) { put (name, value); }
		private static final long serialVersionUID = -8411978027902480605L;
	}


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


	/**
	 * treat list of values as vector
	 */
	public static class Vector <T> extends ItemList <T>
	{

		public Vector () {}

		public Vector (Collection <T> items) { super (items); }

		/**
		 * construct a parameter list for a function call
		 * @param items the values of the parameter to be included
		 * @param converter a conversion object for the data type
		 */
		public Vector (Number [] items, SpaceConversion <T> converter)
		{
			for (Number item : items)
			{
				this.add ( converter.convertFromDouble ( item.doubleValue () ) );
			}
		}

		/**
		 * compute sum of squares of vector components
		 * - standard Pythagorean algorithm for distance computation
		 * @param converter a conversion object for the data type
		 * @return the magnitude
		 */
		public double computeMagnitude (SpaceConversion <T> converter)
		{
			double result = 0.0, v;
			for (int i = 0; i < this.size (); i++)
			{
				v = converter.convertToDouble (this.get (i));
				result += v * v;
			}
			return Math.sqrt (result);
		}

		/**
		 * add elements in point
		 * @param manager a space description for point domain
		 * @return the sum of the elements
		 */
		public T sumOfElements (SpaceDescription <T> manager)
		{
			T result = manager.getZero ();
			for (int i = 0; i < this.size (); i++)
			{ result = manager.add (result, this.get (i)); }
			return result;
		}

		private static final long serialVersionUID = 877255080759661227L;

	}


}

