
package net.myorb.gui.graphics.markets;

import net.myorb.gui.graphics.ScreenPlotter;
import net.myorb.gui.graphics.ToolTipHandler;
import net.myorb.gui.graphics.CoordinateTranslation;

import net.myorb.gui.graphics.markets.data.VolumeProfiles;
import net.myorb.gui.graphics.markets.data.VolumeProfiles.Profile;
import net.myorb.gui.graphics.markets.data.HistoricalData;

import net.myorb.data.abstractions.CommonDataStructures.TextItems;
import net.myorb.data.abstractions.CommonDataStructures.ValueList;
import net.myorb.gui.components.SimpleScreenIO.Widget;
import net.myorb.gui.components.DisplayFrame;

import java.awt.event.MouseEvent;
import java.awt.Color;

/**
 * a ScreenPlotter for Volume Profile display generation
 * @author Michael Druckman
 */
public class VolumeProfilePlotter
	extends ScreenPlotter implements VolumeProfiles.Plotter
{


	static final int
		X_MAX = 400, Y_MAX = 500;
	static final int PRICE_POINTS = 200;
	static final int RELATIVE_BLOCK_COUNTS = 270;


	public VolumeProfilePlotter (double tick)
	{
		super (X_MAX, Y_MAX);
		super.ignoreAspectRatio ();
		this.tick = tick;
	}
	protected double tick;


	/**
	 * connect profile to plot
	 * @param profile the profile being rendered
	 */
	public void digest (Profile profile)
	{
		establishScale (profile);
		ValueList points = drawPricePoints (profile);
		drawRelativePointExtensions (profile, points);
		drawEndpoints ();
	}


	/**
	 * extract O, H, L, and C
	 * @param profile the profile being rendered
	 */
	public void establishScale (Profile profile)
	{
		this.L = profile.getLow ().intValue ();
		this.H = profile.getHigh ().intValue ();
		this.C = profile.getClose ().intValue ();
		this.O = profile.getOpen ().intValue ();
		this.D = ( H - L ) / 10;

		scaleFor
		(
			0, X_MAX, L-D, H+D
		);
	}
	protected int O, H, L, C, D;


	/**
	 * show the plot scale
	 * @param profile the profile being rendered
	 * @return the list of price points found
	 */
	public ValueList drawPricePoints (Profile profile)
	{
		ValueList prices = new ValueList ();
		profile.enumeratePrices (prices);

		TextItems points =
			VolumeProfiles.enumeratePricePoints (profile, tick);
		for (int index=0; index < points.size (); index++)
		{
			String text = points.get (index);
			int price = prices.get (index).intValue ();
			g.drawString (text, PRICE_POINTS+5, scaleY (price));
		}

		return prices;
	}


	/**
	 * show volume and count data
	 * @param profile the profile being rendered
	 * @param points the price points in the profile
	 */
	public void drawRelativePointExtensions (Profile profile, ValueList points)
	{
		int price, pointCol = PRICE_POINTS-2, blockCol = RELATIVE_BLOCK_COUNTS;
		ValueList portions = new ValueList (), counts = new ValueList ();
		profile.enumerateProfile (portions, counts);

		for (int index=0; index < points.size (); index++)
		{
			drawAnnotation
			(
				Color.CYAN, price = points.get (index).intValue (),
				pointCol - 3 * portions.get (index).intValue () / 2,
				pointCol
			);

			drawAnnotation
			(
				Color.BLUE, price, blockCol,
				blockCol + counts.get (index).intValue ()
			);
		}

		String V = sessionVolume (profile.getTotalVolume ());
		setColor (Color.WHITE); g.drawString (V, 10, 20);
	}
	String sessionVolume (long total)
	{
		long V = total; byte scale = 0;
		while (V > 10240) { V /= 1024; scale++; }
		return Long.toString (V) + SCALES.charAt (scale);
	}
	static final String SCALES = " KMGTEP";


	/**
	 * draw a price annotation bar
	 * @param color AWT Color for line
	 * @param price the price point for the bar
	 * @param from the starting point for drawing
	 * @param to the ending point for drawing
	 */
	public void drawAnnotation (Color color, int price, int from, int to)
	{
		setColor ( color ); drawLine ( from, price, to, price );
	}


	/**
	 * show range of session Green for increase and Red for decrease
	 */
	public void drawEndpoints ()
	{
		if (C > O) this.setColor (Color.GREEN);
		else if (C < O) this.setColor (Color.RED);
		else this.setColor (Color.ORANGE);
		
		drawLine
		(
			PRICE_POINTS+1, O, PRICE_POINTS+1, C
		);
	}


	/**
	 * @return a widget holding the plot image
	 */
	public Widget constructDisplayWidget ()
	{
		ProfileTipHandler listener;
		Widget displayWidget = getWidgetForPlot ();
		displayWidget.toComponent ().addMouseMotionListener
			( listener = new ProfileTipHandler ( this ) );
		listener.connect ( displayWidget );
		return displayWidget;
	}


	/* (non-Javadoc)
	 * @see net.myorb.gui.graphics.markets.data.VolumeProfiles.Plotter#show(java.lang.String, java.lang.String)
	 */
	public void show (String title, String iconPath)
	{
		this.frame = new DisplayFrame
			(
				constructDisplayWidget (),
				title
			);
		this.frame.setIcon (iconPath);
		this.frame.show ();
	}
	protected DisplayFrame frame;


	/* (non-Javadoc)
	 * @see net.myorb.gui.graphics.markets.data.VolumeProfiles.Plotter#annotate(int)
	 */
	public void annotate (int price)
	{
		if ( price >= L && price <= H )
		{
			setColor (Color.ORANGE);
			g.drawString ("<", X_MAX-10, scaleY (price));
		}
	}


	/**
	 * ToolTipHandler for price display in profile displays
	 */
	class ProfileTipHandler extends ToolTipHandler
	{
		ProfileTipHandler (CoordinateTranslation.Scaling scaling)
		{ super (scaling); this.scale = scaling.getYScale (); this.top = H + D; }
		protected double scale, top;

		/* (non-Javadoc)
		 * @see net.myorb.gui.graphics.ToolTipHandler#formatTip(java.awt.event.MouseEvent)
		 */
		public String formatTip (MouseEvent event)
		{
			return HistoricalData.format ( tick * ( top - event.getY () / scale ) );
		}
	}


}

