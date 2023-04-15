
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
		public ItemList (Collection <T> items) { this.addAll (items); }
		private static final long serialVersionUID = -4619972438389917002L;
	}


	/**
	 * lists of symbols
	 */
	public static class TextItems extends ItemList <String>
	{ private static final long serialVersionUID = -58202141273735090L; }


	/**
	 * symbol table translation
	 * @param <TARGET> the object type of the mapping
	 */
	public static class SymbolicMap <TARGET> extends HashMap <String, TARGET>
	{ private static final long serialVersionUID = -58202141273735090L; }


	/**
	 * set of items of specified object type
	 * @param <T> type of items
	 */
	public static class PrimitiveSet <T> extends HashSet <T>
	{
		public PrimitiveSet () {}
		public PrimitiveSet (Collection <T> items) { this.addAll (items); }
		private static final long serialVersionUID = 3039467719588772607L;
	}


}
