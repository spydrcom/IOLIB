
package net.myorb.utilities;

import net.myorb.data.abstractions.ServerConventions;
import net.myorb.data.abstractions.ErrorHandling;

import java.util.HashMap;
import java.util.Map;

/**
 * provide RPC service management
 * @author Michael Druckman
 */
public class RpcManagement
{

	/**
	 * table of installed service processors
	 */
	public static final Map<String, ServerConventions.Processor>
	serviceManagers = new HashMap<String, ServerConventions.Processor>();

	/**
	 * search service table for processor
	 * @param name the name given to the service
	 * @return the Processor object configured as the service handler
	 * @throws ErrorHandling.Notification for lookup failure
	 */
	public static ServerConventions.Processor lookupProcessor (String name)
	{
		ServerConventions.Processor p = serviceManagers.get (name);
		if (p == null) { throw new ErrorHandling.Notification ("No such service called " + name); }
		return p;
	}

	/**
	 * add to the services table
	 * @param name the name to give to the service
	 * @param path the path to a Processor object for the service
	 */
	public static void addService (String name, String path)
	{
		try { serviceManagers.put (name, (ServerConventions.Processor) Class.forName (path).newInstance ()); }
		catch (Exception e) { throw new ErrorHandling.Notification ("Illegal service path"); }
	}

	/**
	 * start a service on a given port
	 * @param name the name given to the service
	 * @param port a port number to assign to the service
	 * @return the processor object being used
	 */
	public static Object startService (String name, String port)
	{
		ServerConventions.Processor processor = lookupProcessor (name);
		ServerConventions.provideService (Integer.parseInt (port), processor, "\f");
		return processor;
	}

}

