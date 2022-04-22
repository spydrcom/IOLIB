
package net.myorb.gui.components;

import javax.swing.table.AbstractTableModel;

/**
 * provide for model with events for data updates
 * @author Michael Druckman
 */
public class DisplayTableModel extends AbstractTableModel
{


	/**
	 * @param data contents of table
	 * @param headers column titles
	 */
	DisplayTableModel (Object[][] data, String[] headers)
	{ this.setData (data); this.headers = headers; }
	protected String[] headers;


	/**
	 * @return matrix of table display objects
	 */
	public Object[][] getData () { return data; }
	public void setData (Object[][] data) { this.data = data; }
	protected Object[][] data;
	


	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	public Object getValueAt (int rowIndex, int columnIndex)
	{ return data[rowIndex][columnIndex]; }

	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
	 */
	public String getColumnName (int column) { return headers[column]; }

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	public int getColumnCount () { return headers.length; }

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	public int getRowCount () { return data.length; }


	private static final long serialVersionUID = 1L;
}

