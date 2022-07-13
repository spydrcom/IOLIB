
package net.myorb.rpc.primitive;

import net.myorb.rpc.interpreters.*;

import net.myorb.data.abstractions.ServerAccess;
import net.myorb.data.abstractions.ErrorHandling;

import net.myorb.data.notations.json.JsonSemantics;

import java.util.HashMap;

import org.xml.sax.helpers.DefaultHandler;

/**
 * establish conventions for simple socket server 
 * - assume transaction of simple text send and receive
 * @author Michael Druckman
 */
public class ServerConventions implements Runnable
{


	// types of request processors

	/**
	 * processor base type.
	 * actual implementer must extend this interface.
	 * implementer of just base interface will generate exception.
	 */
	public interface Processor {}

	/**
	 * the table of available service processors
	 */
	public static class ServicesTable extends HashMap <String, Processor>
	{ private static final long serialVersionUID = 857456473159181587L; }

	/**
	 * processor for simple text requests
	 */
	public interface RawTextProcessor extends Processor
	{
		/**
		 * provide simple processing for text transaction
		 * @param request text request for transaction
		 * @return text response to request
		 * @throws Exception for errors
		 */
		String process (String request) throws Exception;
	}

	/**
	 * processor for JSON requests
	 */
	public interface JsonProcessor extends Processor
	{
		/**
		 * provide simple processing for JSON transaction
		 * @param request JSON request for transaction
		 * @return text response to request
		 */
		String process (JsonSemantics.JsonValue request);
	}

	/**
	 * provide access to a SAX parser handler
	 */
	public interface SaxProcessor extends Processor
	{
		DefaultHandler getHandler ();
		String getResponse ();
	}

	/**
	 * processor for XML requests
	 */
	public interface XmlProcessor extends Processor
	{
		SaxProcessor getSaxProcessor ();
	}

	/**
	 * special case for recognized processors
	 */
	public interface RpcHandler extends RawTextProcessor {}

	// types of request processors [end of list]


	/**
	 * @param port the port number to be provided to socket server
	 * @param processor an implementation of text transaction conventions
	 * @param terminator the text of the request that will terminate the service
	 */
	public ServerConventions (int port, Processor processor, String terminator)
	{
		this.identify (processor);
		this.server = new ServerTcpIO (port);
		this.terminator = terminator;
		this.port = port;
	}
	protected ServerTcpIO server;
	protected String terminator;
	protected int port;


	/**
	 * produce a handler given type of processor
	 * @param processor the processor object to be used
	 */
	public void identify (Processor processor)
	{
		if (processor instanceof RawTextProcessor) handler = new TextHandler (processor);
		else if (processor instanceof JsonProcessor) handler = new JsonHandler (processor);
		else if (processor instanceof XmlProcessor) handler = new XmlHandler (processor);
		else throw new ServerAccess.ServerError ("Processor not recognized");
	}
	protected RpcHandler handler;


	/**
	 * execute a server instance in a parallel thread
	 * @param port the port number to be provided to socket server
	 * @param processor an implementation of text transaction conventions
	 * @param terminator the text of the request that will terminate the service
	 */
	public static void provideService
	(int port, Processor processor, String terminator)
	{
		new Thread
		(new ServerConventions (port, processor, terminator))
		.start ();
	}

	/**
	 * a named service rather than anonymous
	 * @param called the name used for the service
	 * @param onPort the port number to be provided to socket server
	 * @param using an implementation of text transaction conventions
	 * @param terminator the text of the request that will terminate the service
	 */
	public static void provideService
	(String called, int onPort, Processor using, String terminator)
	{
		ServerConventions service =
			new ServerConventions (onPort, using, terminator);
		service.setName (called); new Thread (service).start ();
	}

