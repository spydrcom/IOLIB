
package net.myorb.utilities;

/**
 * prepare an object to accept textual configuration data
 * @author Michael Druckman
 */
public interface Configurable
{
	/**
	 * pass configuration data to the object
	 * @param text the text of the configuration data
	 */
	void configure (String text);
}
