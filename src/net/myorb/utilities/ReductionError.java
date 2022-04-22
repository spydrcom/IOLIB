
package net.myorb.utilities;

/**
 * exception that can easily reduce to runtime exception
 * @author Michael Druckman
 */
public class ReductionError extends Exception
{

	/**
	 * throw as RuntimeException with cause
	 */
	public void changeToNotification ()
	{
		throw new RuntimeException (getMessage (), this);
	}

	/**
	 * @param message text of notification
	 */
	public ReductionError (String message) { super (message); }

	private static final long serialVersionUID = 5964687640490733238L;
}
