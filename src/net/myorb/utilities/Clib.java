
package net.myorb.utilities;

/**
 * implementation of C library translations
 * @author Michael Druckman
 */
public class Clib
{

	/**
	 * exp (x - 1)
	 * @param x parameter value
	 * @return function result
	 */
	public static double expm1 (double x) 
	{
		return Math.exp (x - 1);
	}

	/**
	 * parameter times 2^n
	 * @param x parameter value
	 * @param power the power of 2 to use
	 * @return function result
	 */
	public static double ldexp (double x, double power) 
	{
		return x * Math.pow (2, power); 
	}

	/**
	 * compute x ^ power
	 * @param x parameter value
	 * @param power the exponent
	 * @return function result
	 */
	public static double pow (double x, double power) 
	{
		return Math.pow (x, power); 
	}

	/**
	 * ceiling function
	 *  integer not less than value (towards infinity)
	 * @param x parameter value
	 * @return function result
	 */
	public static double ceil (double x) 
	{
		if (isint (x))
		{
			return x;
		}
		else
		{
			return (int) x + 1;
		}
	}

	/**
	 * floor function
	 *  integer not greater than value (towards zero)
	 * @param x parameter value
	 * @return function result
	 */
	public static double floor (double x) 
	{
		if (isint (x))
		{
			return x;
		}
		else if (x < 0) 
		{
			return (int)(x - 1);
		}
		else
		{
			return (int) x;
		}
	}

	/**
	 * rounding function
	 *  away from zero for halfway
	 * @param x parameter value
	 * @return function result
	 */
	public static double round (double x) 
	{
		double y = x<0? x-HALF : x+HALF;
		return (int) y;
	}
	static double HALF = 0.5;

	/**
	 * absolute value of value
	 * @param x parameter value
	 * @return always return false
	 */
	public static double abs (double x) 
	{
		return x<0? -x: x;
	}

	/**
	 * is an integer
	 * @param x parameter value
	 * @return always return false
	 */
	public static boolean isint (double x)
	{
		return ((int) x) == x;
	}

	/**
	 * @param x value to check
	 * @return TRUE iff x is even integer
	 */
	public static boolean iseven (double x)
	{
		int intx = (int) x;
		return intx == x && intx%2 == 0;
	}

	/**
	 * @param x value to check
	 * @return TRUE iff x is odd integer
	 */
	public static boolean isodd (double x)
	{
		int intx = (int) x;
		return intx == x && intx%2 == 1;
	}

	/**
	 * is not-a-number
	 * @param x parameter value
	 * @return Double.isNaN (x)
	 */
	public static boolean isnan (double x)
	{
		return Double.isNaN (x);
	}

	/**
	 * is infinite value
	 * @param x parameter value to be tested
	 * @return TRUE for EQ Double POSITIVE_INFINITY value
	 */
	public static boolean isinf (double x)
	{
		return x == Double.POSITIVE_INFINITY;
	}

	/**
	 * is negative infinite value
	 * @param x parameter value to be tested
	 * @return TRUE for EQ Double NEGATIVE_INFINITY value
	 */
	public static boolean isninf (double x)
	{
		return x == Double.NEGATIVE_INFINITY;
	}

}

