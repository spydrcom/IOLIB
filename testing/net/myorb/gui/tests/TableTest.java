
package net.myorb.gui.tests;

import java.awt.Dimension;

import net.myorb.gui.components.*;

import javax.swing.*;

public class TableTest implements TableProperties
{

	@Override
	public String getTitle() {
		// TODO Auto-generated method stub
		return "table test";
	}

	@Override
	public String[] getColumns() {
		// TODO Auto-generated method stub
		return new String[]{"simple text"};
	}

	@Override
	public Object[][] getCells() {
		// TODO Auto-generated method stub
		return new Object[][]
				{
					{"first"},
					{"second"},
					{"third"},
					{"forth"},
					{"fifth"},
					{"sixth"},
					{"seventh"},
					{"eighth"},
					{"ninth"},
					{"tenth"},
					{"etc"}
				};
	}

	@Override
	public Menu getMenu() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Dimension getDisplayArea() {
		// TODO Auto-generated method stub
		return new Dimension (300, 500);
	}

	@Override
	public void doDoubleClick(int forRowNumber) {
		// TODO Auto-generated method stub
		System.out.println ("DCLICK "+forRowNumber);
	}

	public static void main (String[] args)
	{
		JPanel panel = new JPanel ();
		TableAdapter ta = new TableAdapter ();
		ta.setTableProperties (new TableTest ());
		ta.addTableToPanel (panel);
		DisplayFrame f = new DisplayFrame (panel, "table test");
		f.show ();
	}

}
