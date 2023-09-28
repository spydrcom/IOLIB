
package net.myorb.utilities;

import java.io.PrintStream;

/**
 * Meta-data collection of groups of objects
 * @author Michael Druckman
 */
public class Accounting
{

	/**
	 * evaluation of size of single object
	 */
	public interface Estimation
	{
		/**
		 * @return size of the object
		 */
		long getSize ();
	}

	/**
	 * formatting size using metric standards
	 * @param value the value to be formatted and reported
	 * @return the formatted value
	 */
	public static String scaled (long value)
	{
		return " (" + Metrics.scaledEvaluation (value) + ") ";
	}

	/**
	 * accumulate count of objects and aggregate size
	 */
	public static class RollUp
	{
		/**
		 * @param item a collected object with size estimation available
		 */
		public void accountFor (Estimation item)
		{
			total += item.getSize ();
			items++;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		public String toString ()
		{
			StringBuffer out = new StringBuffer ();
			out.append (items); out.append (" found ");
			out.append (scaled (total));
			return out.toString ();
		}
		protected long items = 0, total = 0;
	}

	/**
	 * associate roll-up with collected object sub-types
	 */
	public static class Summary
		extends Mapping.Association < String, RollUp >
	{

		/**
		 * produce report of meta-data collection
		 * @param out the print stream to be written
		 */
		public void report (PrintStream out)
		{
			RollUp totals;
			for (String item : keySet ())
			{
				totals = get (item);
				out.print (item); out.print ("\t");
				out.println (totals);
			}
		}

		/* (non-Javadoc)
		 * @see net.myorb.utilities.Mapping.Association#allocate()
		 */
		public RollUp allocate () { return new RollUp (); }

		private static final long serialVersionUID = -2108729935815301144L;
	}

}
