
package net.myorb.utilities;

import net.myorb.data.abstractions.BinaryData;
import net.myorb.data.abstractions.SimpleUtilities;

import net.myorb.utilities.AccessControl.BlockReader;

import java.util.HashMap;
import java.util.ArrayList;

import java.util.Collection;

import java.util.List;
import java.util.Map;

/**
 * provide reader for list of properties given element types
 * @author Michael Druckman
 */
public class BlockManager implements BlockReader
{


	public static final BinaryData.Element
	U16		= BinaryData.Element.USHORT,
	S16		= BinaryData.Element.SSHORT,
	U8		= BinaryData.Element.UBYTE,
	S8		= BinaryData.Element.SBYTE,
	S32		= BinaryData.Element.SINT,
	U32		= BinaryData.Element.UINT,
	S64		= BinaryData.Element.SLONG;


	/**
	 * keep map of objects read from source
	 * @param <T> the type of object to be associated with cache
	 */
	public static class Cache <T extends BlockReader>
	{

		/**
		 * mechanism for locating object within source
		 */
		public interface Locator
		{
			/**
			 * @param identifier the name of the object
			 * @return the location within the source for this object
			 * @throws Exception for any errors
			 */
			Number addressFor (String identifier) throws Exception;
		}

		/**
		 * get from cache or read from source
		 * @param <C> the actual class of objects
		 * @param identifier the name of the table
		 * @param objectType the class of the table to get
		 * @param access the object source holding the block
		 * @return the table requested as a block reader
		 * @throws Exception for any errors
		 */
		@SuppressWarnings("unchecked")
		public <C extends T> C getObject
			(
				String identifier,
				Class<C> objectType,
				AccessControl access
			)
		throws Exception
		{
			if (objects.containsKey (identifier)) return (C) objects.get (identifier);
			T newInstance = access.readObject (objectType, locator.addressFor (identifier));
			addObject (identifier, newInstance);
			return (C) newInstance;
		}

		/**
		 * @param name an identifier for the object
		 * @param block a block to be associated with the identifier
		 */
		public void addObject (String name, T block) { objects.put (name, block); }
		protected Map<String,T> objects = new HashMap<String,T>();

		public Cache (Locator locator) { this.locator = locator; }
		protected Locator locator;

	}


	/**
	 * parse a counted collection of items from a source
	 * @param <T> a block manager extension that is constructed
	 * @param count the number of items expected in the collection
	 * @param type the class of items to be posted in the collection
	 * @param collection the collection object the items are allocated to
	 * @param access the source access to read the items from
	 * @param sourceBlock a parent block to copy from 
	 * @throws Exception for any errors
	 */
	public static <T extends BlockManager> void parseCollection
		(
			int count, Class<T> type, Collection<T> collection,
			AccessControl.Access access, BlockManager sourceBlock
		)
	throws Exception
	{
		T item;
		for (int n = count; n > 0; n--)
		{
			item = SimpleUtilities.allocateCollectedItem (type, collection);
			if (sourceBlock != null) item.copyPropertiesFrom (sourceBlock);
			item.readBlock (access);
		}
		if (collection instanceof MappedCollection.CollectionPosting)
		{
			((MappedCollection.CollectionPosting) collection).postAll ();
		}
	}


	/**
	 * items collected and
	 *  order of addition is preserved
	 * @param <T> type of collected items
	 */
	public static class SimleCollection<T> extends ArrayList<T>
	{ private static final long serialVersionUID = -80493633950675240L; }


	/**
	 * a tabular form of representing a table definition
	 */
	public static class BlockDefinition
	{

		/**
		 * @param name the name of the property
		 * @param type the type of the property
		 */
		public void add (String name, BinaryData.Element type)
		{ propertyNames.add (name); propertyTypes.add (type); }

		/**
		 * special case for FIXED types
		 * @param name the name of the property
		 * @param type the type of the property (U or S, 8 16 32 64 bits)
		 * @param scale number of mantissa bits
		 */
		public void add (String name, BinaryData.Element type, int scale)
		{ add (name, type); fixedScaling.put (name, scale); }

		/**
		 * @param definition source of entries to add
		 */
		public void add (BlockDefinition definition)
		{
			if (definition == null) return;
			propertyNames.addAll (definition.propertyNames);
			propertyTypes.addAll (definition.propertyTypes);
			fixedScaling.putAll (definition.fixedScaling);
		}

		/**
		 * @param definitions definitions to be concatenated
		 * @return the full set of entries
		 */
		public BlockDefinition plus (BlockDefinition... definitions)
		{
			BlockDefinition joined;
			(joined = new BlockDefinition ()).add (this);
			for (BlockDefinition definition : definitions)
			{ joined.add (definition); }
			return joined;
		}

		/**
		 * @return property names defined having fixed scaling
		 */
		public String[] getFixedPropertyNames ()
		{
			return fixedScaling.keySet ().toArray (new String[]{});
		}

