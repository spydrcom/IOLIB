
package net.myorb.gui.graphics.markets.data;

import net.myorb.data.abstractions.CommonDataStructures;

/**
 * collect data for Volume Profile
 * @author Michael Druckman
 */
public class VolumeProfiles
{

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
	public static class Accumulations extends CommonDataStructures.OrderedMap <Number, AccumulationPortions>
	{
		private static final long serialVersionUID = 5210401532427507500L;
	}

	/**
	 * OHLC data and accumulation map
	 */
	public interface Profile
	{
		Accumulations getAccumulations ();

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
			CommonDataStructures.ItemList <Number> prices;
			StringBuffer S = new StringBuffer ().append (open).append (",")
				.append (high).append (",").append (low).append (",").append (close).append ("\r\n");
			enumeratePrices ( prices  = new CommonDataStructures.ItemList <Number> () );

			for (Number price : prices)
			{
				AccumulationPortions P = accumulations.get (price);
				S.append (price).append (":").append (P.getTotalVolume ()).append (":");
				S.append (P.getBlockCount ()).append ("\r\n");
			}

			return S.toString ();
		}

		/**
		 * @param into list to contain enumerated price list
		 */
		public void enumeratePrices ( CommonDataStructures.ItemList <Number> into )
		{
			into.addAll ( accumulations.keySet () );
			into.sort ( (n1, n2) -> compare (n1, n2) );
		}

	}

	/**
	 * implementation of Comparator for Number objects
	 * @param arg0 first of values to compare
	 * @param arg1 second value to compare
	 * @return standard -1 0 or 1
	 */
	public static int compare (Number arg0, Number arg1)
	{
		double
			n0 = arg0.doubleValue (),
			n1 = arg1.doubleValue ();
		if (n0 == n1) return 0;
		if (n0 < n1) return 1;
		return -1;
	}

}

