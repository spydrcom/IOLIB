
package net.myorb.gui.graphics.markets.data;

import net.myorb.gui.graphics.markets.VolumeProfilePlotter;

import net.myorb.data.abstractions.CommonDataStructures.TextItems;
import net.myorb.data.abstractions.CommonDataStructures.OrderedMap;
import net.myorb.data.abstractions.CommonDataStructures.ValueList;

import net.myorb.data.notations.json.JsonSemantics;

/**
 * collect data for Volume Profile
 * @author Michael Druckman
 */
public class VolumeProfiles
{


	/**
	 * display for Volume Profile plots
	 */
	public interface Plotter
	{

		/**
		 * @param profile profile to be displayed
		 */
		void digest (Profile profile);

		/**
		 * @param title the title text for the frame
		 * @param iconPath path to an icon
		 */
		void show (String title, String iconPath);

		/**
		 * @param price the units price to use for annotation
		 */
		public void annotate (int price);

	}

	/**
	 * describe contributions to accumulations
	 */
	public interface Portions
	{
		/**
		 * @return the sum of all portions included
		 */
		Number getTotalVolume ();

		/**
		 * @return the number of accumulation points
		 */
		int getBlockCount ();
	}

	/**
	 * enumerate an item from the profile
	 */
	public interface Query
	{
		/**
		 * get value contained
		 * @param from a Portions object
		 * @return the extracted value
		 */
		Number selecting (Portions from);
	}

	/**
	 * details collected for portions
	 */
	public static class AccumulationPortions implements Portions
	{
		AccumulationPortions (Number amount)
		{ this.totalAccumulation = amount.longValue (); blocks = 1; }
		public void increase (Number byAmount) { totalAccumulation += byAmount.longValue (); blocks++; }
		public Number getTotalVolume () { return totalAccumulation; }
		public int getBlockCount () { return blocks; }
		protected long totalAccumulation;
		protected int blocks;
	}

	/**
	 * use price key as index to volume
	 */
	public static class Accumulations
		extends OrderedMap <Number, AccumulationPortions>
	{ private static final long serialVersionUID = 5210401532427507500L; }

	/**
	 * OHLC data and accumulation map
	 */
	public interface Profile
	{

		/**
		 * @return map of prices to volume portions
		 */
		Accumulations getAccumulations ();

		/**
		 * @param into list to contain enumerated price list
		 */
		void enumeratePrices (ValueList into);

		/**
		 * list relative amounts
		 * @param portions portion percent of maximum
		 * @param counts block count percent of maximum
		 */
		void enumerateProfile
		(
			ValueList portions, ValueList counts
		);

		Number getOpen ();
		Number getClose ();
		Number getHigh ();
		Number getLow ();
	}

	/**
	 * Bean implementation for basic profile
	 */
	public static class ProfileCore implements Profile
	{

		public ProfileCore () { this.accumulations = new Accumulations (); }

		/* (non-Javadoc)
		 * @see net.myorb.gui.graphics.markets.data.VolumeProfiles.Profile#getAccumulations()
		 */
		public Accumulations
			getAccumulations () { return accumulations; }
		protected Accumulations accumulations;

		/**
		 * @param price the price point
		 * @param volume the increase in volume to note
		 */
		public void increment (Number price, Number volume)
		{
			if (accumulations.containsKey (price))
			{ accumulations.get (price).increase (volume); }
			else accumulations.put (price, new AccumulationPortions (volume));
		}

		public void setClose (Number close) { this.close = close; }
		public void setOpen (Number open) { this.open = open; }
		public void setHigh (Number high) { this.high = high; }
		public void setLow (Number low) { this.low = low; }
		protected Number open, high, low, close;

