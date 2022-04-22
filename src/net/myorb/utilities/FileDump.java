
package net.myorb.utilities;

import net.myorb.data.abstractions.*;

import java.util.ArrayList;

import java.io.File;

public class FileDump extends SimpleStreamIO
{

	public static void main (String[] args) throws Exception
	{
		//dump (args[0]);
		//dump ("/links/cfg.json");
		//dump ("/workspace/pix.zip");
		dump ("/links/text.lnk");
	}

	public static void dump (String path) throws Exception
	{
		System.out.println (path);
		Source source = new FileSource (new File (path)).getSource ();

		if (source instanceof TextSource)
		{
			printToConsole ((TextSource) source);
		}
		else
		{
			dump (source);
		}
	}

	public static class Buffer
	{
		Buffer (byte[] bytes) { this.bytes = bytes; }
		byte[] bytes;
	}
	@SuppressWarnings ("serial") public static class Parts extends ArrayList <Buffer> {}
	static Parts parts = new Parts ();

	public static void dump (Source source) throws Exception
	{
		System.out.println ("resource is binary");

		int size = 0;
		byte[] bytes = source.get ();
		while (bytes != null)
		{
			size += bytes.length;
			parts.add (new Buffer (bytes));
			bytes = source.get ();
		}
		System.out.println (size + " bytes read in " + parts.size () + " parts");
		
		for (Buffer buf : parts)
		{
			int n = 16;
			for (byte b : buf.bytes)
			{
				int val = b; val &= 255;

				System.out.print (Integer.toHexString (256+val).substring(1).toUpperCase ());

				n--;
				if (n % 2 == 0) System.out.print (" ");
				if (n % 4 == 0) System.out.print (" ");
				System.out.print (" ");

				if (n == 0)
				{
					System.out.println ();
					n = 16;
				}
			}
		}
		System.out.println ();
	}

}
