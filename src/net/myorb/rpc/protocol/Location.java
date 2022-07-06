
package net.myorb.rpc.protocol;

public interface Location
{
	/**
	 * @param serviceNamed the identifier given to the service for recognition
	 * @return the port assigned to the named service
	 */
	String find (String serviceNamed);
}
