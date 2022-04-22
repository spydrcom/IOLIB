
package net.myorb.utilities;

/**
 * provide for generic block read
 * @author Michael Druckman
 */
public class GenericSubBlock extends BlockManager
{

	/**
	 * @param definition the block definition is required for construction
	 * @param blockName block name is required but only used for debugging displays
	 */
	public GenericSubBlock (BlockDefinition definition, String blockName)
	{
		super (definition); this.setBlockName (blockName);
	}

	/**
	 * cause the defined fields to be read from accessed source
	 * @param access the object source holding the block
	 * @return THIS object for use in chain calls
	 * @throws Exception for any errors
	 */
	public BlockManager read (AccessControl.Access access) throws Exception
	{
		readBlock (access); return this;
	}
	
	/**
	 * cause the defined fields to be read from accessed source
	 * @param access the object source holding the block
	 * @param location the location within the source
	 * @return THIS object for use in chain calls
	 * @throws Exception for any errors
	 */
	public BlockManager read (AccessControl access, Number location) throws Exception
	{
		access.readBlock (this, location.longValue ()); return this;
	}

	/**
	 * a simple method for reading a block and having the
	 *  new properties added to another block as a supplement.
	 *  this is useful for extended blocks of sub-class definitions
	 * @param block the block that the supplement will be added to
	 * @param definition the block definition of the supplement
	 * @param access the object source holding the block
	 * @throws Exception for any errors
	 */
	public static void supplement
	(BlockManager block, BlockDefinition definition, AccessControl.Access access)
	throws Exception
	{
		block.copyPropertiesFrom (new GenericSubBlock (definition, "SUPL").read (access));
	}

	/**
	 * the same concept as other supplement, but this reads from specified location
	 * @param block the block that the supplement will be added to
	 * @param definition the block definition of the supplement
	 * @param access the object source holding the block
	 * @param location the location within the source
	 * @throws Exception for any errors
	 */
	public static void supplement
	(BlockManager block, BlockDefinition definition, AccessControl access, Number location)
	throws Exception
	{
		block.copyPropertiesFrom (new GenericSubBlock (definition, "SUPL").read (access, location));
	}

}

