
package net.myorb.data.unicode;

import net.myorb.data.abstractions.Status;

import net.myorb.gui.components.AppendableTable;
import net.myorb.gui.components.UnicodeCharacterBlock;
import net.myorb.gui.components.SimpleScreenIO;
import net.myorb.gui.components.BasicTable;
import net.myorb.gui.components.Menu;

import java.awt.Font;

/**
 * table display of block index data
 * @author Michael Druckman
 */
public abstract class CrossReference extends AppendableTable
{

	/**
	 * build support structure for table display
	 * @param columns the array of column names
	 */
	public CrossReference (String[] columns) { this.columns = columns; setTableProperties (this); }

	public CrossReference (Font font, String[] columns, UnicodeCharacterBlock block)
	{ this (columns); this.font = font; this.block = block; linkLoadAndShow (); }
	protected Font font;

	/*
	 * build display
	 */

	/**
	 * connect table to panel and make visible
	 */
	public void linkLoadAndShow ()
	{
		SimpleScreenIO.Panel p;
		addTableToPanel (p = new SimpleScreenIO.Panel ());	// link table to parent panel
		loadCrossReference ();								// populate table
		show (p);
	}

	/**
	 * populate table
	 */
	protected abstract void loadCrossReference ();

	/*
	 * completion of implementation of AppendableTable
	 */

	/* (non-Javadoc)
	 * @see net.myorb.gui.components.TableProperties#getMenu()
	 */
	public Menu getMenu () { return null; } // not being used

	/* (non-Javadoc)
	 * @see net.myorb.gui.components.TableProperties#getTitle()
	 */
	public String getTitle () { return font.getName (); }

	/* (non-Javadoc)
	 * @see net.myorb.gui.components.TableProperties#getCells()
	 */
	public Object[][] getCells() { allocateTable (5000); return data; }

	/* (non-Javadoc)
	 * @see net.myorb.gui.components.AppendableTable#errorDialog(net.myorb.gui.components.BasicTable, java.lang.String, java.lang.String)
	 */
	public void errorDialog (BasicTable table, String message, String title)
	{
		SimpleScreenIO.presentToUser (Status.genError (table.getDisplayTable (), message, title));
	}

	/* (non-Javadoc)
	 * @see net.myorb.gui.components.TableProperties#getColumns()
	 */
	public String[] getColumns() { return columns; }
	protected String[] columns;

	/**
	 * format array of column data to append as row
	 * @param data the object to place in columns of row
	 * @return the row of data as an array
	 */
	protected Object[] rowFor (Object... data) { return data; }

	/**
	 * track row additions to determine final correct size
	 * @param columns cells of new row
	 */
	protected void addToRowCount (Object[] columns)
	{ appendRow (columns); rowCount++; }

	/**
	 * resize table to actual count of appended rows
	 */
	protected void releaseUnusedRows () { resizeTo (rowCount); }
	protected int rowCount = 0;

	/* (non-Javadoc)
	 * @see net.myorb.gui.components.TableProperties#doDoubleClick(int)
	 */
	public void doDoubleClick (int forRowNumber)
	{
		int base = Toolkit.parseHex
		(getCodeCell (forRowNumber).substring (2));
		if (block == null) block = UnicodeCharacterBlock.show (base);
		else block.goTo (base);
	}

	/**
	 * read Unicode value cell for specified row
	 * @param forRowNumber the row to be read
	 * @return the cell contents
	 */
	protected String getCodeCell (int forRowNumber)
	{ return get (forRowNumber, codeColumn).toString (); }
	protected UnicodeCharacterBlock block = null;
	protected int codeColumn = 1;

	/**
	 * @param p the panel holding the table
	 */
	public void show (SimpleScreenIO.Panel p) 
	{ frame = SimpleScreenIO.show (p, font.getName (), 300, 300); }

	/**
	 * release this cross reference frame
	 */
	public void release ()  { frame.dispose (); }
	protected SimpleScreenIO.Frame frame;

}

