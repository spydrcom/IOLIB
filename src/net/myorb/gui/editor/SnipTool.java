
package net.myorb.gui.editor;

import java.io.File;

/**
 * an editor specifically for tool content
 * @author Michael Druckman
 */
public class SnipTool extends SnipToolDisplays
{


	public static void addSnip (File source, SnipToolPropertyAccess properties)
	{
		SnipToolProcessing.process (source, properties);
	}


	/**
	 * add a tab for a new snip
	 * @param properties access to display components
	 */
	public static void addSnip (SnipToolPropertyAccess properties)
	{
		if (frame == null)
		{ new SnipTool (properties); }
		frame.forceToScreen ();
		add (properties);
	}


	/**
	 * construct singleton version of object
	 * @param properties access to display components
	 */
	public SnipTool (SnipToolPropertyAccess properties)
	{
		buildPanel (properties);
		this.properties = properties;
		prepareSnipToolActions (properties);
		actions.connectTool (this);
		show ();
	}
	public SnipToolPropertyAccess
	getSnipToolPropertyAccess () { return properties; }
	protected SnipToolPropertyAccess properties;


	/**
	 * build display frame and show
	 */
	public void show ()
	{
		buildFrame ().showOrHide (wXh (W, H));
	}


}

