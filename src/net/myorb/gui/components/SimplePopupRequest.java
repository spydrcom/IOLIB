
package net.myorb.gui.components;

import net.myorb.data.abstractions.Status;

import java.util.List;

/**
 * request user input from menu
 * @param <T> the type of menu item data
 * @author Michael Druckman
 */
public abstract class SimplePopupRequest<T> extends SimpleScreenIO
{


	/**
	 * @return the width of the Field
	 */
	public abstract int getFieldWidth ();

	/**
	 * @return the list of available options
	 */
	public abstract List<T> getOptions ();

	/**
	 * @return a title for the frame
	 */
	public abstract String getFrameTitle ();

	/**
	 * @return pixel height of the frame
	 */
	public abstract int getFrameHeight ();

	/**
	 * @return pixel width of the frame
	 */
	public abstract int getFrameWidth ();

	/**
	 * identify selected item to user
	 * @param selectedItem the item selected from the menu
	 * @return a formatted message to feedback to user
	 */
	public abstract String formatNotificationFor (T selectedItem);

	/**
	 * @param item pass selection to implementation
	 */
	public abstract void setSelectedItem (T item);


	/**
	 * construct menu GUI
	 */
	public SimplePopupRequest ()
	{
		Panel p = new Panel ();
		(field = addField (p, getFieldWidth ())).setEditable (false);
		presentToUser (p); new PopupMenu ().doPopup ();
	}
	private Field field;

	/**
	 * @param p panel to be shown
	 */
	public void presentToUser (Panel p)
	{
		(frame = show
			(
				p, getFrameTitle (), getFrameWidth (), getFrameHeight ()
			)
		).forceToScreen ();
	}
	private Frame frame;

	/**
	 * present notice of update
	 */
	private void notifyUser ()
	{
		String msg = formatNotificationFor (selectedItem);
		if (msg != null) presentToUser (new Status (Status.Level.INFO).setMessage (msg));
	}
	private T selectedItem;

	/**
	 * simple menu component
	 */
	class PopupMenu extends FieldMenu<T> implements Runnable
	{

		/**
		 * use FieldMenu algorithms to display menu over field
		 */
		PopupMenu ()
		{
			super
			(
				field, getOptions ()
			);
		}

		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		public void run ()
		{
			try { Thread.sleep (500); }						// short time lapse before disposal of frame
			catch (Exception e) { e.printStackTrace (); }	// record exceptions in trace record for debugging
			frame.dispose ();								// close menu frame after text has been set
			notifyUser ();									// show verification message
		}

		/* (non-Javadoc)
		 * @see net.myorb.gui.components.FieldMenu#acceptSelection(java.lang.Object)
		 */
		public void acceptSelection (T selected)
		{
			super.acceptSelection (selected);				// fill field with selection name
			setSelectedItem (selectedItem = selected);		// selection sent to implementation
			SimpleScreenIO.startBackgroundTask (this);		// response to user
		}

		private static final long serialVersionUID = -1699560219197981734L;
	}

}

