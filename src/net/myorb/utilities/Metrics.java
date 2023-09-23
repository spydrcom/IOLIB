
package net.myorb.utilities;

/**
 * Conventions for value descriptions
 * @author Michael Druckman
 */
public class Metrics
{

	/**
	 * format a scaled size evaluation
	 * @param value the size value to be displayed
	 * @return the formatted evaluation
	 */
	public static String scaledEvaluation (long value)
	{
		int mul = 0;
		long sum = value;
		while (sum > 10240)
		{ sum /= 1024; mul++; }
		return sum + UNITS [mul];
	}
	static String [] UNITS = {" bytes", " KB", " MB", " GB", " TB", " EB", " PB"};

}
