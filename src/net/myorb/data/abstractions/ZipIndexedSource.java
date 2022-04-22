
package net.myorb.data.abstractions;

import java.io.File;
import java.io.OutputStream;
import java.io.FileNotFoundException;

import java.util.Map;

/**
 * connect ZIP source with index of contents
 * @author Michael Druckman
 */
public class ZipIndexedSource
{


	/**
	 * @param file the ZIP file
	 * @throws Exception for errors
	 */
	public ZipIndexedSource (File file) throws Exception
	{
		this.contentIndex = ZipUtilities.index (file);
		this.contentSource = file;
	}
	protected Map <String, Integer> contentIndex;
    protected File contentSource;


    /**
     * identify entry of requested address in ZIP
     * @param requested the requested address as text string
     * @return the index into the ZIP file where resource has been found
     * @throws FileNotFoundException for resource not found
     */
    public int identify (String requested) throws FileNotFoundException
    {
		Integer position = contentIndex.get (requested);
		if (position == null) throw new FileNotFoundException (requested);
		return position.intValue ();
    }


    /**
     * copy named entry to sink
     * @param sinkStream the stream to copy content to
     * @param fromPath the path of the entry to be copied
     * @throws Exception for any errors
     */
    public void copyTo
    (OutputStream sinkStream, String fromPath)
    throws Exception
    {
    	new ZipSource (contentSource, identify (fromPath)).copyTo (sinkStream);
    }


}

