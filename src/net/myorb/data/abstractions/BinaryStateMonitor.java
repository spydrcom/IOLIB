
package net.myorb.data.abstractions;

/**
 * state monitor for binary value types (boolean).
 * @author Michael Druckman
 */
public class BinaryStateMonitor extends StateMonitor<Boolean>
{

	/**
	 * state listener for boolean values
	 */
	public interface BinaryStateListener extends StateListener<Boolean> {}

	/**
	 * value query portion of monitor for boolean values
	 */
	public interface BinaryMonitorEvaluation extends MonitorEvaluation<Boolean> {}

	/**
	 * access to state monitor for boolean values (retrieve the monitor object itself)
	 */
	public interface BinaryMonitoredComponent extends MonitoredComponent<Boolean> {}

	/**
	 * @param evaluator interface that gives access to current value of object
	 */
	public BinaryStateMonitor (BinaryMonitorEvaluation evaluator) { super (evaluator); }

}

