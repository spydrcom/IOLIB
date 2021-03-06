
package net.myorb.gui.graphics.markets.data;

import net.myorb.data.conventional.OHLCV;

import net.myorb.data.conventional.TDF;
import net.myorb.data.conventional.CSV;

import java.util.List;
import java.io.File;

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

}
