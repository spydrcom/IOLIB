
package net.myorb.data.abstractions;

import net.myorb.gui.components.SimpleXmlNodeEditor;
import net.myorb.gui.components.DisplayTablePrimitives;
import net.myorb.gui.components.SimpleMouseEventCallback;

import net.myorb.data.abstractions.SimpleXmlHash.Document;

import javax.swing.table.AbstractTableModel;
import javax.swing.JTable;

/**
 * use simple XML hash as basis for display table
 * @author Michael Druckman
 */
public class SimpleXmlTableModel extends AbstractTableModel
{


	/**
	 * @param document data set for model
	 */
	public SimpleXmlTableModel (Document document)
	{ properties = new SimpleXmlDocumentProperties (this.document = document); }

	/**
	 * @return document providing model data
	 */
	public Document getDocument () { return document; }
	protected Document document;


	/**
	 * @return properties of the document
	 */
	public SimpleXmlDocumentProperties
		getProperties () { return properties; }
	protected SimpleXmlDocumentProperties properties;


	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	public int getColumnCount ()
	{
		return properties.getColumnCount ();
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	public int getRowCount ()
	{
		return document.size ();
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	public Object getValueAt (int row, int column)
	{
		return document.get (row).get (properties.getColumnAttributeFor (column));
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
	 */
	public String getColumnName (int column)
	{
		return properties.getColumnNameFor (column);
	}


	/**
	 * @param number node sequence number from document
	 */
	public void editNode (int number)
	{
		new SimpleXmlNodeEditor (this, number, properties.getElementName ());
	}


	/**
	 * @param table component to add mouse handler
	 */
	public void addMouseHandler (JTable table)
	{
		new MouseHandler (table).addMouseListenerTo (table);
	}
	public void addMouseHandlerFor (Object tableAccess)
	{
		addMouseHandler (DisplayTablePrimitives.getTableFromAccessObject (tableAccess));
	}


	/**
	 * use mouse adapter to trap double click to open editor
	 */
	class MouseHandler extends SimpleMouseEventCallback.Adapter
	{
		public MouseHandler (JTable table) { super (DCLICK, "Mouse event generated error", table); }
		public int getSelectedRow () { return ((JTable) getComponent ()).getSelectedRow (); }
		public void executeAction () throws Exception { editNode (getSelectedRow ()); }
	}
	static final SimpleMouseEventCallback.EventType DCLICK = SimpleMouseEventCallback.EventType.DOUBLE_CLICKED;


	private static final long serialVersionUID = 4722607198055268368L;
}