		/**
		 * use BinaryData scaling algorithm to adjust implied decimal point
		 * @param toProperty name of a property defined having fixed scaling
		 * @param withValue the binary integer value read for the property
		 * @return a BigDecimal Number with the decimal point adjusted
		 */
		public Number applyScaling (String toProperty, Number withValue)
		{
			return BinaryData.scaleFixed (withValue, fixedScaling.get (toProperty));
		}

		/**
		 * @return TRUE : scaling map is not empty
		 */
		public boolean hasFixedProperties () { return ! fixedScaling.isEmpty (); }

		/**
		 * create a holding object for definition line-items
		 */
		public BlockDefinition ()
		{
			propertyNames = new ArrayList<String>();
			propertyTypes = new ArrayList<BinaryData.Element>();
			fixedScaling  = new HashMap<String,Integer>();
		}
		List<BinaryData.Element> propertyTypes;
		Map<String,Integer> fixedScaling;
		List<String> propertyNames;
	}


	/**
	 * @param blockDefinition a definition block for this object
	 */
	protected BlockManager (BlockDefinition blockDefinition)
	{
		this
		(
			blockDefinition.propertyNames.toArray (new String[]{}),
			blockDefinition.propertyTypes.toArray (new BinaryData.Element[]{})
		);
		blockDefinitionUsed = blockDefinition;
	}


	/**
	 * update fixed property values with scaling specified in block definition 
	 */
	public void applyScalingAsAppropriate ()
	{
		if (blockDefinitionUsed != null && blockDefinitionUsed.hasFixedProperties ())
		{
			for (String property : blockDefinitionUsed.getFixedPropertyNames ())
			{
				Number scaledValue =
					blockDefinitionUsed.applyScaling
						(property, propertyMap.get (property));
				propertyMap.put (property, scaledValue);
			}
		}
	}
	protected BlockDefinition blockDefinitionUsed = null;


	/**
	 * @param properies names of the properties
	 * @param elements types for each property
	 */
	protected BlockManager (String[] properies, BinaryData.Element[] elements)
	{ this.properyTags = properies; this.elements = elements; }
	protected BinaryData.Element[] elements;
	protected String[] properyTags;


	/**
	 * override for integrity check beyond check sum
	 * @throws Exception for failed integrity check
	 */
	public void verifyTableIntegrity () throws Exception {}


	/**
	 * override for additional properties beyond header block
	 * @param access the file pointer to the TTF source
	 * @throws Exception for any errors
	 */
	public void readExtendedTableProperties (AccessControl.Access access) throws Exception {}


	/* (non-Javadoc)
	 * @see net.myorb.utilities.AccessControl.BlockReader#readBlock(net.myorb.utilities.AccessControl.Access)
	 */
	public void readBlock (AccessControl.Access access) throws Exception
	{
		for (int i = 0; i < properyTags.length; i++)
		{
			propertyMap.put (properyTags[i], access.read (elements[i]));
		}
		applyScalingAsAppropriate ();
		readExtendedTableProperties (access);
		verifyTableIntegrity ();
	}
	protected HashMap<String,Number> propertyMap = new HashMap<String,Number>();


	/* (non-Javadoc)
	 * @see net.myorb.utilities.AccessControl.BlockReader#setLocation(java.lang.Number)
	 */
	public void setLocation (Number location)
	{ propertyMap.put (BLOCK_BASE_ADDRESS, location); }
	public static final String BLOCK_BASE_ADDRESS = "BLOCK_BASE_ADDRESS";


	/**
	 * @param anotherBlock source of properties to copy
	 */
	public void copyPropertiesFrom (BlockManager anotherBlock)
	{
		propertyMap.putAll (anotherBlock.propertyMap);
	}


	/**
	 * @param withTag name of the property
	 * @return value of the property
	 */
	public Number getProperty (String withTag)
	{ return propertyMap.get (withTag); }


	/**
	 * masked bit test of flag word
	 * @param flag the word containing the flag
	 * @param mask the bit mask for flag or flags (any bit set in mask is tested in flag word)
	 * @return TRUE : one, some, or all bits in mask set in flag word
	 */
	public static boolean isSet (int flag, int mask) { return (flag & mask) > 0; }


	/**
	 * @param flagWordName the name of the property containing the flag word
	 * @param mask the bit mask for flag or flags (any bit set in mask is tested in flag word)
	 * @return TRUE : one, some, or all bits in mask set in flag word
	 */
	public boolean isSet (String flagWordName, int mask)
	{
		return isSet (getProperty (flagWordName).intValue (), mask);
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString ()
	{ return "[" + blockName + "] " + propertyMap.toString (); }
	protected void setBlockName (String to) { blockName = to; }
	public String getBlockName () { return blockName; }
	protected String blockName = "????";


}

