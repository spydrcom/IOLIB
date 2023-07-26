
package net.myorb.gui.graphics.markets.data;

import net.myorb.gui.graphics.ScreenPlotter;
import net.myorb.gui.graphics.markets.MarketChart;

import net.myorb.data.abstractions.CommonDataStructures;
import net.myorb.data.abstractions.Range;

import java.awt.Color;
import java.awt.Dimension;

import java.util.ArrayList;
import java.util.List;

/**
 * base class for study overlays for OHLC market charts
 * @author Michael Druckman
 */
public class Study extends ArrayList <Double>
{


	public static class Parameters extends CommonDataStructures.NumericParameterization
	{ private static final long serialVersionUID = -2743966556712161463L; }


	/**
	 * empty study, treated as List
	 */
	public Study () {}

	/**
	 * @param items initial contents using addAll
	 */
	public Study (List<Double> items) { this.addAll (items); }


	// study description parameters

	/**
	 * @param number count of items to be included
	 * @return study including last item and counting back for number
	 */
	public Study last (int number)
	{
		int count = size (); return new Study (this.subList (count - number, count));
	}

	/**
	 * @param count number of NULL values to be added to study
	 */
	public void fill (int count) { for (int n=count; n > 0; n--) add (null); }

	/**
	 * @param name a name for the study
	 */
	public void setName (String name) { this.name = name; }
	public String getName () { return name; }
	protected String name;

	/**
	 * @param color the color to use to display study
	 */
	public void setColor (Color color) { this.color = color; }
	public Color getColor () { return color; }
	protected Color color;

