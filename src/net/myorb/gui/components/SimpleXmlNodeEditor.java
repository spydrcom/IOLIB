
package net.myorb.gui.components;

import net.myorb.data.abstractions.SimpleXmlHash.Node;
import net.myorb.data.abstractions.SimpleXmlHash.Document;

import net.myorb.data.abstractions.SimpleXmlDocumentProperties;
import net.myorb.data.abstractions.SimpleXmlTableModel;

import net.myorb.data.validation.VerificationLibrary;
import net.myorb.data.validation.Check;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

/**
 * edit attributes of a node
 * @author Michael Druckman
 */
public class SimpleXmlNodeEditor extends SimpleScreenIO
{


	/**
	 * @param tableModel the table model
	 * @param nodeId the sequence number from the document
	 * @param nodeName name of the node to be edited
	 */
	public SimpleXmlNodeEditor (SimpleXmlTableModel tableModel, int nodeId, String nodeName)
	{
		this.nodeName = nodeName;
		this.document = tableModel.getDocument ();
		this.source = document.getNode (this.nodeId = nodeId);
		this.working = new Node (source).withAttributesOnly ();
		show (this.tableModel = tableModel);
	}
	SimpleXmlTableModel tableModel;
	Node source, working;
	Document document;
	String nodeName;
	int nodeId;


	/**
	 * @param tableModel model for table to display
	 */
	void show (SimpleXmlTableModel tableModel)
	{ frame = show (buildPanel (tableModel), "Edit " + nodeName); }
	Frame frame;


	/**
	 * @param p the panel to show
	 * @param title a title for the frame
	 * @return the frame showing the panel
	 */
	public static Frame show (Panel p, String title)
	{
		return show
				(
					p, title,
					SimpleXmlDocumentDisplay.DISPLAY_WIDTH,
					SimpleXmlDocumentDisplay.DISPLAY_HEIGHT
				);
	}


	/**
	 * @param tableModel the table model
	 * @return the panel for display
	 */
	Panel buildPanel (SimpleXmlTableModel tableModel)
	{
		SimpleXmlDocumentProperties p = tableModel.getProperties ();
		fieldFocusHandlers = new FieldFocusHandler[p.getColumnCount ()];
		columnNames = p.getColumnNames (); columnTypes = p.getColumnTypes ();
		columnAttributes = p.getColumnAttributes ();

		FieldFocusHandler f; String attr;
		Panel panel = startGridPanel (null, 0, 2);
		for (int i = 0; i < columnNames.length; i++)
		{
			Check verifier = VerificationLibrary.getCheckFor (columnTypes[i]);
			Field field = addField (panel, columnNames[i], working.get (attr = columnAttributes[i]));
			field.addFocusListener (f = new FieldFocusHandler (working, attr, field, verifier, document, nodeId));
			working.remove (columnAttributes[i]);
			fieldFocusHandlers[i] = f;
		}
		saveButton = addButton (panel, "Save Changes", new SaveCallback ());
//		Panel scrolling = new Panel (); scrolling.add (new JScrollPane (p));
//		return scrolling;
		return panel;
	}
	String[] columnNames, columnAttributes, columnTypes;
	FieldFocusHandler[] fieldFocusHandlers;
	Button saveButton;


	/**
	 * callback for update to node
	 */
	class SaveCallback implements Callback
	{
		public void executeAction () throws Exception { saveChanges (); }
		public String getTitleForErrorDialog () { return "Unable to save"; }
		public Object getAssociatedComponent () { return saveButton; }
	}


	/**
	 * save changed node values
	 */
	public void saveChanges ()
	{
		try
		{
			checkForErrors ();
			document.getNode (nodeId).setAll (working);
			DisplayTablePrimitives.refresh (tableModel);
			frame.dispose ();
		}
		catch (Alert alert)
		{
			alert.presentDialog ();
		}
	}
	public void checkForErrors () throws Alert
	{
		for (FieldFocusHandler f : fieldFocusHandlers)
		{
			if (!f.checked) f.runCheck ();
			if (f.error) alertError ("Unable to save, clear errors first");
		}
	}


}


/**
 * use focus adapter to capture focus gained/lost on field edits
 */
class FieldFocusHandler extends FocusAdapter
{

	/* (non-Javadoc)
	 * @see java.awt.event.FocusAdapter#focusGained(java.awt.event.FocusEvent)
	 */
	public void focusGained (FocusEvent e)
	{
		String value;
		if ((value = staging.get (item)) != null)
		{
			edited.setText (value);
		}
	}

	/* (non-Javadoc)
	 * @see java.awt.event.FocusAdapter#focusLost(java.awt.event.FocusEvent)
	 */
	public void focusLost (FocusEvent e)
	{
		runCheck ();
	}

	/**
	 * use check object to verify data
	 */
	public void runCheck ()
	{
		try
		{
			error = false; checked = true;
			String value = edited.getText ();
			edited.setForeground (SimpleScreenIO.Colour.BLACK);
			if (verifier != null) value = verifier.verify (value, document, staging, nodeId);
			staging.set (item, value); edited.setText (value);
		}
		catch (SimpleScreenIO.Alert alert)
		{
			alert.presentDialog ();
			edited.setForeground (SimpleScreenIO.Colour.RED);
			error = true;
		}
	}

	FieldFocusHandler
		(
			Node staging, String item, SimpleScreenIO.Field edited,
			Check verifier, Document document, int nodeId
		)
	{
		this.staging = staging;
		this.edited = edited; this.verifier = verifier;
		this.document = document; this.nodeId = nodeId;
		this.item = item;
	}
	Check verifier;
	Document document;
	SimpleScreenIO.Field edited;
	Node staging; String item;
	boolean checked = false;
	boolean error = false;
	int nodeId;

}

