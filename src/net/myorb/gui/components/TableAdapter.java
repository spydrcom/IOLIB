
package net.myorb.gui.components;

import javax.swing.JTable;

import javax.swing.JComponent;
import javax.swing.JPanel;

import java.awt.Dimension;

import java.util.HashMap;
import java.util.Map;

/**
 * abstract table creation to be driven by properties interface
 * @author Michael Druckman
 */
public class TableAdapter implements SimpleScreenIO.ScreenAssociation
{

	/**
	 * @param tableProperties interface to properties that define table
	 */
	public void setTableProperties (TableProperties tableProperties)
	{ this.tableProperties = tableProperties; indexColumns (); }
	protected TableProperties tableProperties;

	/* get/set properties associated with the swing table */

	public void setDisplayTable (JComponent tableAccess)
	{ this.displayTable = DisplayTable.getTableFromAccessObject (tableAccess); }
	public JComponent getAssociatedComponent () { return displayTable; }
	public JTable getDisplayTable () { return displayTable; }
	protected JTable displayTable = null;

	/**
	 * add mouse handler as listener on display table
	 */
	public void addMouseListener () { new MouseHandler (displayTable).addMouseListenerTo (displayTable); }

	/**
	 * using Callback Mouse Adapter allows catch of errors in mouse events
	 */
	class MouseHandler extends SimpleMouseEventCallback.Adapter
	{
		public MouseHandler (JTable table) { super (DCLICK, "Mouse event generated error", table); }
		public void executeAction () throws Exception { tableProperties.doDoubleClick (getSelectedRow ()); }
		public int getSelectedRow () { return ((JTable) getComponent ()).getSelectedRow (); }
	}
	static final SimpleMouseEventCallback.EventType DCLICK = SimpleMouseEventCallback.EventType.DOUBLE_CLICKED;

	/**
	 * add mouse listener for popup menu
	 * @param menu the actions for the menu items
	 */
	public void addPopupMenuToTable (Menu menu)
	{
		// use menu manager to add mouse adapter for popup menu built from callback objects
		menu.addAsPopup (displayTable);
	}

	/**
	 * update displayed table with changes
	 */
	public void refreshTable ()
	{
		DisplayTablePrimitives.refreshTable (tableAccess);
	}

	/**
	 * create table and add to panel
	 * @param panel the panel to add to
	 */
	public void addTableToPanel (JPanel panel)
	{
		Dimension d = tableProperties.getDisplayArea ();
		tableAccess = (JComponent) DisplayTable.addTableComponentToPanel
		(
			data = tableProperties.getCells (),
			columns = tableProperties.getColumns (),
			d.width, d.height,
			panel
		);
		setDisplayTable (tableAccess);
		addMouseListener ();
		addPopupMenu ();
	}
	protected JComponent tableAccess = null;
	protected String[] columns = null;

	/**
	 * add menu from table properties
	 */
	private void addPopupMenu ()
	{
		Menu menu;
		if ((menu = tableProperties.getMenu ()) == null) return;
		addPopupMenuToTable (menu);
	}

	/**
	 * @return data buffer currently collecting data
	 */
	public Object[][] getData () { return data; }
	public void setData (Object[][] data) { this.data = data; }
	protected Object[][] data = null;

	/**
	 * display the table defined by the properties
	 */
	public void showTable ()
	{
		JPanel panel;
		addTableToPanel (panel = new JPanel ());
		DisplayTable.showInFrame (panel, tableProperties.getTitle ());
	}

	/**
	 * change buffer in table model
	 */
	public void changeData ()
	{
		DisplayTable.changeData (tableAccess, data);
	}

	/**
	 * set data buffer to appropriate size
	 * @param rows number of rows in table
	 * @return the 2-D array of objects
	 */
	public Object[][] allocateTable (int rows)
	{
		columns = tableProperties.getColumns ();
		data = new Object[rows][columns.length];
		return data;
	}

	/**
	 * allocate new table and change in table model.
	 *  new allocated table will be all null, data must be reset if needed
	 * @param rows number of rows after change
	 */
	public void resizeTable (int rows)
	{
		allocateTable (rows);
		changeData ();
	}

	/**
	 * mapping from column name to cell value for a row
	 */
	public static class ColumnMapping extends HashMap<String,Object>
	{ private static final long serialVersionUID = 1L; }

	/**
	 * @param row the number of the row to set
	 * @param valueMap the mapping from column name to value
	 */
	public void setRow (int row, Map<String,Object> valueMap)
	{
		setRow (data[row], valueMap);
	}

	/**
	 * @param row array to receive values
	 * @param valueMap the mapping from column name to value
	 */
	public void setRow (Object[] row, Map<String,Object> valueMap)
	{
		int col = 0;
		for (String name : columns)
		{
			Object value = valueMap.get (name);
			Object cell = value == null ? "" : value.toString ();
			row[col++] = cell;
		}
	}

	/*
	 * get/set for rows/cells
	 */

	public void setRowEmpty (int number)
	{ setRow (new Object[tableProperties.getColumns().length], number); }

	public void setRow (Object[] row, int number) { data[number] = row; }
	public Object[] getRow (int number) { return data[number]; }

	public Object get (int row, String columnName)
	{ return get (row, getColumnIndexFor (columnName)); }
	public void set (int row, String columnName, Object value)
	{ set (row, getColumnIndexFor (columnName), value); }
	public void set (int row, int col, Object value)
	{ data[row][col] = value; }

	public Object[] getColumn (String columnName)
	{ return getColumn (getColumnIndexFor (columnName)); }
	public Object get (int row, int col) { return data[row][col]; }

	public Object[] getColumn (int columnNumber)
	{
		int size = data.length;
		Object[] column = new Object[size];
		for (int r = 0; r < size; r++) { column[r] = data[r][columnNumber]; }
		return column;
	}


	/**
	 * construct index to support lookup
	 */
	public void indexColumns ()
	{
		int columnIndex = 0;
		indexForColumn = new HashMap<String,Integer>();
		for (String columnName : tableProperties.getColumns ())
		{ indexForColumn.put (columnName, columnIndex++); }
	}

	/**
	 * @param columnName name of column
	 * @return index used for column
	 */
	public int getColumnIndexFor
	(String columnName) { return indexForColumn.get (columnName); }
	protected Map<String,Integer> indexForColumn = null;

}
