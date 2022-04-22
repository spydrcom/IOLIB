
package net.myorb.gui.components;

/**
 * provide simple defaults for an appendable table instance
 * @author Michael Druckman
 */
public class SimpleTableAdapter extends AppendableTable
{

	/**
	 * @param columns titles for the columns
	 */
	public SimpleTableAdapter (String[] columns)
	{ this.columns = columns; setTableProperties (this); }
	protected String[] columns;

	/**
	 * @param columns titles for the columns
	 * @param initialSize number of rows initially allocated
	 */
	public SimpleTableAdapter (String[] columns, int initialSize)
	{ this (columns); this.initialSize = initialSize; }
	protected int initialSize = 1000;

	/* (non-Javadoc)
	 * @see net.myorb.lotto.gui.components.TableProperties#getColumns()
	 */
	public String[] getColumns () { return columns; }

	/* (non-Javadoc)
	 * @see net.myorb.lotto.gui.components.TableProperties#getCells()
	 */
	public Object[][] getCells () { allocateTable (initialSize); return data; }

	/* (non-Javadoc)
	 * @see net.myorb.lotto.gui.components.AppendableTable#errorDialog(net.myorb.lotto.gui.components.BasicTable, java.lang.String, java.lang.String)
	 */
	public void errorDialog (BasicTable c, String message, String title) {}

	/* (non-Javadoc)
	 * @see net.myorb.lotto.gui.components.TableProperties#doDoubleClick(int)
	 */
	public void doDoubleClick (int forRowNumber) {}

	/* (non-Javadoc)
	 * @see net.myorb.lotto.gui.components.TableProperties#getTitle()
	 */
	public String getTitle () { return null; }

	/* (non-Javadoc)
	 * @see net.myorb.lotto.gui.components.TableProperties#getMenu()
	 */
	public Menu getMenu () { return null; }

}

