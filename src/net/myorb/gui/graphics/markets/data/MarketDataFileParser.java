
package net.myorb.gui.graphics.markets.data;

import net.myorb.data.conventional.*;

import java.util.List;
import java.io.*;

/**
 * file parser for market data
 * @author Michael Druckman
 */
public class MarketDataFileParser
{


	public interface PeriodDescription
	{
		PeriodDescription describing
			(String periodName);
		String getPeriodId ();
		String getMultiplier ();
		String getStartDate ();
		String getEndDate ();
		
	}

	public interface StreamParser
	{
		void parse
		(String market, PeriodDescription period, OHLCV.Series series)
		throws Exception;
	}


	/**
	 * file extensions recognized
	 */
	public enum Type { CSV, CSVR, TDF, TDFR, YFH }

	/**
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

