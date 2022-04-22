
package net.myorb.data.abstractions;

import net.myorb.data.abstractions.SimpleStreamIO.TextSource;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import java.net.Socket;

/**
 * text source/sink interface implementation for socket layer networking
 * @author Michael Druckman
 */
public class ServerAccess
{


	/**
	 * unique exception for errors originated from RPC exchange
	 */
	public static class ServerError extends RuntimeException
	{
		public ServerError (String message) { super (message); }
		public ServerError (String message, Exception e) { super (message, e); }
		private static final long serialVersionUID = -4265193187426079261L;
	}


	/**
	 * @param hostName the host running the server
	 * @param portNumber the port number the service listens on
	 */
	public ServerAccess
		(
			String hostName, int portNumber
		)
	{
		this.hostName = hostName;
		this.portNumber = portNumber;
	}
	String hostName;
	int portNumber;


	/**
	 * @param request a text request to pass by socket
	 * @return the text source to read the response
	 */
	public TextSource getSocketSource
		(
			String request
		)
	{
		try
		{
			Socket s = null;
		    writeTo (s = new Socket (hostName, portNumber), request);
		    return new TextSource (s.getInputStream ());
		}
		catch (Exception e)
		{
			throw new ServerError ("Error processing socket source", e);
		}
	}


	/**
	 * @param s the socket being written to
	 * @param message the text of the message to send
	 * @throws Exception for IO errors
	 */
	public void writeTo (Socket s, String message) throws Exception
	{
	    PrintWriter out =
	    	new PrintWriter
	    	(
	    		s.getOutputStream (), true
	    	);
	    out.println (message);
	}


	/**
	 * @param s the socket being read
	 * @return the text line read from the socket
	 * @throws Exception for IO errors
	 */
	public String readFrom (Socket s) throws Exception
	{
	    BufferedReader in = 
	        new BufferedReader
	        (
	            new InputStreamReader (s.getInputStream ())
	        );
	    return in.readLine ();
	}


	/**
	 * implement the request/response handshake
	 * @param request the text of the message to send as request
	 * @return the text line response read from the socket
	 */
	public String issueRequest
		(
			String request
		)
	{
		Socket s = null;
		String response = "";
	
		try
		{
		    s = new Socket
		    	(hostName, portNumber);
		    writeTo (s, request);
		    response = readFrom (s);
		}
		catch (Exception e)
		{
			throw new ServerError ("Error producing response to socket request", e);
		}
		finally
		{
			try { if (s != null) s.close (); }
			catch (Exception x) {}
		}
	
		return response;
	}


}

