
package net.myorb.gui.components;

import javax.swing.JPanel;
import javax.swing.JDialog;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.SwingUtilities;
import javax.swing.RootPaneContainer;
import javax.swing.JInternalFrame;
import javax.swing.ImageIcon;
import javax.swing.JMenuBar;
import javax.swing.JFrame;

import java.awt.Dialog.ModalityType;

import java.awt.Window;
import java.awt.Toolkit;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Point;

/**
 * this is a collection of static methods for common GUI ops.
 * tools for generic JNLP utilities for swing applications
 * @author Michael Druckman
 */
public class GuiToolkit extends Alerts
{


	/**
	 * @param fromFilePath path to icon graphic file
	 * @return the icon image representation
	 */
	public static ImageIcon getIcon (String fromFilePath)
	{
		return new ImageIcon (fromFilePath);
	}

	/**
	 * @param filePath path to image file
	 */
	public static void setApplicationIcon
	(String filePath) { icon = getIcon (filePath); }
	static ImageIcon icon = null;

	/**
	 * @param display the object to be represented by icon
	 */
	public static void setIcon (DisplayContainer display)
	{
		if (icon == null) return;
		setIcon (display, icon);
	}

	/**
	 * @param display the object to be represented by icon
	 * @param to the icon to be used
	 */
	public static void setIcon
	(DisplayContainer display, ImageIcon to)
	{
		if (display instanceof JInternalFrame)
		{ ((JInternalFrame) display).setFrameIcon (to); }
		display.setIconImage (to.getImage ());
	}


	/**
	 * @param frameController establish a Desktop control object
	 */
	public static void setFrameController
		(MdiFrameController frameController)
	{ desktopControl = frameController; }

	/**
	 * @param menuBar the menu bar for the master frame
	 */
	public static void setMenuBar (JMenuBar menuBar)
	{ if (desktopControl != null) desktopControl.setMenuBar (menuBar); }
	public static JDesktopPane getDesktop ()
	{
		if (desktopControl == null) return null;
		return desktopControl.getDesktop ();
	}

	/**
	 * @param title text for title of display object
	 * @return a DisplayContainer for the allocated frame
	 */
	public static DisplayContainer newDisplayContainer (String title)
	{
		DisplayContainer display;
		if (desktopControl == null)
		{ display = new DisplayRoot (title); }
		else display = desktopControl.addNewFrame (title);
		setIcon (display);
		return display;
	}
	static MdiFrameController desktopControl = null;

	/**
     * @param d the dimensions of the item to center
     * @return the point of the location for center
     */
    public static Point center (Dimension d)
    {
        Dimension screen =
                Toolkit.getDefaultToolkit ().getScreenSize ();
        int h = (int) (screen.getHeight () / 2 - d.getHeight () / 2);
        int w = (int) (screen.getWidth () / 2 - d.getWidth () / 2);
        return new Point (w, h);
    }

    public static void center (JDialog dialog)
    {
        dialog.setLocation (center (dialog.getSize ()));
    }

    public static void center (RootPaneContainer frame)
    {
    	asComponent (frame).setLocation (center (asComponent (frame).getSize ()));
    }

    public static void center (Component component)
    {
        component.setLocation (center (component.getSize ()));
    }

    /**
     * @param frame the frame to be shown
     */
    public static void showAtLocation (final RootPaneContainer frame)
    {
        invoke
        (
        	() ->
       		{
       			asComponent (frame).setVisible (true);
            }
        );
    }

    /**
     * @param frame the frame to treat as container
     * @return the object as a container
     */
    public static Container asContainer (final RootPaneContainer frame)
    {
    	return (Container) frame;
    }

    /**
     * @param frame the frame to treat as Component
     * @return the object as a Component
     */
    public static Component asComponent (final RootPaneContainer frame)
    {
    	return (Component) frame;
    }

    /**
     * @param frame the container to test
     * @return TRUE for instance of JFrame
     */
    public static boolean isCommonFrame (final RootPaneContainer frame)
    {
    	return frame instanceof JFrame;
    }

    /**
     * @param frame the frame to treat as JFrame
     * @return the object as a JFrame
     */
    public static JFrame asFrame (final RootPaneContainer frame)
    {
    	return (JFrame) frame;
    }

    /**
     * @param frame the container to test
     * @return TRUE for instance of JInternalFrame
     */
    public static boolean isInternalFrame (final RootPaneContainer frame)
    {
    	return frame instanceof MdiFrameController.InternalFrame;
    }

