
package net.myorb.gui.tests;

import java.awt.event.ActionListener;

import net.myorb.jxr.JxrParser;
import net.myorb.jxr.JxrPrimitives;
import net.myorb.jxr.JxrReport;

public class MenuBarTest
{

	public static void main (String... args) throws Exception
	{

//		JxrPrimitives.SymbolTable symbols = JxrParser.read ("data/JxrTest.xml");

//		JxrPrimitives.SymbolTable symbols = JxrParser.read ("data/BeanTest.xml");

//		JxrPrimitives.SymbolTable symbols = JxrParser.read ("data/NestedTest.xml");

//		System.out.println ("SYMBOLS: "+symbols);

		JxrPrimitives.SymbolTable symbols =
				JxrParser.read ("JxrTests/MenuBarTest.xml", null);
		JxrReport.dump (symbols);

	}

	public ActionListener getAction ()
	{
		return (e) ->
		{
			System.out.println ("OK NOW");
		};
	}

}
