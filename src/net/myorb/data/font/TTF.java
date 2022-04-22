
package net.myorb.data.font;

import net.myorb.data.font.truetype.*;
import net.myorb.gui.graphics.*;
import net.myorb.utilities.*;

import java.io.File;

/**
 * a file parser for True Type Font files
 * @author Michael Druckman
 */
public class TTF extends AccessControl
{


	/**
	 * @param file the file to parse
	 * @return an instance of the TTF parser
	 * @throws Exception for any errors
	 */
	public static TTF newInstance (File file) throws Exception
	{
		TTF ttf;
		(ttf = new TTF (file)).readFileHeader ();
		LocaTable.postTable (ttf);
		return ttf;
	}


	/**
	 * @param file the file to parse
	 */
	protected TTF (File file) { super (file); }


	/**
	 * @throws Exception for any errors
	 */
	public void readFileHeader () throws Exception
	{
		readBlock (header = new FileHeader (), 0);
		verifyChecksums ();
	}
	public FileHeader getFileHeader () { return header; }
	protected FileHeader header;


	/**
	 * run checksum tests on each block
	 */
	public void verifyChecksums ()
	{
		try { header.verifyChecksums (this); }
		catch (Exception e) { e.printStackTrace (); }
	}


	/*
	 * table management
	 */


	public HeadTable getHeadTable () throws Exception { return HeadTable.getTable (this); }
	public MaxpTable getMaxpTable () throws Exception { return MaxpTable.getTable (this); }
	public LocaTable getLocaTable () throws Exception { return LocaTable.getTable (this); }
	public CmapTable getCmapTable () throws Exception { return CmapTable.getTable (this); }
	public NameTable getNameTable () throws Exception { return NameTable.getTable (this); }


	/*
	 * Glyf table
	 */


	/**
	 * @param n the sequence index of the glyf
	 * @return a glyf table object found as entry in file
	 * @throws Exception for any errors
	 */
	public GlyfTableEntry getGlyfEntry (int n) throws Exception
	{
		return GlyfTableEntry.getGlyfEntry (n, this);
	}


	/**
	 * @return the number of glyf entries
	 * @throws Exception for any errors
	 */
	public int getGlyfCount () throws Exception
	{
		return getMaxpTable ().getProperty (MaxpTable.numGlyphs).intValue ();
	}


	/**
	 * lookup GLYF index from code
	 * @param characterCode the character code to find
	 * @return the GLYF index for the specified code, 0 if not found
	 * @throws Exception for any errors
	 */
	public int getGlyfIndexFor (int characterCode) throws Exception
	{
		return getCmapTable ().getGlyfMap (this).getGlyfIndexFor (characterCode);
	}


	/**
	 * draw GLYF found for character code
	 * @param characterCode the code value to use
	 * @throws Exception for any errors
	 */
	public void drawGlyf (int characterCode) throws Exception
	{
		int glyfIndex = getGlyfIndexFor (characterCode);
		//System.out.println ("index(" + characterCode + ")=" + glyfIndex);
		drawGlyfFor (glyfIndex);
	}
	public void drawGlyfFor (int glyfIndex) throws Exception
	{
		drawGlyf (getGlyfEntry (glyfIndex), glyfIndex);
	}
	public void drawGlyf (GlyfTableEntry glyf, int glyfIndex) throws Exception
	{
		if (glyf instanceof SimpleGlyf)
		{
			ScreenPlotter
			p = new ScreenPlotter (300);
			((SimpleGlyf) glyf).draw (p);
			p.show ("GLYF [index = " + glyfIndex + "]");
		}
		else
		{
			CompoundGlyf cmpGlyf = (CompoundGlyf) glyf;
			for (int index : cmpGlyf.getGlyfIndicies ())
			{
				drawGlyfFor (index);
			}
		}
	}


	/*
	 * unit test / debugging output
	 */


	/**
	 * format displays of table contents
	 * @throws Exception for any errors
	 */
	public void dumpTables () throws Exception
	{
		System.out.println (getHeadTable ());
		System.out.println (getMaxpTable ());
		System.out.println (getNameTable ());
		System.out.println (getCmapTable ());
		System.out.println (getLocaTable ());
	}


	/**
	 * format displays of GLYF entries
	 * @throws Exception for any errors
	 */
	public void dumpGlyfs () throws Exception
	{
		for (int n = 0; n < getGlyfCount (); n++)
		{
			System.out.print (n);
			System.out.print ("  ");
			System.out.println (getGlyfEntry (n));
		}
	}


}

