
package net.myorb.gui.graphics.markets.data;

import net.myorb.data.conventional.CSV;

/**
 * YAHOO Financial History
 *  file type manager based on CSV
 *  with date formatting of the type YYYY-MM-DD
 * @author Michael Druckman
 */
public class YFH extends CSV
{

	/**
	 * parser for Yahoo Financial CSV date format YYYY-MM-DD
	 */
	public static class DateManager extends DateFormat
	{

		/* (non-Javadoc)
		 * @see net.myorb.data.abstractions.SimpleDateManager#toMonthOrdinal(java.lang.String)
		 */
		public int toMonthOrdinal (String m) { return Integer.parseInt (m) - 1; }

		/* (non-Javadoc)
		 * @see net.myorb.data.abstractions.SimpleDateManager#delimiter()
		 */
		public String delimiter () { return "-"; }

		/**
		 * order of components is YMD
		 */
		protected DateManager () { super (Order.YMD); }

	}

	public YFH () { super (new DateManager ()); }

}
