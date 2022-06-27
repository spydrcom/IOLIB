
package net.myorb.abstraction.tests;

import net.myorb.data.abstractions.ServerConventions;
import net.myorb.data.abstractions.ServerTcpIO;

public class TcpServer
{

	public static void main (String[] s) throws Exception
	{
		System.out.println ("test starts");
		ServerConventions.provideService (8081, new Processor (), "\f");
		System.out.println ("test ends");
	}

	public static void simpleTest () throws Exception
	{
		ServerTcpIO.Connection c;
		ServerTcpIO server = new ServerTcpIO (8081);
		
		while (true)
		{
			c = server.accept ();

			String content = c.read ();
			System.out.println (content);
			
			c.write ("content received");
			c.write (content);
			c.write ("*EOT*");

			c.close ();
		}
	}

}

class Processor implements ServerConventions.RawTextProcessor
{

	@Override
	public String process(String request) {
		System.out.println (request);
		return "OK, size=" + request.length();
	}
	
}

