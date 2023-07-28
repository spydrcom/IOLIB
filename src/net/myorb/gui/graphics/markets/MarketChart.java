
package net.myorb.gui.graphics.markets;

import net.myorb.gui.graphics.markets.data.HistoricalData;
import net.myorb.gui.graphics.markets.data.Study;

import net.myorb.gui.graphics.ScreenPlotter;

import net.myorb.data.abstractions.Range;
import net.myorb.data.abstractions.CommonDataStructures;
import net.myorb.data.conventional.OHLCV;

import javax.swing.JComponent;
import java.awt.Dimension;
import java.awt.Color;

import java.util.List;

/**
 * use plotter for OHLC market charts
 * @author Michael Druckman
 */
public class MarketChart extends ScreenPlotter
{


	/*
	 * display component model specifications
	 */

	public static final Dimension
	DEFAULT_CHART_DIMENSION = new Dimension (1200, 800),
	SPLIT_VIEW_DIMENSION = new Dimension (1500, 400);


	/**
	 * manage the display components
	 */
	public interface DisplayManager
	{
		/**
		 * produce chart display
		 * @param chart the chart to be shown
		 * @param type the time frame type for the chart
		 */
		void showToUser (MarketChart chart, String type);
	}

	/**
	 * provide source of Display Manager objects
	 */
	public interface DisplayManagerFactory
	{
		/**
		 * @return newly constructed display manager
		 */
		DisplayManager newDisplayManager ();
	}


	/**
	 * draw individual bar showing volume information
	 */
	public interface VolumeDetail
	{
		/**
		 * evaluate volume data
		 * @param bars the chart bars to be plotted
		 */
		void buildVolumeThresholds (List<OHLCV.Bar> bars);

		/**
		 * draw OHLC with volume hint
		 * @param bar the bar being drawn
		 * @param x the x axis offset being drawn
		 * @return width of bar drawn
		 */
		int drawBar (OHLCV.Bar bar, int x);
	}


	/*
	 * chart object constructors
	 */

	/**
	 * @param chartSize dimensions of chart
	 */
	public MarketChart (Dimension chartSize)
	{
		super (chartSize);
		this.volumeDetail = new AverageRelativeVolume (this);
		this.ignoreAspectRatio ();
	}
	protected VolumeDetail volumeDetail;

	/**
	 * create 1200x800 display
	 * @param data a history object for data collection
	 */
	public MarketChart (HistoricalData data)
	{
		this (DEFAULT_CHART_DIMENSION);
		setData (data);
	}

	/**
	 * @param parent the origination point of the zoom
	 */
	public MarketChart (MarketChart parent)
	{
		this (parent.getData ().copy ()); parent.hData.hide ();
	}

	/**
	 * use OHLCV fields in historical data
	 */
	public MarketChart ()
	{
		this (new HistoricalData (OHLCV.FIELDS));
	}


	/*
	 * constant range and threshold features
	 */

	/**
	 * @return the range set by caller
	 */
	public Range getFixedRange () { return fixedRange; }
	public void setFixedRange (Number low, Number high)
	{ this.fixedRange = new Range (low, high); }
	protected Range fixedRange = null;

	/**
	 * @param at level to mark as threshold
	 */
	public void addThreshold (Number at) { thresholds.add (at); }
	protected CommonDataStructures.ItemList <Number> thresholds;


	/*
	 * chart rendering
	 */

	/**
	 * render sequence of bars
	 * @param bars the OHLC data to plot
	 */
	public void drawChart (List <OHLCV.Bar> bars)
	{
		int events = bars.size () + 1;
		volumeDetail.buildVolumeThresholds (bars);
		Range rangeForChart = fixedRange==null? OHLCV.getPriceRange (bars): fixedRange;
		scaleForTimeSeries (events, PIXELS_PER_BAR, rangeForChart);

		int x = PIXELS_PER_BAR;
		for (OHLCV.Bar bar : bars)
		{
			int w = volumeDetail.drawBar (bar, x);			// High / Low
			drawSpur (bar.getClose (), x, x + w + 2);		// Close
			drawSpur (bar.getOpen (), x - 2, x);			// Open
			x += PIXELS_PER_BAR;
		}

		buildTitleFor (bars, events - 2);
		this.barData = bars;
	}
	public void drawSpur
		(double at, int from, int to) { drawLine (from, at, to, at); }
	public static final int PIXELS_PER_BAR = 10;


