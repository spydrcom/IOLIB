
package net.myorb.gui.editor.model;

public class SnipToolToken
{


	public static class AttributeKey
	{
		public AttributeKey () {}
		public String toString() { return "token"; }
	}

	/**
	 * Key to be used in AttributeSet's holding a value of Token.
	 */
	public static final Object TokenAttribute = new AttributeKey();


	public SnipToolToken (String representation, int scanValue)
	{
		this.representation = representation;
		this.scanValue = scanValue;
	}
	protected String representation;
	protected int scanValue;


	/**
	 * A human readable form of the token, useful for things like lists,
	 * debugging, etc.
	 */
	public String toString ()
	{
		return representation;
	}


	/**
	 * Numeric value of this token. This is the value returned by the scanner
	 * and is the tie between the lexical scanner and the tokens.
	 * @return the scanValue
	 */
	public int getScanValue ()
	{
		return scanValue;
	}


	/**
	 * Specifies the category of the token
	 * as a string that can be used as a label.
	 * @return beginning to dot
	 */
	public String getCategory ()
	{
		String nm = getClass ().getName ();
		int nmStart = nm.lastIndexOf ('.') + 1;			// not found results in 0
		return nm.substring (nmStart, nm.length ());
	}


	/**
	 * Returns a hash-code for this set of attributes.
	 * @return a hash-code value for this set of attributes.
	 */
	public final int hashCode ()
	{
		return scanValue;
	}


	/**
	 * Compares this object to the specified object. The result is
	 * <code>true</code> if and only if the argument is not <code>null</code>
	 * and is a <code>Font</code> object with the same name, style, and point
	 * size as this font.
	 * 
	 * @param obj
	 *            the object to compare this font with.
	 * @return <code>true</code> if the objects are equal; <code>false</code>
	 *         otherwise.
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

