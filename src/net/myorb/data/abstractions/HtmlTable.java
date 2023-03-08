
package net.myorb.data.abstractions;

import java.util.ArrayList;

/**
 * generic HTML table compilation object
 * @author Michael Druckman
 */
public class HtmlTable
{


	/**
	 * prepare default properties
	 */
	public HtmlTable ()
	{
		setDefaultAttributes ();
	}


	/**
	 * key elements have associated attribute lists
	 */
	public enum Elements
	{

		DOC ("HTML"),			// tags that structure DOCUMENT
		TITLE ("TITLE"),
		DOCHEAD ("HEAD"),
		DOCBODY ("BODY"),
		DIVISION ("DIV"),
		PARAGRAPH ("P"),

		TABLE ("TABLE"),		// tags that structure TABLE
		TABLEHEAD ("THEAD"),
		TABLEBODY ("TBODY"),
		COLUMNHEADER ("TH"),
		TABLEHEADER ("TH"),
		TABLECELL ("TD"),
		TABLEROW ("TR");

		/**
		 * elements have a name and a set of attributes
		 * @param tag the name applied to the element
		 */
		private Elements (String tag)
		{ attributes = new Attributes (); this.tag = tag; }

		/**
		 * @return the attributes map modification access object
		 */
		public SimpleUtilities.PropertySetting getProperties () { return attributes; }
		public Attributes getAttributes () { return attributes; }

		/**
		 * @return formatted closing tag
		 */
		public String close () { return "</" + tag + ">"; }
		public String tagName () { return tag; }

		/**
		 * @param to the buffer collecting content
		 * @return the buffer for CHAIN pattern
		 */
		public StringBuffer formatted (StringBuffer to)
		{ return to.append ("<").append (tag).append (attributes).append (">"); }
		protected String tag; protected Attributes attributes;

	}


	/**
	 * attributes are collected as mapped properties
	 */
	public static class Attributes extends SimpleUtilities.PropertiesMap
	{

		/**
		 * @param pairs name/value pairs for attribute settings
		 */
		public void set (Object... pairs)
		{
			for (int i = 0; i < pairs.length; i += 2)
			{
				setProperty (pairs[i].toString (), pairs[i+1]);
			}
		}

		/* (non-Javadoc)
		 * @see java.util.AbstractMap#toString()
		 */
		public String toString ()
		{
			StringBuffer
			buffer = new StringBuffer ();
			for (String s : keySet ())
			{
				buffer.append (" ").append (s)
				.append ("='").append (get (s))
				.append ("'");
			}
			return buffer.toString ();
		}
		private static final long serialVersionUID = -1099482652438286063L;
	}


	/**
	 * default boiler-plate appearance properties
	 */
	public void setDefaultAttributes ()
	{
		Elements.TABLE.getAttributes ().set (BORDER, 1, PAD, 5, SP, 5);
		Elements.TABLEHEADER.getAttributes ().set (BG, SILVER, S, FS_120);
		Elements.COLUMNHEADER.getAttributes ().set (BG, GRAY, FG, WHITE, S, FS_110);
		//Elements.COLUMNHEADER.getAttributes ().set (BG, GRAY, FG, WHITE, S, FS_110);		// looking for consistent behavior between
		//Elements.DIVISION.getAttributes ().set (BG, GRAY, S, "color:White;", S, FS_110);	// Java JLabel render and IE render
		//Elements.DIVISION.getAttributes ().set (BG, GRAY, FG, WHITE, S, FS_110);			// NOT found, IE keeps FG BLACK
		Elements.TABLEHEAD.getAttributes ().set (BG, GRAY, FG, WHITE, S, FS_110);
		Elements.TABLECELL.getAttributes ().set (S, FS_105);
	}
	public static final String FG = "color", BG = "bgcolor", S = "style", FS = "font-size:";
	public static final String FS_120 = FS+"120%;", FS_110 = FS+"110%;", FS_105 = FS+"105%;";
	public static final String SILVER = "#C0C0C0", GRAY = "#808080", WHITE = "#FFFFFF";
	public static final String BORDER = "border", PAD = "cellpadding";
	public static final String SP = "cellspacing", CS = "colspan";


