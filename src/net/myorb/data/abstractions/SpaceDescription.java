
package net.myorb.data.abstractions;

/**
 * methods and properties providing needed qualities of a field
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public interface SpaceDescription<T>
{

	/**
	 * compute internal representation for scalar
	 * @param x simple integer value to convert to internal representation
	 * @return internal representation for value
	 */
	T newScalar (int x);

	/**
	 * determine value of opposite sign
	 * @param x value to be negated
	 * @return negated value
	 */
	T negate (T x);

	/**
	 * is the value zero
	 * @param x value to be checked
	 * @return TRUE for zero value
	 */
	boolean isZero (T x);
	
	/**
	 * is the value negative
	 * @param x value to be checked
	 * @return TRUE for negative value
	 */
	boolean isNegative (T x);

	/**
	 * compute sum of pair of terms
	 * @param x the value of the left side of computation
	 * @param y the value of the right side of computation
	 * @return result of computation
	 */
	T add (T x, T y);
	
	/**
	 * compute product x*y
	 * @param x the value of the left side of computation
	 * @param y the value of the right side of computation
	 * @return result of computation
	 */
	T multiply (T x, T y);
	
	/**
	 * compute division 1/x
	 * @param x the value of the divisor of the computation
	 * @return result of computation
	 */
	T invert (T x);

	/**
	 * compute x^e for integer exponent
	 * @param x value to be used as base in expression
	 * @param exponent the value of the exponent
	 * @return the computed value
	 */
	T pow (T x, int exponent);

	/**
	 * get the conjugate of the specified value
	 * @param x the value to be used to compute conjugate
	 * @return the conjugate of the parameter
	 */
	T conjugate (T x);
	
	/**
	 * compute comparison x LT y
	 * @param x the value of the left side of computation
	 * @param y the value of the right side of computation
	 * @return result of computation
	 */
	boolean lessThan (T x, T y);

	/**
	 * get the value zero in this representation (additive identity)
	 * @return the value zero
	 */
	T getZero ();

	/**
	 * get the value one in this representation (multiplicative identity)
	 * @return the value zero
	 */
	T getOne ();

}
