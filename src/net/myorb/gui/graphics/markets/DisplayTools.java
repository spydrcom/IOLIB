
package net.myorb.gui.graphics.markets;

import net.myorb.gui.components.DisplayFrame;
import net.myorb.gui.components.SimpleScreenIO;

import net.myorb.gui.graphics.markets.data.*;

import net.myorb.gui.graphics.DisplayImaging;
import net.myorb.gui.graphics.ScreenPlotter;

import net.myorb.data.abstractions.SimpleStreamIO;
import net.myorb.data.conventional.OHLCV;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JComponent;

import java.awt.Dimension;

/**
 * display primitives that provide screen layout
 * @author Michael Druckman
 */
public class DisplayTools extends SimpleScreenIO
{


	/**
	 * @param type the indication of the series type
	 * @return a formatted suffix for the title
	 */
	public static String typeIndicator (String type)
	{
		if (type == null) return "";
		return " (" + type + ")";
	}


	/**
	 * @param chart the chart object being prepared for display
	 * @param type the time-frame type to associate with data in this chart
	 */
	public static void showToUser
		(
			MarketChart chart, String type
		)
	{
		new DisplayFrame
		(
			(Widget) makeConsolidatedPanel (chart, type),
			chart.getTitle ()
		).show ();
	}


	/*
	 * consolidated panels
	 */

	/**
	 * @param chart the chart object being prepared for display
	 * @param type the time-frame type to associate with data in this chart
	 * @return a panel with combined chart and quotes
	 */
	public static Panel makeConsolidatedPanel (MarketChart chart, String type)
	{
		if (type.startsWith ("D"))
		{
			appendReport (chart);
		}

		return makeConsolidatedPanel
		(
			makeChartPanel (chart, type), makeQuotePanel (chart)
		);
	}

	/**
	 * @param chartPanel the panel containing the chart
	 * @param quotePanel the panel containing the historical data
	 * @return the combined panel
	 */
	public static Panel makeConsolidatedPanel (Panel chartPanel, Panel quotePanel)
	{
		Panel consolidatedPanel = new Panel ();
		consolidatedPanel.add (chartPanel); consolidatedPanel.add (quotePanel);
		return consolidatedPanel;
	}


	/*
	 * S&R report formatter
	 */

	/**
	 * add chart data to report
	 * @param chart the chart to add to the report
	 */
	public static void appendReport (MarketChart chart)
	{
		OHLCV.Bar lastBar = chart.getLastBar ();
		StringBuffer report = new StringBuffer ();

		// date, ticker and last session close
		String today = MarketDateProcessing.getTodaysDate ();
		report.append ("\r\n\t").append (today).append ("\t");
		report.append (chart.getMarketName ()).append ("\t\t");
		F (lastBar.getClose (), report).append ("\r\n");

		// session range
		report.append ("\t");
		F (lastBar.getLow (), report).append ("\t - \t");
		F (lastBar.getHi (), report).append ("\r\n").append ("\r\n");

		// tabulated support and resistance levels
		report.append (chart.getSrData ().tabulated (lastBar));
		report.append ("\r\n");

		// add to report buffer
		sessionReport.append (report);
	}
	static StringBuffer F (double price, StringBuffer report)
	{
		report.append (HistoricalData.format (price));
		return report;
	}
	static StringBuffer sessionReport = new StringBuffer ();

	/**
	 * save collected data to file
	 * @param called file name to use
	 */
	public static void saveReport (String called)
	{
		try
		{
			SimpleStreamIO.saveToFile
			(
				sessionReport.toString (),
				new java.io.File ("data", called + ".txt")
			);
			sessionReport = new StringBuffer ();
		}
		catch (Exception e)
		{
			throw new RuntimeException ("Error saving report", e);
		}
	}

	/**
	 * @param group an identifier for a watch list if needed
	 */
	public static void saveReportAs (String group)
	{
		String name = MarketDateProcessing.getTodaysDate ();
		if (group != null) name += "-" + group;
		saveReport (name);
	}

	/**
	 * report with date as name with no group
	 */
	public static void saveReport ()
	{
		saveReport (MarketDateProcessing.getTodaysDate () + "-SupRes");
	}


