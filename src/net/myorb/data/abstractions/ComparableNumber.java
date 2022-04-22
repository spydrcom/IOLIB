
package net.myorb.data.abstractions;

/**
 * extending Number to implement Comparable.
 *  comparison is being done with values extended to double float
 * @author Michael Druckman
 */
public class ComparableNumber extends Number implements Comparable<Number>
{

	Number boxed;
	public ComparableNumber (Number number) { boxed = number; }

	/* (non-Javadoc)
	 * @see java.lang.Number#intValue()
	 */
	public int intValue () { return boxed.intValue (); }
	public long longValue () { return boxed.longValue (); }
	public float floatValue () { return boxed.floatValue (); }
	public double doubleValue () { return boxed.doubleValue (); }

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo (Number other)
	{
		return SimpleUtilities.compareRealNumbers (boxed, other);
	}

	public static class List extends java.util.ArrayList<ComparableNumber>
	{ private static final long serialVersionUID = -3313180201639265013L; }

	private static final long serialVersionUID = -1257957423067416668L;
}

