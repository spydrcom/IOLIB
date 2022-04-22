
package net.myorb.abstraction.tests;

import net.myorb.jxr.*;

public class JxrTest {

	public static void main (String[] args) throws Exception
	{
		System.out.println 
		(
			JxrParser.read ("cfg/EstablishCommonSymbols.xml")
		);
	}

}
