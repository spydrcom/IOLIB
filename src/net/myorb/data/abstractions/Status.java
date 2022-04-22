
package net.myorb.data.abstractions;

/**
 * capture information for status updates
 * @author Michael Druckman
 */
public class Status
{


	public enum Level { PLAIN, INFO, WARN, ERROR }

	public static final String DEFAULT_TITLE = "Status Change Notice";


	/**
	 * identify source of information
	 */
	public interface Localization
	{
		/**
		 * used by JOptionPane to select placement of dialog on screen
		 * @return the component associated with an error condition
		 */
		Object getAssociatedComponent ();

		/**
		 * used by JOptionPane to present frame for dialog
		 * @return a title to use generating an error dialog
		 */
		String getTitleForErrorDialog ();
	}


	/**
	 * @param level a level setting is required for all Status objects
	 */
	public Status (Level level)
	{
		setLevel (level);
	}

	public Status (String message, Level level)
	{
		this (level); setMessage (message);
	}

	public Status (Exception exception)
	{
		this (exception.getMessage (), Level.ERROR); setCause (exception);
	}

	public Status (Exception exception, Localization localization)
	{
		this (exception); setLocalization (localization);
	}

	public Status (Object component, String message, Level level)
	{
		this (message, level); setComponent (component);
	}

	public Status (String message, Level level, Localization localization)
	{
		this (message, level); setLocalization (localization);
	}


	/**
	 * simple start for chain construction
	 * @param level the level of the status is required
	 * @return new Status instance set to specified level
	 */
	public static Status newInstance (Level level) { return new Status (level); }
	public static Status newErrorStatus () { return newInstance (Status.Level.ERROR); }


	/**
	 * use dialog information to express error
	 * @param component a component to be associated with error
	 * @param message the text of the error message to be displayed
	 * @param title the title for the dialog frame showing the error
	 * @return the completed Status object
	 */
	public static Status genError (Object component, String message, String title)
	{
		return newErrorStatus ().setMessage (message).setComponent (component).setTitle (title);
	}


	/*
	 * properties of a Status
	 */

	private Level level;
	private String message;
	private Exception cause = null;
	private Object component = null;
	private String title = null;


	/*
	 * get/set properties
	 */

	public Level getLevel () { return level; }
	public Status setLevel (Level level) { this.level = level; return this; }

	public String getMessage () { return message; }
	public Status setMessage (String message) { this.message = message; return this; }

	public void linkCause (Exception exception)
	{ if (cause != null) exception.initCause (cause); }
	public Status setCause (Exception cause) { this.cause = cause; return this; }
	public Exception getCause () { return cause; }

	public Object getComponent () { return component; }
	public Status setComponent (Object component) { this.component = component; return this; }

	public String getTitle () { return title==null? DEFAULT_TITLE: title; }
	public Status setTitle (String title) { this.title = title; return this; }


	/**
	 * @param localization an object holding Localization properties
	 * @return THIS status with Localization properties set
	 */
	public Status setLocalization (Localization localization)
	{
		setComponent (localization.getAssociatedComponent ());
		setTitle (localization.getTitleForErrorDialog ());
		return this;
	}
	public Status fillLocalization (Localization localization)
	{
		if (component == null) setComponent (localization.getAssociatedComponent ());
		if (title == null) setTitle (localization.getTitleForErrorDialog ());
		return this;
	}


	/**
	 * build description of the source of an issue
	 * @param component the GUI component associated with the issue
	 * @param title a title for the dialog to be presented
	 * @return a localization object with the description
	 */
	public static Localization describeSource (final Object component, final String title)
	{
		return new Localization ()
				{
					public Object getAssociatedComponent() { return component; }
					public String getTitleForErrorDialog() { return title; }
				};
	}


}

