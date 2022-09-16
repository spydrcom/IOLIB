
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
		return riseOverRun
		(
			f.body (x),				// beginning at
			f.body (x + delta),		// ending at
			delta					// length
		);
	}


	/**
	 * compute second order derivative
	 * @param f the function to target the approximation
	 * @param x the domain point of the approximation
	 * @param delta the value to use of the run
	 * @return the computed approximation
	 */
	public static double second
		(
			FunctionWrapper.F <Double> f,
			double x, double delta
		)
	{
		return second (f, f.body (x), x, delta);
	}
	public static double second
		(
			FunctionWrapper.F <Double> f,
			double evaluatedAtX, double x, double delta
		)
	{
		double
			earlyDerivative =		// evaluate at delta before X
					riseOverRun
					(
						f.body (x - delta), evaluatedAtX, delta
					),
			lateDerivative =		// evaluate at delta after X
					riseOverRun
					(
						evaluatedAtX, f.body (x + delta), delta
					);
		return riseOverRun			// evaluate tangent of derivative
		(
			earlyDerivative, lateDerivative, delta
		);
	}


	/**
	 * elementary calculus derivative formula
	 * @param evaluationBeforeRun compute f(x) where x is evaluation point
	 * @param evaluationAfterRun compute f(x+delta)
	 * @param lengthOfRun the value of delta
	 * @return the computed derivative
	 */
	public static double riseOverRun
		(
			double evaluationBeforeRun,
			double evaluationAfterRun,
			double lengthOfRun
		)
	{
		return
			(evaluationAfterRun - evaluationBeforeRun)	// the value of the rise
						/ lengthOfRun;					// the value of the run
		//
		// this has approximated the computation
		// of the  tangent  to the  function  curve
		// at the evaluation point specified by X
		//
		//   ---   the quality of this
		// approximation depends on how small
		// delta is relative to X
		//
	}


}

