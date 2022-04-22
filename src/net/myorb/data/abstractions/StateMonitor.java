
package net.myorb.data.abstractions;

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

/**
 * change listener linkage to a state monitor.
 * @author Michael Druckman
 */
public class StateMonitor<T> implements ChangeListener
{

	/**
	 * indicate component contains monitor
	 */
	public interface MonitoredComponent<T>
	{
		/**
		 * @return the monitor object
		 */
		StateMonitor<T> getMonitor ();
	}

	/**
	 * access to value of component
	 */
	public interface MonitorEvaluation<T>
	{
		/**
		 * @return current value of monitored item
		 */
		T getCurrentState ();
	}

	/**
	 * a listener for radio button value changes
	 */
	public interface StateListener<T>
	{
		/**
		 * @param newValue the new value of the component
		 */
		void stateChanged (T newValue);
	}

	/**
	 * @param listener new value of listener
	 */
	public void setListener
	(StateListener<T> listener) { this.listener = listener; }
	private StateListener<T> listener = null;

	/* (non-Javadoc)
	 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
	 */
	public void stateChanged (ChangeEvent event)
	{ if (listener != null) listener.stateChanged (evaluator.getCurrentState ()); }
	private MonitorEvaluation<T> evaluator;

	/**
	 * @param evaluator an interface to the value of the component
	 */
	public StateMonitor (MonitorEvaluation<T> evaluator) { this.evaluator = evaluator; }

}
