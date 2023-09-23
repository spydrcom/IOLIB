
package net.myorb.data.conventional;

import java.util.HashMap;

/**
 * Associate a collection of objects with an object that describes the association
 * @param <Key> the key for the map of associated items to the reference
 * @param <Associated> object type for collection of associated items
 * @author Michael Druckman
 */
public abstract class Association < Key, Associated >
		extends HashMap < Key, Associated >
{

	/**
	 * @return a collection to associate with the key being described
	 */
	public abstract Associated allocate ();

	/**
	 * @param item a key value to be associated with a collection
	 * @return the collection object associated with the key
	 */
	public Associated getItemsFor (Key item)
	{
		Associated collection = get (item);
		if (collection == null)
		{
			collection = allocate ();
			put (item, collection);
		}
		return collection;
	}

	private static final long serialVersionUID = 5604323361054351785L;

}