    /**
     * @param frame the frame to treat as JInternalFrame
     * @return the object as a JInternalFrame
     */
    public static JInternalFrame asInternalFrame (final RootPaneContainer frame)
    {
    	return (MdiFrameController.InternalFrame) frame;
    }

    /**
     * @param frame the frame to treat as window
     * @return the object as a Window
     */
    public static Window asWindow (final RootPaneContainer frame)
    {
    	if (isInternalFrame (frame))
    	{
    		MdiFrameController.InternalFrame iFrame =
    				(MdiFrameController.InternalFrame) frame;
    		return iFrame.getMdiFrameController ().getMasterFrame ();
    	}
    	return (Window) frame;
    }

    /**
     * @param frame the container to show
     * @param p the location at which to show
     */
    public static void showAtLocation (final RootPaneContainer frame, Point p)
    {
    	asComponent (frame).setLocation (p); showAtLocation (frame);
    }

    /**
     * @param frame the container to show
     */
    public static void centerAndShow (final RootPaneContainer frame)
    {
    	center (frame); showAtLocation (frame);
    }

    /**
     * functionality shared for window types frame / dialog
     * @param container the inner container for the window object
     * @param component the component to be added to the container
     * @param window the window sub-class sharing functionality
     * @param parent the parent object for display placement
     */
    public static void addComponentTo
    	(
    		Container container, Component component, 
    		Window window, JFrame parent
    	)
    {
        container.setLayout
        	(new BorderLayout (5, 5));
        container.add (component);

        window.pack ();
        if (parent != null)
        { GuiToolkit.centerRelativeTo (window, parent); }
        invokeVisible (window);
    }

    /**
     * @param child the component to reposition
     * @param parent the relative component to use for positioning
     */
    public static void centerRelativeTo
    	(Container child, Container parent)
    {
        Dimension dim = parent.getSize ();
        Point loc = parent.getLocationOnScreen ();

        Dimension size = child.getSize ();

        loc.x += (dim.width - size.width) / 2;
        loc.y += (dim.height - size.height) / 2;

        if (loc.x < 0) {
            loc.x = 0;
        }
        if (loc.y < 0) {
            loc.y = 0;
        }

        Dimension screen = child.getToolkit ().getScreenSize ();

        if (size.width > screen.width) {
            size.width = screen.width;
        }
        if (size.height > screen.height) {
            size.height = screen.height;
        }

        if (loc.x + size.width > screen.width) {
            loc.x = screen.width - size.width;
        }
        if (loc.y + size.height > screen.height) {
            loc.y = screen.height - size.height;
        }

        child.setBounds (loc.x, loc.y, size.width, size.height);
    }

    /**
     * set the frame icon
     * @param resource path to the icon resource
     * @param f the frame for which to set the icon
     */
    public static void setIcon (String resource, DisplayContainer f)
    {
        try
        {
            f.setIconImage
            (
            	createImageIcon (resource, "Icon").getImage ()
            );
        }
        catch (Exception x) {}
    }

    /**
     * @param container container JFrame or JInternalFrame
     * @param to new value for visible
     */
    public static void setVisible (RootPaneContainer container, boolean to)
    {
    	asComponent (container).setVisible (to);
    }


    /**
     * force frame always-on-top
     * @param container container JFrame or JInternalFrame
     */
    public static void setTop (RootPaneContainer container)
    {
    	asFrame (container).setAlwaysOnTop (true);
    }


    /**
     * @param container JFrame or JInternalFrame
     * @param title the text of the title
     */
    public static void setTitle (RootPaneContainer container, String title)
    {
    	if (isCommonFrame (container))
    	{
    		asFrame (container).setTitle (title);
    	}
    	else if (isInternalFrame (container))
    	{
    		asInternalFrame (container).setTitle (title);
    	}
    }

    /**
     * @param container JFrame or JInternalFrame
     */
    public static void dispose (RootPaneContainer container)
    {
    	if (isCommonFrame (container))
    	{
    		asFrame (container).dispose ();
    	}
    	else if (isInternalFrame (container))
    	{
    		asInternalFrame (container).dispose ();
    	}
    }

    /**
     * @param container JFrame or JInternalFrame
     * @param op value of OP to use
     */
    public static void setCloseOp (RootPaneContainer container, int op)
    {
    	if (isCommonFrame (container))
    	{
    		asFrame (container).setDefaultCloseOperation (op);
    	}
    	else if (isInternalFrame (container))
    	{
    		asInternalFrame (container).setDefaultCloseOperation (op);
    	}
    }

