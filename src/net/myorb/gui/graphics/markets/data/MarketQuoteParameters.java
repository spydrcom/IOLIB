
package net.myorb.gui.graphics.markets.data;

import net.myorb.data.conventional.OHLCV;

/**
 * generic descriptions of parameters to quote systems
 * @author Michael Druckman
 */
public class MarketQuoteParameters
{

	/**
	 * describe the time frame for the quote
	 */
	public interface PeriodDescription
	{
		/**
		 * @param periodName minute hour day week month quarter year
		 * @return a chain return of the description object
		 */
		PeriodDescription describing
			(String periodName);

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

}
