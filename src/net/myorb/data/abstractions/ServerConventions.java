
package net.myorb.data.abstractions;

/**
 * establish conventions for simple socket server 
 * - assume transaction of simple text send and receive
 * @author Michael Druckman
 */
public class ServerConventions implements Runnable
{


	/**
	 * process implements text transaction
	 */
	public interface Processor
	{
		/**
		 * provide simple processing for text transaction
		 * @param request text request for transaction
		 * @return text response to request
		 */
		String process (String request);
	}


	/**
	 * @param port the port number to be provided to socket server
	 * @param processor an implementation of text transaction conventions
	 * @param terminator the text of the request that will terminate the service
	 */
	public ServerConventions (int port, Processor processor, String terminator)
	{
		server = new ServerTcpIO (port);
		this.terminator = terminator;
		this.processor = processor;
	}
	protected Processor processor;
	protected ServerTcpIO server;
	protected String terminator;


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
		(
			new ServerConventions (port, processor, terminator)
		).run ();
	}


	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run ()
	{
		process ();
	}
	

	/**
	 * infinite loop of accept on socket
	 */
	public void process ()
	{
		try 
		{
			do {
				process (server.accept ());
			} while (true);
		}
		catch (Exception e) {}
	}


	/**
	 * @param c a connection to be processed
	 */
	public void process (ServerTcpIO.Connection c)
	{
		try
		{
			String request = c.read ();

			if (request.equals (terminator))
			{
				c.write ("Termination request seen, server exit");
				throw new RuntimeException ("Termination request seen");
			}

			c.write (processor.process (request));
		}
		finally { c.close (); }
	}


}

