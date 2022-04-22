
package net.myorb.gui.components;

/**
 * capture hi/lo values and provide display and range-check methods
 * @author Michael Druckman
 */
public class SliderRangeManager implements SliderRangeInput.RangeReader
{

	/* (non-Javadoc)
	 * @see net.myorb.lotto.gui.GuiSliderRangeComponent.RangeReader#setRangeLo(int)
	 */
	public void setRangeLo (int lo) { this.lo = lo; }

	/* (non-Javadoc)
	 * @see net.myorb.lotto.gui.GuiSliderRangeComponent.RangeReader#setRangeHi(int)
	 */
	public void setRangeHi (int hi) { this.hi = hi; }

	/**
	 * determine item in/out of slider range
	 * @param value the value to be checked for being in range
	 * @return TRUE = not in range described by sliders
	 */
	public boolean outOfRange (int value) { return value < lo || value > hi; }

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString () { return lo + " - " + hi; }

	/**
	 * @return last captured lo value
	 */
	public int getLo () { return lo; }

	/**
	 * @return last captured hi value
	 */
	public int getHi () { return hi; }

	private int lo, hi;

}

