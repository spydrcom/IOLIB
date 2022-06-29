
package net.myorb.gui;

import javax.swing.SwingWorker;

/**
 * SwingWorker wrapper for tasks to run under swing APP threads
 * @author Michael Druckman
 */
public class BackgroundTask extends SwingWorker<Object, Object>
{

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
	 * @see javax.swing.SwingWorker#doInBackground()
	 */
	protected Object doInBackground () throws Exception
	{
		task.run ();
		return null;
	}

}
