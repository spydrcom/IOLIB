
package net.myorb.data.conventional;

import net.myorb.gui.graphics.markets.SupportAndResistance;
import net.myorb.gui.graphics.markets.data.HistoricalData;

import net.myorb.data.abstractions.Range;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Open, High, Low, Close, Volume market data format
 * @author Michael Druckman
 */
public class OHLCV implements CharacterDelimited.Processor
{


	/**
	 * the fields that constitute description of a bar
	 */
	public static final String[] FIELDS = new String[]{"Date", "Open", "High", "Low", "Close", "Volume"};


	/**
	 * optional weights to apply to averages
	 */
	public enum Weights
	{
		CLOSE_ONLY, CLOSE_WEIGHTED, EVEN_WEIGHTED
	}
	protected Weights usingWeight = Weights.EVEN_WEIGHTED;


	/**
	 * one time series event described as a market data bar
	 */
	public interface Bar
	{

		/**
		 * @return the HI for the bar
		 */
		double getHi ();

		/**
		 * @return the LOW for the bar
		 */
		double getLow ();

		/**
		 * @return the OPEN for the bar
		 */
		double getOpen ();

		/**
		 * @return the CLOSE for the bar
		 */
		double getClose ();

		/**
		 * @return the weighted value for the bar
		 */
		double getValue ();

		/**
		 * @return the VOLUME for the bar
		 */
		long getVolume ();

		/**
		 * @param name the name of the study
		 * @param value the value of the study in this bar
		 */
		void associateStudyValue (String name, double value);

		/**
		 * @param name study field name
		 * @return the associated value
		 */
		double getStudyValue (String name);

		/**
		 * @param data object to use for capture
		 */
		void captureData (HistoricalData data);

		/**
		 * @param data storage object for data
		 */
		void doCapture (HistoricalData data);

		/**
		 * @param name the name of the market described
		 */
		void setMarketName (String name);

		/**
		 * @return the name of the market described
		 */
		String getMarketName ();

		/**
		 * @param toValue the new value to use
		 */
		void setDate (String toValue);

		/**
		 * @return the time-stamp for the bar
		 */
		String getDate ();
	}


	/**
	 * describe a series of bars
	 */
	public static class Series implements CharacterDelimited.Processor
	{

		/**
		 * treat the OHLCV data as a bar in the series
		 */
		public static class Event extends OHLCV implements Bar
		{ Event (CharacterDelimited.Reader reader) { process (reader); } }

		/* (non-Javadoc)
		 * @see net.myorb.data.conventional.CharacterDelimited.Processor#process(net.myorb.data.conventional.CharacterDelimited.Reader)
		 */
		public void process (CharacterDelimited.Reader reader)
		{
			Bar B;
			bars.add (B = new Event (reader));
			annotate (B, reader);
		}
		protected List<Bar> bars = new ArrayList<Bar> ();

		/**
		 * @param bar a new bar to be extended with content
		 * @param fromReader the CSV reader being used to parse source
		 */
		public void annotate (Bar bar, CharacterDelimited.Reader fromReader) {}

		/**
		 * @param name the name of the market described
		 */
		public void setMarketName (String name)
		{
			for (Bar bar : bars) bar.setMarketName (name);
		}

		/**
		 * @return the list of bars taken from the series in reverse order
		 */
		public List<Bar> getReversedBars ()
		{
			List<Bar> reversed = new ArrayList<Bar> ();
			for (Bar b : bars) { reversed.add (0, b); }
			return reversed;
		}

		/**
		 * @return the list of bars taken from the series
		 */
		public List<Bar> getBars () { return bars; }

	}


	/*
	 * implementation of the Bar interface
	 */

	public double getHi () { return h; }
	public double getLow () { return l; }
	public double getOpen () { return o; }
	public double getClose () { return c; }
	public long getVolume () { return volume; }

	public String getDate () { return date==null ? "Predictive" : date; }
	public void setDate (String toValue) { this.date = toValue; }

	/*
	 * study associations
	 */

	/**
	 * @param name the name of the study
	 * @param value the value to associate
	 */
	public void associateStudyValue
	(String name, double value) { studies.add (name); values.put (name, value); }
	protected HashMap<String,Double> values = new HashMap<String,Double>();
	protected List<String> studies = new ArrayList<String>();

