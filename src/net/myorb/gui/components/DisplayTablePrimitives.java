
package net.myorb.gui.components;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import javax.swing.JScrollPane;
import javax.swing.JComponent;
import javax.swing.JViewport;
import javax.swing.JTable;

/**
 * operations on tables built from abstract table model
 * @author Michael Druckman
 */
public class DisplayTablePrimitives extends DisplayComponents
{


	/**
	 * @param tableAccess access object for table
	 * @return the table from the access object
	 */
	public static JTable getTableFromAccessObject (Object tableAccess)
	{
		return getTableInScroll ((JScrollPane) tableAccess);
	}


	/**
	 * traverse down from scroll pane to get table
	 * @param pane the scroll pane component
	 * @return the table found
	 */
	public static JTable getTableInScroll (JComponent pane)
	{
		JViewport v = (JViewport)pane.getComponents()[0];
		return (JTable)v.getComponents()[0];
	}


	/**
	 * format table with headers and scroll bars
	 * @param model the model for the table
	 * @return a table with a scroll pane
	 */
	public static JComponent tabulate (TableModel model)
	{
		return new JScrollPane (new JTable (model));
	}


	/**
	 * format and display a table.
	 * @param model the model for the table
	 * @param title the title for the frame
	 * @param width of the displayed panel
	 * @param height of the panel
	 * @return access to table
	 */
	public static Object showTable
	(TableModel model, String title, int width, int height)
	{
		JComponent tableAccess = tabulate (model);
		showComponent (tableAccess, title, width, height);
		return tableAccess;
	}


	/**
	 * disable cell editing
	 * @param tableAccess access object for table
	 */
	public static void disableEditing (Object tableAccess)
	{
		JTable table = getTableFromAccessObject (tableAccess);
		table.setDefaultEditor (table.getColumnClass (0), null);
	}


	/**
	 * perform update of displayed table data
	 * @param model the model of the table being displayed
	 */
	public static void refresh (TableModel model)
	{
		((AbstractTableModel) model).fireTableDataChanged ();
	}


	/**
	 * refresh table display
	 * @param tableAccess access object for table
	 */
	public static void refreshTable (Object tableAccess)
	{
		refresh (getTableFromAccessObject (tableAccess).getModel ());
	}


}
