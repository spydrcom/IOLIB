
package net.myorb.data.abstractions;

import java.text.SimpleDateFormat;
import java.text.DateFormat;

import java.util.Calendar;
import java.util.Date;

/**
 * parse and format date items
 * @author Michael Druckman
 */
public class SimpleDateManager
{

	/**
	 * Month-Day-Year or Year-Month-Day or Day-Month-Year
	 */
	public enum Order { MDY, YMD, DMY }

	/**
	 * @param order the order of the components
	 */
	public SimpleDateManager (Order order) { this.order = order; }
	public SimpleDateManager () { this (Order.MDY); }
	protected Order order;

	/**
	 * TLA for names of months
	 */
	public enum Month { JAN, FEB, MAR, APR, MAY, JUN, JUL, AUG, SEP, OCT, NOV, DEC }
	public static Month parseMonth (String text) { return Month.valueOf (text.substring (0, 3).toUpperCase ()); }

	/**
	 * @return character to discard
	 */
	public String redundant () { return ","; }

	/**
	 * @return character that separates fields
	 */
	public String delimiter () { return " "; }

	/**
	 * @param text the text to be parsed
	 * @return the util.Date representation
	 */
	public Date parse (String text)
	{
		String source =
			text.replaceAll (redundant (), "");
		String parts[] = source.split (delimiter ());

		switch (order)
		{
			case MDY: return parse (parts[0], parts[1], parts[2]);
			case YMD: return parse (parts[1], parts[2], parts[0]);
			case DMY: return parse (parts[1], parts[0], parts[2]);
		}
		return null;
	}

	/**
	 * @param m text describing month
	 * @return the ordinal of the month
	 */
	public int toMonthOrdinal (String m)
	{
		return parseMonth (m).ordinal ();
	}

	/**
	 * @param y text describing year (may need 2 digit translation)
	 * @return the proper year value
	 */
	public int toYearOrdinal (String y)
	{
		return Integer.parseInt (y);
	}

	/**
	 * @param d text describing day
	 * @return the proper day value
	 */
	public int toDayOrdinal (String d)
	{
		return Integer.parseInt (d);
	}

	/**
	 * @param m text describing month
	 * @param d text describing day (simple integer day)
	 * @param y text describing year (2 or 4 digits)
	 * @return the util.Date representation
	 */
	public Date parse (String m, String d, String y)
	{
		calendar.set
		(
			toYearOrdinal (y),
			toMonthOrdinal (m),
			toDayOrdinal (d)
		);
		return calendar.getTime ();
	}
	protected Calendar calendar = Calendar.getInstance ();

	/**
	 * @param date the util.Date value
	 * @return the formatted text
	 */
	public String format (Date date) { return dateformat.format (date); }
	protected DateFormat dateformat = SimpleDateFormat.getDateInstance ();

}
