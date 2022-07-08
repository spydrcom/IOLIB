
package net.myorb.rpc.protocol;

/**
 * request port assignment given a named service
 * @author Michael Druckman
 */
public interface Location
{
	/**
	 * @param serviceNamed the identifier given to the service for recognition
	 * @return the port assigned to the named service
	 */
	String find (String serviceNamed);
}
