
package net.myorb.gui.components;

import net.myorb.data.abstractions.SimpleXmlHash.Document;
import net.myorb.data.abstractions.SimpleXmlHash.Node;

import java.io.File;

/**
 * collect list of files into document
 * @author Michael Druckman
 */
public class SimpleXmlFileList
{


	public static final String FILE = "File", FILE_SIZE = "FileSize", FILE_NAME = "FileName", FILE_LIST = "FileList";


	/**
	 * @param directory the directory of files
	 * @param isTemplateDirectory include template file analysis
	 * @return a document containing the file list of the directory
	 * @throws Exception for any errors
	 */
	public static Document getFileListDocument
		(File directory, boolean isTemplateDirectory)
	throws Exception
	{
		Document document = Document.read (FILE_LIST); Node n;

		for (File f : directory.listFiles ())
		{
			long size = f.length ();
			String name = f.getName ();
			name = name.substring (0, name.length () - 4);
			if (isTemplateDirectory && name.indexOf (' ') >= 0) continue;
			(n = new Node (FILE)).set (FILE_NAME, name).set (FILE_SIZE, size).addTo (document);
			if (isTemplateDirectory && size < 100000) getFileRecordCount (name, n);
		}

		return document;
	}


	/**
	 * @param fileName name of document to read
	 * @param DescriptionNode the node that describes the document
	 * @return the number of nodes in the document, -1 for error
	 */
	public static int getFileRecordCount (String fileName, Node DescriptionNode)
	{
		try
		{
			int records = Document.read (fileName).size ();
			DescriptionNode.set ("RecordCount", records);
			return records;
		}
		catch (Exception e) { e.printStackTrace (); return -1; }
	}


	/**
	 * @param document the file list document
	 * @param atNodeIndex the index of the node to read
	 * @return Selected Document Name
	 */
	public static String getFileNameFor (Document document, int atNodeIndex)
	{
		return document.get (atNodeIndex).get (FILE_NAME);
	}


}

