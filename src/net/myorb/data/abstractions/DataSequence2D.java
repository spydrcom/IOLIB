
package net.myorb.data.abstractions;

import java.util.List;

/**
 * list object for use in collection of 2-dimensional data points
 * @param <T> type of component values on which operations are to be executed
 * @author Michael Druckman
 */
public class DataSequence2D<T>
{


	/**
	 * represent sequences of data point in 2D
	 * @param <DataType> type of data used
	 */
	public interface PointCollector<DataType>
	{
		/**
		 * 
		 * add a 2D data point to collection
		 * @param x the x-axis coordinate
		 * @param y the y-axis coordinate
		 */
		void addPoint (DataType x, DataType y);
	}


	/**
	 * X and Y axis data are collected in parallel array objects
	 */
	public DataSequence2D ()
	{ this (new DataSequence<T> (), new DataSequence<T> ()); }
	public DataSequence2D (DataSequence<T> xAxis, DataSequence<T> yAxis)
	{ this.xAxis = xAxis; this.yAxis = yAxis; setSpaceManager (yAxis.spaceManager); }
	public DataSequence<T> xAxis, yAxis;


	/*
	 * connect space manager
	 */
	public void setSpaceManager (Function<T> f) { this.spaceManager = f.getSpaceDescription (); }
	public void setSpaceManager (SpaceDescription<T> spaceManager) { this.spaceManager = spaceManager; }
	public SpaceDescription<T> getSpaceManager () { return this.spaceManager; }	
	SpaceDescription<T> spaceManager;


	/**
	 * implement range limit on points
	 * @param value the limit to impose on range values
	 * @return value to use, 0 for out of range
	 */
	public T checkLimit (T value)
	{
		T v = value;
		if (spaceManager == null || rangeLimit == null) return value;
		if (spaceManager.isNegative (value)) v = spaceManager.negate (v);
		return spaceManager.lessThan (rangeLimit, v)? spaceManager.getZero(): value;
	}
	public void setRangeLimit (T rangeLimit)
	{ this.rangeLimit = rangeLimit; }
	T rangeLimit = null;


	/**
	 * verify axis vectors are same length
	 * @return mutual length of x-axis and y-axis vectors
	 * @throws RuntimeException for vector length mismatch
	 */
	public int verifyDataSet () throws RuntimeException
	{
		int xsize = xAxis.size (), ysize = yAxis.size ();
		if (xsize != ysize) throw new RuntimeException (SIZES + ", " + xsize + " != " + ysize);
		return xsize;
	}
	static final String SIZES = "Inconsistant data set, vector length mismatch";


	/**
	 * populate a data sequence
	 * @param x the list of values of X
	 * @param y the list of values of Y
	 */
	public void populate (List<T> x, List<T> y)
	{
		xAxis.addAll (x); yAxis.addAll (y);
	}


	/**
	 * collect data points along a homogeneous 
	 *  spaced sequence as described by a supplied equation
	 * @param equation the equation to use to generate the data points
	 * @param from starting value of the domain to build sequence on
	 * @param to ending point of the domain sequence
	 * @param by the increment between points
	 * @param <T> sequence data type
	 * @return THIS data set
	 */
	public static <T> DataSequence2D<T> collectDataFor
		(Function<T> equation, T from, T to, T by)
	{
		DataSequence2D<T> ds = new DataSequence2D<T>();
		ds.xAxis.clear (); ds.yAxis.clear (); T x = from;
		ds.setSpaceManager (equation);

		while (!ds.spaceManager.lessThan (to, x))
		{
			try { ds.addSample (x, equation.eval (x)); }
			catch (Exception e) { ErrorHandling.checkForTermination (e); }
			x = ds.spaceManager.add (x, by);
		}

		return ds;
	}


	/**
	 * collect data points over specified domain
	 * @param equation the equation to use to generate the data points
	 * @param overDomain sequence of values making up the domain
	 * @param <T> sequence data type
	 * @return THIS data set
	 */
	public static <T> DataSequence2D<T> collectDataFor
		(Function<T> equation, DataSequence<T> overDomain)
	{
		DataSequence2D<T> ds = new DataSequence2D<T>();
		ds.xAxis.clear (); ds.yAxis.clear ();
		ds.setSpaceManager (equation);

		for (T x : overDomain)
		{
			try { ds.addSample (x, equation.eval (x)); }
			catch (Exception e) { ErrorHandling.checkForTermination (e); }
		}

		return ds;
	}


	/**
	 * add points of sequence to collection
	 * @param collector object that collects points
	 * @return THIS collector
	 */
	public PointCollector<T> addTo (PointCollector<T> collector)
	{
		int pointCount = verifyDataSet ();
		for (int i = 0; i < pointCount; i++)
		{
			T d = xAxis.get (i), r = yAxis.get (i);
			if (rangeLimit != null) r = checkLimit (r);
			collector.addPoint (d, r);
		}
		return collector;
	}


	/**
	 * a point is added to the sample set
	 * @param x the x-axis coordinate value of the sample point
	 * @param y the y-axis coordinate value of the sample point
	 */
	public void addSample (T x, T y)
	{
		xAxis.add (x); yAxis.add (y);
	}


	/**
	 * correct for float rounding error 
	 *  causing early termination of domain generation
	 * @return THIS data set
	 */
	public DataSequence2D<T> corrected ()
	{
		if (xAxis.size () > yAxis.size ())
		{ xAxis.remove (xAxis.size () - 1); }
		return this;
	}


}

