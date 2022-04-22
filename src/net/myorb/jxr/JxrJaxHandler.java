
package net.myorb.jxr;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;

import java.io.InputStream;
import java.util.HashSet;

/**
 * SAX handler for parsing JXR source
 * @author Michael Druckman
 */
public class JxrJaxHandler extends DefaultHandler
{


	public enum Types
	{

		// outermost tag
		JXR,

		// class declaration
		PACKAGE,
		CLASS,

		// class instance
		POPULATE,
		NEW,

		// method/field access
		EVALUATE,
		FIELD,
		CALL,

		// data containers
		INT,
		FLOAT,
		STRING,
		TRUE,
		FALSE,

		// object manipulation
		SUBSTRING,
		PRINT,
		ARRAY,
		EQUALS,
		IFNOT,
		IF,

		// symbol manipulation
		COMMON,
		TABULATE,
		EXPORTALL,
		EXPORT,

		// macro expansion / source nesting
		MACRO,
		EXPAND,
		READ,

		// bean access
		BEAN,
		REFERTO,
		GET,
		TEXT,
		SET,
		IS,

		// stack reduction
		SAVEAS,
		VOID

	}
	public static Types lookup (String name) { return Types.valueOf (name.trim ().toUpperCase ()); }


	JxrJaxHandler (JxrParser parser)
	{
		this.state = parser;
	}
	JxrParser state;


	/**
	 * identify set of nodes expected to hold text
	 */
	public static class TextContainer extends HashSet<Types>
	{
		TextContainer ()
		{
			add (Types.INT);
			add (Types.FLOAT);
			add (Types.STRING);
			add (Types.PACKAGE);
		}
		private static final long serialVersionUID = 5726183130611073915L;
	}
	private static final TextContainer TEXT_CONTAINERS = new TextContainer ();


	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
	 */
	public void characters (char[] ch, int start, int length) throws SAXException
	{
		if (inContainer)
		{
			state.tos ().add (new String (ch, start, length));
		}
	}
	private boolean inContainer = false;


	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	public void startElement (String uri, String localName, String qName, Attributes attributes) throws SAXException
	{
		Types typeOfNode = lookup (qName);
		inContainer = TEXT_CONTAINERS.contains (typeOfNode);
		JxrReport.pushNode (qName);
		state.push (attributes);

		if (typeOfNode == Types.BEAN)
		{
			state.getParameterObjectsTOS ();
		}
	}


	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	public void endElement (String arg0, String arg1, String qName) throws SAXException
	{
		Object last = null;
		Types typeOfNode = lookup (qName);

		try { last = state.processNode (typeOfNode); }
		catch (Exception e) { processError (e, qName); }

		JxrReport.popNode ();
		if (typeOfNode == Types.JXR) return;

		state.endOfNode (last);
		inContainer = false;
	}


	/**
	 * pass back node information
	 */
	public static class ExtendedSAXException extends SAXException
	{

		String message, nodeName;
		JxrSymManager.AttributeHash attributes;

		ExtendedSAXException
			(
				Exception cause, String message, String nodeName,
				JxrSymManager.AttributeHash attributes
			)
		{
			super (cause);
			this.attributes = attributes;
			this.nodeName = nodeName;
			this.message = message;
		}

		/* (non-Javadoc)
		 * @see org.xml.sax.SAXException#toString()
		 */
		public String toString ()
		{
			StringBuffer text = new StringBuffer ();
			text.append (message).append (": ").append (nodeName).append (" ");
			for (String at : attributes.keySet ())
			{
				text.append (at).append ("=").append (attributes.get (at)).append (" ");
			}
			return text.toString ();
		}

		private static final long serialVersionUID = 5613732701349973230L;
	}
	void nodeProcessingError (Exception e, String element) throws ExtendedSAXException
	{
		if (JxrReport.VERBOSE_ERROR_TRACE) e.printStackTrace ();
		throw new ExtendedSAXException (e, e.getMessage (), element, state.attributes);
	}
	void processError (Exception e, String element) throws ExtendedSAXException
	{
		if (JxrReport.RAISE_AS_EXCEPTION) nodeProcessingError (e, element);
		JxrReport.processError (e, element, state.attributes.toString ());
	}


	/**
	 * @return a new instance of SAX parser
	 * @throws ParserConfigurationException configuration error
	 * @throws SAXException error indicated by implementation author
	 */
	public SAXParser getSAXParser ()
	throws ParserConfigurationException, SAXException
	{
		return PARSER_FACTORY.newSAXParser ();
	}
	static final SAXParserFactory PARSER_FACTORY = SAXParserFactory.newInstance ();


	/**
	 * @param source the XML input stream
	 * @return the processing state left over from the parse
	 * @throws Exception for any parser errors
	 */
	public JxrProcessingState parse (InputStream source) throws Exception
	{
		getSAXParser ().parse (source, this);
		return state;
	}


}

