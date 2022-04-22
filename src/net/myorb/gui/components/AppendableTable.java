
package net.myorb.gui.components;

/**
 * abstraction for simple appendable table
 * @author Michael Druckman
 */
public abstract class AppendableTable extends BasicTable
{

	/**
	 * @return first open row number
	 */
	public int getCurrentSize () { return nextRow; }

	/**
	 * change size of displayed table
	 * @param newSize number of rows after change
	 */
	public void resizeTo (int newSize)
	{
		int oldSize = nextRow;
		Object[][] oldContents = data;
		resizeTable (newSize); nextRow = 0;
		appendMultiple (oldContents, oldSize);
	}

	/**
	 * @param count size to grow table by
	 */
	public void extendBy (int count)
	{
		resizeTo (getCurrentSize () + count);
	}

	/**
	 * @param items rows to be appended
	 * @param upTo maximum number of rows to append
	 */
	public void appendMultiple (Object[][] items, int upTo)
	{
		int remaining = upTo;
		for (Object[] next : items)
		{
			if (remaining-- == 0) break;
			setRow (next, nextRow++);
		}
		refreshTable ();
	}

	/**
	 * append row at end of table
	 * @param item objects to add to end of table
	 */
	public void appendRow (Object[] item)
	{
		setRow (item, nextRow++);
		refreshTable ();
	}

	/**
	 * fill empty table entries
	 */
	public void clean ()
	{
		for (int r = 0; isInTable (r); r++)
		{ if (isEmpty (r)) fillEmpties (r); }
		resizeTo (nextRow);
		refreshTable ();
	}

	/**
	 * check index not past append row
	 * @param row the row index to check
	 * @return TRUE = row is in table range
	 */
	public boolean isInTable (int row) { return row < nextRow; }

	/**
	 * check first column for null
	 * @param row the row index to check
	 * @return TRUE = row is empty
	 */
	public boolean isEmpty (int row) { return get (row, 0) == null; }

	/**
	 * copy and clear rows
	 * @param from source row
	 * @param to destination row
	 */
	public void move (int from, int to) { clear (copy (from, to)); }

	/**
	 * find next non-empty row
	 * @param from starting row number
	 * @return row number where non-empty found or -1 if not found
	 */
	public int find (int from)
	{
		for (int r = from; isInTable (r); r++)
		{ if (!isEmpty (r)) return r; }
		return -1;
	}

	/**
	 * copy from row to row
	 * @param from source row
	 * @param to destination row
	 * @return destination index at end of copy
	 */
	public int copy (int from, int to)
	{
		for (int r = from; r < nextRow; r++)
		{ setRow (getRow (r), to++); }
		return to;
	}

	/**
	 * set rows to empty
	 * @param from starting row index
	 */
	public void clear (int from)
	{
		for (int r = from; isInTable (r); r++)
		{ setRowEmpty (r); }
		nextRow = from;
	}

	/**
	 * move lower items up to fill empty rows
	 * @param from starting row index
	 */
	public void fillEmpties (int from)
	{
		int next = find (from);
		if (next < 0) nextRow = from;
		else move (next, from);
	}

	/**
	 * copy selected rows from source
	 * @param from source table with selected rows
	 * @param to destination table to be appended
	 */
	public static void copy (AppendableTable from, AppendableTable to)
	{
		int[] rows = from.getDisplayTable ().getSelectedRows ();
		if (rows == null || rows.length == 0) { from.selectionError (); return; }
		copy (rows, from, to);
	}
	public static void copy (int[] rows, AppendableTable from, AppendableTable to)
	{
		to.resizeTo (to.nextRow + rows.length);
		for (int row : rows) { to.appendRow (from.getRow (row)); from.setRowEmpty (row); }
		to.refreshTable (); to.getDisplayTable ().addRowSelectionInterval (to.nextRow-rows.length, to.nextRow-1);
		from.clean (); from.getDisplayTable ().clearSelection ();
	}

	/**
	 * message for unable to copy condition
	 */
	public void selectionError () { errorDialog (this, "No rows selected", "Unable to move"); }

	/**
	 * abstract dialog to post error
	 * @param c screen component associated
	 * @param message the text of the error message
	 * @param title the title for the dialog frame
	 */
	public abstract void errorDialog (BasicTable c, String message, String title);

	/**
	 * index of next row that append will write to
	 */
	protected int nextRow = 0;

}
