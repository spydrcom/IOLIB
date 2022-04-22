
package net.myorb.gui.components;

import javax.swing.*;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.*;
import java.net.URL;

/**
 * helper extension to swing frame
 * @author Michael Druckman
 */
public class Frame extends DisplayRoot
	implements WindowListener
{


	static final long serialVersionUID = -9137784343921565533L;

	/**
	 * control window closing events
	 */
	protected Frame ()
	{
		if (!SwingUtilities.isEventDispatchThread ())
		{
			System.out.println (getClass ().getCanonicalName () +
					" needs to be created on the EDT!");
		}
                
		setDefaultCloseOperation
			(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener (this);
	}

	/**
	 * construct with resource icon
	 * @param resource path to icon for frame display
	 */
	protected Frame (String resource)
	{
		this (); GuiToolkit.setIcon (resource, this);
	}
        

	/**
	 * static access for reading class resource list
	 * @param resource path to resource being sought
	 * @return the URL of the resource
	 */
	public static URL getResource (String resource) 
	{
		if (frame == null) frame = new Frame ();
		return frame.getClass ().getResource (resource); 
	}
	private static Frame frame = null;


	/**
	 * simple access to content pane
	 * @param c component to be added
	 */
	protected void addContent (Component c)
	{ getContentPane ().add (c); }


	/**
	 * prepare to display window content
	 * @param title the title of the window of this content
	 * @param c the components containing content to display
	 */
	protected void setContent (String title, Component c)
	{
		addContent (c);
		setTitle (title);
		pack ();
	}
	protected void setContent (String title)
	{
		setContent (title, _appanel);
	}

	/**
	 * change cursor to indicate wait state
	 */
	public void useWaitCursor ()
	{ setCursor (Cursor.getPredefinedCursor (Cursor.WAIT_CURSOR)); }

	/**
	 * reset cursor once operation complete
	 */
	public void resetDefaultCursor () { setCursor (Cursor.getDefaultCursor ()); }


	// implementation of WindowListener interface

	public void windowClosed(WindowEvent arg0) {}
	public void windowActivated(WindowEvent arg0) {}
	public void windowDeactivated(WindowEvent arg0) {}
	public void windowDeiconified(WindowEvent arg0) {}
	public void windowIconified(WindowEvent arg0) {}
	public void windowOpened(WindowEvent arg0) {}


	/* (non-Javadoc)
	 * @see java.awt.event.WindowListener#windowClosing(java.awt.event.WindowEvent)
	 */
	public void windowClosing (WindowEvent e)
	{
		if (_appanel == null) System.exit (0);
		getContentPane ().remove (_appanel);
		_appanel = null;
	}
	protected void setApplicationPanel (Component applicationPanel)
	{ _appanel = applicationPanel; }
	protected Component _appanel;


}
