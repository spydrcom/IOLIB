
package net.myorb.utilities;

import java.util.ArrayList;

/**
 * collect items to be processed and submit in order for processing
 * @param <T> the type of the queue items
 */
public abstract class ProcessingQueue<T> extends ArrayList<T>
{

	/**
	 * the processing to be done for each item
	 * @param item the item from the queue
	 */
	public abstract void process (T item);

	/**
	 * queue submission request, each item processed in turn
	 */
	public void processItems () { for (T item : this) process (item); }

	private static final long serialVersionUID = 1L;
}