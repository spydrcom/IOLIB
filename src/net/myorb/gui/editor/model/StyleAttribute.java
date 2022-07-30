
package net.myorb.gui.editor.model;

/**
 * Attribute objects passed as Style attributes
 * @author Michael Druckman
 */
public class StyleAttribute
{

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString () { return called; }
	public StyleAttribute (String called) { this.called = called; }
	protected String called;

}
