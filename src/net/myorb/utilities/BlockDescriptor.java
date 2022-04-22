
package net.myorb.utilities;

/**
 * block descriptor with check sum logic
 * @author Michael Druckman
 */
public class BlockDescriptor extends BlockManager
{


	public static final String OFFSET = "OFFSET", LENGTH = "LENGTH", CHECKSUM = "CHECKSUM";


	public BlockDescriptor (String[] properies, AccessControl.Element[] elements)
	{
		super (properies, elements);
	}


	public BlockDescriptor (BlockDefinition definition)
	{
		super (definition);
	}


	/*
	 * core properties
	 */


	public Number getCheckSumElement ()
	{
		return propertyMap.get (CHECKSUM);
	}

	public Number getOffsetElement ()
	{
		return propertyMap.get (OFFSET);
	}

	public Number getLengthElement ()
	{
		return propertyMap.get (LENGTH);
	}


	/*
	 * descriptor block check sum
	 */


	/**
	 * @param access file access to read with
	 * @param alignedOn alignment element
	 * @throws Exception for any errors
	 */
	public void verifyCheckSum
	(AccessControl access, AccessControl.Element alignedOn)
	throws Exception
	{
		Number computedCheckSum = computeCheckSum (access, alignedOn);
		if (!AccessControl.equivalent (getCheckSumElement (), computedCheckSum, alignedOn))
		{ throw new ChecksumError (this); }
	}


	/**
	 * exception carrying descriptor for check-sum failure
	 */
	public static class ChecksumError extends Exception
	{
		BlockDescriptor descriptor;
		public ChecksumError (BlockDescriptor descriptor)
		{
			super ("Check sum could not be verified, block " + descriptor.getBlockName ());
			this.descriptor = descriptor;
		}
		private static final long serialVersionUID = -2291033047018942302L;
	}


	/**
	 * @param access file access to read with
	 * @param alignedOn alignment element unit and size
	 * @return the computed value of the table checksum
	 * @throws Exception for any errors
	 */
	public Number computeCheckSum (AccessControl access, AccessControl.Element alignedOn) throws Exception
	{
		CheckSumProcessor csr = new CheckSumProcessor (alignedOn, getLengthElement ().longValue ());
		access.readBlock (csr, getOffsetElement ().longValue ());
		return csr.getComputedCheckSum ();
	}


}


class CheckSumProcessor implements AccessControl.BlockReader
{

	CheckSumProcessor (AccessControl.Element alignedOn, long length)
	{
		long align = AccessControl.alignmentFor (alignedOn);
		this.nelements = (length + align - 1) / align;
		this.alignedOn = alignedOn;
	}
	AccessControl.Element alignedOn;
	long nelements;

	/* (non-Javadoc)
	 * @see net.myorb.utilities.AccessControl.BlockReader#setLocation(java.lang.Number)
	 */
	public void setLocation (Number location) {}

	/* (non-Javadoc)
	 * @see net.myorb.utilities.AccessControl.BlockReader#readBlock(net.myorb.utilities.AccessControl.Access)
	 */
	public void readBlock (AccessControl.Access access) throws Exception
	{
		while (nelements-- > 0) computedCheckSum += access.read (alignedOn).longValue ();
	}
	public long getComputedCheckSum () { return computedCheckSum; }
	long computedCheckSum = 0;

}

