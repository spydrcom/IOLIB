
package net.myorb.gui.components;

import javax.swing.JFrame;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;

import javax.swing.SwingUtilities;

import java.awt.Container;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;

import java.util.ArrayList;

/**
 * simple methods and helper classes for implementing MDI frames
 * @author Michael Druckman
 */
public class MdiFrameController
{


	/**
	 * prepare to enumerate list of internal frames
	 */
	public MdiFrameController ()
	{
		frames = new ArrayList<InternalFrame>();
		desk = new JDesktopPane ();
		desk.setVisible (true);
	}


	/**
	 * @return the desktop pane created for this application
	 */
	public JDesktopPane getDesktop () { return desk; }
	JDesktopPane desk;


	/**
	 * simple version of internal frame object
	 */
	public static class InternalFrame extends DisplayChild
	{

		/**
		 * @param title the title for the frame
		 */
		public InternalFrame (String title)
		{
			super (title);
			this.setDefaultCloseOperation (HIDE_ON_CLOSE);
			this.add (mainPanel = new JPanel ());
		}

		public MdiFrameController getMdiFrameController () { return this.frameController; }
		void setMdiFrameController (MdiFrameController frameController)
		{ this.frameController = frameController; }
		MdiFrameController frameController;

		/**
		 * @return a panel assigned as the content container
		 */
		public JPanel getMainPanel () { return mainPanel; }
		private JPanel mainPanel;

		/**
		 * @param c an AWT component to add to the frame
		 */
		public void addComponent (Component c) { mainPanel.add (c); }

		/**
		 * pack and set visible
		 */
		public void packAndExpose ()
		{
			this.pack (); this.setVisible (true);
		}

		private static final long serialVersionUID = 4309596795813467347L;
	}


	/**
	 * @param title a title for the frame
	 * @return the newly created internal frame
	 */
	public InternalFrame addNewFrame (String title)
	{
		InternalFrame newFrame = new InternalFrame (title);
		newFrame.setMdiFrameController (this);
		if (masterFrame != null)
		{
			masterFrame.getContentPane ().add (newFrame);
			desk.add (newFrame);
		}
		else if (frames != null) frames.add (newFrame);
		return newFrame;
	}


	public void
		releaseFrames () { this.frames = null; }
	private ArrayList<InternalFrame> frames = null;
	public static boolean LEGACY_FRAME_ALGORITHM = false;


	/**
	 * @param menuBar the menu bar for the master frame
	 */
	public void setMenuBar (JMenuBar menuBar)
	{ this.menuBar = menuBar; }
	JMenuBar menuBar = null;


	/**
	 * @param title a title for the master frame
	 * @return the master frame object
	 */
	public DisplayRoot constructMasterFrame (String title)
	{
		masterFrame = new DisplayRoot (title);
		masterFrame.setMinimumSize (new Dimension (400, 300));
		if (menuBar != null) { masterFrame.setJMenuBar (menuBar); }
		GuiToolkit.setIcon (masterFrame);
		addInternalFrames (masterFrame);
		masterFrame.add (desk);
		return masterFrame;
	}


	/**
	 * @param master the display frame being built
	 */
	public void addInternalFrames
		(DisplayRoot master)
	{
		JInternalFrame prv = null;
		if (frames == null) return;
		Container contentPane = master.getContentPane ();
		for (InternalFrame f : frames)
		{
			if (prv != null) f.setLocation (offsetHorizFrom (prv));
			contentPane.add (prv = f); desk.add (f); f.packAndExpose ();
		}
		if (LEGACY_FRAME_ALGORITHM) { releaseFrames (); }
	}


	/**
	 * @param frame item to compute relative horizontal distance from
	 * @return the point for the adjacent item
	 */
	public static Point offsetHorizFrom (JInternalFrame frame)
	{
		return new Point (frame.getX () + frame.getWidth () + 10, frame.getY ());
	}


	/**
	 * arrange tiles vertically
	 */
	public void tileVertically ()
	{
		JInternalFrame prv = null;
		if (frames == null) return;
		for (JInternalFrame f : frames)
		{
			if (prv != null) f.setLocation (offsetVertFrom (prv));
			prv = f;
		}
	}


	/**
	 * @param frame item to compute relative vertical distance from
	 * @return the point for the adjacent item
	 */
	public static Point offsetVertFrom (JInternalFrame frame)
	{
		return new Point (frame.getX (), frame.getY () + frame.getHeight () + 10);
	}


	/**
	 * use swing utilities to display master frame
	 */
	public void showMasterFrame ()
	{
		SwingUtilities.invokeLater
		(
			() ->
			{
				masterFrame.pack (); masterFrame.setVisible (true);
				masterFrame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
				masterFrame.setExtendedState (JFrame.MAXIMIZED_BOTH);
			}
		);
	}


	/**
	 * @return access to master frame
	 */
	public DisplayRoot getMasterFrame ()
	{
		return masterFrame;
	}
	private DisplayRoot masterFrame = null;


}

