
package net.myorb.data.abstractions;

/**
 * description of an integer range
 */
public class Range extends Numerics
{


	/**
	 * a list of connected ranges
	 */
	public static class List
		extends java.util.ArrayList<Range>
	{ private static final long serialVersionUID = 1L; }

	/**
	 * a parallel list of ranges to their sizes
	 */
	public static class Sizes extends IntList
	{ private static final long serialVersionUID = 1L; }


	/**
	 * build range parsing text bounds
	 * @param bounds the lo and hi values as text array
	 */
	public Range (String[] bounds)
	{ this (bounds[0], bounds[1]); }

	/**
	 * @param lo the lo value (as text)
	 * @param hi the hi value (as text)
	 */
	public Range (String lo, String hi)
	{
		this (parseTruncatedDecimalNumber (lo), parseTruncatedDecimalNumber (hi));
	}

	/**
	 * build range from  lo and hi value
	 * @param lo the lo value
	 * @param hi the hi value
	 */
	public Range (Number lo, Number hi) { this.lo = lo; this.hi = hi; }


	/**
	 * describe a distribution
	 * based on average and mean deviation
	 * @param average the value of the mean at the distribution point
	 * @param deviation the associated deviation value
	 * @return the range for this distribution
	 */
	public static Range forDistribution (Number average, Number deviation)
	{
		Number
			lo = average.floatValue () - deviation.floatValue (),
			hi = average.floatValue () + deviation.floatValue ();
		if (lo.floatValue () < 0) lo = 0;
		return new Range (lo, hi);
	}
	public static Range forDistribution (Number average, Number deviation, Number deviationMultiplier)
	{ return forDistribution (average, deviation.floatValue () * deviationMultiplier.floatValue ()); }


	/**
	 * check value between bounds
	 * @param n the value to be checked
	 * @return TRUE = n GE lo AND n LE hi
	 */
	public boolean isWithinBounds (Number n)
	{
		double v = n.doubleValue ();
		if (USE_FLOATING) return v >= lo.doubleValue () && v <= hi.doubleValue ();
		return v >= lo.intValue () && v <= hi.intValue ();
	}
	boolean USE_FLOATING = false;


	/*
	 * getters for lo and hi values as int or as number
	 */

	public Number getLo () { return lo; }
	public int getRoundedLo () { return lo.intValue (); }
	public int getRoundedHi () { return hi.intValue (); }
	public Number getHi () { return hi; }


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString ()
	{ return formatRestricted (lo) + " - " + formatRestricted (hi); }
	public String toRounded () { return lo.intValue () + " - " + hi.intValue (); }
	private Number lo, hi;


}

