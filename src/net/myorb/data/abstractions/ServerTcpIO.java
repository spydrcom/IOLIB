
package net.myorb.data.abstractions;

import java.net.*;
import java.io.*;

/**
 * Simple TCP socket service manager for text content
 * @author Michael Druckman
 */
public class ServerTcpIO
{


	/**
	 * read and write operations made available on a connection
	 */
	public interface Connection
	{
		/**
		 * close the connection
		 * @throws ServerAccess.ServerError for any error
		 */
		void close () throws ServerAccess.ServerError;

		/**
		 * read content from connection
		 * @return text return from read of connection
		 * @throws ServerAccess.ServerError for any error
		 */
		String read () throws ServerAccess.ServerError;

		/**
		 * @param content write content to connection
		 */
		void write (String content);
	}


	/**
	 * reduce NET and IO generated errors to a RuntimeException.
	 * - a message will accompany the exception as will the Cause
	 * @param message the message to connect with the error condition
	 * @param exception the original Exception being reduced to a ServerError
	 * @throws ServerAccess.ServerError the reduced version of the Exception
	 */
	public static void error
		(String message, Exception exception)
	throws ServerAccess.ServerError
	{
		throw new ServerAccess.ServerError (message, exception);
	}


	/**
	 * construct a ServerSocket connected to a given port
	 * @param portNumber the port number to accept connections on
	 * @throws ServerAccess.ServerError for any error
	 */
	public ServerTcpIO (int portNumber) throws ServerAccess.ServerError
	{
		try
		{
			serverSocket = new ServerSocket (portNumber);
		}
		catch (Exception e)
		{
			error ("Socket creation error", e);
		}
	}
	protected ServerSocket serverSocket;


	/**
	 * accept a socket connection from the ServerSocket object
	 * @return an Implementation of Connection interface
	 * @throws ServerAccess.ServerError
	 */
	public Connection accept () throws ServerAccess.ServerError
	{
		try
		{
			return new ConnectionInstance (serverSocket.accept ());
		}
		catch (Exception e)
		{
			error ("Socket accept error", e);
		}
		return null;
	}


}


/**
 * Implementation of ServerTcpIO.Connection
 */
class ConnectionInstance implements ServerTcpIO.Connection
{


	ConnectionInstance (Socket clientSocket) throws ServerAccess.ServerError
	{
		this.clientSocket = clientSocket;
		this.connectStreams ();
	}
	protected Socket clientSocket;


	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.ServerTcpIO.Connection#close()
	 */
	public void close () throws ServerAccess.ServerError
	{
		try { clientSocket.close (); }
		catch (Exception e) { ServerTcpIO.error ("Error closing socket", e); }
	}


	/**
	 * establish read and write streams
	 * @throws ServerAccess.ServerError for any error
	 */
	void connectStreams () throws ServerAccess.ServerError
	{
		try
		{
			in = new BufferedReader
			   	(
			   		new InputStreamReader (clientSocket.getInputStream ())
			   	);
			out = new PrintWriter (clientSocket.getOutputStream (), true);
		}
		catch (Exception e)
		{
			ServerTcpIO.error ("Socket connection creation error", e);
		}
	}
	protected BufferedReader in;
	protected PrintWriter out;


	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.ServerTcpIO.Connection#write(java.lang.String)
	 */
	public void write (String content)
	{
		out.println (content);
	}


	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.ServerTcpIO.Connection#read()
	 */
	public String read () throws ServerAccess.ServerError
	{
		try { return in.readLine (); }
		catch (Exception e) { ServerTcpIO.error ("Socket read error", e); }
		return null;
	}


}

