
package net.myorb.gui.components;

/**
 * table components building a file list display
 * @author Michael Druckman
 */
public class SimpleFileList
{


	public static final String[] COLUMNS = new String[]{"Name", "Type", "Size"};


	/**
	 * @return access to the table component
	 */
	public Table getTable () { return table; }
	protected Table table = null;


	/**
	 * @return access to the frame
	 */
	public TableFrame getFrame () { return tableDisplay; }
	protected TableFrame tableDisplay = null;


	/**
	 * show table frame with given title and table component
	 * @param title the title for the frame showing the table
	 * @param table the table component to display
	 */
	public void show (String title, Table table)
	{
		tableDisplay = new TableFrame (this.table = table, title);
	}


	/**
	 * table component for display
	 */
	public static class Table extends SimpleTableAdapter
		implements AppAccessMenuManager.ResourceAccess
	{

		public Table () { super (COLUMNS); }

		/**
		 * a row describing a file
		 * @param name the name of the file
		 * @param length the length of the file
		 */
		public void setRow (String name, long length)
		{
			String type = "";
			int dot = name.lastIndexOf ('.');

			if (dot >= 0)
			{
				type = name.substring (dot + 1);
				name = name.substring (0, dot);
			}

			set (nextRow, 0, name);
			set (nextRow, 1, type);
			set (nextRow, 2, length);

			nextRow++;
		}

		/**
		 * @return the list of indexes of selected table items
		 * @throws AppAccessMenuManager.UserError for user request problems
		 */
		public int[] getSelectedItems () throws AppAccessMenuManager.UserError
		{
			int[] items = displayTable.getSelectedRows ();
			if (items.length == 0) throw new AppAccessMenuManager.UserError ("No items selected");
			return items;
		}
		public int getSelectedItem () throws AppAccessMenuManager.UserError
		{
			int[] selected = getSelectedItems ();
			if (selected.length != 1) throw new AppAccessMenuManager.UserError ("Only one item should be selected");
			return selected[0];
		}

		/* (non-Javadoc)
		 * @see net.myorb.gui.components.SimpleTableAdapter#getMenu()
		 */
		public Menu getMenu ()
		{
			Menu menu = new Menu ();
			AppAccessMenuManager.addToMenu (menu, this);
			return menu;
		}

		/**
		 * enumerate paths to items selected in GUI
		 * @param selected the row numbers selected in GUI
		 * @return the text of the path list
		 * @throws Exception for any errors
		 */
		public String getPathsFor (int[] selected) throws Exception
		{
			StringBuffer buffer = new StringBuffer ();
			for (int row : selected) { buffer.append (" ").append (getPathFor (row)); }
			return buffer.toString ();
		}

		/**
		 * get path to selected item in GUI
		 * @param selected a selected row number
		 * @return the path to the selected item
		 * @throws Exception for any errors
		 */
		public String getPathFor (int selected) throws Exception { return null; }

		/* (non-Javadoc)
		 * @see net.myorb.gui.components.AppAccessMenuManager.ResourceAccess#getSelectedPath()
		 */
		public String getSelectedPath () throws Exception
		{ return getPathFor (getSelectedItem ()); }

	}


	/**
	 * frame for display of table component
	 */
	public static class TableFrame extends SimpleScreenIO
	{

		public TableFrame (SimpleTableAdapter adapter, String title)
		{
			Panel p = new Panel ();
			adapter.addTableToPanel (p);
			frame = show (p, title + " (working...)", 600, 300);
			this.title = title;
		}

		public void done () { frame.setTitle (title); }
		protected Frame frame; String title;

	}


}

