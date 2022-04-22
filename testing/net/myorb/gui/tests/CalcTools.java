
package net.myorb.gui.tests;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;

import javax.swing.JMenuItem;
import javax.swing.JMenu;

import net.myorb.gui.components.MdiFrameController;
import net.myorb.gui.components.SimpleMenuBar;

public class CalcTools
{

	static MdiFrameController ctlr;

	static MdiFrameController.InternalFrame env, script, session, render, f3, f4;

	static ActionListener envlistener = new ActionListener ()
	{
		public void actionPerformed(ActionEvent e)
		{
			MdiFrameController.InternalFrame i =
					ctlr.addNewFrame ("new frame");
			i.packAndExpose();
		}
	};

	static ActionListener tablistener = new ActionListener ()
	{
		public void actionPerformed(ActionEvent e)
		{
			env.packAndExpose ();
		}
	};

	public static void main (String[] args)
	{
		JButton b;
		ctlr = new MdiFrameController ();

		env = ctlr.addNewFrame ("Environment");
		script = ctlr.addNewFrame ("Script");
		session = ctlr.addNewFrame ("Session");

		session.getMainPanel().add (new JLabel ("Session"));
		session.getMainPanel().add (b = new JButton ("Add New One"));
		b.addActionListener (envlistener);
		session.packAndExpose ();

		script.getMainPanel().add (new JLabel ("Symbols"));
		script.getMainPanel().add (b = new JButton ("Show Environment"));
		b.addActionListener (envlistener);
		script.packAndExpose ();

		env.getMainPanel().add (new JLabel ("Content of environment")); //f3.packAndExpose ();

		SimpleMenuBar mb = new SimpleMenuBar ();

		JMenu
		envData = mb.addMenu("Data"),
		envScripts = mb.addMenu("Scripts"),
		envSymbols = mb.addMenu("Symbols"),
		envFunctions = mb.addMenu("Functions");

		envData.add ("Import");
		envData.add ("Refresh");
		envScripts.add ("Execute");
		envScripts.add ("Read");
		envSymbols.add ("Add");
		envSymbols.add ("Rename");
		envFunctions.add ("Add");
		envFunctions.add ("Drop");

		env.setJMenuBar(mb.getMenuBar());

		mb = new SimpleMenuBar ();

		JMenu
		home = mb.addMenu("Home"),
		data = mb.addMenu("Data"),
		primes = mb.addMenu("Primes");

		JMenuItem mi = 
		home.add ("Tabulate");
		home.add ("Symbols");
		home.add ("Functions");

		mi.addActionListener (tablistener);

		data.add ("PI");
		data.add ("Sigma");
		data.add ("Hypot");

		primes.add ("Sieve");
		primes.add ("Tabulated");
		primes.add ("Primes");

		ctlr.constructMasterFrame ("CalcTools");
		ctlr.getMasterFrame().setJMenuBar (mb.getMenuBar());

		ctlr.showMasterFrame ();
	}

}