	/**
	 * get range of attached values
	 * @return the Range for the study
	 */
	public Range getRange ()
	{
		Double
		lo = Double.MAX_VALUE, hi = Double.MIN_VALUE;
		for (Double v : this)
		{
			if (v < lo) lo = v;
			if (v > hi) hi = v;
		}
		return new Range (lo, hi);
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
	 * @param defaultRange the computed range
	 * @return the computed range or fixed if set
	 */
	public Range chooseRange (Range defaultRange)
	{
		return fixedRange==null? defaultRange: fixedRange;
	}

	/**
	 * @param at level to mark as threshold
	 */
	public void addThreshold (Number at) { thresholds.add (at); }
	protected CommonDataStructures.ItemList <Number> thresholds = new CommonDataStructures.ItemList <Number> ();

	/**
	 * add threshold lines to plot
	 * @param plotter the plotter being updated
	 */
	public void drawThresholds (ScreenPlotter plotter, double xMax)
	{
		for (Number threshold : thresholds)
		{
			double value = threshold.doubleValue ();
			plotter.drawLine (0, value, xMax, value);
		}
	}


	// study plot drawing methods

	/**
	 * draw the study using given scale
	 * @param plotter the plotter object to draw with
	 * @param pixelsPerEvent the x-axis width per event
	 * @param scale the scaling factor based on range
	 */
	public void drawStudy
		(
			ScreenPlotter plotter,
			int pixelsPerEvent,
			double scale
		)
	{
		PlotManager pmgr =
			new PlotManager ( plotter, pixelsPerEvent );
		for (Double studyItem : this)
		{
			if (studyItem != null)
			{ pmgr.draw ( studyItem  / scale ); }
			else pmgr.incX ();
		}
	}

	/**
	 * draw the study
	 * @param plotter the plotter object to draw with
	 * @param pixelsPerEvent the x-axis width per event
	 * @param range the range of values in this study
	 */
	public void drawStudy
		(
			ScreenPlotter plotter,
			int pixelsPerEvent,
			Range range
		)
	{
		plotter.ignoreAspectRatio ();
		double scale = plotter.scaleForTimeSeries
			( size (), pixelsPerEvent, chooseRange (range) );
		drawThresholds ( plotter, size () * pixelsPerEvent );
		drawStudy ( plotter, pixelsPerEvent, scale );
	}

	/**
	 * draw the study
	 * - range of values computed
	 * @param plotter the plotter object to draw with
	 * @param pixelsPerEvent the x-axis width per event
	 */
	public void drawStudy
		(
			ScreenPlotter plotter,
			int pixelsPerEvent
		)
	{
		drawStudy (plotter, pixelsPerEvent, getRange ());
	}

	/**
	 * draw the study
	 * - range of values known to be 0 to 100
	 * @param plotter the plotter object to draw with
	 * @param pixelsPerEvent the x-axis width per event
	 */
	public void drawNormalizedStudy
		(
			ScreenPlotter plotter,
			int pixelsPerEvent
		)
	{
		drawStudy ( plotter, pixelsPerEvent, R100 );
	}
	static Range R100 = new Range (0, 100);

	/**
	 * @param from starting point
	 * @param to last + 1 per sublist convention
	 * @return a study of the selected range
	 */
	public Study zoom (int from, int to)
	{
		return new Study (subList (from, to));
	}


	// normalization of study sequences

	/**
	 * @param R ranges to aggregate
	 * @return range from lowest to highest
	 */
	public Range aggregate (Range... R)
	{
		double L = Double.MAX_VALUE, H = Double.MIN_VALUE;
		for (Range r : R)
		{
			L = Math.min (L, r.getLo ().doubleValue ());
			H = Math.max (H, r.getHi ().doubleValue ());
		}
		return new Range (L, H);
	}

	/**
	 * @param R range of values to treat as 0 and 100
	 */
	public void adjust (Range R)
	{
		double L = R.getLo ().doubleValue (),
			H = R.getHi ().doubleValue (), D = H - L;
		for (int n = 0; n < this.size (); n++)
		{
			double V = ( this.get (n) - L ) / D;
			this.set (n, 100 * V);
		}
	}

	/**
	 * adjust range of data points to 0-1
	 */
	public void adjust ()
	{
		this.adjust (this.getRange ());
	}


	// plotter object construction methods

	/**
	 * @param w the width of the display
	 * @param h the height of the display
	 * @param pixelsPerEvent the x-axis width per event
	 * @return a plot object for the display
	 */
	public ScreenPlotter plotStudy (int w, int h, int pixelsPerEvent)
	{
		ScreenPlotter plot = new ScreenPlotter (w, h);
		drawStudy (plot, pixelsPerEvent);
		return plot;
	}
	public ScreenPlotter plotStudy (Dimension d, int pixelsPerEvent)
	{ return plotStudy (d.width, d.height, pixelsPerEvent); }

	/**
	 * @return plot object with scale parameters matching chart default
	 */
	public ScreenPlotter plotDefaultSizedStudy ()
	{
		return plotStudy (MarketChart.DEFAULT_CHART_DIMENSION, MarketChart.PIXELS_PER_BAR);
	}
	public ScreenPlotter plotSplitViewSizedStudy ()
	{
		return plotStudy (MarketChart.SPLIT_VIEW_DIMENSION, MarketChart.PIXELS_PER_BAR);
	}


	private static final long serialVersionUID = -598691272751036548L;

}


/**
 * manage context of ongoing plot
 * @author Michael Druckman
 */
class PlotManager
{

	/**
	 * @param nextValue next value in sequence
	 */
	void draw
		(
			double nextValue
		)
	{
		if (lastValue != null)
		{
			plotter.drawLine
			(
				nextX - pixelsPerEvent,
				lastValue.doubleValue (),
				nextX, nextValue
			);
		}
	
		lastValue = nextValue;
		incX ();
	}

	/**
	 * step X-axis for sequence plot
	 */
	void incX () { nextX += pixelsPerEvent; }

	/**
	 * @param plotter the plotter object to use
	 * @param pixelsPerEvent the computed X-axis increment
	 */
	PlotManager (ScreenPlotter plotter, int pixelsPerEvent)
	{
		this.plotter = plotter;
		this.pixelsPerEvent = pixelsPerEvent;
		this.nextX = pixelsPerEvent;
		this.lastValue = null;
	}

	int nextX, pixelsPerEvent;
	ScreenPlotter plotter;
	Double lastValue;

}

