
package net.myorb.data.abstractions;

import java.util.ArrayList;
import java.util.List;

/**
 * list object for use in collection of data points
 * @param <T> type of component values on which operations are to be executed
 * @author Michael Druckman
 */
public class DataSequence<T> extends ArrayList<T>
{

	/**
	 * data from list becomes sequence
	 * @param items ordered data to be placed in sequence
	 * @return the new sequence object
	 * @param <T> sequence data type
	 */
	public static <T> DataSequence<T> fromList (List<T> items)
	{
		DataSequence<T> ds = new DataSequence<T>();
		ds.addAll (items);
		return ds;
	}

	/**
	 * construct evenly spaced sequence
	 * @param lo the lo end of the sequence
	 * @param inc the space between points
	 * @param count number of elements
	 * @param mgr the data type manager
	 * @param <T> sequence data type
	 * @return the sequence
	 */
	public static <T> DataSequence<T> evenlySpaced
	(T lo, T inc, int count, SpaceDescription<T> mgr)
	{
		T x = lo;
		DataSequence<T> ds = new DataSequence<T>(); ds.setSpaceManager (mgr);
		for (int i = count; i > 0; i--) { ds.add (x); x = mgr.add (x, inc); }
		return ds;
	}

	/**
	 * construct evenly spaced sequence
	 * @param lo the lo end of the sequence
	 * @param hi the hi end of the sequence
	 * @param inc the space between points
	 * @param mgr the data type manager
	 * @param <T> sequence data type
	 * @return the sequence
	 */
	public static <T> DataSequence<T> evenlySpaced
		(T lo, T hi, T inc, SpaceDescription<T> mgr)
	{
		T x = lo;
		DataSequence<T> ds = new DataSequence<T>(); ds.setSpaceManager (mgr);
		T threshold = mgr.add (hi, mgr.multiply (inc, mgr.invert (mgr.newScalar (2))));
		while (!mgr.lessThan (threshold, x)) { ds.add (x); x = mgr.add (x, inc); }
		return ds;
	}

	/**
	 * sequence for 0..count
	 * @param count items in sequence
	 * @param mgr the data type manager
	 * @param <T> sequence data type
	 * @return the sequence
	 */
	public static <T> DataSequence<T> timeSeriesFor (int count, SpaceDescription<T> mgr)
	{
		return evenlySpaced (mgr.getZero (), mgr.newScalar (count), mgr.getOne (), mgr);
	}

	/**
	 * collect data points along a homogeneous 
	 *  spaced sequence as described by a supplied equation
	 * @param equation the equation to use to generate the data points
	 * @param from starting value of the domain to build sequence on
	 * @param to ending point of the domain sequence
	 * @param by the increment between points
	 * @param <T> sequence data type
	 * @return THIS sequence
	 */
	public static <T> DataSequence<T> collectDataFor
		(Function<T> equation, T from, T to, T by)
	{
		SpaceDescription<T> mgr;
		DataSequence<T> ds = new DataSequence<T>();
		ds.setSpaceManager (mgr = equation.getSpaceDescription ());
		for (T t : evenlySpaced (from, to, by, mgr))
		{ ds.add (equation.eval (t)); }
		return ds;
	}

	/**
	 * compute function values over a domain sequence
	 * @param equation the equation to use to generate the data points
	 * @param over the domain for the sequence
	 * @param <T> sequence data type
	 * @return THIS sequence
	 */
	public static <T> DataSequence<T> collectDataFor
		(Function<T> equation, DataSequence<T> over)
	{
		DataSequence<T> ds;
		(ds = new DataSequence<T>()).setSpaceManager (equation);
		for (T x : over) ds.add (equation.eval (x));
		return ds;
	}

	/**
	 * @return the front half of the sequence
	 */
	public DataSequence<T> halfSequence ()
	{
		DataSequence<T> ds = new DataSequence<T> ();
		ds.addAll (this.subList (0, this.size () / 2));
		ds.setSpaceManager (this.spaceManager);
		return ds;
	}

	public void setSpaceManager (Function<T> f) { this.spaceManager = f.getSpaceDescription (); }
	public void setSpaceManager (SpaceDescription<T> spaceManager) { this.spaceManager = spaceManager; }
	public SpaceDescription<T> getSpaceManager () { return this.spaceManager; }	
	SpaceDescription<T> spaceManager;

	private static final long serialVersionUID = -8149652164295181061L;
}
