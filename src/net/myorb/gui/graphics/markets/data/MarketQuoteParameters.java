
package net.myorb.gui.graphics.markets.data;

import net.myorb.data.conventional.OHLCV;

import java.util.List;

/**
 * generic descriptions of parameters to quote systems
 * @author Michael Druckman
 */
public class MarketQuoteParameters
{


	/**
	 * identifiers for time frames
	 */
	public enum Periods { MIN, HOUR, DAY, WEEK, MONTH, QUARTER, YEAR }


	/**
	 * notation assigned to time frame
	 * @param periodId the generic ID for the time frame
	 * @return the notation string
	 */
	public static String notationFor (Periods periodId)
	{
		switch (periodId)
		{
			case DAY: return "D";
			case HOUR: return "H";
			case MIN: return "T";
			case MONTH: return "M";
			case QUARTER: return "Q";
			case WEEK: return "W";
			case YEAR: return "Y";
			default: return "";
		}
	}


	/**
	 * describe the time frame for the quote
	 */
	public interface PeriodDescription
	{
		/**
		 * @param periodId minute hour day week month quarter year
		 * @return a chain return of the description object
		 */
		PeriodDescription describing
			(Periods periodId);

		/**
		 * @return minute hour day week month quarter year
		 */
		String getPeriodId ();

		/**
		 * @return the number of periods in one time frame unit
		 */
		String getMultiplier ();

		/**
		 * @return the starting point for the time frame
		 */
		String getStartDate ();

		/**
		 * @return the ending point for the time frame
		 */
		String getEndDate ();
		
	}


	/**
	 * quote a security for a given market over time frame
	 */
	public interface StreamParser
	{
		/**
		 * parse a quote into a series of bars
		 * @param market the identifier for the security
		 * @param period description of the time frame for the quote
		 * @param series the generic quote bars series description resulting
		 * @throws Exception for any form of error found in the quote process
		 */
		void parse
		(String market, PeriodDescription period, OHLCV.Series series)
		throws Exception;
	}


	/**
	 * apply parser to quote stream
	 * @param market name of the market quoted
	 * @param period a description of the time period
	 * @param parser a parser for the stream source
	 * @return the list of bars parsed from file
	 * @throws Exception for IO errors
	 */
	public static List<OHLCV.Bar> read
		(
			String market,
			PeriodDescription period,
			StreamParser parser
		)
	throws Exception
	{
		OHLCV.Series series = new OHLCV.Series ();
		parser.parse (market, period, series);
		return series.getBars ();
	}


}
