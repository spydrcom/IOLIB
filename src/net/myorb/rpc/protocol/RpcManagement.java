
package net.myorb.rpc.protocol;

import net.myorb.rpc.primitive.ServerConventions;

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

	/**
	 * @param serviceNamed the identifier for the service
	 * @param requestingPort the port commonly assigned for this service
	 * @return the port assigned by the service management system
	 */
	public static String post (String serviceNamed, String requestingPort)
	{
		return verifyRegistrar ().post (serviceNamed, requestingPort);
	}

	/**
	 * specify the RPC Registrar to use for the application session
	 * @param registrarPath the path to configure as the Registrar object
	 */
	public static void identifyRegistrar (String registrarPath)
	{ identifiedRegistrarPath = registrarPath; }
	static String identifiedRegistrarPath;

	/**
	 * check status of Registrar object
	 * - if null Registrar will construct on reference
	 * @return the current Registrar object
	 */
	public static Registration verifyRegistrar ()
	{
		if (registrar == null)
		{ forceRegistrarConstruction (); }
		return registrar;
	}

	/**
	 * construct on reference for Registrar object
	 */
	public static void forceRegistrarConstruction ()
	{
		Class<?> registrarClass; Object registrarObject;

		try { registrarClass = Class.forName (identifiedRegistrarPath); }
		catch (Exception e) { throw new ErrorHandling.Terminator (BAD_CLASS, e); }

		try { registrarObject = registrarClass.newInstance (); }
		catch (Exception e) { throw new ErrorHandling.Terminator (BAD_INSTANCE, e); }

		try { registrar = (Registration) registrarObject; }
		catch (Exception e) { throw new ErrorHandling.Terminator (BAD_OBJECT, e); }
	}
	static final String
		BAD_CLASS = "Registrar path is invalid",
		BAD_INSTANCE = "Registrar could not be constructed",
		BAD_OBJECT = "Registrar path refers to an invalid object class";
	static Registration registrar = null;

}

