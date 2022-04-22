
package net.myorb.data.abstractions;

/**
 * exchange representation of values between full structures and double values
 * @param <T>  the full structured representation of component values
 * @author Michael Druckman
 */
public interface SpaceConversion<T>
{

	/**
	 * @param value the double representation of the value
	 * @return the representation of the value as T
	 */
	T convertFromDouble (Double value);

	/**
	 * @param value the representation as T
	 * @return the double representation of the value
	 */
	Double convertToDouble (T value);

}
