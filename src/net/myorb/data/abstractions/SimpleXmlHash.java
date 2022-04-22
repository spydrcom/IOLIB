
package net.myorb.data.abstractions;

import net.myorb.data.abstractions.SimpleXmlHash.Node;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import org.xml.sax.helpers.DefaultHandler;
import javax.xml.parsers.SAXParserFactory;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.PrintStream;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * abstraction of XML as array of hash
 * @author Michael Druckman
 */
public class SimpleXmlHash extends ArrayList <HashMap <String,String>>
{


	/**
	 * keys to hash maps with particular significance
	 */
	public static final String
	TEXT = "<>",  //  the text contents of the element
	NAME = "[]";  //  the name of the element


	/**
	 * abstraction of XML document as list of hash map
	 */
	public static class Document extends SimpleXmlHash
	{

		/**
		 * for internal use
		 */
		protected Document () {}

		/**
		 * @param rootNode name of the root node
		 */
		public Document (String rootNode) { super (rootNode); }

		/**
		 * @param rootNode node to be used as root
		 */
		public Document (Node rootNode) { documentNode = rootNode; }

		/**
		 * @param node the new root node
		 * @return the document
		 */
		public Document setAsRoot (Node node) { documentNode = node; return this; }

		private static final long serialVersionUID = -8699747214859018190L;
	}


	/**
	 * @return Iterator for node list
	 */
	public Iterable<Node> getNodeList ()
	{
		return new NodeList (this);
	}


	/**
	 * @param number the sequence number of the node within document
	 * @return the node at the specified location
	 */
	public Node getNode (int number)
	{
		HashMap <String,String> contents = get (number);
		if (contents instanceof Node) return (Node) contents;
		Node node = new Node (contents);
		set (number, node);
		return node;
	}


	/**
	 * abstraction of XML node as hash map
	 */
	public static class Node extends HashMap <String,String>
	{

		/**
		 * @param name the element qName
		 */
		public Node (String name) { put (NAME, name); }

		/**
		 * @param contents a map of name/value pairs
		 * @param <T> type of data in map
		 */
		public <T> Node (Map <String,T> contents) { setAll (contents); }

		/**
		 * @return the name of the node
		 */
		public String getName () { return get (NAME); }

		/**
		 * @param document add the node to specified document
		 */
		public void addTo (Document document) { document.add (this); }

		/**
		 * @param text the text to place in node
		 * @return the node with text attached
		 */
		public Node addText (String text) { put (TEXT, text); return this; }

		/**
		 * @return the text of the node
		 */
		public String getText () { return get (TEXT); }

		/**
		 * @param <T> value data type
		 * @param attr name of attribute
		 * @param value the attribute value
		 * @return the element
		 */
		public <T> Node set (String attr, T value) { put (attr, value.toString ()); return this; } 

		/**
		 * @param <T> value data type
		 * @param attributes map of attributes to be added
		 * @return the element
		 */
		public <T> Node setAll (Map<String,T> attributes)
		{
			for (String key : attributes.keySet ())
				put (key, attributes.get (key).toString ());
			return this;
		}

		/**
		 * @param attr name of the attribute
		 * @return value parsed as a number
		 */
		public Number getNumeric (String attr)
		{
			String text = get (attr);
			if (text == null) return null;
			return Double.parseDouble (text);
		}

		/**
		 * @param attr name of attribute to remove
		 * @return the element
		 */
		public Node withOut (String attr) { remove (attr); return this; }
		public Node withAttributesOnly () { return withOutName ().withOut (TEXT); }
		public Node withOutName () { return withOut (NAME); }

		/**
		 * @return Iterator for attribute list
		 */
		public Iterable<String> getAttributeList ()
		{
			List<String> attributes = new ArrayList<String> ();
			attributes.addAll (new Node (this).withAttributesOnly ().keySet ());
			return attributes;
		}

		/**
		 * @return full XML hash becomes document
		 */
		public Document toDocumentRoot () { return new Document (this); }

		/**
		 * @param document the document to become the parent
		 */
		public void setAsRootOf (Document document) { document.setAsRoot (this); }

		private static final long serialVersionUID = -7744283839044311018L;
	}


	/**
	 * for internal use
	 */
	protected SimpleXmlHash () {}

