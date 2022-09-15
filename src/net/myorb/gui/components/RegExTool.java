
package net.myorb.gui.components;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import java.awt.Dimension;
import java.awt.Component;

/**
 * GUI component to search entries of a text list using RegEx
 * - this extends FindWidget allowing multiple RegEx changed queries
 * @author Michael Druckman
 */
public class RegExTool extends FindWidget
{


	public RegExTool (String columnName)
	{
		super (columnName);
		this.setIgnoreCase (true);		// default to ignore case
		this.setExpression (".+");		// default will match everything
	}


	/**
	 * build a DisplayFrame to show in GUI
	 * @param title a title for the display frame
	 */
	public void show (String title)
	{
		this.search = new SearchComponent (this);
		f = new DisplayFrame (this.search.getComponent (), title);
		f.show (new Dimension (250, 400));
	}
	protected SearchComponent search;


	/**
	 * change table display to shown new data view
	 */
	void queryChanged ()
	{
		updateResults (search.getQuery ());
		updateDisplay (getTableAdapter ());
	}


	/**
	 * apply query expression to capture appropriate content
	 * @param query the text input from GUI
	 */
	void updateResults (String query)
	{
		this.setExpression (query);
		this.refresh ();
	}


	/**
	 * cause display components to alter to show new data
	 * @param table the table component displayed
	 */
	void updateDisplay (TableAdapter table)
	{
		table.setData (this.getCells ()); table.changeData ();
		table.refreshTable ();
	}


	private static final long serialVersionUID = -3814745804228729179L;
}


/**
 * construct form component to be displayed
 */
class SearchComponent extends SimpleScreenIO
{

	/**
	 * allocation of components
	 */
	SearchComponent ()
	{
		this.regex = new Field (20); this.panel = new Panel ();
	}

	/**
	 * connections between components
	 * @param tool the tool to be connected
	 */
	SearchComponent (RegExTool tool)
	{
		this ();

		this.panel.add (this.regex);
		this.panel.add (tool.getDisplayTablePanel ());

		this.icase = addCheckBox (panel, "Ignore Case", true);
		this.eol = addCheckBox (panel, "Wild EOL", true);

		addChangeDetection
		(
			new ChangeDetection (this.tool = tool)
		);
	}

	/**
	 * add listeners to components to detect changes
	 * @param detector listeners to be used
	 */
	void addChangeDetection (ChangeDetection detector)
	{
		this.icase.addItemListener (detector);
		this.regex.addKeyListener (detector);
		this.eol.addItemListener (detector);
	}
	// flags for Ignore-Case and Wild-EOL features
	// - see getQuery method to understand the implementation
	protected CheckBox icase, eol;

	/**
	 * get the built form to be displayed
	 * @return this widget as AWT component
	 */
	Component getComponent ()
	{
		return this.panel.toComponent ();
	}
	protected Panel panel;

	/**
	 * get text of query modified to contract
	 * @return the text of the current query form
	 */
	String getQuery ()
	{
		// get user specific text
		String query = this.regex.getText ();
		if (this.eol.isSelected ()) query += ".+";
		this.tool.setIgnoreCase (this.icase.isSelected ());
		// query is now modified to conform with contract
		return query;
	}
	protected RegExTool tool;
	protected Field regex;

}


/**
 * input key strokes indicate a change in the query
 * - check boxes indicate changes to the query when changed also
 */
class ChangeDetection extends KeyAdapter implements ItemListener
{

	/* (non-Javadoc)
	 * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
	 */
	public void itemStateChanged (ItemEvent e) { this.tool.queryChanged (); }

	/* (non-Javadoc)
	 * @see java.awt.event.KeyAdapter#keyReleased(java.awt.event.KeyEvent)
	 */
	public void keyReleased (KeyEvent e) { this.tool.queryChanged (); }

	ChangeDetection (RegExTool tool) { this.tool = tool; }
	protected RegExTool tool;

}

