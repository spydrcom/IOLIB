
package net.myorb.gui.graphics.markets.data;

import net.myorb.data.conventional.CSV;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * YAHOO Financial History
 *  file type manager based on CSV
 *  with date formatting of the type YYYY-MM-DD
 * @author Michael Druckman
 */
public class YFH extends CSV
{

	/**
	 * parser for YAHOO Financial CSV date format YYYY-MM-DD
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

	public String getTodaysDate ()
	{ return dateFormatter.format (new Date ()); }
	public SimpleDateFormat getDateFormatter () { return dateFormatter; }
	public SimpleDateFormat getTimeFormatter () { return timeFormatter; }
	protected SimpleDateFormat dateFormatter = new SimpleDateFormat ("YYYY-MM-dd");
	protected SimpleDateFormat timeFormatter = new SimpleDateFormat ("YYYY-MM-dd hh:mm");

	public YFH () { super (new DateManager ()); }

}
