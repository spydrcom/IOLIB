
package net.myorb.rpc.interpreters;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.helpers.DefaultHandler;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.StringReader;
import java.io.Reader;

/**
 * parse JSON text used in communications
 * @author Michael Druckman
 */
public class XmlInterpreter
{

	public XmlInterpreter
	(DefaultHandler saxHandler)
	{ this.saxHandler = saxHandler; }
	protected DefaultHandler saxHandler;

	/**
	 * process an XML request packet
	 * @param request the text string assumed to be XML source
	 * @throws Exception for XML parser errors
	 */
	public void interpret (String request) throws Exception
	{
		parse (new StringReader (request));
	}

	/**
	 * @param source a Reader used to feed the parser
	 * @param processor the SAX processor object supplied in construction
	 * @throws Exception for errors
	 */
	public void parse (Reader source) throws Exception
	{ getSAXParser ().parse (new InputSource (source), saxHandler); }

	/**
	 * @return instance of SAX parser
	 * @throws ParserConfigurationException configuration errors
	 * @throws SAXException errors in SAX parser
	 */
	public static SAXParser getSAXParser ()
	throws ParserConfigurationException, SAXException { return PARSER_FACTORY.newSAXParser (); }
	static final SAXParserFactory PARSER_FACTORY = SAXParserFactory.newInstance ();

}