    public static void setCloseExit (RootPaneContainer container)
    {
    	setCloseOp (container, JFrame.EXIT_ON_CLOSE);
    }

    public static void setCloseDispose (RootPaneContainer container)
    {
    	setCloseOp (container, JFrame.DISPOSE_ON_CLOSE);
    }

    public static void setCloseHide (RootPaneContainer container)
    {
    	setCloseOp (container, JFrame.HIDE_ON_CLOSE);
    }

    /**
     * create an image-icon from the specified resource bundled with this class
     * @param path the path to the resource
     * @param description description of the resource
     * @return an <code>ImageIcon</code> for the specified resource
     */
    public static ImageIcon createImageIcon
    	(String path, String description)
    {
        java.net.URL url;
        if ((url = Frame.getResource (path)) != null)
        {
            return new ImageIcon (url, description);
        }
        else
        {
            System.err.println ("Couldn't find icon: " + path);
            return null;
        }
    }

    /**
     * @param title text of title for frame
     * @param c the component to display as frame contents
     * @param resource an icon for display on frame
     * @return a DisplayContainer for the frame
     */
    public static DisplayContainer createFrame
    	(String title, Component c, String resource)
    {
    	DisplayContainer frame = newDisplayContainer (title);
        frame.getContentPane ().add (c);
        setIcon (resource, frame);
        frame.pack ();
        return frame;
    }

    public static DisplayContainer createFrame
    	(
            String title, Component c,
            Dimension size, String resource
    	)
    {
    	DisplayContainer frame =
        	createFrame (title, c, resource);
        frame.setSize (size);
        return frame;
    }

    public static DisplayContainer createFrame
    	(
            String title, Component c,
            Dimension preferredSize,
            boolean exitOnClose
    	)
    {
    	DisplayContainer frame = newDisplayContainer (title);
        if (exitOnClose) setCloseExit (frame);
        frame.getContentPane ().add (c);
        frame.setSize (preferredSize);
        return frame;
    }

    public static DisplayContainer displayFrame
    	(
            String title, Component c,
            Dimension preferredSize,
            boolean exitOnClose
    	)
    {
    	DisplayContainer f = createFrame
        	(title, c, preferredSize, exitOnClose);
        GuiToolkit.centerAndShow (f);
        return f;
    }

    /**
     * @param c the frame/internal/dialog for the content
     * @param title text of title for frame display header
     * @param content the component to display as frame contents
     */
    public static void setContent
    	(
    		Container c, String title,
    		Component content
    	)
    {
        Container p = null;

        if (c instanceof JDialog)
        {
            JDialog d = (JDialog) c;
            p = d.getContentPane ();
            d.setTitle (title);
        }
        else if (c instanceof JFrame)
        {
            JFrame f = (JFrame) c;
            p = f.getContentPane ();
            f.setTitle (title);
        }
        else if (c instanceof JInternalFrame)
        {
        	JInternalFrame f = (JInternalFrame) c;
            p = f.getContentPane ();
            f.setTitle (title);
        }

        p.removeAll ();
        p.add (content);
    }

    /**
     * @param title text of title for frame display header
     * @param c the frame/internal/dialog for the content
     * @param parent the DisplayContainer to associate
     * @param size the dimensions of the dialog
     * @return the dialog object
     */
    public static JDialog displayModalDialog
    	(
    		String title, JComponent c,
    		DisplayContainer parent, Dimension size
    	)
    {
        final JDialog dialog = new JDialog
        	(
        		asWindow (parent), ModalityType.APPLICATION_MODAL
        	);
        JPanel cp = (JPanel) dialog.getContentPane ();
        cp.setBorder (new javax.swing.border.EmptyBorder (0, 0, 0, 0));
        cp.setLayout (new BorderLayout (0, 0));
        c.setPreferredSize (size);
        cp.add (c);

        dialog.setTitle (title);
        dialog.setDefaultCloseOperation (JDialog.HIDE_ON_CLOSE);
        dialog.pack ();

        centerRelativeTo (dialog, asContainer (parent));
        return dialog;
    }

    /**
     * @param c the container to make visible
     */
    public static void invokeVisible (final Container c)
    {
        invoke
        (
        	() ->
        	{
        		c.setVisible (true);
        	}
        );
    }

    /**
     * @param task a runnable task to invoke later
     */
    public static void invoke (Runnable task)
    {
        SwingUtilities.invokeLater (task);
    }

}

