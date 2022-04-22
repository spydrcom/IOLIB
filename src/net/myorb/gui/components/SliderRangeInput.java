
package net.myorb.gui.components;

import javax.swing.JPanel;

/**
 * provide for sliders defining hi/lo of range
 * @author Michael Druckman
 */
public class SliderRangeInput
{

	/**
	 * description of an object that can persist the range
	 */
	public interface RangeReader
	{
		/**
		 * @param lo the lo value to persist
		 */
		public void setRangeLo (int lo);

		/**
		 * @param hi the hi value to persist
		 */
		public void setRangeHi (int hi);
	}

	/**
	 * extended slider property list for listener
	 */
	public interface RangeSlider
	{
		int getValue ();
		boolean isHiEndOfRange ();
	}

	/**
	 * version of listener that can pass the additional methods
	 */
	public interface RangeChangeListener
	{
		void sliderValueChanged (RangeSlider slider);
	}

	/**
	 * sliders that establish rating range
	 * @param p panel being constructed, add these components
	 * @param loValue the (allowed) lo value of the range to be shown
	 * @param hiValue the (allowed) hi value of the range to be shown
	 * @param loValueTitle the text description for the lo value
	 * @param hiValueTitle the text description for the hi value
	 * @param listener the listener object to be used
	 */
	public void addRangeSliders
		(
			JPanel p,
			int loValue, int hiValue,
			String loValueTitle, String hiValueTitle,
			RangeChangeListener listener
		)
	{
		loSlider = new RangeSliderComponent (loValueTitle, loValue, hiValue, loValue, listener);
		hiSlider = new RangeSliderComponent (hiValueTitle, loValue, hiValue, hiValue, listener); hiSlider.setHiEnd ();
		p.add (loSlider.longForm ()); p.add (hiSlider.longForm ());
	}
	private RangeSliderComponent loSlider, hiSlider;

	/**
	 * @param sliderRangeReader object that will persist the read of the range
	 */
	public void readRangeFromSliders (RangeReader sliderRangeReader)
	{
		sliderRangeReader.setRangeLo (loSlider.getValue ());
		sliderRangeReader.setRangeHi (hiSlider.getValue ());
	}

}


/**
 * extend simple GUI slider to permit recognition of which end of range changed
 */
class RangeSliderComponent extends SliderInput
	implements SliderRangeInput.RangeSlider, SliderInput.SliderListener
{

	public RangeSliderComponent
	(String title, int lo, int hi, int initial, SliderRangeInput.RangeChangeListener listener)
	{ super (title, lo, hi, initial); if ((rangeListener = listener) != null) setListener (this); }
	protected SliderRangeInput.RangeChangeListener rangeListener;

	/* (non-Javadoc)
	 * @see net.myorb.lotto.gui.GuiSlider.SliderListener#sliderValueChanged(int)
	 */
	public void sliderValueChanged (int newValue)
	{
		rangeListener.sliderValueChanged (this);
	}

	/* (non-Javadoc)
	 * @see net.myorb.lotto.gui.GuiSliderRangeComponent.RangeSlider#isHiEndOfRange()
	 */
	public boolean isHiEndOfRange () { return hiEnd; }
	public void setHiEnd () { hiEnd = true; }
	protected boolean hiEnd = false;

	private static final long serialVersionUID = 1L;
}

