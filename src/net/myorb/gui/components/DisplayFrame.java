
package net.myorb.gui.components;

import javax.swing.RootPaneContainer;

import javax.swing.JComponent;
import javax.swing.JMenuBar;
import javax.swing.JButton;

import java.awt.event.KeyListener;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * construct frame for panel
 * @author Michael Druckman
 */
public class DisplayFrame implements Runnable
{


	public static final int DEFAULT_DISPLAY_AREA_SIZE = 700;


	public static final int MARGIN = 20;


	static HashMap <String, Color> colors;


	/*
	 * recognized colors
	 */

	static
	{
		colors =
			new HashMap<String, Color> ();
		colors.put ("BLACK", Color.black);
		colors.put ("ORANGE", Color.orange);
		colors.put ("GREEN", Color.green);
		colors.put ("WHITE", Color.white);
		colors.put ("BLUE", Color.blue);
		colors.put ("RED", Color.red);
	}


	/**
	 * get the AWT color from the name
	 * @param named the name of the color
	 * @return the AWT color object
	 */
	public static Color getColor (String named)
	{
		return colors.get (named);
	}


	/**
	 * generic display of a panel
	 * @param component the component to display
	 * @param title the title for the frame
	 */
	public DisplayFrame (Component component, String title)
	{
		this.title = title;
		this.component = component;
	}
	protected Component component = null;


	public String getTitle () { return title; }
	public void setTitle (String title) { this.title = title; GuiToolkit.setTitle (frame, title); }
	protected String title;


	/**
	 * addition of menu bar
	 * @param component the component to display
	 * @param title the title for the frame
	 * @param menuBar menu bar for frame
	 */
	public DisplayFrame (JComponent component, String title, JMenuBar menuBar)
	{ this (component, title); this.menuBar = menuBar; }

	/**
	 * allow frame built by widget content
	 * @param widget the widget to use as primary component
	 * @param title the title for the frame
	 */
	public DisplayFrame (SimpleScreenIO.Widget widget, String title)
	{ this (widget.toComponent (), title); }

	/**
	 * basic frame, all properties to be set separately
	 */
	public DisplayFrame () {}


	/**
	 * @param frame object holding frame
	 * @return the root panel
	 */
	public static Container getFrameRoot (Object frame)
	{ return toFrame (frame).getContentPane (); }
	public static DisplayContainer toFrame (Object frame)
	{ return (DisplayContainer) frame; }


	/**
	 * @param path file holding icon graphic
	 */
	public void setIcon (String path)
	{
		iconPath = path;
	}
	String iconPath = null;


	/**
	 * @param preferredSize dimensions of frame
	 * @return newly constructed frame
	 */
	DisplayContainer construct (Dimension preferredSize)
	{
		DisplayContainer f = GuiToolkit.newDisplayContainer (title);
		if (preferredSize != null) f.setPreferredSize (preferredSize);
		if (iconPath != null) GuiToolkit.setIcon (f, GuiToolkit.getIcon (iconPath));
        if (menuBar != null) f.setJMenuBar (menuBar);
        f.add (component); f.pack (); frame = f;
        return frame;
	}
	protected MdiFrameController mdi = null;


	/**
	 * @param menuBar menu object to be used
	 */
	public void setMenuBar (JMenuBar menuBar)
	{
		this.menuBar = menuBar;
	}
	protected JMenuBar menuBar = null;


	/**
	 * @return new frame with size set from content panel
	 */
	DisplayContainer constructPreSized ()
	{
		DisplayContainer f = construct (null);
		GuiToolkit.asComponent (f).setMinimumSize
			(component.getMinimumSize ());
		return f;
	}


	/**
	 * @param button the button to become frame default
	 */
	public void setDefaultButton (JButton button)
	{ frame.getRootPane ().setDefaultButton (button); }


