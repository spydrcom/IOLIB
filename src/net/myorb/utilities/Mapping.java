
package net.myorb.utilities;

import java.util.*;

/**
 * An extended form of Map implementation
 * @author Michael Druckman
 */
public class Mapping
{

	/*
	 * mappings that support generic key values
	 */

	/**
	 * Associate a collection of objects with an object that describes the association
	 * @param <Key> the key for the map of associated items to the reference
	 * @param <Group> object type for collection of associated items
	 * @author Michael Druckman
	 */
	public static abstract class Association < Key, Group >
			extends HashMap < Key, Group >
	{

		/**
		 * @return a collection to associate with the key being described
		 */
		public abstract Group allocate ();

		/**
		 * @param item a key value to be associated with a collection
		 * @return the collection object associated with the key
		 */
		public Group getItemsFor (Key item)
		{
			Group collection; if ( ( collection = get (item) ) == null )
			{ collection = allocate (); put (item, collection); }
			return collection;
		}

		private static final long serialVersionUID = 5604323361054351785L;

	}

	/**
	 * connect an item with a key
	 * @param <Key> association identity
	 */
	public interface IdentifiedItem < Key >
	{
		/**
		 * @param key associated key for item
		 */
		void setKey (Key key);

		/**
		 * @return associated key for item
		 */
		Key getKey ();
	}

	/**
	 * group items with common key
	 * @param <Key> key common to items in group
	 */
	public interface IdentifiedGroup < Key >
	{
		/**
		 * @param item the object to place into group
		 */
		void include (IdentifiedItem <Key> item);
	}

	/**
	 * permit multiple object to be mapped from common key
	 * @param <Key> the type of key connecting items
	 */
	public static abstract class OneToMany < Key >
			extends Association < Key, IdentifiedGroup <Key> >
	{

		/**
		 * @param item object to be included in mapping for associated key
		 */
		public IdentifiedGroup <Key> include (IdentifiedItem <Key> item)
		{
			IdentifiedGroup <Key> G;
			( G = getItemsFor ( item.getKey () ) ).include (item);
			return G;
		}

		private static final long serialVersionUID = 3257536523490438922L;
		
	}


	/*
	 * mappings using text based key values (associations based on String key values)
	 */

	/**
	 * a base class for items holding text for key values
	 */
	public static class TextKeyedItem implements IdentifiedItem < String >
	{
		/* (non-Javadoc)
		 * @see net.myorb.utilities.Mapping.IdentifiedItem#setKey(java.lang.Object)
		 */
		public void setKey (String text) { this.text = text; }

		/* (non-Javadoc)
		 * @see net.myorb.utilities.Mapping.IdentifiedItem#getKey()
		 */
		public String getKey () { return text; }

		protected String text = null;
	}

	/**
	 * groups of objects for text based mapping (implemented as ArrayList of TextKeyedItem)
	 */
	@SuppressWarnings("serial") public static class TextKeyedGroup
		extends ArrayList < IdentifiedItem <String> > implements IdentifiedGroup < String >
	{
		/* (non-Javadoc)
		 * @see net.myorb.utilities.Mapping.IdentifiedGroup#include(net.myorb.utilities.Mapping.IdentifiedItem)
		 */
		public void include (IdentifiedItem <String> item) { this.add ( (IdentifiedItem <String>) item ); }
	}

	/**
	 * Text based OneToMany mapping
	 */
	@SuppressWarnings("serial") public static class TextToMany extends OneToMany < String >
	{
		/* (non-Javadoc)
		 * @see net.myorb.utilities.Mapping.Association#allocate()
		 */
		public IdentifiedGroup <String> allocate () { return new TextKeyedGroup (); }
	}

}
