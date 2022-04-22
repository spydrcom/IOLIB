
package net.myorb.data.abstractions;

/**
 * describe a range with an increment
 * @author Michael Druckman
 */
public class PrimitiveRangeDescription
{

	protected Double lo, hi, increment;

	public Number getLo () { return lo; }
	public Number getIncrement () { return increment; }
	public Number getHi () { return hi; }


	/**
	 * format using array notation
	 * @param symbol the symbol in the range
	 * @param precision decimal places to be displayed
	 * @return text of notation
	 */
	public String toStandardNotation (String symbol, int precision)
	{
		String incrementDisplay = increment==0? "": " <> " + fmt(increment, precision);
		return "[ " + fmt(lo, precision) + " <= " + symbol + " <= " + fmt(hi, precision) + incrementDisplay + " ]";
	}
	private String fmt (double x, int precision) { return ValueDisplayProperties.formatDecimalString (x, precision); }
	public String toStandardNotation (String symbol) { return toStandardNotation (symbol, 0); }


	/**
	 * @param description another instance of a range descriptor
	 */
	public PrimitiveRangeDescription (PrimitiveRangeDescription description)
	{
		this.lo = description.lo;
		this.increment = description.increment;
		this.hi = description.hi;
	}

	/**
	 * @param lo the text of the lo bound of the range
	 * @param hi the text of the hi bound of the range
	 * @param increment the text of the increment of the range
	 */
	public PrimitiveRangeDescription (String lo, String hi, String increment)
	{
		this.lo = Double.parseDouble (lo);
		this.increment = Double.parseDouble (increment);
		this.hi = Double.parseDouble (hi);
	}

	/**
	 * @param lo the text of the lo bound of the range
	 * @param hi the text of the hi bound of the range
	 * @param increment the text of the increment of the range
	 */
	public PrimitiveRangeDescription (Number lo, Number hi, Number increment)
	{
		this.lo = lo.doubleValue ();
		this.increment = increment.doubleValue ();
		this.hi = hi.doubleValue ();
	}

}

