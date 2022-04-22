
package net.myorb.gui.components;

import net.myorb.data.abstractions.Status;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.awt.Component;

/**
 * action listener wrapper that discards basic event info.
 * exceptions are caught and presented as error dialog specified in SimpleScreenIO
 * @author Michael Druckman
 */
public class SimpleCallback extends SimpleScreenIO implements ActionListener
{


	/**
	 * empty constructor; callback must be set manually
	 */
	protected SimpleCallback () {}

	/**
	 * @param callback the object to invoke with doCall
	 */
	public SimpleCallback (Callback callback) { setCallback (callback); }

	/**
	 * @param callback the properties object identifying the action and the source localization
	 */
	public void setCallback (Callback callback) { this.callback = callback; }
	public Callback getCallback () { return callback; }
	protected Callback callback;


	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed (ActionEvent event) { doCall (callback); }


	/**
	 * perform callback action
	 * @param callback the callback implementation to execute on
	 */
	public static void doCall (Callback callback)
	{
		try
		{
			callback.executeAction ();
		}
		catch (Alert alert)
		{
			if (TRACE)
				alert.printStackTrace ();
			alert.presentDialog (callback);
		}
		catch (Exception e)
		{
			if (TRACE)
				e.printStackTrace ();
			presentToUser (new Status (e, callback));
		}
	}
	protected static boolean TRACE = true;


	/**
	 * must implement all Callback methods.
	 * will supply all error handling functions supplied by Callback.
	 */
	public static class Adapter	extends SimpleCallback implements Callback
	{

		/**
		 * connects THIS as Callback; properties must be set separately
		 */
		public Adapter () { setCallback (this); }

		/**
		 * properties set and callback connected
		 * @param title the title to be used for error dialog frame as needed
		 * @param component the associated component for error dialog
		 */
		public Adapter (String title, Object component)
		{ this (); setProperties (title, component); }

		public Adapter (Status.Localization localization)
		{ this (); setProperties (localization); }


		/**
		 * @param title the title for the error handling dialog frame
		 * @param component the component to be associated with the error dialog
		 */
		public void setProperties (String title, Object component)
		{ this.component = component; this.errorDialogTitle = title; }

		public void setProperties (Status.Localization localization)
		{
			setProperties
			(
				localization.getTitleForErrorDialog (),
				localization.getAssociatedComponent ()
			);
		}

		protected Component getComponent () { return doAssociation (component); }
		protected String errorDialogTitle = null; protected Object component = null;

		/**
		 * @return message to be displayed if callback is not yet specified
		 */
		public String unimplementedErrorMessage () { return "Unimplemented Action"; }
		public Status unimplemented () { return newErrorStatus ().setMessage (unimplementedErrorMessage ()); }

		/* (non-Javadoc)
		 * @see net.myorb.lotto.gui.components.SimpleScreenIO.Callback#executeAction()
		 */
		public void executeAction () throws Exception { alert (unimplemented ()); } // default behavior

		/* (non-Javadoc)
		 * @see net.myorb.lotto.gui.components.SimpleScreenIO.Callback#getTitleForErrorDialog()
		 */
		public String getTitleForErrorDialog () { return errorDialogTitle; } // used in error handler

		/* (non-Javadoc)
		 * @see net.myorb.lotto.gui.components.SimpleScreenIO.Callback#getAssociatedComponent()
		 */
		public Object getAssociatedComponent () { return component; } // used in error handler

	}


	/**
	 * no error expected, reduced need for construction
	 */
	public static class DefaultForUnexpectedError extends Adapter
	{
		public String unimplementedErrorMessage ()
		{ return "No action override found in extended default callback adapter"; }
		public DefaultForUnexpectedError () { super ("Unexpected Error", null); }
	}


}

