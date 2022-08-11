
package net.myorb.data.abstractions.language;

import net.myorb.gui.editor.SnipToolScanner;

import java.util.List;

/**
 * driver for the line parser specific to the Snip tool abstractions
 * @author Michael Druckman
 */
public abstract class ContextSpecificAnalyzer implements SnipToolScanner
{


	public ContextSpecificAnalyzer ()
	{
		this.parser = new ContextSpecificParser ();
	}
	protected ContextSpecificParser parser;


	/* (non-Javadoc)
	 * @see net.myorb.gui.editor.SnipToolScanner#updateSource(java.lang.StringBuffer, int)
	 */
	public void updateSource (StringBuffer source, int position)
	{
		this.scans = parser.ScanLine (source);
		this.markLocation (position);
		this.buffer = source;
	}
	protected List<ContextSpecificParser.Scan> scans;


	/**
	 * keep sync of coordinates between swing model and view or document
	 * @param position the position in model coordinates for start of buffer
	 */
	public void markLocation (int position)
	{
		this.current = 0; this.start = position;
		this.setLastSourcePosition (0);
	}
	protected StringBuffer buffer;
	protected int start, current;


	/**
	 * identify updated position within source buffer
	 * @param offset the offset from the model start of the buffer
	 */
	public void setLastSourcePosition (int offset)
	{ this.lastSourcePosition = this.start + offset; }
	protected int lastSourcePosition;


	/* (non-Javadoc)
	 * @see net.myorb.gui.editor.SnipToolScanner#getLastSourcePosition()
	 */
	public int getLastSourcePosition () { return lastSourcePosition; }


	/* (non-Javadoc)
	 * @see net.myorb.gui.editor.SnipToolScanner#getDefaultStyleCode()
	 */
	public int getDefaultStyleCode () { return defaultStyle; }
	protected int defaultStyle;


	/* (non-Javadoc)
	 * @see net.myorb.gui.editor.SnipToolScanner#trackWS()
	 */
	public void trackWS () { parser.trackWS (); trackingWS = true; }
	boolean trackingWS = false;


}

