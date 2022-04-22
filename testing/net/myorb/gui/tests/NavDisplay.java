
package net.myorb.gui.tests;

import net.myorb.gui.graphics.NAV;
import net.myorb.gui.graphics.NAV.Location;
import net.myorb.gui.components.SimpleScreenIO;

import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

import java.awt.Color;

public class NavDisplay extends SimpleScreenIO implements KeyListener
{


	public static void main (String[] args) throws Exception
	{
		Panel root = startGridPanel (null, 3, 3);
		for (int i = 0; i < 9; i++)
		{
			Panel p = new Panel (); p.setBackground (Color.WHITE);
			Location.values () [i].setAssociatedWith (p);
			root.add (p);
		}
		currentPanel ().setBackground (Color.GREEN);
		((java.awt.Component)(f = new Frame (root, "Nav")).show ())
		.addKeyListener (new NavDisplay ());
	}
	static Frame f;


	public static Panel currentPanel ()
	{ return (Panel) current.getAssociatedWith (); }
	static Location current = Location.CM;


	@Override public void keyPressed (KeyEvent event)
	{
		Location to;
		int code = event.getKeyCode ();
		NAV.Direction d = NAV.forCode (code);

		if (d == null) return;

		if ((to = current.move (d)) == null)
		{
			System.out.println ("Illegal Move");
		}
		else
		{
			System.out.println ("Move To: " + to);
			currentPanel ().setBackground (Color.WHITE);
			current = to; currentPanel ().setBackground (Color.GREEN);
		}
	}


	@Override public void keyReleased (KeyEvent event) {}
	@Override public void keyTyped (KeyEvent event) {}


}

