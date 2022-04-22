
package net.myorb.gui.components;

/**
 * shopping cart abstraction implementation
 * @author Michael Druckman
 */
public class CartManager
{


	/**
	 * size of lists is extended in blocks
	 */
	static final int BLOCK = 1000;


	/**
	 * initialize cart and sections
	 */
	public void freshCart ()
	{
		cart = null; sections = null;
		cartNext = -1; section = -1;
	}
	protected Object cart[][], sections[][][];
	protected int cartNext, section;


	/**
	 * simple cart is a one block array
	 */
	public void allocateSimpleCart ()
	{
		cart = new Object[BLOCK][];
	}

	/**
	 * make a new empty cart available
	 */
	public void open ()
	{
		allocateSimpleCart ();
		cartNext = 0;
	}


	/**
	 * small cart becomes a section of a large cart
	 */
	public void addCartToSections ()
	{
		sections[section++] = cart;
	}


	/**
	 * add next block to sectional cart
	 */
	public void allocateCartSection ()
	{
		allocateSimpleCart ();
		addCartToSections ();
		cartNext = 0;
	}


	/**
	 * allocate a sectional cart
	 */
	public void needALargerCart ()
	{
		section = 0;
		sections = new Object[BLOCK][][];
		addCartToSections ();
	}


	/**
	 * @param row items to add as columns of row to cart
	 */
	public void addToCart (Object[] row)
	{
		if (cartNext == cart.length)
		{
			if (sections == null)
			{ needALargerCart (); }
			allocateCartSection ();
		}
		cart[cartNext++] = row;
	}


	/**
	 * flush a sectional cart section by section
	 * @param t sections are appended to this table
	 */
	public void flushSections (AppendableTable t)
	{
		int n =
			extendForSections (t);
		for (int s = 0; s < n; s++)
		{ t.appendMultiple (sections[s], BLOCK); }
		t.appendMultiple (cart, cartNext);
	}


	/**
	 * compute new size for table and extend
	 * @param t the table to be extended
	 * @return full sections in cart
	 */
	public int extendForSections (AppendableTable t)
	{
		int fullSections = section - 1;
		int extendingCount = fullSections * BLOCK + cartNext;
		t.extendBy (extendingCount);
		return fullSections;
	}


	/**
	 * flush a simple cart
	 * @param t the table to be appended
	 */
	public void flushCart (AppendableTable t)
	{
		int extendingCount = cartNext;
		t.extendBy (extendingCount);
		t.appendMultiple (cart, cartNext);
	}


	/**
	 * dump cart contents into table
	 * @param t the table being extended
	 */
	public void close (AppendableTable t)
	{
		if (sections == null) flushCart (t);
		else flushSections (t);
		freshCart ();
	}


}

