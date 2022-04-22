
package net.myorb.gui.components;

import net.myorb.data.abstractions.SimpleXmlTableModel;

import net.myorb.data.abstractions.SimpleXmlHash.Document;
import net.myorb.data.abstractions.SimpleXmlHash.Node;

import javax.swing.JTable;

/**
 * display document as table
 * @author Michael Druckman
 */
public class SimpleXmlDocumentDisplay
{

	/**
	 * default size of frames
	 */
	public static final int DISPLAY_WIDTH = 300, DISPLAY_HEIGHT = 200;

	/**
	 * @param args name of document to be displayed
	 * @throws Exception for any errors
	 */
	public static void main (String... args) throws Exception
	{
		new SimpleXmlDocumentDisplay (Document.read (args[0])).showTable ();
	}

	/**
	 * @param document the document to be displayed
	 */
	public SimpleXmlDocumentDisplay (Document document)
	{
		this.document = document;
	}
	protected Document document;

	/**
	 * show display table
	 */
	public void showTable ()
	{
		model = new SimpleXmlTableModel (document);
		String title = model.getProperties ().getDocumentTitle ();
		tableAccess = DisplayTablePrimitives.showTable
				(model, title, DISPLAY_WIDTH, DISPLAY_HEIGHT);
		model.addMouseHandlerFor (tableAccess);
		linkMenu (tableAccess);
	}
	protected SimpleXmlTableModel model;
	protected Object tableAccess;

	/**
	 * @param access the object with access to the table
	 */
	public void linkMenu (Object access)
	{
		displayTable =
			DisplayTablePrimitives.getTableFromAccessObject (access);
		getMenu ().addAsPopup (displayTable);
	}
	protected JTable displayTable;

	/**
	 * @return menu item for save action
	 */
	public MenuItem getSaveAction ()
	{
		return new SaveAction ();
	}
	public void nextAction () throws Exception {}

	/**
	 * @return menu to be added as popup for display
	 */
	public Menu getMenu () { return new ActionsMenu (); }

	/**
	 * menu of actions
	 */
	class ActionsMenu extends Menu
	{
		ActionsMenu ()
		{
			add (new EditAction ());
			add (new InsertAction ());
			add (new DeleteAction ());
			add (getSaveAction ());
		}
		private static final long serialVersionUID = 6138111398139350825L;
	}

	/**
	 * edit selected element of document
	 */
	class EditAction extends MenuItem
	{
		EditAction () { super ("Edit", "Edit Failed", tableAccess); }
		public void executeAction () throws Exception
		{
			new SimpleXmlNodeEditor (model, displayTable.getSelectedRow (), model.getProperties ().getElementName ());
		}
	}

	/**
	 * insert one new element in document
	 */
	class InsertAction extends MenuItem
	{
		InsertAction () { super ("Insert", "Insert Failed", tableAccess); }
		public void executeAction () throws Exception
		{
			String nodeName;
			new Node (nodeName = model.getProperties ().getElementName ()).addTo (document);
			new SimpleXmlNodeEditor (model, document.size()-1, nodeName);
		}
	}

	/**
	 * delete selected table rows
	 */
	class DeleteAction extends MenuItem
	{
		DeleteAction () { super ("Delete", "Delete Failed", tableAccess); }
		public void executeAction () throws Exception
		{
			for (int row : displayTable.getSelectedRows ())
			{
				document.remove (row);
			}
			DisplayTablePrimitives.refresh (model);
		}
	}

	/**
	 * save to file in default directory
	 */
	class SaveAction extends MenuItem
	{
		SaveAction () { super ("Save", "Save Failed", tableAccess); }
		public void executeAction () throws Exception
		{
			document.write ();
		}
	}

	/**
	 * redirect save action to next processing step
	 */
	public class NextAction extends MenuItem
	{
		public NextAction () { super ("Next", "Next Failed", tableAccess); }
		public void executeAction () throws Exception
		{
			nextAction ();
		}
	}

	/**
	 * @param d document to be shown
	 */
	public static void showDocument (Document d)
	{
		try
		{ d.write (System.out); }
		catch (Exception e) { e.printStackTrace (); }
	}
	public void showDocument () { showDocument (document); }

}
