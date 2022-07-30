
package net.myorb.gui.editor.model;

import java.util.ArrayList;

/**
 * Token representation in the model
 * - refactor of original JavaKit example from Sun
 * - by Timothy Prinzing version 1.2 05/27/99
 * - refactor done in summer 2022
 * @author Michael Druckman
 */
public class SnipToolToken
{


	/**
	 * first entry in SystemTokens is assumed to be the UNKNOWN token
	 */
	public static final int UNKNOWN_CODE = 0;


	/**
	 * mechanism for importing tokens from parent layer
	 */
	public static class SystemTokens extends ArrayList<SnipToolToken>
	{
		public static final SnipToolToken UNKNOWN_TOKEN =
			new SnipToolToken ("UNSPECIFIED", "UNKNOWN", UNKNOWN_CODE);
		private static final long serialVersionUID = 1469834646429970757L;
		public SystemTokens () { this.add (UNKNOWN_TOKEN); }
		// first is constant / others to follow
	}


	/**
	 * Key to be used in AttributeSet's holding a value of Token.
	 */
	public static final StyleAttribute TokenAttribute = new StyleAttribute ("Token");


	public SnipToolToken (String category, String representation, int scanValue)
	{
		this (representation, scanValue);
		this.category = category;
	}
	protected String category = null;


	public SnipToolToken (String representation, int scanValue)
	{
		this.representation = representation;
		this.scanValue = scanValue;
	}


	/**
	 * A human readable form of the token, useful for things like lists,
	 * debugging, etc.
	 */
	public String toString () { return representation; }
	protected String representation;


	/**
	 * Numeric value of this token. This is the value returned by the scanner
	 * and is the tie between the lexical scanner and the tokens.
	 * @return the scanValue
	 */
	public int getScanValue () { return scanValue; }
	protected int scanValue;


	/**
	 * Specifies the category of the token
	 * as a string that can be used as a label.
	 * @return stated or computed value
	 */
	public String getCategory ()
	{
		if (this.category == null)
		{ return getCategoryByClass (); }
		else return this.category;
	}


	/**
	 * Specifies the category of the token
	 * as a string that can be used as a label.
	 * @return class name beginning at last dot
	 */
	public String getCategoryByClass ()
	{
		String nm = getClass ().getName ();			// assumes token is extended into CAT class
		int nmStart = nm.lastIndexOf ('.') + 1;				// not found results in 0
		return nm.substring (nmStart, nm.length ());
	}


	/**
	 * Returns a hash-code for this set of attributes.
	 * @return a hash-code value for this set of attributes.
	 */
	public final int hashCode () { return scanValue; }


	/**
	 * Compares this object to the specified object.
	 * The result is <code>true</code> if and only if
	 * the argument is not <code>null</code> and is a
	 * <code>Token</code> object with the same scanValue as this Token.
	 * @param obj the object to compare this token with.
	 * @return <code>true</code> if the objects are equal;
	 * <code>false</code> otherwise.
	 */
	public final boolean equals (Object obj)
	{
		if (obj instanceof SnipToolToken)
		{
			SnipToolToken t = (SnipToolToken) obj;
			return (scanValue == t.scanValue);
		}
		return false;
	}


}

