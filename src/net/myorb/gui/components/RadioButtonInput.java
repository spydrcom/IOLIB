
package net.myorb.gui.components;

import net.myorb.data.abstractions.BinaryStateMonitor;

import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;

/**
 * binary radio button component.
 *  first options is defaulted to selected.
 *  context test returns TRUE when first is still selected.
 * @author Michael Druckman
 */
public class RadioButtonInput extends JPanel implements SimpleScreenIO.Valued<Boolean>,
	BinaryStateMonitor.BinaryMonitorEvaluation, BinaryStateMonitor.BinaryMonitoredComponent
{


	/**
	 * @return TRUE = first option has remained selected
	 */
	public boolean isFirstSelected () { return button1.isSelected (); }

	/* (non-Javadoc)
	 * @see net.myorb.lotto.gui.StateMonitor.MonitorEvaluation#getCurrentState()
	 */
	public Boolean getCurrentState () { return ! isFirstSelected (); }

	/* (non-Javadoc)
	 * @see net.myorb.lotto.gui.components.SimpleScreenIO.Valued#getValue()
	 */
	public Boolean getValue () { return getCurrentState (); }

	/* (non-Javadoc)
	 * @see net.myorb.lotto.gui.StateMonitor.MonitoredComponent#getMonitor()
	 */
	public BinaryStateMonitor getMonitor () { return monitor; }


	/**
	 * construct 2 radio buttons and add to panel and to group
	 * @param button1Text the text to be displayed on the first radio button (defaulted to selected)
	 * @param button2Text the text to be displayed on the second radio button
	 */
	public RadioButtonInput (String button1Text, String button2Text)
	{
	    button1 = new JRadioButton (button1Text);
	    button2 = new JRadioButton (button2Text);

	    button1.setActionCommand (button1Text); group.add (button1);
	    button2.setActionCommand (button2Text); group.add (button2);

	    button1.addChangeListener (monitor = new BinaryStateMonitor (this));

	    this.add (button1); this.add (button2);
	    button1.setSelected (true);
	    setSize (300, 100);
	}
    private ButtonGroup group = new ButtonGroup();
    private JRadioButton button1, button2;
    private BinaryStateMonitor monitor;


	private static final long serialVersionUID = 1L;
}

