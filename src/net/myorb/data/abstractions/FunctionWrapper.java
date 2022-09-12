
package net.myorb.data.abstractions;

/**
 * simple lambda expression declaration wrapper for functions
 * @param <T> data type for functions
 * @author Michael Druckman
 */
public class FunctionWrapper <T> implements Function <T>
{

	/**
	 * a lambda expression enabled form of function body declaration
	 * @param <T> data type for functions
	 */
	public interface F <T>
	{
		/**
		 * @param x parameter to the function
		 * @return computed result
		 */
		T body (T x);
	}

	public FunctionWrapper
	(F <T> f, SpaceDescription <T> s)
	{ this.f = f; this.s = s; }

	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.ManagedSpace#getSpaceDescription()
	 */
	@Override
	public SpaceDescription <T>
	getSpaceDescription () { return s; }
	protected SpaceDescription <T> s;

	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.Function#eval(java.lang.Object)
	 */
	@Override
	public T eval (T x)
	{ return f.body (x); }
	protected F <T> f;

}