	/*
	 * Bar data access
	 */

	public List<OHLCV.Bar> getBarData () { return barData; }
	public OHLCV.Bar getLastBar () { return barData.get (barData.size()-1); }
	protected List<OHLCV.Bar> barData;


	/*
	 * frame title management
	 */

	/**
	 * @param bars the bars of the chart
	 * @param lastBar the index of the last bar
	 */
	public void buildTitleFor (List<OHLCV.Bar> bars, int lastBar)
	{
		makePrediction ();
		OHLCV.Bar idBar = bars.get (0);
		this.marketName = idBar.getMarketName ();
		this.dateRange = idBar.getDate () + " - " + bars.get (lastBar).getDate ();
		this.title = (marketName==null? "": marketName + ": ") + dateRange;
		this.bars = bars;
	}
	protected List<OHLCV.Bar> bars;

	/**
	 * @return name of market charted
	 */
	public String getMarketName () { return marketName; }
	protected String marketName;

	/**
	 * @return name of market charted
	 */
	public String getDateRange () { return dateRange; }
	protected String dateRange;

	/**
	 * @param text qualifier to be appended to title text
	 */
	public void appendToTitle (String text)
	{
		title += text;
	}
	public String getTitle () { return title; }
	protected String title;


	/*
	 * time frame association
	 */

	/**
	 * @param appendage time frame indicator to be displayed
	 */
	public void setAppendage (String appendage)
	{
		appendTypeToTitle (this.appendage = appendage);
	}
	public String getAppendage () { return appendage; }
	protected String appendage;

	/**
	 * @param type the type to append to title
	 */
	public void appendTypeToTitle (String type)
	{
		appendToTitle (DisplayTools.typeIndicator (type));
	}


	/*
	 * historical data and support/resistance connections
	 */

	public HistoricalData getSrData () { return srData; }
	public void setSrData (HistoricalData srData) { this.srData = srData; }
	public void setData (HistoricalData data) { this.hData = data; }
	public HistoricalData getData () { return hData; }
	protected HistoricalData hData, srData;


	/*
	 * forward prediction bar maintenance
	 */

	/**
	 * cache for coming bar prediction (support / resistance)
	 */
	public void makePrediction () { predictive = new OHLCV (); }

	/**
	 * @return a bar of predictive support / resistance numbers
	 */
	public OHLCV getPredictive () { return predictive; }
	protected OHLCV predictive;

	/**
	 * @param data storage object for predictive data
	 */
	public void capturePredictive (HistoricalData data)
	{
		predictive.doCapture (data);
	}


	/*
	 * associated studies
	 */

	/**
	 * @param study the data for the plot
	 * @param color the color to use in the display
	 * @param named a name for the study
	 */
	public void addStudy (List<Double> study, Color color, String named)
	{
		setColor (color);
		int nextX = PIXELS_PER_BAR, lastX = PIXELS_PER_BAR;
		Double lastItem = null;
		int index = 0;

		for (Double studyItem : study)
		{
			if (studyItem != null)
			{
				if (lastItem != null)
				{ drawLine (lastX, lastItem, nextX, studyItem); }
				if (index == bars.size ()) predictive.associateStudyValue (named, studyItem);
				else bars.get (index).associateStudyValue (named, lastItem = studyItem);
			}
			lastX = nextX; nextX += PIXELS_PER_BAR;
			index++;
		}

		addNewStudy (new Study (study), color, named);
	}

	/**
	 * @param newStudy the new Study object
	 * @param color the color to use in the display
	 * @param named a name for the study
	 */
	public void addNewStudy (Study newStudy, Color color, String named)
	{
		studies.add (newStudy);
		newStudy.setColor (color);
		newStudy.setName (named);
		hData.addItem (named);
	}
	protected List<Study> studies = new CommonDataStructures.ItemList <Study> ();


