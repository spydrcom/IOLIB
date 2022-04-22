
package net.myorb.utilities;

/**
 * process series of numbers to establish range
 * @author Michael Druckman
 */
public class NumericRange
{

	Double lo = Double.MAX_VALUE;
	Double hi = Double.MIN_VALUE;
	
	/**
	 * @param number test value for new hi/lo
	 */
	public void evaluate (Number number)
	{
		double v = number.doubleValue ();
		if (v < lo) lo = v;
		if (v > hi) hi = v;
	}

	/**
	 * @param numbers list of numbers to be evaluated
	 */
	public void evaluate (Number[] numbers)
	{
		for (Number n : numbers) evaluate (n);
	}

	/**
	 * @param numbers list of double flots to evaluate
	 */
	public void evaluate (double[] numbers)
	{
		for (Number n : numbers) evaluate (n);
	}

	/**
	 * @return the lo value seen in evaluations
	 */
	public Number getLo () { return lo; }

	/**
	 * @return the hi value seen in evaluations
	 */
	public Number getHi () { return hi; }

}
