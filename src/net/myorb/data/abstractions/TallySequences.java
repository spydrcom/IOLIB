
package net.myorb.data.abstractions;

import java.util.ArrayList;

/**
 * keep count of sequence lengths
 * @author Michael Druckman
 */
public class TallySequences extends ArrayList <Integer>
{

	public TallySequences () {}

	/**
	 * @param initialValues items to place in list
	 */
	public TallySequences (int[] initialValues) { add (initialValues); }

	/**
	 * @param maximum largest sequence to follow
	 */
	public TallySequences (int maximum)
	{
		this.maximum = maximum; zeroOut ();
	}
	private static final long serialVersionUID = 2066283193120007934L;

	/**
	 * start with fresh new list
	 */
	public void zeroOut ()
	{
		this.clear (); for (int i = 0; i <= maximum; i++) add (0);
	}
	int maximum;

	/**
	 * @param items array of Integer objects
	 */
	public void add (Integer[] items)
	{
		SimpleUtilities.add (items, this);
	}

	/**
	 * @param items array of Number objects
	 */
	public void add (Number[] items)
	{
		add (Numerics.toIntegers (items));
	}

	/**
	 * @param items array of int values to be added
	 */
	public void add (int[] items)
	{
		add (Numerics.toNumbers (items));
	}

	/**
	 * sequence has ended, add to tally
	 */
	public void endSequence ()
	{
		if (sequenceLength >= this.size ())
		{
			sequencesExceedingMaximumLength++;
		}
		else
		{
			tallyFor (sequenceLength);
		}
		sequenceLength = 0;
	}

	/**
	 * @param item the value to be marked
	 */
	public void tallyFor (int item)
	{
		this.set (item, this.get (item) + 1);
	}

	/**
	 * @return number of sequences larger than maximum
	 */
	public int getSequencesExceedingMaximumLength ()
	{ return sequencesExceedingMaximumLength; }
	int sequencesExceedingMaximumLength = 0;

	/**
	 * add excess count item to end of list
	 */
	public void endTally ()
	{
		this.add (sequencesExceedingMaximumLength);
	}

	/**
	 * @return sum of all items
	 */
	public int getSum () { return Numerics.sum (this); }

	/**
	 * increment the sequence length
	 */
	public void increment () { sequenceLength++; }
	int sequenceLength = 0;

	/**
	 * @return tally object with per-kilo per item
	 */
	public TallySequences toPerKilo ()
	{
		long sum = getSum ();
		TallySequences perKilo = new TallySequences ();
		for (int i = 0; i < this.size (); i++)
		{
			long item = this.get (i);
			long itemPerKilo = KILO * item / sum;
			perKilo.add ((int) itemPerKilo);
		}
		return perKilo;
	}
	public static final long KILO = 1000;

	/**
	 * @param ideals the list of ideal values
	 * @return the difference between THIS and ideals
	 */
	public TallySequences relativeTo (TallySequences ideals)
	{
		TallySequences differences = new TallySequences ();
		for (int i = 0; i < this.size (); i++)
		{
			differences.add (this.get (i) - ideals.get (i));
		}
		return differences;
	}

	/**
	 * @return display with trailing zero item(s) removed
	 */
	public String trimTrailingZeros ()
	{
		TallySequences trimmed;
		(trimmed = new TallySequences ()).addAll (this);
		return trimTrailingZeros (trimmed);
	}

	/**
	 * @param trimmed list to be trimmed
	 * @return the trim list display
	 */
	public static String trimTrailingZeros
			(TallySequences trimmed)
	{
		int pos = trimmed.size () - 1;
		while (pos > 0 && trimmed.get (pos) == 0)
		{   trimmed.remove   (pos--);   }
		return trimmed.toString ();
	}

	/**
	 * @return a formatted display
	 */
	public String formatted ()
	{
		if (sequencesExceedingMaximumLength > 0)
		{ return trimTrailingZeros () + " + " + sequencesExceedingMaximumLength; }
		return trimTrailingZeros ();
	}

}
