
package net.myorb.gui.graphics.markets.data;

import net.myorb.gui.graphics.markets.data.MarketQuoteParameters.*;
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
	 * lookup name from allowed choices
	 * @param name the name of the file extension
	 * @return item of the Type list
	 */
	public static Type identifyType (String name)
	{
		return Type.valueOf (name);
	}


	/**
	 * identify file type from path
	 * @param path the full path to the file
	 * @return the identified type
	 */
	public static Type getTypeFrom (String path)
	{
		try
		{
			int dot = path.lastIndexOf ('.');
			String fileType = path.substring (dot + 1);
			return identifyType ( MarketQuoteParameters.stripUC (fileType) );
		}
		catch (Exception e)
		{
			throw new RuntimeException ("Invalid file type for Historical archive");
		}
	}


	/**
	 * parse a file of quote histories
	 * @param market name of the market quoted
	 * @param f the file identified as market history for period
	 * @param t the file type of the historical source
	 * @return the parsed series of quote bars
	 * @throws Exception for any error
	 */
	public static OHLCV.Series parse (String market, File f, Type t) throws Exception
	{
		if ( ! f.exists () )
		{ throw new RuntimeException ("Historical archive not found"); }
		OHLCV.Series series = new OHLCV.Series ();

		switch (t)
		{
			case CSV: case CSVR:	new CSV ().parse (f, series); break;
			case TDF: case TDFR:	new TDF ().parse (f, series); break;
			case YFH:				new YFH ().parse (f, series);
		}

		series.setMarketName (market);
		return series;
	}


	/**
	 * order parsed data according to type
	 * @param series the parsed series of data points
	 * @param type the type of the source file read
	 * @return the bars reordered as appropriate
	 */
	public static List <OHLCV.Bar> selected (OHLCV.Series series, Type type)
	{
		switch (type)
		{
			case YFH:
			case CSV:	case TDF:	return series.getBars ();
			case CSVR:	case TDFR:	return series.getReversedBars ();
			default:				return null;
		}
	}


	/**
	 * parse a historical market source
	 * @param source path to source file
	 * @param market name of the market quoted
	 * @return the list of bars parsed from file
	 * @throws Exception for IO errors
	 */
	public static List <OHLCV.Bar> read (String source, String market) throws Exception
	{
		Type type = getTypeFrom (source);
		OHLCV.Series series = parse (market, new File (source), type);
		return selected (series, type);
	}


	/**
	 * identify file type from configuration
	 * @param timeFrame the time frame for the data of interest
	 * @param fileType the file extension identifier
	 * @return the extension for the time frame
	 */
	public static String typeFor (Periods timeFrame, String fileType)
	{
		return MarketQuoteParameters.frequencyFor (timeFrame) + "." + MarketQuoteParameters.strip (fileType);
	}


	/**
	 * construct file path to historical data
	 * @param market name of the market quoted
	 * @param timeFrame the time period of interest
	 * @param fileType the file extension identifier
	 * @return the path to the data file
	 * @throws Exception for errors
	 */
	public static String pathFor
	(String market, MarketQuoteParameters.Periods timeFrame, String fileType)
	throws Exception
	{
		return "data/" + market + "-" + typeFor (timeFrame, fileType);
	}


	/**
	 * read file of historical data
	 * @param market name of the market quoted
	 * @param timeFrame the time period of interest
	 * @param fileType the file extension identifier
	 * @return the data parsed into a list of bars
	 * @throws Exception for any errors
	 */
	public static List<OHLCV.Bar> read
	(String market, MarketQuoteParameters.Periods timeFrame, String fileType)
	throws Exception
	{
		return read ( pathFor (market, timeFrame, fileType), market );
	}


}