	public double getStudyValue (String name)
	{
		if ( ! values.containsKey (name) ) return 0.0;
		return values.get (name);
	}

	/**
	 * @return a display of the name/value pairs
	 */
	StringBuffer associatedStudies ()
	{
		StringBuffer buffer = new StringBuffer ();
		for (String name : studies)
		{
			buffer.append (name).append ("=").append (values.get (name)).append (" ");
		}
		return buffer;
	}


	/*
	 * historical data sample captures
	 */

	/**
	 * @param data a history object for data capture
	 */
	public void captureData (HistoricalData data)
	{
		if (data == null) return;
		data.set ("Date", getDate ()); data.set ("Volume", volume);
		data.set ("Open", o); data.set ("Close", c);
		data.set ("High", h); data.set ("Low", l);
		captureStudies (data);
	}

	/**
	 * @param data a history object for data capture
	 */
	public void captureStudies (HistoricalData data)
	{
		for (String name : studies)
		{
			data.set (name, values.get (name));
		}
	}

	/**
	 * @param data the capture object for the prediction display
	 */
	public void doCapture (HistoricalData data)
	{
		if (data instanceof SupportAndResistance)
		{ captureStudies (data); }
		else captureData (data);
	}


	/*
	 * bar processing
	 */

	/**
	 * @return weights applied for averages
	 */
	public double getValue ()
	{
		switch (usingWeight)
		{
			case CLOSE_WEIGHTED:	return (2*c + l + h) / 4;
			case EVEN_WEIGHTED:		return (c + l + h) / 3;
			default:				return c;
		}
	}

	/* (non-Javadoc)
	 * @see net.myorb.data.conventional.CharacterDelimited.Processor#process(net.myorb.data.conventional.CharacterDelimited.Reader)
	 */
	public void process (CharacterDelimited.Reader reader)
	{
		date = reader.getFormattedDate ("Date");
		volume = reader.getNumber ("Volume").longValue ();

		o = reader.getNumber ("Open").doubleValue ();
		c = reader.getNumber ("Close").doubleValue ();
		h = reader.getNumber ("High").doubleValue ();
		l = reader.getNumber ("Low").doubleValue ();
	}
	protected String date; protected long volume;
	protected double o, h, l, c;

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString ()
	{
		return
		" D=" + getDate () +
		" O=" + o + " H=" + h +
		" L=" + l + " C=" + c + " V=" + volume +
		" " + associatedStudies ();
	}


	/*
	 * market name 
	 */

	/**
	 * @param name the market name to associate with the chart
	 */
	public void setMarketName (String name) { this.marketName = name; }
	public String getMarketName () { return marketName; }
	protected String marketName;


	/*
	 * range determinations
	 */

	/**
	 * @param bars the list of Bar items to examine
	 * @return the price Range for the list of bars
	 */
	public static Range getPriceRange (List<Bar> bars)
	{
		double l, h;
		double lo = Double.MAX_VALUE, hi = Double.MIN_VALUE;
		for (Bar bar : bars)
		{
			if ((l = bar.getLow ()) < lo) lo = l;
			if ((h = bar.getHi ()) > hi) hi = h;
		}
		return new Range (lo, hi);
	}

	/**
	 * @param bars the list of Bar items to examine
	 * @return the volume Range for the list of bars
	 */
	public static Range getVolumeRange (List<Bar> bars)
	{
		long v;
		long lo = Long.MAX_VALUE, hi = Long.MIN_VALUE;
		for (Bar bar : bars)
		{
			v = bar.getVolume ();
			if (v < lo) lo = v;
			if (v > hi) hi = v;
		}
		return new Range (lo, hi);
	}

	/**
	 * @param bars the list of Bar items to examine
	 * @return the average volume for the list of bars
	 */
	public static Long getAverageVolume (List<Bar> bars)
	{
		long sum = 0, count = 0;
		for (Bar bar : bars)
		{
			long v = bar.getVolume ();
			sum += v; count++;
		}
		return sum / count;
	}


}

