
package net.myorb.rpc.protocol;

import net.myorb.data.abstractions.ErrorHandling;

/**
 * provide RPC service registration management
 * @param <T> the type of the task manager
 * @author Michael Druckman
 */
public class RegistrationManager <T>
{

	public RegistrationManager (String registrarPath)
	{
		identifyRegistrar (registrarPath);
	}

	/**
	 * specify the RPC Registrar to use for the application session
	 * @param registrarPath the path to configure as the Registrar object
	 */
	public void identifyRegistrar (String registrarPath)
	{ identifiedRegistrarPath = registrarPath; }
	protected String identifiedRegistrarPath = null;

	/**
	 * check status of Registrar object
	 * - if null Registrar will construct on reference
	 * @return the current Registrar object
	 */
	public T verifyRegistrar ()
	{
		if (registrar == null)
		{
			if (identifiedRegistrarPath == null)
			{ throw new ErrorHandling.Terminator (BAD_PATH); }
			forceRegistrarConstruction ();
		}
		return registrar;
	}

	/**
	 * construct on reference for Registrar object
	 */
	@SuppressWarnings("unchecked")
	public void forceRegistrarConstruction ()
	{
		Class<?> registrarClass; Object registrarObject;

		try { registrarClass = Class.forName (identifiedRegistrarPath); }
		catch (Exception e) { throw new ErrorHandling.Terminator (BAD_CLASS, e); }

		try { registrarObject = registrarClass.newInstance (); }
		catch (Exception e) { throw new ErrorHandling.Terminator (BAD_INSTANCE, e); }

		try { registrar = (T) registrarObject; }
		catch (Exception e) { throw new ErrorHandling.Terminator (BAD_OBJECT, e); }
	}
	static final String
		BAD_CLASS = "Registrar path is invalid",
		BAD_PATH = "Registrar was never specified",
		BAD_INSTANCE = "Registrar could not be constructed",
		BAD_OBJECT = "Registrar path refers to an invalid object class";
	protected T registrar = null;

}
