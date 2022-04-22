
package net.myorb.gui.graphics.markets;

import net.myorb.data.abstractions.SimpleUtilities;
import net.myorb.data.abstractions.SimpleUtilities.MapToList;

import net.myorb.gui.graphics.markets.data.HistoricalData;

import java.awt.Color;
import java.util.HashSet;
import java.util.List;

/**
 * display object for market data Support And Resistance numbers
 * @author Michael Druckman
 */
public class SupportAndResistance extends HistoricalData
{

	/* (non-Javadoc)
	 * @see net.myorb.gui.graphics.markets.HistoricalData#set(java.lang.String, java.lang.Number)
	 */
	public void set (String name, Number value)
	{
		valueToNames.add (value.doubleValue (), name);
		itemMap.put (name, new Label (""));
		super.set (name, value);
	}
	protected MapToList<Double,String> valueToNames = new MapToList<Double,String>();

	/**
	 * sort data and build display before display of frame
	 */
	public void show ()
	{
		prep (); show ("Support/Resistance");
	}

	/* (non-Javadoc)
	 * @see net.myorb.gui.graphics.markets.data.HistoricalData#prep()
	 */
	public void prep ()
	{
		List<Double> values = SimpleUtilities.orderedKeys (valueToNames);
		int index = values.size (); double threshold = values.get (index - 1) / 1000;

		double prev = 0.0; Label l; 
		resetDisplay ();

		while (--index >= 0)
		{
			Double value = values.get (index);
			for (String name : valueToNames.get (value))
			{
				if (done.contains (name)) continue; else done.add (name);		// do not allow multiple copies of same tag

				display.add (new Label (itemMap.get (name).getText ()));		// each display needs own copy of each label
				display.add (l = new Label (name));								// value followed by tag

				if (Math.abs (value-prev) < threshold)							// RED means CLOSE
				{ l.setForeground (Color.red); }

				prev = value;
			}
		}
	}

	/**
	 * start new display from scratch
	 */
	public void resetDisplay () { done.clear (); initDisplay (); }
	protected HashSet<String> done = new HashSet<String> ();

}