	/*
	 * display management
	 */

	/**
	 * @param text the text of the tip
	 */
	public void showTip (String text)
	{
		display.setToolTipText (text);
	}

	/**
	 * @param bar event to be described
	 */
	public void describe (OHLCV.Bar bar)
	{
		//System.out.println (bar);
		showTip (bar.getDate ());
		bar.captureData (hData);
	}

	/**
	 * @param index the index of time frame to show
	 */
	public void describeStudies (int index)
	{
		Double value;
		for (Study s : studies)
		{
			value = s.get (index);
			if (value == null) continue;
			hData.set (s.getName (), value);
		}
	}

	/**
	 * @param forItem x-axis pixel number for record identification of record
	 */
	public void showDateTip (int forItem)
	{
		OHLCV.Bar bar;
		int index = toIndex (forItem);
		if (index == last || outOfRange (index)) return;
		if ((bar = bars.get (index)) != null)
		{
			describe (bar);
			describeStudies (index);
		}
		last = index;
	}
	protected int last = -1;

	/**
	 * @param x the scaled x value from the plot
	 * @return the Bar.Series structure index for this x
	 */
	int toIndex (int x) { return x / PIXELS_PER_BAR - 1; }
	// was -2 changed to -1 on 6/6/23 to correct tool tip

	/**
	 * @param index the translated index from the plot mouse-over
	 * @return TRUE : index is not in Series range
	 */
	boolean outOfRange (int index) { return index < 0 || index > bars.size () - 1; }


	/*
	 * zoom feature implementation
	 */

	/**
	 * drag has been invoked
	 * @param from the starting x position
	 * @param to the ending x position
	 */
	public void zoom (int from, int to)
	{
		int fromIndex, toIndex;

		if (from < to)
		{
			fromIndex = toIndex (from);
			toIndex = toIndex (to);
		}
		else
		{
			toIndex = toIndex (from);
			fromIndex = toIndex (to);
		}

		if (outOfRange (toIndex) || outOfRange (fromIndex)) return;
		///System.out.println (bars.get (fromIndex).getDate () + " - " + bars.get (toIndex).getDate ());
		zoom (bars, fromIndex, toIndex + 1);
	}

	/**
	 * @param bars the parent chart full set of bars
	 * @param from the starting x position
	 * @param to the ending x position
	 */
	public void zoom (List<OHLCV.Bar> bars, int from, int to)
	{
		List<OHLCV.Bar> section = bars.subList (from, to);
		MarketChart zoomChart = new MarketChart (this);
		zoomChart.drawChart (section);

		for (Study s : studies)
		{
			Study zoom = s.zoom (from,  to);
			zoomChart.addStudy (zoom, s.getColor (), s.getName ());
		}

		zoomChart.drawChart (section);
		zoomChart.setSrData (getSrData ());
		zoomChart.showToUser (appendage);
	}


	/*
	 * screen display management
	 */

	/**
	 * @param manager the display manager to be used
	 */
	public static void setDisplayManager
		(DisplayManager manager) { defaultManager = manager; }
	public static DisplayManager defaultManager;

	/**
	 * @return new manager from determined source
	 */
	DisplayManager getDisplayManager ()
	{
		if (displayManagerFactory == null) return defaultManager;
		else return displayManagerFactory.newDisplayManager ();
	}

	/**
	 * identify factory for DisplayManager object
	 * @param chosenDisplayManagerFactory chosen factory object
	 */
	public static void setDisplayManagerFactory
	(DisplayManagerFactory chosenDisplayManagerFactory)
	{ displayManagerFactory = chosenDisplayManagerFactory; }
	public static DisplayManagerFactory displayManagerFactory = null;

	/**
	 * @param type the time frame of the chart
	 */
	public void showToUser (String type)
		{ selectedManager.showToUser (this, type); }
	protected DisplayManager selectedManager = getDisplayManager ();

	/**
	 * @return component holding the chart
	 */
	public JComponent getDisplayComponent () { return display; }