	/*
	 * study displays
	 */

	/**
	 * @param chart the source chart
	 * @param study the study to add to plot panel
	 * @return split study panel
	 */
	public static Panel makeStudyPanel (MarketChart chart, Study study)
	{
		Panel master = startGridPanel (null, 0, 1);
		master.add (makeSplitViewChartPanel (chart));
		addPlotToPanel (study.plotSplitViewSizedStudy (), master);
		return master;
	}

	/**
	 * @param chart original chart object
	 * @return split view panel with chart inserted
	 */
	public static Panel makeSplitViewChartPanel (MarketChart chart)
	{
		Panel panel = new Panel ();
		MarketChart splitViewChart = new MarketChart (MarketChart.SPLIT_VIEW_DIMENSION);
		splitViewChart.drawChart (chart.getBarData ());
		addChartWithHandler (splitViewChart, panel);
		return panel;
	}


	/*
	 * screen segment panels
	 */

	/**
	 * @param chart the chart object being prepared for display
	 * @param type the time-frame type to associate with data in this chart
	 * @return a panel with the chart image
	 */
	public static Panel makeChartPanel (MarketChart chart, String type)
	{
		chart.setAppendage (type);
		Panel chartPanel = new Panel ();
		addChartWithHandler (chart, chartPanel);
		return chartPanel;
	}

	/**
	 * @param chart the chart object being prepared for display
	 * @param chartPanel a panel to contain the chart
	 */
	public static void addChartWithHandler (MarketChart chart, Panel chartPanel)
	{
		new ChartMouseMotionHandler (chart)
		.addTo (addChartToPanel (chart, chartPanel));
	}

	/**
	 * @param chart the chart object being prepared for display
	 * @param chartPanel a panel to contain the chart
	 * @return the component holding the image
	 */
	public static JComponent addChartToPanel (MarketChart chart, Panel chartPanel)
	{
		return chart.setDisplayComponent (addPlotToPanel (chart, chartPanel));
	}

	/**
	 * @param plot the plot object being prepared for display
	 * @param plotPanel a panel to contain the plot
	 * @return the component holding the image
	 */
	public static Label addPlotToPanel (ScreenPlotter plot, Panel plotPanel)
	{
		return DisplayImaging.buildPlotPanel (plot, plotPanel, null);
	}

	/**
	 * @param chart the chart object being prepared for display
	 * @return a panel with the bar quotes display and S/R display
	 */
	public static Panel makeQuotePanel (MarketChart chart)
	{
		Panel quotePanel = new Panel (new Grid (0, 1));
		HistoricalData supportAndResistance = chart.getSrData (), barDisplay = chart.getData ();
		newSubPanel (barDisplay, "Selected Bar", quotePanel);
		
		if (supportAndResistance != null)
		{
			addItems (new Panel (), null, quotePanel);
			newSubPanel (supportAndResistance, "Support & Resistance", quotePanel);
		}
		return quotePanel;
	}


	/*
	 * sub-panel areas
	 */

	/**
	 * @param history historical data to be displayed in the panel
	 * @param title the title for the border or NULL for no border
	 * @param toPanel the panel being built
	 */
	public static void newSubPanel (HistoricalData history, String title, Panel toPanel)
	{
		history.prep (); addItems (new JScrollPane (history.getDisplayPanel ()), title, toPanel);
	}

	/**
	 * @param subPanel the component to be added
	 * @param title the title for the border or NULL for no border
	 * @param toPanel the panel being built
	 */
	public static void addItems (JComponent subPanel, String title, Panel toPanel)
	{
		subPanel.setPreferredSize (new Dimension (200, 200));
		if (title != null) subPanel.setBorder (BorderFactory.createTitledBorder (title));
		toPanel.add (subPanel);
	}


	/*
	 * screen manager setting
	 */

	/**
	 * identify display tools as the chart display manager
	 */
	public static void setConsolidatedDisplayManager ()
	{
		MarketChart.setDisplayManager
		(
			new MarketChart.DisplayManager ()
			{
				public void showToUser (MarketChart chart, String type)
				{
					DisplayTools.showToUser (chart, type);
				}
			}
		);
	}


}

