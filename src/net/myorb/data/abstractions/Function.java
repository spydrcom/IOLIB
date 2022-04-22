
package net.myorb.data.abstractions;

/**
 * a wrapper for passing procedures as parameters
 * @param <T> type of component values on which operations are to be executed
 * @author Michael Druckman
 */
public interface Function<T> extends ManagedSpace<T>
{
	/**
	 * call the function given single parameter
	 * @param x the value of the parameter to the function
	 * @return the resulting value computed by the function
	 */
	T eval (T x);
}