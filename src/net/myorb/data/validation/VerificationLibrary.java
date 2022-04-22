
package net.myorb.data.validation;

import java.util.HashMap;

/**
 * describe verifiers for data types
 * @author Michael Druckman
 */
public class VerificationLibrary
{

	/**
	 * a map from type name to a Check class
	 */
	public static class Verifiers extends HashMap<String,Check>
	{ private static final long serialVersionUID = -8462774834073855026L; }

	/**
	 * the map of type names to Check objects
	 */
	public static Verifiers verifiers = new Verifiers ();

	/**
	 * @param verifierMap the map to use for finding check objects
	 */
	public static void setVerifiers (Verifiers verifierMap) { verifiers = verifierMap; }

	/**
	 * @param verifier Check object to add to library
	 */
	public static void addVerifier (Check verifier) { verifiers.put (verifier.toString (), verifier); }

	/**
	 * @param verifierPath path to Check class
	 * @throws Exception for any errors
	 */
	public static void addVerifier (String verifierPath) throws Exception
	{
		Check verifier = (Check) Class.forName (verifierPath).newInstance ();
		verifiers.put (verifier.toString (), verifier);
	}

	/**
	 * @param type the name of the data type
	 * @return the Check object or NULL for not found
	 */
	public static Check getCheckFor (String type) { return verifiers.get (type); }


}

