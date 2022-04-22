
package net.myorb.data.abstractions;

/**
 * description for data type used by implementer
 * @param <T> type of component values on which operations are to be executed
 * @author Michael Druckman
 */
public interface ManagedSpace<T>
{
	/**
	 * get manager for the type used
	 * @return a space manager for the type
	 */
	SpaceDescription<T> getSpaceDescription ();
}
