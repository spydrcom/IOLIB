
package net.myorb.gui.components;

import net.myorb.utilities.ApplicationShell;

/**
 * provide for use of system APPs as OpenWith menu items
 * @author Michael Druckman
 */
public class AppAccessMenuManager
{


	/**
	 * passing errors out from commands
	 */
	public static class UserError extends ApplicationShell.ShellError
	{
		public UserError (String message) { super (message); }
		private static final long serialVersionUID = -6926687183108632029L;
	}


	/**
	 * change unexpected exception to RuntimeException
	 * @param error exception being processed
	 */
	public static void unexpectedError (Exception error)
	{ error.printStackTrace (); throw new RuntimeException (UE, error); }
	static final String UE = "Unexpected Exception";


	/**
	 * provide access to resources
	 */
	public interface ResourceAccess
	{
		/**
		 * @return a filesystem path to a file resource
		 * @throws Exception for any errors
		 */
		String getSelectedPath () throws Exception;
	}


	/**
	 * menu item for Open With selections
	 */
	public static class OpenWith extends MenuItem
	{
		/**
		 * @param app the name of the APP
		 * @param access the means of access to resource path
		 */
		OpenWith (String app, ResourceAccess access)
		{
			super ("Open With " + app);
			this.access = access;
			this.app = app;
		}
		ResourceAccess access;
		String app;

		/* (non-Javadoc)
		 * @see net.myorb.gui.components.SimpleCallback.Adapter#executeAction()
		 */
		public void executeAction () throws Exception
		{
			try
			{
				ApplicationShell.commandFor (app, access.getSelectedPath ());
			}
			catch (UserError u) { u.changeToNotification (); }
			catch (ApplicationShell.ShellError s) { throw s; }
			catch (Exception e) { unexpectedError (e); }
		}
	}


	/**
	 * @param menu the menu being built
	 * @param access a resource access implementation
	 */
	public static void addToMenu (Menu menu, ResourceAccess access)
	{
		for (String app : ApplicationShell.getApps ())
		{
			menu.add (new OpenWith (app, access));
		}
	}


}

