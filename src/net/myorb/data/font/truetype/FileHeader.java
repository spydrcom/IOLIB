
package net.myorb.data.font.truetype;

import net.myorb.data.font.TTF;

import net.myorb.utilities.AccessControl;
import net.myorb.utilities.AccessControl.BlockReader;
import net.myorb.utilities.MappedCollection;
import net.myorb.utilities.BlockManager;

/**
 * definition of file header
 *  including table descriptors
 * @author Michael Druckman
 */
public class FileHeader extends BlockManager
	implements BlockManager.Cache.Locator
{


//	Type	Name			Description
//	Fixed	sfnt version	0x00010000 for version 1.0.
//	USHORT 	numTables		Number of tables. 
//	USHORT 	searchRange		(Maximum power of 2  numTables) x 16.
//	USHORT 	entrySelector	Log2(maximum power of 2  numTables).
//	USHORT 	rangeShift		NumTables x 16-searchRange.


	public static final String
	scalarType		= "scalarType",
	numTables		= "numTables",
	searchRange		= "searchRange",
	entrySelector	= "entrySelector",
	rangeShift		= "rangeShift";

	public static final BlockDefinition DEFINITION = new BlockDefinition ();

	static
	{
		DEFINITION.add (	scalarType,			S32,	16		);		// Fixed 16.16
		DEFINITION.add (	numTables, 			U16				);
		DEFINITION.add (	searchRange, 		U16				);
		DEFINITION.add (	entrySelector,		U16				);
		DEFINITION.add (	rangeShift,			U16				);
	}

	/**
	 * describe table properties
	 */
	public FileHeader ()
	{
		super (DEFINITION);
		objectCache = new BlockManager.Cache<BlockReader> (this);
		descriptorCollection = MappedCollection.newInstance (new OffsetTableDescriptor[]{});
	}


	/* (non-Javadoc)
	 * @see net.myorb.utilities.BlockManager#readExtendedTableProperties(net.myorb.utilities.AccessControl.Access)
	 */
	public void readExtendedTableProperties (AccessControl.Access access) throws Exception
	{
		parseCollection
		(
			getProperty (numTables).intValue (),
			OffsetTableDescriptor.class, descriptorCollection,
			access, null
		);
	}
	protected MappedCollection<OffsetTableDescriptor> descriptorCollection;


	/*
	 * find descriptor in collection
	 */


	/**
	 * find descriptor by name
	 * @param tag name of the descriptor
	 * @return the descriptor found from the name
	 * @throws Exception for any errors
	 */
	public OffsetTableDescriptor find (String tag) throws Exception
	{
		return descriptorCollection.find (tag);
	}


	/*
	 * check sum
	 */


	/**
	 * @param ttf the TTF object requesting the verify
	 * @throws Exception for any errors
	 */
	public void verifyChecksums (TTF ttf) throws Exception
	{
		for (OffsetTableDescriptor descriptor : descriptorCollection)
		{
			descriptorCollection.lookup (descriptor.getTag ()).verifyCheckSum (ttf);
		}
	}


	/*
	 * manage creation of tables
	 */


	/**
	 * @param <T> class of table
	 * @param tag the name of the table
	 * @param objectType the class of the table to get
	 * @param access the access to the source stream
	 * @return the table requested cast to T
	 * @throws Exception for any errors
	 */
	public <T extends BlockReader> T getTable
	(String tag, Class<T> objectType, AccessControl access)
	throws Exception
	{
		return objectCache.getObject (tag, objectType, access);
	}
	protected BlockManager.Cache<BlockReader> objectCache;


	/* (non-Javadoc)
	 * @see net.myorb.utilities.BlockManager.Cache.Locator#addressFor(java.lang.String)
	 */
	public Number addressFor (String identifier) throws Exception
	{
		return descriptorCollection.find (identifier).getOffset ();
	}


	/**
	 * add table to cache
	 * @param identifier name within cache
	 * @param object the object to be posted
	 */
	public void addTable (String identifier, BlockReader object)
	{
		objectCache.addObject (identifier, object);
	}


	/* (non-Javadoc)
	 * @see net.myorb.utilities.AccessControl.BlockReader#setLocation(java.lang.Number)
	 */
	public void setLocation (Number location) {}


}

