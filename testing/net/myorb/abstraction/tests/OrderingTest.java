
package net.myorb.abstraction.tests;

import net.myorb.utilities.*;
import net.myorb.utilities.Ordering.Element;

import java.util.*;

public class OrderingTest {

	enum T {A, B, C, D, E, F, G}

	static class C implements Ordering.Element
	{

		C (T t, int n) { this.t = t; this.n = n; }
		T t; int n;

		public Number relativeTo(Element other) {
			C c = (C) other; return this.t.ordinal() - c.t.ordinal();
		}

		public String toString ()
		{
			return t.toString() + Integer.toString(n);
		}

	}

	static class X extends C
	{
		X (T t, int n) { super (t, n); }
		public Number relativeTo(Element other) {
			C c = (C) other; return this.n - c.n;
		}
	}

	public static void main (String[] args) throws Exception
	{
		int n = 1;
		List<C> c = new ArrayList<C>(); List<X> x = new ArrayList<X>();
		List<T> orig = Arrays.asList (T.B, T.D, T.F, T.A, T.D, T.G, T.E, T.C, T.G, T.F);
		for (T e : orig) { c.add (new C (e, n)); x.add (new X (e, n++)); }
		System.out.println (Ordering.aSortedList (c));
		System.out.println (Ordering.aSortedList (x));
	}

}
