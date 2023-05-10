
package net.myorb.gui;

import net.myorb.data.abstractions.ConcurrentArrayList;

import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.Lock;

/**
 * serialize computations related to Background Tasks
 * @author Michael Druckman
 */
public class BackgroundTaskQueue implements BackgroundTask.Completed
{


	public BackgroundTaskQueue ()
	{
		this.queue = new ConcurrentArrayList <> ();
		this.rwLock = new ReentrantReadWriteLock ();
		this.readLock = rwLock.readLock ();
	}
	protected ConcurrentArrayList <BackgroundTask> queue;
	protected ReentrantReadWriteLock rwLock;
	private final Lock readLock;


	/**
	 * @param task a Runnable object to be scheduled in the background
	 */
	public void schedule (Runnable task)
	{
		schedule (new BackgroundTask (task, this));
	}


	/**
	 * @param task a BackgroundTask to be scheduled
	 */
	public void schedule (BackgroundTask task)
	{
		this.queue.add (task);
		this.runNextAvailable ();
	}


	/**
	 * run the next task available if status allows
	 */
	public void runNextAvailable ()
	{
		try
		{
			BackgroundTask t;
			this.readLock.lock ();
			if (this.taskIsActive) return;
			if ( (t = queue.getFirst ()) == null ) return;
			this.taskIsActive = true;
			t.startTask ();
		}
		finally { this.readLock.unlock (); }
	}
	protected boolean taskIsActive = false;


	/* (non-Javadoc)
	 * @see net.myorb.gui.BackgroundTask.Completed#processCompletion()
	 */
	public void processCompletion ()
	{
		// mark task completed
		this.taskIsActive = false;

		// and determine if next is ready
		this.runNextAvailable ();
	}


}

