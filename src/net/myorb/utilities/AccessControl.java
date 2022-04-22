
package net.myorb.utilities;

import net.myorb.data.abstractions.BinaryData;
import net.myorb.data.abstractions.ComparableNumber;

import java.io.RandomAccessFile;
import java.io.File;

/**
 * random access to binary blocked files
 * @author Michael Druckman
 */
public class AccessControl extends BinaryData
{


	/**
	 * @param access the file pointer to source
	 * @param element the type of element for list entries
	 * @return the value read from the source boxed as Number
	 * @throws Exception for any errors
	 */
	public static Number read (Access access, Element element) throws Exception
	{
		switch (element)
		{
			case SBYTE:		return access.readByte ();
			case SSHORT:	return access.readShort ();
			case UBYTE:		return access.readUnsignedByte ();
			case USHORT:	return access.readUnsignedShort ();
			case UINT:		return readUnsignedInt (access);
			case SLONG:		return access.readLong ();
			case SINT:		return access.readInt ();
			default:		break;
		}
		throw new Exception ("Unsupported element type: " + element.toString ());
	}


	/**
	 * default Fixed value as
	 *  16 bit characteristic and 16 bit mantissa
	 * @param access the file pointer to the source
	 * @return the value boxed as (BigDecimal) Number
	 * @throws Exception for any errors
	 */
	public static Number readFixed (Access access) throws Exception
	{
		return readFixed (access, Element.SINT, 16);
	}


	/**
	 * read an element and shift the decimal point
	 * @param access the file pointer to the source being read
	 * @param sizedElement the element to be read (16, 32, 64 bits and signed/unsigned)
	 * @param mantissaBits number of bits to right of decimal point
	 * @return the value boxed as (BigDecimal) Number
	 * @throws Exception for any errors
	 */
	public static Number readFixed
	(Access access, Element sizedElement, int mantissaBits)
	throws Exception
	{
		return scaleFixed (access.read (sizedElement), mantissaBits);
	}


	/**
	 * extend to long and mask to low 32 bits
	 * @param access the file pointer to the source
	 * @return the value boxed as (Long) Number
	 * @throws Exception for any errors
	 */
	public static Number readUnsignedInt (Access access) throws Exception
	{
		return maskUnsignedInteger (access.readInt ());
	}


	/**
	 * @param access the file pointer to the source
	 * @param element the type of element for list entries
	 * @param entries the count of entries in the list
	 * @return the list read from the source
	 * @throws Exception for any errors
	 */
	public static ComparableNumber.List getElementList
	(AccessControl.Access access, AccessControl.Element element, int entries)
	throws Exception
	{
		ComparableNumber.List list = new ComparableNumber.List();
		for (int i = 0; i < entries; i++) list.add (new ComparableNumber (read (access, element)));
		return list;
	}


	/**
	 * access restricted to reading
	 */
	public static class Access extends RandomAccessFile
	{

		/**
		 * @param length number of bytes to be read
		 * @param from the location within the source to read the string
		 * @return a string read as bytes and treated as UTF-8
		 * @throws Exception for any errors
		 */
		public String readString
		(int length, long from) throws Exception
		{ this.seek (from); return readString (length); }

		/**
		 * @param encoding the encoding (UTF-8 or UTF-16) for format interpretation
		 * @param length the number of bytes in the string (or twice words for UTF-16)
		 * @param from the location within the source to read the string
		 * @return a string read from source with encoding applied
		 * @throws Exception for any errors
		 */
		public String readString
		(String encoding, int length, long from) throws Exception
		{ this.seek (from); return readString (length, encoding); }

		/**
		 * @param length number of bytes to be read
		 * @return a string read as bytes and treated as UTF-8
		 * @throws Exception for any errors
		 */
		public String readString (int length) throws Exception
		{
			byte[] bytes;
			readFully (bytes = new byte[length]);
			return new String (bytes);
		}

		/**
		 * @param length the number of bytes in the string (or twice words for UTF-16)
		 * @param encoding the encoding (UTF-8 or UTF-16) for format interpretation
		 * @return a string read from source with encoding applied
		 * @throws Exception for any errors
		 */
		public String readString (int length, String encoding) throws Exception
		{
			byte[] bytes;
			readFully (bytes = new byte[length]);
			return new String (bytes, encoding);
		}

		/**
		 * @param element a unit of bits with/without sign extension
		 * @return an element read from the source as a number
		 * @throws Exception for any errors
		 */
		public Number read (Element element) throws Exception
		{ return AccessControl.read (this, element); }

		/**
		 * @param file the source to be read from
		 * @throws Exception for any errors
		 */
		public Access (File file) throws Exception
		{ super (file, "r"); }
	}


	/**
	 * enable a class to read a block of binary data
	 */
	public interface BlockReader
	{
		/**
		 * read a block of binary data
		 * @param access the file pointer to a random access file
		 * @throws Exception for any errors
		 */
		void readBlock (Access access) throws Exception;

		/**
		 * allow block to retain original lockation
		 * @param location the address used to find the block
		 */
		void setLocation (Number location);
	}


	/**
	 * @param file the file providing the binary data source
	 */
	protected AccessControl (File file)
	{
		this.file = file;
	}
	private File file;


	/*
	 * get access to file at specified location
	 */


	/**
	 * open a random access file
	 *  and seek the requested offset
	 * @param at the offset into the file
	 * @return the file pointer to be used to read data
	 * @throws Exception for any errors
	 */
	public Access getAccess (long at) throws Exception
	{
		Access access = new Access (file);
		if (at != 0) access.seek (at);
		return access;
	}


	/**
	 * provide access to a reader
	 * @param reader the reader object that will parse the block
	 * @param from the offset into the file where the block is located
	 * @throws Exception for any errors
	 */
	public void readBlock (BlockReader reader, long from) throws Exception
	{
		Access access = null;
		try { access = getAccess (from); reader.readBlock (access); }
		finally { if (access != null) access.close (); }
	}


	/**
	 * load a new object instance
	 * @param <T> an extender of BlockReader to be read from source
	 * @param objectType the class (implementing BlockReader) of the file to be read
	 * @param location the address within the source of the object to read
	 * @return a new instance of the object
	 * @throws Exception for any errors
	 */
	public <T extends BlockReader> T readObject
		(
			Class<T> objectType, Number location
		)
	throws Exception
	{
		T newInstance;
		(newInstance = objectType.newInstance ()).setLocation (location);
		readBlock (newInstance, location.longValue ());
		return newInstance;
	}


}

