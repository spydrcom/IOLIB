
package net.myorb.gui.components;

import net.myorb.data.abstractions.Range;

/**
 * GUI component for display of ranges
 * @author Michael Druckman
 */
public class Bounds extends SimpleScreenIO
{

	/**
	 * container for grouping range displays
	 */
	public static class Grouping
		extends java.util.ArrayList<Bounds>
	{ private static final long serialVersionUID = 1L; }

	/**
	 * @param container the panel to hold lo/hi pair
	 * @param fieldWidth width of each part (lo/hi) of range
	 */
	public void add (Panel container, int fieldWidth)
	{
		lo = addField (container, fieldWidth);
		hi = addField (container, fieldWidth);
		this.container = container;
	}

	/**
	 * @return the panel containing the lo/hi pair
	 */
	public Panel getContainer () { return container; }
	private Panel container;

	/**
	 * add a bound pair to a panel
	 * @param parent the parent panel to be appended
	 * @param fieldWidth the width of each part of the range
	 * @return the new Bounds object constructed
	 */
	public static Bounds newInstance (Panel parent, int fieldWidth)
	{
		Bounds bounds = new Bounds ();
		bounds.add (startPanel (parent), fieldWidth);
		return bounds;
	}

	/**
	 * @param range the range to be displayed
	 */
	public void setRange (Range range)
	{
		lo.setText (range.getLo ().toString ());
		hi.setText (range.getHi ().toString ());
	}

	/**
	 * @return a Range object with the displayed values
	 */
	public Range getRange ()
	{ return new Range (lo.getText (), hi.getText ()); }
	public void clear () { lo.setText (""); hi.setText (""); }
	private Field lo, hi;

}

