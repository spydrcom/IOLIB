
package net.myorb.gui.components;

import javax.swing.JPanel;
import javax.swing.JComponent;
import javax.swing.JTable;

/**
 * display a table of data
 * @author Michael Druckman
 */
public class DisplayTable extends DisplayTablePrimitives
{


	/**
	 * format table with headers and scroll bars
	 * @param data the data to place in the table
	 * @param headers the headers for the columns
	 * @return a scroll pane holding the table
	 */
	public static JComponent tabulate
		(Object[][] data, String[] headers)
	{
		return tabulate (new DisplayTableModel (data, headers));
	}


	/**
	 * change table contents
	 * @param tableAccess access object for changed table
	 * @param newData new data matrix for table
	 */
	public static void changeData (Object tableAccess, Object[][] newData)
	{
		JTable table = getTableFromAccessObject (tableAccess);
		DisplayTableModel model = (DisplayTableModel) table.getModel ();
		model.setData (newData);
		refresh (model);
	}


	/**
	 * generate table as component of panel
	 * @param data the data to place in the table for display
	 * @param headers the headers for the columns of the table
	 * @param width of the displayed panel (preferred)
	 * @param height of the displayed panel
	 * @param toAddTo panel to add to
	 * @return access to table
	 */
	public static Object addTableComponentToPanel
	(Object[][] data, String[] headers, int width, int height, JPanel toAddTo)
	{
		JComponent tableAccess = tabulate (data, headers);
		addToPanel (tableAccess, width, height, toAddTo);
		return tableAccess;
	}


	/**
	 * format and display a table.
	 *  returned access object can be used to retrieve table.
	 *  helper method below will allow refresh of table from access object
	 * @param data the data to place in the table for display
	 * @param headers the headers for the columns
	 * @param title the title for the frame
	 * @param width of the displayed panel
	 * @param height of the panel
	 * @return access to table
	 */
	public static Object showTable
	(Object[][] data, String[] headers, String title, int width, int height)
	{
		JComponent tableAccess = tabulate (data, headers);
		showComponent (tableAccess, title, width, height);
		return tableAccess;
	}


	/**
	 * format and display a table
	 * @param data the data to place in the table
	 * @param headers the headers for the columns
	 * @param title the title for the frame
	 * @param size the size of the panel
	 */
	public static void showTable
	(Object[][] data, String[] headers, String title, int size)
	{
		JComponent access;
		disableEditing (access = tabulate (data, headers));
		showComponent (access, title, size);
	}


}