	/**
	 * format opening tag for specified element
	 * @param buffer the buffer collecting HTML document contents
	 * @param element the element holding the attributes
	 * @return the buffer for chain calls
	 */
	public StringBuffer formatTag (StringBuffer buffer, Elements element)
	{ open.add (0, element); return element.formatted (buffer); }
	private ArrayList<Elements> open = new ArrayList<>();


	/**
	 * format a closing tag for element on top of stack
	 * @return the formatted closing tag for element on top of stack
	 */
	public String closeTag () { return open.remove (0).close (); }


	/**
	 * format complete tag around content
	 * @param buffer the buffer collecting HTML document contents
	 * @param element the element holding the attributes
	 * @param content the content to be enclosed
	 * @return buffer for CHAIN calls
	 */
	public StringBuffer formatContent
	(StringBuffer buffer, Elements element, Object content)
	{
		return formatTag (buffer, element).append (content).append (closeTag ());
	}


	/**
	 * format the THEAD content
	 * @param buffer the buffer collecting output
	 */
	public void formatThead (StringBuffer buffer)
	{
		formatTag (buffer, Elements.TABLEHEAD); // THEAD

		if (tableHeader != null)
		{
			formatTag (buffer, Elements.TABLEROW);
			Elements.TABLEHEADER.getAttributes ().set (CS, columnCount);
			formatContent (buffer, Elements.TABLEHEADER, tableHeader)
			.append (closeTag ()); // TR
		}

		if (columnHeaders != null)
		{
			formatTag (buffer, Elements.TABLEROW);
			for (String th : columnHeaders)
			{
				formatTag (buffer, Elements.COLUMNHEADER);
				formatContent (buffer, Elements.DIVISION, th)
				.append (closeTag ()); // TH
			}
			buffer.append (closeTag ()); // TR
		}

		buffer.append (closeTag ()); // THEAD
	}
	private int columnCount = -1;


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString ()
	{
		StringBuffer buffer = new StringBuffer ();
		formatTag (buffer, Elements.DOC);							// HTML

		if (title != null)
		{
			formatTag (buffer, Elements.DOCHEAD);					// HEAD
			formatContent (buffer, Elements.TITLE, title)			// TITLE
			.append (closeTag ());
		}

		formatTag (buffer, Elements.DOCBODY);						// BODY
		formatTag (buffer, Elements.PARAGRAPH);						// P
		formatTag (buffer, Elements.TABLE);							// TABLE

		// THEAD is not a required part of the table
		if (tableHeader != null || columnHeaders != null) { formatThead (buffer); }
		// the table body encloses all the rows and columns making the cell matrix boundaries clear
		formatContent (buffer, Elements.TABLEBODY, tableRows);

		// close remaining open tags, the stack keep track
		while (open.size () > 0) buffer.append (closeTag ());
		// the buffer now contains a complete document
		return buffer.toString ();
	}


	/**
	 * @param title text of the document title
	 */
	public void setTitle (String title)
	{
		this.title = escape (title);
	}
	private String title = null;


	/**
	 * @param to text of table header
	 */
	public void setTableHeader (String to)
	{
		tableHeader = escape (to);
	}
	private String tableHeader = null;


	/**
	 * @param to list of column headers
	 * @return THIS to allow compound calls
	 */
	public HtmlTable setColumnHeaders (String... to)
	{
		columnHeaders = to; return this;
	}
	private String[] columnHeaders = null;


	/**
	 * @param columns the text for each column of a row
	 */
	public void addRow (String... columns)
	{
		if (columnCount < 0)
		{
			columnCount = columns.length;
		}
		else if (columnCount != columns.length)
		{
			throw new RuntimeException ("Table width change not expected");
		}

		formatTag (tableRows, Elements.TABLEROW);

		for (String column : columns)
		{
			formatTag (tableRows, Elements.TABLECELL).append (escape (column)).append (closeTag ());
		}

		tableRows.append (closeTag ()); // TR
	}
	private StringBuffer tableRows = new StringBuffer ();


	/**
	 * @param source the text to be escaped
	 * @return the text altered to comply with HTML escape conventions
	 */
	public String escape (String source)
	{
		return source
		.replaceAll ("&", "&amp;")
		.replaceAll ("<", "&lt;")
		.replaceAll (">", "&gt;")
//		java label translation is NOT consistent
//		.replaceAll ("'", "&apos;")
		.replaceAll ("\"", "&quot;");
	}


}

