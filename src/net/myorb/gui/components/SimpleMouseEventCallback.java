
package net.myorb.gui.components;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Component;

/**
 * mouse event listener wrapper that discards basic event info.
 * exceptions are caught and presented as error dialog specified in SimpleScreenIO
 * @author Michael Druckman
 */
public class SimpleMouseEventCallback extends SimpleCallback 
{

	/**
	 * the types of events that can be caught
	 */
	public enum EventType {CLICKED, DOUBLE_CLICKED, ENTERED, PRESSED}

	/**
	 * complete connection of event type with action
	 * @param callback the object to invoke with doCall
	 * @param type the type of event to associate with action
	 */
	public SimpleMouseEventCallback (SimpleScreenIO.Callback callback, EventType type)
	{ super (callback); this.eventType = type; simpleMouseAdapter = new SimpleMouseAdapter (); }
	protected SimpleMouseAdapter simpleMouseAdapter;
	protected EventType eventType;

	/**
	 * @param component AWT component to have mouse listener added
	 */
	public void addMouseListenerTo (Component component) { component.addMouseListener (simpleMouseAdapter); }

	/**
	 * must implement all Callback methods.
	 * will supply all error handling functions supplied by Callback.
	 */
	public static class Adapter extends SimpleCallback.Adapter
	{
		/**
		 * connects THIS as Callback; properties must be set separately
		 * @param type the type of mouse event to be processed by this adapter
		 */
		public Adapter (EventType type)
		{ super (); mouseCallback = new SimpleMouseEventCallback (this, type); }
		protected SimpleMouseEventCallback mouseCallback;

		/**
		 * properties set and callback connected
		 * @param type the type of mouse event to be processed by this adapter
		 * @param title the title to be used for error dialog frame as needed
		 * @param component the associated component for error dialog
		 */
		public Adapter (EventType type, String title, Object component) { this (type); setProperties (title, component); }

		/**
		 * @return message to be displayed if callback is not yet specified
		 */
		public String unimplementedErrorMessage () { return "Unimplemented Mouse Adapter Callback"; }

		/**
		 * @param component AWT component to have mouse listener added
		 */
		public void addMouseListenerTo (Component component)
		{ mouseCallback.addMouseListenerTo (component); }
	}

	/**
	 * extended AWT Mouse Adapter that uses Callback mechanism when listener event is invoked
	 */
	class SimpleMouseAdapter extends MouseAdapter
	{
		/* (non-Javadoc)
		 * @see java.awt.event.MouseAdapter#mouseClicked(java.awt.event.MouseEvent)
		 */
		public void mouseClicked (MouseEvent event)
		{
			if (eventType != SimpleMouseEventCallback.EventType.DOUBLE_CLICKED)
			{ if (eventType != SimpleMouseEventCallback.EventType.CLICKED) return; }
			else if (event.getClickCount () != 2) return;
			SimpleCallback.doCall (callback);
		}

		/* (non-Javadoc)
		 * @see java.awt.event.MouseAdapter#mouseEntered(java.awt.event.MouseEvent)
		 */
		public void mouseEntered (MouseEvent event)
		{
			if (eventType != SimpleMouseEventCallback.EventType.ENTERED) return;
			SimpleCallback.doCall (callback);
		}

		/* (non-Javadoc)
		 * @see java.awt.event.MouseAdapter#mousePressed(java.awt.event.MouseEvent)
		 */
		public void mousePressed (MouseEvent event)
		{
			if (eventType != SimpleMouseEventCallback.EventType.PRESSED) return;
			SimpleCallback.doCall (callback);
		}
	}

	public static class SingleClickAdapter extends Adapter
	{ public SingleClickAdapter () { super (EventType.CLICKED); } }

	public static class DoubleClickAdapter extends Adapter
	{ public DoubleClickAdapter () { super (EventType.DOUBLE_CLICKED); } }

}



