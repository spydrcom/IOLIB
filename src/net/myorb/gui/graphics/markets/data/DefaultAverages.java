
package net.myorb.gui.graphics.markets.data;

import net.myorb.gui.graphics.markets.data.MarketQuoteParameters.Periods;

/**
 * averaging periods for time frames chosen
 * @author Michael Druckman
 */
public class DefaultAverages extends MarketQuoteParameters.Averaging
{

	/* (non-Javadoc)
	 * @see net.myorb.gui.graphics.markets.data.MarketQuoteParameters.Averaging#getHistoryType()
	 */
	public String getHistoryType () { return null; }

	/* (non-Javadoc)
	 * @see net.myorb.gui.graphics.markets.data.MarketQuoteParameters.Averaging#getBars(net.myorb.gui.graphics.markets.data.MarketQuoteParameters.Periods)
	 */
	public int getBars (Periods timeFrame)
	{
		switch (timeFrame)
		{
			case MONTH: return 12;
			case HOUR: return 48;
			case MIN: return 100;
			default: break;
		}
		return 30;
	}

	/* (non-Javadoc)
	 * @see net.myorb.gui.graphics.markets.data.MarketQuoteParameters.Averaging#getPeriod(net.myorb.gui.graphics.markets.data.MarketQuoteParameters.Periods)
	 */
	public int getPeriod (Periods timeFrame)
	{
		switch (timeFrame)
		{
			case MONTH: return 12;
			case HOUR: return 10;
			case MIN: return 9;
			default: break;
		}
		return 13;
	}

	/* (non-Javadoc)
	 * @see net.myorb.gui.graphics.markets.data.MarketQuoteParameters.Averaging#getLiveDate(net.myorb.gui.graphics.markets.data.MarketQuoteParameters.Periods)
	 */
	public String getLiveDate (Periods timeFrame) { return null; }

	public DefaultAverages () { super ( new java.util.HashMap <String,String> () ); }

	private static final long serialVersionUID = -4593173041623800375L;

}