	/**
	 * @param documentName name for root element
	 */
	public SimpleXmlHash (String documentName)
	{ this.documentNode = new Node (documentName); }

	/**
	 * @return the root node of the document
	 */
	public Node getRootNode () { return documentNode; }
	protected Node documentNode;


	/**
	 * @param to stream to write content to
	 * @throws Exception for any errors
	 */
	public void write (OutputStream to) throws Exception
	{
		String name = documentNode.getName (), text;
		PrintStream out = new PrintStream (to);

		out.print ("<"); out.print (name);					// format root element
		writeAttributes (documentNode, out);				// format root element attributes
		out.println (">");									// root always expected to have child nodes
		
		for (Node element : getNodeList ())
		{
			String elementName =
				element.containsKey (NAME) ?
						element.get (NAME) : "ITEM";
			out.print (" <" + elementName);

			writeAttributes (element, out);					// format element attributes

			text = element.getText ();						// optional text block ???

			if (text == null)
				out.println ("/>");							// no text block found
			else
			{
				out.print (">"); out.print (text);			// text is included
				out.println ("</" + elementName + ">");		// closing tag for element
			}
		}

		text = documentNode.getText ();						// text of root node (when found)
		if (text != null) out.println (text);				// is included after all nested elements

		out.println ("</" + name + ">");					// end of document
		out.close ();
	}
	public void write () throws Exception
	{
		write (new FileOutputStream ("data/" + documentNode.getName () + ".xml"));
	}


	/**
	 * format Node attributes into XML format
	 * @param ofElement the element containing node of attributes
	 * @param to the stream being written
	 */
	public void writeAttributes (Node ofElement, PrintStream to)
	{
		for (String attribute : ofElement.getAttributeList ())
		{
			to.print (" "); to.print (attribute); to.print ("='");
			to.print (ofElement.get (attribute)); to.print ("'");
		}
	}


	/**
	 * @param source stream source for XML
	 * @return the list of node attributes
	 * @throws Exception for errors
	 */
	public static Document read (InputStream source) throws Exception
	{
		Document document = new Document ();
		SAXParserFactory.newInstance ().newSAXParser ().parse (source, document.getHandler ());
		new Node (document.remove (0)).setAsRootOf (document);
		return document;
	}
	public static Document read (String name) throws Exception
	{
		return read (new FileInputStream ("data/" + name + ".xml"));
	}
	Handler getHandler () { return new Handler (); }


	/**
	 * convert elements to hash tables
	 */
	class Handler extends DefaultHandler
	{

		/* (non-Javadoc)
		 * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
		 */
		public void characters (char[] ch, int start, int length) throws SAXException
		{
			String txt = "",
				next = new String (ch, start, length);
			if (next.trim ().isEmpty ()) return;

			if (last.containsKey (TEXT))
				txt = last.get (TEXT);
			last.put (TEXT, (txt + next).trim ());
		}

		/* (non-Javadoc)
		 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
		 */
		public void startElement (String uri, String localName, String qName, Attributes attributes) throws SAXException
		{
			Node map;
			include (map = new Node (qName));
			for (int i = 0; i < attributes.getLength (); i++)
			{ map.put (attributes.getQName (i), attributes.getValue (i)); }
		}

		/* (non-Javadoc)
		 * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
		 */
		public void endElement (String arg0, String arg1, String arg2) throws SAXException
		{
			last = get (0);
		}

		/**
		 * @param map the hash to include into document as node
		 */
		void include (Node map) { add (map); last = map; }
		HashMap <String,String> last;

	}


	private static final long serialVersionUID = 6791282303511020703L;
}


/**
 * array of nodes abstracted as node list
 */
class NodeList implements Iterable<Node>, Iterator<Node>
{

	NodeList (SimpleXmlHash document)
	{
		this.document = document;
		this.nodeCount = document.size ();
	}
	int currentNode = 0, nodeCount;
	SimpleXmlHash document;

	/* (non-Javadoc)
	 * @see java.util.Iterator#hasNext()
	 */
	public boolean hasNext ()
	{
		return currentNode < nodeCount;
	}

	/* (non-Javadoc)
	 * @see java.util.Iterator#next()
	 */
	public Node next ()
	{
		return document.getNode (currentNode++);
	}

	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<Node> iterator ()
	{
		return this;
	}
	
}