		/* (non-Javadoc)
		 * @see net.myorb.gui.graphics.markets.data.VolumeProfiles.Profile#getLow()
		 */
		public Number getLow () { return low; }
		public Number getOpen () { return open; }
		public Number getClose () { return close; }
		public Number getHigh () { return high; }
		
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		public String toString ()
		{
			ValueList prices;
			StringBuffer S = new StringBuffer ().append (open).append (",")
				.append (high).append (",").append (low).append (",").append (close).append ("\r\n");
			enumeratePrices ( prices  = new ValueList () );

			for (Number price : prices)
			{
				AccumulationPortions P = accumulations.get (price);
				S.append (price).append (":").append (P.getTotalVolume ()).append (":");
				S.append (P.getBlockCount ()).append ("\r\n");
			}

			return S.toString ();
		}

		/* (non-Javadoc)
		 * @see net.myorb.gui.graphics.markets.data.VolumeProfiles.Profile#enumeratePrices(net.myorb.data.abstractions.CommonDataStructures.ItemList)
		 */
		public void enumeratePrices ( ValueList into )
		{
			into.addAll ( accumulations.keySet () );
			into.sort ( (n1, n2) -> ValueList.ordered (n1, n2, 1) );
		}

		/* (non-Javadoc)
		 * @see net.myorb.gui.graphics.markets.data.VolumeProfiles.Profile#enumerateProfile(net.myorb.gui.graphics.markets.data.VolumeProfiles.ValueList, net.myorb.gui.graphics.markets.data.VolumeProfiles.ValueList)
		 */
		public void enumerateProfile
			(
				ValueList portions, ValueList counts
			)
		{
			enumerate (this, portions, (P) -> P.getTotalVolume ());
			enumerate (this, counts, (P) -> P.getBlockCount ());
		}

	}

	/**
	 * digest data collected in profile
	 * @param profile the profile to process
	 * @param items the list being built of values
	 * @param using the Query for extracting values
	 */
	public static void enumerate
		(
			Profile profile, ValueList items, Query using
		)
	{
		Accumulations A = profile.getAccumulations ();
		Long high = 0l, value; ValueList prices; Number item;
		profile.enumeratePrices ( prices  = new ValueList () );

		for (Number PP : prices)
		{
			items.add ( item = using.selecting (A.get (PP)) );
			if ( (value = item.longValue ()) > high) high = value;
		}

		scale (items, high);
	}

	/**
	 * scale values to percent of max
	 * @param items the values to be scaled
	 * @param max the max value
	 */
	public static void scale
		(
			ValueList items, long max
		)
	{
		for (int index = 0; index < items.size (); index++)
		{
			long N = 100 * items.get (index).longValue ();
			items.set (index, N / max);
		}
	}

	/**
	 * produce list of price points
	 * @param profile the profile to enumerate
	 * @param tick the tick value for the prices
	 * @return the list of price points
	 */
	public static TextItems enumeratePricePoints
		(Profile profile, double tick)
	{
		Number H, L = 0;
		ValueList prices; boolean OK = false;
		TextItems pricePoints = new TextItems ();

		profile.enumeratePrices ( prices  = new ValueList () );
		long current = fromUnits (H = prices.get (0), tick);

		for (Number PP : prices)
		{
			String item = "- ";
			long units = fromUnits (L = PP, tick);
			if (current != units) { item += ( current = units ); OK = true; }
			pricePoints.add (item);
		}

		if ( ! OK )
		{
			int N = pricePoints.size () - 1;
			pricePoints.set (0, HistoricalData.format (H.doubleValue () * tick));
			pricePoints.set (N, HistoricalData.format (L.doubleValue () * tick));
		}

		return pricePoints;
	}

	/**
	 * treat price as multiple of tick objects
	 * @param N the price value to be converted to units
	 * @param tick the size of the tick for the value
	 * @return the number of ticks for value
	 */
	public static long toUnits
		(JsonSemantics.JsonNumber N, double tick)
	{
		Double units = Math.floor
			(N.getNumber().doubleValue () / tick);
		return units.longValue ();
	}
	public static long fromUnits (Number N, double tick)
	{
		Double ticks = Math.floor
			(N.doubleValue () * tick);
		return ticks.longValue ();
	}

	/**
	 * build a new plotter instance
	 * @param tick the tick value for the display
	 * @return the new plotter object
	 */
	public static Plotter newInstance  (double tick)
	{
		return new VolumeProfilePlotter (tick);
	}

}

