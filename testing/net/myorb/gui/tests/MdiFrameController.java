
package net.myorb.gui.tests;

import javax.swing.*;

public class MdiFrameController extends JDesktopPane
{


	public static class InternalFrame extends JInternalFrame
	{

		public InternalFrame (String title)
		{
			super (title, true, true, true, true);
			this.add (mainPanel = new JPanel ());
		}
		public JPanel getMainPanel () { return mainPanel; }
		JPanel mainPanel;

		public void packAndExpose ()
		{
			this.pack ();
			this.setVisible (true);
		}

		private static final long serialVersionUID = 4309596795813467347L;
	}
	public void connect (InternalFrame frame)
	{
		masterFrame.getContentPane ().add (frame);
	}


	public InternalFrame addNewFrame (String title)
	{
		InternalFrame newFrame = new InternalFrame (title);
		this.add (newFrame);
		return newFrame;
	}


	public JFrame constructMasterFrame (String title)
	{
		JInternalFrame prv = null;
		masterFrame = new JFrame (title);
		java.awt.Container contentPane = masterFrame.getContentPane ();

		for (JInternalFrame f : this.getAllFrames ())
		{
			if (prv != null)
			{
				f.setLocation
				(
					prv.getX () + prv.getWidth () + 10, prv.getY ()
				);
			}
			contentPane.add (f);
			prv = f;
		}

		this.setVisible (true);
		this.masterFrame.add (this);
		return masterFrame;
	}

//    int x2 = frame1.getX() + frame1.getWidth() + 10;
//    int y2 = frame1.getY();
//    frame2.setLocation(x2, y2);

	public void showMasterFrame ()
	{
		SwingUtilities.invokeLater
		(
			() ->
			{
				masterFrame.setDefaultCloseOperation
					(JFrame.DISPOSE_ON_CLOSE);
				masterFrame.pack (); masterFrame.setVisible (true);
				masterFrame.setExtendedState (JFrame.MAXIMIZED_BOTH);
			}
		);
	}

	public JFrame getMasterFrame ()
	{
		return masterFrame;
	}
	JFrame masterFrame;


	private static final long serialVersionUID = 6720235983608276301L;
}
