
package net.myorb.gui.graphics.markets.data;

import net.myorb.gui.graphics.ScreenPlotter;
import net.myorb.gui.graphics.markets.MarketChart;

import net.myorb.data.abstractions.Range;

import java.awt.Color;
import java.awt.Dimension;

import java.util.ArrayList;
import java.util.List;

/**
 * base class for study overlays for OHLC market charts
 * @author Michael Druckman
 */
public class Study extends ArrayList<Double>
{

	/**
	 * empty study, treated as List
	 */
	public Study () {}

	/**
	 * @param items initial contents using addAll
	 */
	public Study (List<Double> items) { this.addAll (items); }

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

	/**
	 * @param plotter the plotter object to draw with
	 * @param pixelsPerEvent the x-axis width per event
	 */
	public void drawStudy (ScreenPlotter plotter, int pixelsPerEvent)
	{
		double scale =
		scaleForTimeSeries (plotter, pixelsPerEvent);
		int lastX = pixelsPerEvent, nextX = pixelsPerEvent;
		Double lastItem = null, nextItem = null;

		for (Double studyItem : this)
		{
			if (studyItem != null)
			{
				lastItem = nextItem;
				nextItem = studyItem / scale;

				if (lastItem != null)
				{
					plotter.drawLine
					(
						lastX, lastItem.doubleValue(),
						nextX, nextItem.doubleValue()
					);
				}
			}

			lastX = nextX; nextX += pixelsPerEvent;
		}
	}
	double scaleForTimeSeries (ScreenPlotter plotter, int pixelsPerEvent)
	{
		plotter.ignoreAspectRatio ();
		return plotter.scaleForTimeSeries
		(
			size (),
			pixelsPerEvent,
			getRange ()
		);
	}

	/**
	 * @param from starting point
	 * @param to last + 1 per sublist convention
	 * @return a study of the selected range
	 */
	public Study zoom (int from, int to)
	{
		return new Study (subList (from, to));
	}

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
