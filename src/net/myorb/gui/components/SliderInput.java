
package net.myorb.gui.components;

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

import javax.swing.JSlider;
import javax.swing.JPanel;
import javax.swing.JLabel;

/**
 * slider component for changing data values
 * @author Michael Druckman
 */
public class SliderInput extends JPanel implements ChangeListener
{

	/**
	 * a listener for slider value changes
	 */
	public interface SliderListener
	{
		/**
		 * @param newValue the new value of the slider
		 */
		void sliderValueChanged (int newValue);
	}

	/**
	 * @param title a text title for the display panel
	 * @param lo the lo value the slider is to be allowed
	 * @param hi the hi value the slider is to be allowed
	 * @param initial the initial value the slider is to be set
	 */
	public SliderInput (String title, int lo, int hi, int initial)
	{
		add (slider = new JSlider (lo, hi, initial));
		this.display = new GuiSliderDisplay (title, initial);
		this.currentSliderValue = initial;
		slider.addChangeListener (this);
	}
	public JPanel getDisplay () { return display; }
	private GuiSliderDisplay display;
	private JSlider slider;

	/**
	 * @param listener new value of listener
	 */
	public void setListener
	(SliderListener listener) { this.listener = listener; }
	SliderListener listener = null;

	/**
	 * @return both slider and label panels
	 */
	public JPanel longForm ()
	{
		JPanel p = new JPanel ();
		p.add (this); p.add (display);
		return p;
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
	 */
	public void stateChanged (ChangeEvent event)
	{
		display.setValue (currentSliderValue = slider.getValue ());
		if (listener != null) { listener.sliderValueChanged (currentSliderValue); }
		slider.setToolTipText (display.getDisplayText ());
	}
	public int getValue () { return currentSliderValue; }
	protected int currentSliderValue;

	private static final long serialVersionUID = 1L;
}


/**
 * a text panel holding a title and current value display for the slider
 */
class GuiSliderDisplay extends JPanel
{

	GuiSliderDisplay (String title, int initial)
	{
		add (new JLabel (title));
		add (setLabel (initial));
	}

	/**
	 * @param value an integer value to place in the display component
	 * @return the display component
	 */
	private JLabel setLabel (int value)
	{
		setValue (value);
		return valueDisplay;
	}
	private JLabel valueDisplay = new JLabel ();

	/**
	 * @return text text being displayed
	 */
	public String getDisplayText () { return valueDisplay.getText (); }

	/**
	 * @param value an integer value to place in the display component
	 */
	public void setValue (int value) { setValue (Integer.toString (value)); }

	/**
	 * @param text the new value to be displayed
	 */
	public void setValue (String text) { valueDisplay.setText (text); }

	private static final long serialVersionUID = 1L;
}

