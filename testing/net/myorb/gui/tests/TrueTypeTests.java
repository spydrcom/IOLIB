
package net.myorb.gui.tests;

import net.myorb.data.font.TTF;
import net.myorb.data.font.truetype.NameTable;
import net.myorb.gui.components.SimpleScreenIO;

import java.awt.Font;
import java.io.File;

/**
 * unit tests for true type font parser
 * @author Michael Druckman
 */
public class TrueTypeTests
{

	/**
	 * unit test
	 * @param args not used
	 * @throws Exception for any errors
	 */
	public static void main (String... args) throws Exception
	{
		TTF ttf = TTF.newInstance (new File ("fonts/roboto.ttf"));
		//TTF ttf = TTF.newInstance (new File ("fonts/marav2.ttf"));

		ttf.getLocaTable ().fullyCacheTable ();

		ttf.dumpGlyfs ();
		ttf.dumpTables ();

		int glyfIndex = 300;
		//299, 300, 1013, 1014, 858
		ttf.drawGlyfFor (glyfIndex);

		ttf.drawGlyf (0x497);
		ttf.drawGlyf (48);

		unicodeTest ();
	}


	/*
	 * multi-language Unicode test methods
	 */


	/**
	 * the "HEAV" TTF has Name records with
	 *  multiple languages that require Unicode for correct display.
	 *  a swing frame showing labels that are set with Unicode font allow this.
	 * @throws Exception for any errors
	 */
	public static void unicodeTest () throws Exception
	{
		UnicodeDisplay.show
		(
			TTF.newInstance (new File ("fonts/HEAV.TTF"))
			.getNameTable ().getRecordsOfType (2)
		);
	}
	public static class UnicodeDisplay extends SimpleScreenIO
	{

		/**
		 * the Roboto TTF provides for Greek and Russian character sets.
		 *  the frame will show each line in a separate label with font set to Roboto.
		 * @param entries the entries of name table to display
		 * @throws Exception for any errors
		 */
		public static void show (java.util.List<NameTable.Entry> entries) throws Exception
		{
			Panel p = startGridPanel (null, 0, 2);
			for (NameTable.Entry entry : entries)
			{
				p.add (newLabel (entry.getLanguage ()));
				p.add (newLabel (entry.getText ()));
			}
			show (p, "Unicode Test", 600, 800);
		}

		/**
		 * create label with font set
		 * @param forText the text for the label
		 * @return label holding text
		 */
		static Label newLabel (String forText)
		{
			Label l = new Label (forText);
			l.setFont (roboto);
			return l;
		}

		static	//  read TTF from source file and derive 30pt scaled version
		{
			try
			{
				roboto = Font.createFont
						(
							Font.TRUETYPE_FONT, new File ("fonts/roboto.ttf")
						).deriveFont (30.0f);
			} catch (Exception e) { e.printStackTrace (); }
		}
		static Font roboto;

	}


}

