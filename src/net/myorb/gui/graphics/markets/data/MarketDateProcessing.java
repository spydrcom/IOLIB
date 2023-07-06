
package net.myorb.gui.graphics.markets.data;

import net.myorb.data.abstractions.SimpleStreamIO;
import net.myorb.data.conventional.OHLCV;

import java.util.Date;

/**
 * management of date specific data using YAHOO history formats
 * @author Michael Druckman
 */
public class MarketDateProcessing
{


	// time-zone correction
	static Long SEC_PER_HR = 60*60L;
	static Long MS_PER_HR = SEC_PER_HR*1000;
	static Long TZONE_DIFFERENCE = 8L;


	// YAHOO CSV is adopted convention
	public static final YFH YAHOO = new YFH ();


	/**
	 * @return text representation of todays date YYYY-MM-dd
	 */
	public static String getTodaysDate () { return YAHOO.getTodaysDate (); }

	/**
	 * @param daysAgo number of days past
	 * @return the formatted date for specified day
	 */
	public static String getPriorDate (long daysAgo)
	{
		long time = System.currentTimeMillis ();
		return dateFrom (time - daysAgo * MS_PER_HR * 24);
	}


	/**
	 * convert long number value to date by 1970 milli standard
	 * @param N the number taken from the JSON object field
	 * @return the YYYY-mm-dd formatted date
	 */
	public static String dateFrom (Number N)
	{
		long localTime = timeZoneAdjust (N);
		return YAHOO.getDateFormatter ().format (new Date (localTime));
	}

	public static String timeFrom (Number N)
	{
		long localTime = timeZoneAdjust (N);
		String T = YAHOO.getTimeFormatter ().format (new Date (localTime));
		return T.replace (' ', ',');
	}

	static long timeZoneAdjust (Number N)
	{
		return N.longValue() + TZONE_DIFFERENCE*MS_PER_HR;
	}


	/**
	 * parse historical data from the YAHOO CSV format
	 * @param source text source for history to be read
	 * @param series a series of bars collecting price history
	 * @throws Exception for any error
	 */
	public static void parseHistory
		(SimpleStreamIO.TextSource source, OHLCV.Series series)
	throws Exception
	{
		YAHOO.parse (source, series);
	}


}

