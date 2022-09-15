
package net.myorb.gui.components;

import net.myorb.data.abstractions.RegEx;

import javax.swing.JPanel;
import javax.swing.JTable;

import java.awt.Dimension;

/**
 * GUI component to find entries of a text list using RegEx
 * @author Michael Druckman
 */
public class FindWidget extends RegEx implements TableProperties
{


	/**
	 * identify action for double click
	 */
	public interface SelectionAction
	{
		/**
		 * @param text the data of the table item selected
		 */
		void process (String text);
	}


	/**
	 * display is single column table
	 * @param columnName a name for the data being searched
	 */
	public FindWidget (String columnName)
	{
		this.columnName = columnName;
	}


	/**
	 * retrieve column name
	 * @return the name selected for the data
	 */
	public String getColumnName ()
	{
		return columnName;
	}
	protected String columnName;


	/**
	 * use a column of data for search
	 * @param number the column number for the source
	 * @param from the table holding the column
	 */
	public void useColumn (int number, JTable from)
	{
		this.startList ();

		for (int r = 0; r < from.getRowCount (); r++)
		{
			this.addToList (from.getValueAt (r, number).toString ());
		}
	}


	/**
	 * identify destination for chosen data
	 * @param action the action to take on double click
	 */
	public void setSelectionAction (SelectionAction action)
	{
		this.action = action;
	}
	protected SelectionAction action = null;


	/**
	 * double click selection done and needs processing
	 * @param text the data in the selected cell
	 */
	public void select (String text)
	{
		if (action == null)
		{ System.out.println ("Selected: " + text); }
		else action.process (text);
	}


	/**
	 * a row of the table is identified for selection
	 * @param row the row number selected
	 */
	public void select (int row)
	{
		select (this.get (row));
		f.dispose ();
	}


	/*
	 * implementation of TableProperties
	 */


	/* (non-Javadoc)
	 * @see net.myorb.gui.components.TableProperties#getTitle()
	 */
	public String getTitle () {
		return "NOT USED";
	}


	/* (non-Javadoc)
	 * @see net.myorb.gui.components.TableProperties#getColumns()
	 */
	public String[] getColumns () {
		return new String[]{getColumnName ()};
	}


	/* (non-Javadoc)
	 * @see net.myorb.gui.components.TableProperties#getCells()
	 */
	public Object[][] getCells () {
		int rows = this.size (), row = 0;
		Object cells [][] = new Object[rows][1];
		for (String data : this) { cells[row++][0] = data; }
		return cells;
	}


	/* (non-Javadoc)
	 * @see net.myorb.gui.components.TableProperties#getMenu()
	 */
	public Menu getMenu () {
		return null;
	}


	/* (non-Javadoc)
	 * @see net.myorb.gui.components.TableProperties#getDisplayArea()
	 */
	public Dimension getDisplayArea () {
		return new Dimension (200, 260);
	}


	/* (non-Javadoc)
	 * @see net.myorb.gui.components.TableProperties#doDoubleClick(int)
	 */
	public void doDoubleClick (int forRowNumber) {
		select (forRowNumber);
	}


	/**
	 * build panel holding table
	 * @return a panel holding the display table
	 */
	public JPanel getDisplayTablePanel ()
	{
		JPanel panel = new JPanel ();
		adapter = new TableAdapter ();
		adapter.setTableProperties (this);
		adapter.addTableToPanel (panel);
		return panel;
	}
	public TableAdapter getTableAdapter () { return adapter; }
	protected TableAdapter adapter;


	/**
	 * display table to screen
	 * @param title a title for the display frame
	 */
	public void show (String title)
	{
		f = new DisplayFrame (getDisplayTablePanel (), title);
		f.show ();
	}
	protected DisplayFrame f;


	private static final long serialVersionUID = -77948030724233209L;
}

