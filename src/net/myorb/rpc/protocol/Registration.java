
package net.myorb.rpc.protocol;

/**
 * register a service for use in the system
 * @author Michael Druckman
 */
public interface Registration
{
	/**
	 * post a service to the system and request port assignment
	 * @param serviceNamed the identifier given to the service for recognition
	 * @param requestingPort the port commonly assigned for this service
	 * @return the port assigned by the service management system
	 */
	String post (String serviceNamed, String requestingPort);
}
