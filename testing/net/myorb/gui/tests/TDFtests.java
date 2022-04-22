
package net.myorb.gui.tests;

import net.myorb.data.conventional.CharacterDelimited;
import net.myorb.data.conventional.TDF;

import java.util.ArrayList;
import java.io.*;

public class TDFtests implements CharacterDelimited.Processor
{

	class Row extends ArrayList<Double>
	{
		private static final long serialVersionUID = -5959440295612162551L;
	}

	/* (non-Javadoc)
	 * @see net.myorb.data.conventional.CharacterDelimited.Processor#process(net.myorb.data.conventional.CharacterDelimited.Reader)
	 */
	public void process (CharacterDelimited.Reader reader)
	{
		Row row = new Row ();
		for (int i = 0; i < 31; i++)
		{
			row.add (reader.getNumber("").doubleValue());
		}
		System.out.println (row);
	}

	public static void main (String[] a) throws Exception
	{
		new TDF ().parse (new File ("data/VC31L.TDF"), new TDFtests ());
	}

}
