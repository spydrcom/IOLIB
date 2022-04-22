
package net.myorb.data.abstractions;

import java.math.BigDecimal;

/**
 * description and processing for binary values
 * @author Michael Druckman
 */
public class BinaryData
{

	/*
	 * elements and byte size lookup table for elements
	 */

	public static enum Element {SBYTE, UBYTE, SSHORT, USHORT, SINT, UINT, SLONG, ULONG};
	public static final Byte[] ALIGNMENT = new Byte[]{1, 1, 2, 2, 4, 4, 8, 8};


	/**
	 * @param element the type of an element
	 * @return the byte size of the element
	 */
	public static int alignmentFor (Element element)
	{
		return ALIGNMENT[element.ordinal ()];
	}


	/**
	 * move implied decimal point to left of bit 0
	 * @param value the full set of bits with implied decimal point to right of bit 0
	 * @param mantissaBits the number of bits to move decimal point left
	 * @return the scaled value boxed as (BigDecimal) Number
	 */
	public static Number scaleFixed (Number value, int mantissaBits)
	{
		boolean negative;
		long longValue = value.longValue ();
		if (negative = longValue < 0) longValue = -longValue;
		BigDecimal fixed = new BigDecimal (longValue).divide (TWO.pow (mantissaBits));
		return negative ? fixed.negate () : fixed;
	}
	static BigDecimal TWO = new BigDecimal (2);


	/**
	 * mask the low half of a long (64 bit) integer
	 * @param value a number to be masked to unsigned integer
	 * @return a (long) Number holding a 32 bit unsigned value
	 */
	public static Number maskUnsignedInteger (Number value)
	{
		return value.longValue () & 0xFFFFFFFF;
	}


	/**
	 * @param left one of elements to compare
	 * @param right other of elements to compare
	 * @param as the type to use for compare
	 * @return TRUE : values are same
	 */
	public static boolean equivalent (Number left, Number right, Element as)
	{
		switch (as)
		{
			case SBYTE:						return left.byteValue  () == right.byteValue  ();
			case SSHORT:	case UBYTE:		return left.shortValue () == right.shortValue ();
			case SINT:		case USHORT:	return left.intValue   () == right.intValue   ();
			case SLONG:		case UINT:		return left.longValue  () == right.longValue  ();
			default:						return false;
		}
	}


}