	/**
	 * execute a server loop (in current thread)
	 * @param port the port number to be provided to socket server
	 * @param processor an implementation of text transaction conventions
	 * @param terminator the text of the request that will terminate the service
	 */
	public static void runService
	(int port, Processor processor, String terminator)
	{
		new ServerConventions (port, processor, terminator).process ();
	}


	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run ()
	{
		try
		{ process (); }
		catch (Exception e)
		{ showStateChange (e.getMessage ()); }
	}


	/**
	 * format message to System.err
	 * @param message a message for description of the reason
	 */
	public void showStateChange (String message) { System.err.println (terminating (message)); }


	/**
	 * @param message a message for description of the reason
	 * @return a formatted status message
	 */
	public String terminating (String message) { return stateChange ("terminating") + ", " + message; }


	/**
	 * @param state the new state of the service
	 * @return a formatted status message
	 */
	public String stateChange (String state) { return identify () + " " + state; }


	/**
	 * @return a formatted identity message
	 */
	public String identify ()
	{
		return "Service (" + name + ") on port " + port;
	}


	/**
	 * @param name a name for the service
	 */
	public void setName (String name) { this.name = name; }
	protected String name = "ANONYMOUS";


	/**
	 * infinite loop of accept on socket
	 */
	public void process ()
	{
		try { do { process (server.accept ()); } while (true); }
		catch (ErrorHandling.Messages identifiedMessage) { throw identifiedMessage; }
		catch (Exception e) { ErrorHandling.unexpected (e); }
		finally { server.close (); }
	}


	/**
	 * process an accepted connection
	 * @param connection a connection to be processed
	 */
	public void process (ServerTcpIO.Connection connection)
	{
		try
		{
			String request;
			if ((request = connection.read ()).equals (terminator))
			{
				connection.write ("Termination request seen, server exit");
				throw new ErrorHandling.Notification (">>> Termination request seen");
			} else { connection.write (handler.process (request)); }
		}
		catch (ErrorHandling.Messages identifiedMessage) { throw identifiedMessage; }
		catch (Exception e) { ErrorHandling.unexpected (e); }
		finally { connection.close (); }
	}


}


/**
 * Handler object wrapping RAW-TEXT processor objects
 */
class TextHandler implements ServerConventions.RpcHandler
{

	protected TextHandler (ServerConventions.Processor processor)
	{ this.processor = (ServerConventions.RawTextProcessor) processor; }
	protected ServerConventions.RawTextProcessor processor;

	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.ServerConventions.RawTextProcessor#process(java.lang.String)
	 */
	public String process (String request) throws Exception
	{
		return processor.process (request);
	}

}


/**
 * Handler object wrapping JSON processor objects
 */
class JsonHandler extends JsonInterpreter
	implements ServerConventions.RpcHandler
{

	protected JsonHandler (ServerConventions.Processor processor)
	{ this.processor = (ServerConventions.JsonProcessor) processor; }
	protected ServerConventions.JsonProcessor processor;

	/**
	 * process a JSON request packet
	 * @param request the text string assumed to be JSON source
	 * @return the response generated by the JSON processor
	 * @throws Exception for JSON parser errors
	 */
	public String process (String request) throws Exception
	{
		return processor.process (interpret (request));
	}

}


/**
 * Handler object wrapping XML processor objects
 */
class XmlHandler extends XmlInterpreter
	implements ServerConventions.RpcHandler
{

	protected XmlHandler (ServerConventions.Processor processor)
	{ this (((ServerConventions.XmlProcessor) processor).getSaxProcessor ()); }

	protected XmlHandler (ServerConventions.SaxProcessor processor)
	{
		super (processor.getHandler ());
		this.saxProcessingManager = processor;
	}
	protected ServerConventions.SaxProcessor saxProcessingManager;

	/**
	 * process an XML request packet
	 * @param request the text string assumed to be XML source
	 * @return the response generated by the XML processor
	 * @throws Exception for XML parser errors
	 */
	public String process (String request) throws Exception
	{
		interpret (request);
		return saxProcessingManager.getResponse ();
	}

}

