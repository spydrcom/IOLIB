
package net.myorb.gui.tests;

import net.myorb.gui.components.TextLineInputListener;
import net.myorb.gui.components.DisplayContainer;
import net.myorb.gui.components.SimpleScreenIO;
import net.myorb.gui.components.GuiToolkit;
import net.myorb.gui.components.Console;

import javax.swing.JTextArea;
import java.awt.Color;

public class ConsoleTest extends Console
{

	public static void original ()
	{
		SimpleScreenIO.Panel p =
			new SimpleScreenIO.Panel ();
		p.setBackground (Color.WHITE);
		p.setSize (300, 300);

		JTextArea t = new JTextArea ();
		t.setBackground (Color.WHITE);
		t.setSize (300, 300);
		t.setColumns (130);
		p.add (t);

		new TextLineInputListener
		(
			(line) -> System.out.println (line),
			t
		);
		SimpleScreenIO.show
		(
			new SimpleScreenIO.Scrolling (p),
			"console", 300, 300
		);
	}

	/* (non-Javadoc)
	 * @see net.myorb.gui.components.Console#process(java.lang.String)
	 */
	public void process (String line)
	{
		System.out.println (line);
	}

	public static void main (String[] args)
	{
		ConsoleTest console = new ConsoleTest
		(
			Color.WHITE,
			700, 700,
			60
		);
		DisplayContainer f =
			console.show ("CALCLIB", 700, 700).getSwingComponent ();
		GuiToolkit.setIcon (f, GuiToolkit.getIcon ("images/CalcTools.gif"));
	}

	public ConsoleTest
		(
			Color color,
			int width, int height,
			int columns
		)
	{
		super (color, width, height, columns);
	}

}
