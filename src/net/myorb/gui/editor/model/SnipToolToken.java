
package net.myorb.gui.editor.model;

/**
 * Token representation in the model
 * - refactor of original JavaKit example from Sun
 * - by Timothy Prinzing version 1.2 05/27/99
 * - refactor done in summer 2022
 * @author Michael Druckman
 */
public class SnipToolToken
{


	public SnipToolToken (String representation, int styleCode)
	{
		this.representation = representation;
		this.styleCode = styleCode;
	}


	/**
	 * A human readable form of the token,
	 * useful for things like lists,
	 * debugging, etc.
	 */
	public String toString () { return representation; }
	protected String representation;

	/**
	 * @return the code assigned for the style of this token
	 */
	public int getStyleCode () { return styleCode; }
	public void setStyleCode (int styleCode) { this.styleCode = styleCode; }
	protected int styleCode;


}

