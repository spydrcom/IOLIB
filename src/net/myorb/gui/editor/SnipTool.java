
package net.myorb.gui.editor;

/**
 * an editor specifically for tool content
 * @author Michael Druckman
 */
public class SnipTool extends SnipToolDisplays
{


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

