
package net.myorb.gui.components;

import java.awt.Component;

import javax.swing.JOptionPane;

/**
 * static methods for message displays to user
 * @author Michael Druckman
 */
public class Alerts
{


	/**
	 * display a message to the user
	 * @param component the parent component
	 * @param message the message to display
	 * @param type of message to display 
	 */
	public static void message (Component component, String message, int type) 
	{
		JOptionPane.showMessageDialog
		(
				component, message, "Message", type
		);
	}


	/**
	 * display an info message to the user
	 * @param component the parent component
	 * @param message the message to display
	 */
	public static void info (Component component, String message) 
	{
		message
		(
				component, message, 
				JOptionPane.INFORMATION_MESSAGE
		);
	}


	/**
	 * display an alert message to the user
	 * @param component the parent component
	 * @param message the message to display
	 */
	public static void warn (Component component, String message) 
	{
		message
		(
				component, message, 
				JOptionPane.WARNING_MESSAGE
		);
	}


	/**
	 * display an error message to the user
	 * @param component the parent component for context
	 * @param exception the exception thrown 
	 */
	public static void error (Component component, Exception exception)
	{
		String msg = exception.getMessage ();
		StringBuffer message = new StringBuffer();
		if (msg == null || msg.length() == 0)
		{
			message.append ("Error: ").append (exception.getClass ().getCanonicalName ());
		}
		else message.append (exception.getMessage ()).append ("\n");
		if (exception != null) { exception.printStackTrace (); }
		warn (component, message.toString ());
	}


	/**
	 * display an exception message to the user
	 * @param component the parent component for context
	 * @param exception the exception thrown 
	 */
	public static void unhandled (Component component, Exception exception)
	{
		StringBuffer message = new StringBuffer();
		message.append ("Error: ").append (exception.getClass ().getCanonicalName ())
			.append ("\n").append ("Cause: ").append (exception.getMessage ()).append ("\n");
		if (exception != null) { exception.printStackTrace (); }
		warn (component, message.toString ());
	}

        
    /**
	 * Pops up an alert warning if selection is more than 1
	 * @param component the component associated with the issue (if found)
	 * @param count the number of selected rows
	 */
	public static void doOnePickWarning (Component component, Integer count)
	{
		if (count > 1) {
			warn (component, "Warning: Only processing the first row selected.");
		}
	}
}
