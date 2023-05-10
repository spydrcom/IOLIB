
package net.myorb.gui;

import javax.swing.SwingWorker;

/**
 * SwingWorker wrapper for tasks to run under swing APP threads
 * @author Michael Druckman
 */
public class BackgroundTask extends SwingWorker<Object, Object>
{

	/**
	 * task completion event notification mechanism
	 */
	public interface Completed
	{
		/**
		 * method called indicating task has completed
		 */
		void processCompletion ();
	}

	public BackgroundTask (Runnable task, Completed completionProcessor)
	{ this (task); this.setCompletionProcessor (completionProcessor); }

	protected Completed completionProcessor = null;
	public void setCompletionProcessor (Completed C)
	{ this.completionProcessor = C; }

	/**
	 * @param task the implementation of the task
	 */
	public BackgroundTask (Runnable task)
	{
		this.task = task;
	}
	protected Runnable task;

	/**
	 * start execution of the task
	 */
	public void startTask ()
	{
		execute ();
	}
	
	/**
	 * terminate execution of the task
	 */
	public void terminateTask ()
	{
		cancel (true);
	}

	/* (non-Javadoc)
	 * @see javax.swing.SwingWorker#done()
	 */
	public void done ()
	{
		if (completionProcessor == null) return;
		completionProcessor.processCompletion ();
	}

	/* (non-Javadoc)
	 * @see javax.swing.SwingWorker#doInBackground()
	 */
	protected Object doInBackground () throws Exception
	{ task.run (); return null; }

}
