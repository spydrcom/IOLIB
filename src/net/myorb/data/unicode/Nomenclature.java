
package net.myorb.data.unicode;

import net.myorb.data.abstractions.SimpleUtilities;
import net.myorb.gui.components.UnicodeCharacterBlock;

import java.awt.Font;

import java.io.BufferedReader;
import java.io.FileReader;

import java.util.HashMap;
import java.util.Map;

/**
 * symbols and code mappings to Unicode characters
 * @author Michael Druckman
 */
public class Nomenclature
{


	/**
	 * simple characters including pre-composed items
	 */
	public static class BasicInformation
	{
		public int code;
		public String description;
		public BasicInformation alternate;
		public String toString () { return Toolkit.hex4 (code); }
		BasicInformation (int code)
		{ this.code = code; }
	}


	/**
	 * extended case describing decomposed form
	 */
	public static class DecomposedInformation
			extends BasicInformation
	{
		int mark;													// mark identifies the over-strike character
		public String toString ()
		{ return Toolkit.formatDecomposedCode (code, mark); }		// displayed as basic-mark
		DecomposedInformation (int code, int mark)
		{ super (code); this.mark = mark; }
	}


	/**
	 * parse the descriptors file
	 * @throws Exception for IO errors
	 */
	public static void readCodeMappingsFile () throws Exception
	{
		String line;
		FileReader file =
			new FileReader ("data/CodeMappings.txt");
		BufferedReader reader = new BufferedReader (file);
		while ((line = reader.readLine ()) != null)
		{ process (line.split (",")); }
		reader.close ();
		file.close ();
	}


	/**
	 * @param descriptors description line split by comma delimiter
	 * @throws Exception for IO errors
	 */
	public static void process (String... descriptors) throws Exception
	{
		BasicInformation descriptor;

		String IDs = descriptors[0].trim (),
			parts[] = IDs.split ("-"), ID = parts[0];
		int code = Toolkit.parseHex (ID.substring (1));

		if (parts.length > 1)
		{
			int markCode = Toolkit.parseHex (parts[1]);
			descriptor = new DecomposedInformation (code, markCode);
		}
		else descriptor = new BasicInformation (code);

		descriptor.description = descriptors[1].trim ();

		if (codeMap.containsKey (code))
		{
			String kind = link (codeMap.get (code), descriptor);
			Toolkit.trace (kind + " alternate found " + descriptor);
		}
		else codeMap.put (code, descriptor);
		
		for (int i = 2; i < descriptors.length; i++)
		{
			ID = descriptors[i].trim ();
			if (encoded.containsKey (ID))
			{ Toolkit.trace ("Duplicate symbol found: " + ID); }
			if (!ID.isEmpty ()) encoded.put (ID, descriptor);
		}
	}


	/**
	 * alternate should point to decomposed form
	 * @param info the original descriptor for this code
	 * @param alt the decomposed version descriptor
	 * @return kind of information in alternate
	 */
	public static String link (BasicInformation info, BasicInformation alt)
	{
		String kind = (alt instanceof DecomposedInformation)? "Decomposed": "Precomposed";
		alt.alternate = info.alternate;
		info.alternate = alt;
		return kind;
	}


	/**
	 * lazy init of description map
	 * @throws Exception for IO errors
	 */
	public static void forceLoad () throws Exception
	{
		if (codeMap != null) return;
		codeMap = new HashMap<Integer,BasicInformation>();
		encoded = new HashMap<String,BasicInformation>();
		readCodeMappingsFile ();
	}
	static Map<Integer,BasicInformation> codeMap = null;
	static Map<String,BasicInformation> encoded = null;


	/**
	 * get description of Unicode value
	 * @param code the value of sought description
	 * @return the description text, empty if not found
	 */
	public static BasicInformation getInformation (int code)
	{
		try { forceLoad (); }
		catch (Exception e) { e.printStackTrace (); return null; }
		if (!codeMap.containsKey (code)) return null;
		return codeMap.get (code);
	}


	/**
	 * get Unicode value for symbol
	 * @param symbol the text of the symbol to find
	 * @return information block for the symbol, or null if not found
	 * @throws Exception for IO errors
	 */
	public static BasicInformation lookup (String symbol) throws Exception
	{
		forceLoad ();
		if (!encoded.containsKey (symbol)) return null;
		return encoded.get (symbol);
	}


	/**
	 * @throws Exception for IO errors
	 */
	public static void dumpSymbolList () throws Exception
	{
		forceLoad ();
		if (silent) return;
		for (String symbol : SimpleUtilities.orderedKeys (encoded))
		{
			System.out.print (symbol);
			BasicInformation info = encoded.get (symbol);
			System.out.print (" ["); System.out.print (info); System.out.print ("] ");
			System.out.print (": "); System.out.print (info.description);
			System.out.println ();
		}
	}


	/**
	 * @throws Exception for IO errors
	 */
	public static void dumpCodeList () throws Exception
	{
		forceLoad ();
		if (silent) return;
		BasicInformation info;
		for (Integer code : SimpleUtilities.orderedKeys (codeMap))
		{
			dump (info = codeMap.get (code));
			while ((info = info.alternate) != null) dump (info);
		}
	}
	public static void dump (BasicInformation info) throws Exception
	{
		System.out.print (info); System.out.print (": ");
		System.out.print (info.description);
		System.out.println ();
	}


	/**
	 * a table display for an index
	 */
	public static class Dictionary extends CrossReference
	{

		static final String COLUMNS[] = new String[]
		{
			"Symbol", "Primary Code", "Mark Code", "Symbol Description"
		};

		public Dictionary (Font font, UnicodeCharacterBlock block)
		{
			super (font, COLUMNS, block);
		}

		/* (non-Javadoc)
		 * @see net.myorb.data.unicode.CrossReference#loadCrossReference()
		 */
		public void loadCrossReference ()
		{
			try
			{
				forceLoad ();
				BasicInformation info; String markCode;
				for (String symbol : SimpleUtilities.orderedKeys (encoded))
				{
					markCode = "";
					info = encoded.get (symbol);
					if (info instanceof DecomposedInformation)
					{ markCode = Toolkit.hex4 (((DecomposedInformation)info).mark); }
					addToRowCount (rowFor (symbol, Toolkit.hex4 (info.code), markCode, info.description));
				}
				releaseUnusedRows ();
			}
			catch (Exception e) { e.printStackTrace (); }
		}

	}


	/**
	 * unit test map
	 * @param p not used
	 * @throws Exception for errors
	 */
	public static void main (String... p) throws Exception
	{
		dumpCodeList (); //dumpSymbolList ();
		new Nomenclature.Dictionary (new Font ("Algerian", Font.PLAIN, 8), null);
	}
	static boolean silent = false;


}

