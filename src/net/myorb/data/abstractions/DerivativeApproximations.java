
package net.myorb.data.abstractions;

import net.myorb.data.abstractions.FunctionWrapper;

/**
 * compute Derivative Approximations for functions of real domain
 * - computation done as rise / run given a delta to use as the run value
 * @author Michael Druckman
 */
public class DerivativeApproximations
{


	/**
	 * compute a derivative
	 * - real domain approximation
	 * @param f the function to target
	 * @param order the order of the derivative 1-2
	 * @param x the domain point of the approximation
	 * @param delta the value to use for the run
	 * @return the computed approximation value
	 * @throws RuntimeException order error
	 */
	public static double compute
	(FunctionWrapper.F <Double> f, int order, double x, double delta)
	throws RuntimeException
	{
		switch (order)
		{
			case 0: return f.body (x);
			case 1: return first (f, x, delta);
			case 2: return second (f, x, delta);
			default:
		}

		throw new RuntimeException
		(
			order < 0
				? "Negative derivative order not supported"
				: "Too many derivatives required"
		);
	}


	/**
	 * compute first order derivative
	 * @param f the function to target the approximation
	 * @param x the domain point of the approximation
	 * @param delta the value to use of the run
	 * @return the computed approximation
	 */
	public static double first (FunctionWrapper.F <Double> f, double x, double delta)
	{
		// evaluation points for computation of rise
		double e1 = f.body (x), e2 = f.body (x + delta);
		// compute rise and divide by run
		double d = (e2 - e1) / delta;
		return d;
	}


	/**
	 * compute second order derivative
	 * @param f the function to target the approximation
	 * @param x the domain point of the approximation
	 * @param delta the value to use of the run
	 * @return the computed approximation
	 */
	public static double second (FunctionWrapper.F <Double> f, double x, double delta)
	{
		// evaluation points for computation of rise
		double e1 = f.body (x - delta), e2 = f.body (x);
		double e3 = e2, e4 = f.body (x + delta);
		// derivative rise/run calculation
		double d1 = (e2 - e1) / delta;
		double d2 = (e4 - e3) / delta;
		// rise or fall of derivatives
		return (d2 - d1) / delta;
	}


}

