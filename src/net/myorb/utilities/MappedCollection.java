
package net.myorb.utilities;

import java.util.HashMap;
import java.util.Map;

/**
 * keep a map with a parallel object list
 * @param <T> the type of items in the collection
 * @author Michael Druckman
 */
public class MappedCollection<T> extends BlockManager.SimleCollection<T>
{

	/**
	 * @param <T> type of items in collection
	 * @param t an empty array of collection items to identify type
	 * @return a CollectionPosting implementation
	 */
	public static <T> MappedCollection<T> newInstance (T[] t)
	{
		return new PostingCollection<T>();
	}

	/**
	 * cause all items added to collection to be posted to map
	 */
	public interface CollectionPosting
	{
		/**
		 * post items to lookup table
		 */
		void postAll ();
	}
	protected Map<String,T> itemMap = new HashMap<String,T>();

	/**
	 * @param name the name to be found
	 * @return the descriptor for the table
	 */
	public T lookup (String name) { return itemMap.get (name); }

	/**
	 * @param name the name to be found
	 * @return the table if found, exception for failure
	 * @throws Exception for any errors
	 */
	public T find (String name) throws Exception
	{
		T item = lookup (name);
		if (item == null) throw new Exception ("Item not found: " + name);
		return item;
	}

	private static final long serialVersionUID = 1067071791950887826L;
}


/**
 * post all items in collection to map
 * @param <T> type of items in collection
 */
class PostingCollection<T> extends MappedCollection<T>
	implements MappedCollection.CollectionPosting
{
	/* (non-Javadoc)
	 * @see net.myorb.utilities.MappedCollection.CollectionPosting#postAll()
	 */
	public void postAll ()
	{
		for (T item : this)
		{
			itemMap.put (item.toString (), item);
		}
	}
	private static final long serialVersionUID = 7705265441949739211L;
}

