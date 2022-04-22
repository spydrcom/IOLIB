
package net.myorb.gui.tests;

import net.myorb.gui.components.MdiFrameController;

import javax.swing.JButton;
import javax.swing.JLabel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MdiTest
{

	static MdiFrameController ctlr;

	static MdiFrameController.InternalFrame f1, f2, f3, f4;

	static ActionListener listener1 = new ActionListener ()
	{
		public void actionPerformed(ActionEvent e)
		{
			f3.packAndExpose ();
		}
	};
	static ActionListener listener2 = new ActionListener ()
	{
		public void actionPerformed(ActionEvent e)
		{
//			//MdiFrameController ctlr2 = new MdiFrameController (ctlr);
//			MdiFrameController ctlr2 = ctlr;
//			f4 = ctlr2.addNewFrame ("added frame");
//
//			//ctlr.addInternalFrames (ctlr2);
//
//			ctlr.getMasterFrame ().getContentPane ().add (f4);
//			ctlr.getMasterFrame ().add (ctlr2);
//
//			f4.getMainPanel().add (new JLabel ("Content of Frame 4"));
//			f4.packAndExpose ();
//
//			ctlr2.setVisible (true);

			//ctlr.repaint();
			//ctlr.setVisible(false);ctlr.setVisible(true);
			//ctlr.getMasterFrame().setVisible(false);//ctlr.getMasterFrame().setVisible(true);
			//ctlr.constructMasterFrame ("Master replaced");
			//ctlr.showMasterFrame ();

			//ctlr.getDesktopManager().openFrame(f4);
			//ctlr.setVisible(false);ctlr.setVisible(true);
			//ctlr.getMasterFrame().setVisible(false);//ctlr.getMasterFrame().setVisible(true);
			//ctlr.showMasterFrame();
		}
	};

	public static void main (String[] args)
	{
		JButton b;
		ctlr = new MdiFrameController ();

		f1 = ctlr.addNewFrame ("F1");
		f2 = ctlr.addNewFrame ("F2");
		f3 = ctlr.addNewFrame ("F3");

		f1.getMainPanel().add (new JLabel ("Content of Frame 1"));
		f1.getMainPanel().add (b = new JButton ("Add New One"));
		b.addActionListener (listener2);
		f1.packAndExpose ();

		f2.getMainPanel().add (new JLabel ("Content of Frame 2"));
		f2.getMainPanel().add (b = new JButton ("Expose Frame 3"));
		b.addActionListener (listener1);
		f2.packAndExpose ();

		f3.getMainPanel().add (new JLabel ("Content of Frame 3")); //f3.packAndExpose ();

		//JFrame f =
		ctlr.constructMasterFrame ("Master");
		ctlr.showMasterFrame ();
	}

}
