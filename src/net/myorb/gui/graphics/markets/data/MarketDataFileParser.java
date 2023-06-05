
package net.myorb.gui.graphics.markets.data;

import net.myorb.gui.graphics.markets.data.MarketQuoteParameters.Periods;
import net.myorb.data.conventional.*;

import java.util.List;
import java.io.*;

/**
 * file parser for market data
 * @author Michael Druckman
 */
public class MarketDataFileParser
{


	/**
	 * file extensions recognized
	 */
	public enum Type { CSV, CSVR, TDF, TDFR, YFH }


	/**
	 * parse a historical market source
	 * @param source path to source file
	 * @param market name of the market quoted
	 * @return the list of bars parsed from file
	 * @throws Exception for IO errors
	 */
	public static List<OHLCV.Bar> read (String source, String market) throws Exception
	{
		File f;
		int dot = source.lastIndexOf ('.');
		Type type = Type.valueOf (source.substring (dot+1).toUpperCase ());
		if (!(f = new File (source)).exists ()) return null;
		OHLCV.Series series = new OHLCV.Series ();

		switch (type)
		{
			case CSV: case CSVR:	new CSV ().parse (f, series); break;
			case TDF: case TDFR:	new TDF ().parse (f, series); break;
			case YFH:				new YFH ().parse (f, series);
		}

		series.setMarketName (market);

		switch (type)
		{
			case YFH:
			case CSV:	case TDF:	return series.getBars ();
			case CSVR:	case TDFR:	return series.getReversedBars ();
			default:				return null;
		}
	}


	/**
	 * identify file type assuming YAHOO historical format
	 * @param periodId the time frame for the data of interest
	 * @return the extension for the time frame
	 */
	public static String typeFor (Periods periodId)
	{
		switch (periodId)
		{
			case DAY: return "Daily.yfh";
			case HOUR: return "Hourly.yfh";
			case QUARTER: return "Quarterly.yfh";
			case WEEK: return "Weekly.yfh";
			case MONTH: return "Monthly.yfh";
			case YEAR: return "Yearly.yfh";
			default: return "???.yfh";
		}
	}


	/**
	 * construct file path to historical data
	 * @param market name of the market quoted
	 * @param timeFrame the time period of interest
	 * @return the path to the data file
	 * @throws Exception for errors
	 */
	public static String pathFor (String market, MarketQuoteParameters.Periods timeFrame) throws Exception
	{
		return "data/" + market + "-" + typeFor (timeFrame);
	}


	/**
	 * read file of historical data
	 * @param market name of the market quoted
	 * @param timeFrame the time period of interest
	 * @return the data parsed into a list of bars
	 * @throws Exception for any errors
	 */
	public static List<OHLCV.Bar> read (String market, MarketQuoteParameters.Periods timeFrame) throws Exception
	{
		return read ( pathFor (market, timeFrame), market );
	}


}

