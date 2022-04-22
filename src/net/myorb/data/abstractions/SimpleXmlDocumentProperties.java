
package net.myorb.data.abstractions;

import net.myorb.data.abstractions.SimpleXmlHash.Node;
import net.myorb.data.abstractions.SimpleXmlHash.Document;

/**
 * description of the document from attributes held in the root node
 * @author Michael Druckman
 */
public class SimpleXmlDocumentProperties
{


	/**
	 * attributes holding frame title and element name
	 */
	public static final String
	DISPLAY_TITLE = "DISPLAY_TITLE",
	ROOT_DISPLAY_TITLE = "DISPLAY_TITLE",
	ELEMENT_NAME = "ELEMENT_NAME";


	/**
	 * attributes holding template properties
	 */
	public static final String
	ROOT_ELEMENT_NAME = "ROOT_ELEMENT_NAME",
	INSTANCE_DISPLAY_TITLE = "INSTANCE_DISPLAY_TITLE",
	INSTANCE_ELEMENT_NAME = "INSTANCE_ELEMENT_NAME";


	/**
	 * attribute roots providing descriptions of columns
	 */
	public static final String
	COLUMN_NAME  = "COL_NAME_",   COLUMN_ATTR = "COL_ATTR_",   COLUMN_TYPE = "COL_TYPE_",
	COLUMN_COUNT = "COLUMN_COUNT";


	/**
	 * core template properties
	 */
	public interface TemplateUpdate
	{
		/**
		 * @return name of root element
		 */
		String getRootElementName ();

		/**
		 * @return display title for insance
		 */
		String getInstanceDisplayTitle ();

		/**
		 * @return element name used in instance
		 */
		String getInstanceElementName ();
	}


	/**
	 * @param document the document to be described
	 */
	public SimpleXmlDocumentProperties (Document document)
	{ setColumnMapFrom (document.getRootNode ()); }


	/**
	 * @param columnMap the node to hold the description
	 * @param number the number of the column
	 * @param name the name of the column
	 * @param attr the attribute of the column
	 * @param type the type of the column
	 */
	public static void setColumnMap
	(Node columnMap, int number, String name, String attr, String type)
	{
		columnMap
		.set (COLUMN_NAME + number, name)
		.set (COLUMN_ATTR + number, attr)
		.set (COLUMN_TYPE + number, type);
	}

	/**
	 * @param tableName root element name
	 * @param columnNames names of the columns
	 * @param columnAttributes attributes for the columns
	 * @param columnTypes data type for each column
	 * @return map of names and attributes
	 */
	public static Node buildTemplateFor
		(String tableName, String[] columnNames, String[] columnAttributes, String[] columnTypes)
	{
		int count;
		Node columnMap = new Node (tableName).set (COLUMN_COUNT, count = columnNames.length);
		for (int i = 1; i <= count; i++)
		{
			setColumnMap (columnMap, i, columnNames[i-1], columnAttributes[i-1], columnTypes[i-1]);
		}
		//System.out.println (columnMap);
		return columnMap;
	}

	/**
	 * @param map the table root node
	 */
	public void setColumnMapFrom (Node map)
	{
		rootNode = map;
		columnCount = map.getNumeric (COLUMN_COUNT).intValue ();
		columnNames = new String[columnCount]; columnTypes = new String[columnCount];
		columnAttributes = new String[columnCount];

		for (int i = 1; i <= columnCount; i++)
		{
			columnTypes[i-1] = map.get (COLUMN_TYPE + i);
			columnAttributes[i-1] = map.get (COLUMN_ATTR + i);
			columnNames[i-1] = map.get (COLUMN_NAME + i);
		}
	}
	protected Node rootNode;


	public String getDocumentTitle ()
	{ return rootNode.get (ROOT_DISPLAY_TITLE); }
	public String getElementName () { return rootNode.get (ELEMENT_NAME); }


	public int getColumnCount () { return columnCount; }
	public String[] getColumnTypes() { return columnTypes; }
	public String[] getColumnNames () { return columnNames; }
	public String[] getColumnAttributes () { return columnAttributes; }
	public String getColumnNameFor (int column) { return columnNames[column]; }
	public String getColumnAttributeFor (int column) { return columnAttributes[column]; }
	protected String[] columnNames, columnAttributes, columnTypes;
	protected int columnCount;


	public static void updateTemplate (TemplateUpdate details, Document document) throws Exception
	{
		Node n = document.getRootNode ();
		String rootElementName = details.getRootElementName ();
		n.set (SimpleXmlHash.NAME, rootElementName + "Template")
			.set (INSTANCE_DISPLAY_TITLE, details.getInstanceDisplayTitle ())
			.set (INSTANCE_ELEMENT_NAME, details.getInstanceElementName ())
			.set (ROOT_ELEMENT_NAME, rootElementName);
		document.write ();
	}


	public static Document prepareInstance (TemplateUpdate details, Node template) throws Exception
	{
		return template.set (DISPLAY_TITLE, details.getInstanceDisplayTitle ())
		.set (ELEMENT_NAME, details.getInstanceElementName ()).toDocumentRoot ();
	}


	static String chk (String source) { return source==null? "": source; }
	public static String checkInstanceDisplayTitle (Node rootNode) { return chk (rootNode.get (INSTANCE_DISPLAY_TITLE)); }
	public static String checkInstanceElementName (Node rootNode) { return chk (rootNode.get (INSTANCE_ELEMENT_NAME)); }
	public static String checkRootElementName (Node rootNode) { return chk (rootNode.get (ROOT_ELEMENT_NAME)); }


}