	/**
	 * remove frame from screen
	 */
	public void dispose () { GuiToolkit.dispose (frame); }
	public void forceToScreen () { GuiToolkit.setVisible (frame, true); }
	public void removeFromScreen () { GuiToolkit.setVisible (frame, false); }
	public DisplayContainer getSwingComponent () { return frame; }
	protected DisplayContainer frame = null;

	public interface InputListener extends KeyListener {}
	public void setInputListener (InputListener inputListener)
	{
		GuiToolkit.asComponent (frame).addKeyListener (inputListener);
	}


	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run ()
	{
		GuiToolkit.setVisible (construct (null), true);
	}


	/**
	 * submit to event queue
	 * @return the new frame object
	 */
	public DisplayContainer show ()
	{
		DisplayContainer f;
		//EventQueue.invokeLater (this);
		GuiToolkit.centerAndShow (f = construct (null));
        GuiToolkit.setCloseDispose (f);
        frameCalled.put (title, f);
        frames.add (f);
        lastFrame = f;
        return f;
	}
	public DisplayContainer show (Dimension preferredSize)
	{
		DisplayContainer f;
		//EventQueue.invokeLater (this);
		GuiToolkit.centerAndShow (f = construct (preferredSize));
        GuiToolkit.setCloseDispose (f);
        frameCalled.put (title, f);
        frames.add (f);
        lastFrame = f;
        return f;
	}
	public DisplayContainer showOrHide (Dimension preferredSize)
	{
		DisplayContainer f;
		//EventQueue.invokeLater (this);
		GuiToolkit.centerAndShow (f = construct (preferredSize));
        GuiToolkit.setCloseHide (f);
        frameCalled.put (title, f);
        frames.add (f);
        lastFrame = f;
        return f;
	}
	public RootPaneContainer showOrHide (int w, int h)
	{
		return showOrHide (new Dimension (w, h));
	}
	public RootPaneContainer showOrHide ()
	{
		DisplayContainer f;
		//EventQueue.invokeLater (this);
		GuiToolkit.centerAndShow (f = constructPreSized ());
        GuiToolkit.setCloseHide (f);
        frameCalled.put (title, f);
        frames.add (f);
        lastFrame = f;
        return f;
	}
	public RootPaneContainer showAt (Point p)
	{
		DisplayContainer
			f = construct (null);
        GuiToolkit.setCloseDispose (f);
		if (p == null) GuiToolkit.centerAndShow (f);
		else GuiToolkit.showAtLocation (f, p);
        frameCalled.put (title, f);
        frames.add (f);
        lastFrame = f;
        return f;
	}
	public void showAndExit ()
	{
		DisplayContainer f;
		//EventQueue.invokeLater (this);
		GuiToolkit.centerAndShow (f = construct (null));
		// GuiToolkit.setIcon ("logo.gif", f);
        GuiToolkit.setCloseExit (f);
        frameCalled.put (title, f);
        frames.add (f);
        lastFrame = f;
	}
	public static DisplayContainer lastFrame = null;

	/**
	 * remove most recent addition
	 * @return the frame removed
	 */
	public static RootPaneContainer removeLast ()
	{
		int n = frames.size () - 1;
		RootPaneContainer last = frames.get (n);
		frames.remove (n);
		return last;
	}
	public static ArrayList <RootPaneContainer> frames = new ArrayList <> ();

	public static RootPaneContainer getFrame (String called) { return frameCalled.get (called); }
	public static HashMap <String, RootPaneContainer> frameCalled = new HashMap <> ();

	public static void alwaysOnTop ()
	{
		if (GuiToolkit.isCommonFrame (lastFrame))
		{
			GuiToolkit.asFrame (lastFrame).setAlwaysOnTop (true);
		}
	}


	/**
	 * @param to new title to be set on last frame shown
	 */
	public static void changeTitle (String to)
	{
		if (lastFrame == null)
			throw new RuntimeException ("Last frame not set");
		GuiToolkit.setTitle (lastFrame, to);
	}

	public static void changeToExit ()
	{
        GuiToolkit.setCloseExit (lastFrame);
	}


}