	/**
	 * @param display the component displaying the chart
	 * @return the same component for chain calls
	 */
	public JComponent setDisplayComponent (JComponent display)
	{ return this.display = display; }
	protected JComponent display;


}


/**
 * volume attribute for bar representation using Average Relative model
 */
class AverageRelativeVolume implements MarketChart.VolumeDetail
{

	AverageRelativeVolume
		(ScreenPlotter plot) { this.plot = plot; }
	protected ScreenPlotter plot;

	/* (non-Javadoc)
	 * @see net.myorb.gui.graphics.markets.MarketChart.VolumeDetail#buildVolumeThresholds(java.util.List)
	 */
	public void buildVolumeThresholds (List<OHLCV.Bar> bars)
	{
		this.averageVolume =
			OHLCV.getAverageVolume (bars);
		this.reduced = averageVolume / 4;
		this.doubleAverage = averageVolume * 2;
		this.extreme = averageVolume * 3;
		this.light = averageVolume / 8;
	}
	protected Long averageVolume, reduced, light, doubleAverage, extreme;

	/* (non-Javadoc)
	 * @see net.myorb.gui.graphics.markets.MarketChart.VolumeDetail#drawBar(net.myorb.data.conventional.OHLCV.Bar, int)
	 */
	public int drawBar (OHLCV.Bar bar, int x)
	{
		int n = 0;
		long volume = bar.getVolume ();

		if (volume < light) { draw (bar, Color.pink, x); n++; }
		if (volume < reduced) { draw (bar, Color.magenta, x+n); n++; }
		draw (bar, Color.white, x+n); n++;

		if (volume > doubleAverage) { draw (bar, Color.yellow, x+n); n++; }
		if (volume > extreme) { draw (bar, Color.red, x+n); n++; }

		plot.setColor (Color.white);
		return n;
	}
	void draw (OHLCV.Bar bar, Color color, int x)
	{
		plot.setColor (color);
		plot.drawLine (x, bar.getLow (), x, bar.getHi ());
	}

}


/**
 * volume attribute for bar representation using Logarithmic model
 */
class LogarithmicVolume implements MarketChart.VolumeDetail
{

	LogarithmicVolume
		(ScreenPlotter plot) { this.plot = plot; }
	protected ScreenPlotter plot;

	/*
	 * logarithmic volume display implementation
	 */

	/**
	 * @param volume the volume to be represented
	 * @return 1 for lightest, 2 for 2X - 4X, 3 for 4X - 8X, 4 for higher
	 */
	public int getBarWidth (long volume)
	{
		if (volume < doubleThreshold) return 1;
		if (volume < redoubleThreshold) return 2;
		if (volume < extremeThreshold) return 3;
		return 4;
	}
	protected long doubleThreshold, redoubleThreshold, extremeThreshold;

	/* (non-Javadoc)
	 * @see net.myorb.gui.graphics.markets.MarketChart.VolumeDetail#buildVolumeThresholds(java.util.List)
	 */
	public void buildVolumeThresholds (List<OHLCV.Bar> bars)
	{
		Range volumeRange = OHLCV.getVolumeRange (bars);
		long lightestVolumeOfSeries = volumeRange.getLo ().longValue ();
		redoubleThreshold = 4 * lightestVolumeOfSeries;
		extremeThreshold = 8 * lightestVolumeOfSeries;
		doubleThreshold = 2 * lightestVolumeOfSeries;
	}

	/* (non-Javadoc)
	 * @see net.myorb.gui.graphics.markets.MarketChart.VolumeDetail#drawBar(net.myorb.data.conventional.OHLCV.Bar, int)
	 */
	public int drawBar (OHLCV.Bar bar, int x)
	{
		int w = getBarWidth (bar.getVolume ());
		for (int i = 0; i < w; i++)
		{
			plot.setColor (barColor[i]);
			plot.drawLine (x+i, bar.getLow (), x+i, bar.getHi ());
		}
		plot.setColor (Color.white);
		return w;
	}
	protected Color[] barColor = new Color[]{Color.white, Color.yellow, Color.magenta, Color.pink};

}

