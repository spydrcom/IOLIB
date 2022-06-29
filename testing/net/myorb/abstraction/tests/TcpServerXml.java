
package net.myorb.abstraction.tests;

import net.myorb.data.abstractions.ServerConventions;

import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.SAXException;
import org.xml.sax.Attributes;

/**
 * test class for XML RPC service
 */
public class TcpServerXml
{

	public static void main (String[] s) throws Exception
	{
		System.out.println ("test starts");
		ServerConventions.provideService (8081, new XmlProcessor (), "\f");
		System.out.println ("test ends");
	}

}

class XmlProcessor extends DefaultHandler
	implements ServerConventions.SaxProcessor, ServerConventions.XmlProcessor
{

	/**
	 * allocate a buffer for the response
	 */
	protected XmlProcessor ()
	{ buffer = new StringBuffer (); }
	protected StringBuffer buffer = null;

	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.ServerConventions.XmlProcessor#getSaxProcessor()
	 */
	public ServerConventions.SaxProcessor getSaxProcessor () { return this; }

	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.ServerConventions.SaxProcessor#getHandler()
	 */
	public DefaultHandler getHandler () { return this; }

	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.ServerConventions.SaxProcessor#getResponse()
	 */
	public String getResponse () { return buffer.toString (); }

	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	public void startElement
		(String uri, String localName, String qName, Attributes attributes)
	throws SAXException
	{
		buffer.append ("NodeName=").append (qName).append (": ");

		for (int i = 0; i < attributes.getLength (); i++)
		{
			String name = attributes.getQName (i);
			String valu = attributes.getValue (i);

			buffer.append (name)
			.append ("=").append (valu)
			.append ("; ");
		}
	}

}

