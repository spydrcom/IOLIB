
package net.myorb.data.abstractions;

import java.util.ArrayList;

import java.util.List;
import java.util.Set;

/**
 * compile HTML document for HELP display
 * @author Michael Druckman
 */
public class HelpTableCompiler extends HtmlTable
{

	/**
	 * provide request for names and descriptions
	 */
	public interface TableCompilationAccess
	{
		/**
		 * @return the names being described
		 */
		Set<String> getElementNames ();

		/**
		 * @param name the name of an element
		 * @return the description of that element
		 */
		String descriptionFor (String name);
	}

	/**
	 * @param title the title for the displayed table
	 * @param tableHeader the text of the table header row
	 */
	public HelpTableCompiler (String title, String tableHeader)
	{
		setTitle (title); setTableHeader (tableHeader);
	}

	/**
	 * @param access the access to the data for the document
	 * @return an ordered list of the names being described
	 */
	public List<String> orderedListFrom (TableCompilationAccess access)
	{
		List<String> items = new ArrayList<String>();
		items.addAll (access.getElementNames ());
		items.sort (null);
		return items;
	}

	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.HtmlTable#setColumnHeaders(java.lang.String[])
	 */
	public HelpTableCompiler setColumnHeaders (String... to)
	{ super.setColumnHeaders (to); return this; }

	/**
	 * @param access the access to the data for the document
	 * @return the resulting HTML table representation
	 */
	public HtmlTable buildFrom (TableCompilationAccess access)
	{
		for (String item : orderedListFrom (access))
		{ addRow (item, access.descriptionFor (item)); }
		return this;
	}

}
