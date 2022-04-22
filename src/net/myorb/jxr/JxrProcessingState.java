
package net.myorb.jxr;

import java.util.HashMap;

import org.xml.sax.Attributes;

/**
 * the state of nested attributes and objects
 * @author Michael Druckman
 */
public class JxrProcessingState extends JxrPrimitives
{


	SymbolTable symbols = new SymbolTable ();
	ObjectStack objectStack = new ObjectStack ();
	AttributeStack attributeStack = new AttributeStack ();


	/**
	 * add a list of named symbols into a hash
	 * @param from the text list of symbol names to be added
	 * @param table a hash of symbols being built
	 */
	public void addTo (String from, SymbolTable table)
	{
		JxrSymManager.addSymbolsTo (from, symbols, table);
	}


	/**
	 * @param table the hash being appended
	 */
	public void addAllSymbolsTo (SymbolTable table)
	{
		table.putAll (symbols);
	}


	/**
	 * @return the class-path identified in attributes
	 */
	public Object getClasspathFromAttributes ()
	{
		return symbols.getClasspathFrom (attributes);
	}


	/**
	 * parameters constructed from node attributes
	 * @param table a hash of symbols being built
	 */
	public void addParametersFromAttributes (SymbolTable table)
	{
		addTo (JxrSymManager.getParameter (attributes), table);
	}


	/**
	 * add a list of named symbols to COMMON hash
	 * @param from the text list of symbol names to be added
	 */
	public void addToCommon (String from)
	{
		JxrSymManager.addSymbolsTo (from, symbols, common);
	}
	private static final SymbolTable common = new SymbolTable ();


	/**
	 * add symbols identified in attributes
	 */
	public void addSymbolsToCommon ()
	{
		addToCommon (JxrSymManager.getSymbols (attributes));
	}


	/**
	 * @param symbols parameters passed into nested layer
	 */
	public void include (HashMap<String,Object> symbols)
	{
		this.symbols.putAll (symbols); this.symbols.putAll (common);
	}


	/**
	 * push a node onto the stack
	 * @param attributes node contents to push
	 */
	public void push (Attributes attributes)
	{
		attributeStack.push (attributes); objectStack.push ();
	}


	/**
	 * pop the attributes stack
	 */
	public void popAttributes ()
	{
		attributes = attributeStack.pop ();
	}
	JxrSymManager.AttributeHash attributes;


	/**
	 * @return top element of object stack
	 */
	public ObjectList tos ()
	{
		return objectStack.tos ();
	}


	/**
	 * pop the object stack
	 */
	public void pop ()
	{
		popAttributes ();
		objects = objectStack.pop ();
		top = objectStack.isEmpty () ? null : tos ();
	}
	protected ObjectList objects, top;


	/**
	 * @return first entry on object stack
	 */
	public Object getFirstFromTOS ()
	{
		return top.first ();
	}


	/**
	 * easy access to current first entry in object list
	 * @return first entry in current object list
	 */
	public Object getFirstFromLastPopped ()
	{
		return objects.first ();
	}

	/**
	 * @return text version of last popped
	 */
	public String getLastPoppedText ()
	{
		return getFirstFromLastPopped ().toString ();
	}


	/**
	 * evaluate the parameters found in the attributes on TOS
	 */
	public void getParameterObjectsTOS ()
	{
		getParameterObjects
		(
			JxrSymManager.getParameter
					(attributeStack.tos ()),
			symbols, tos ()
		);
	}


	/**
	 * @return symbol reference located thru attribute
	 */
	public Object getReference ()
	{
		return symbols.get (JxrSymManager.getReference (attributes, symbols));
	}


	/**
	 * @return parameters identified in attributes last popped
	 */
	public ObjectList getParameterObjectsFromAttributes ()
	{
		return getParameterObjects (attributes, symbols, objects);
	}


	/**
	 * allocate hash on first reference
	 * @return the exports hash
	 */
	public SymbolTable getExports ()
	{
		if (exports == null)
		{ exports = new SymbolTable (); }
		return exports;
	}
	private SymbolTable exports = null;	

	/**
	 * @return hash of symbols chosen for export
	 */
	public SymbolTable getExportedSymbols ()
	{
		return exports!=null ? exports : SymbolTable.EMPTY;
	}


	/**
	 * verify stack is empty at end of JXR node
	 */
	public void stackCheck ()
	{
		int n;
		if ((n = attributeStack.size ()) == 0) return;
		System.out.println ("Attribute stack has not cleared, " + n + " nodes remain");
		for (int i=n-1; i>=0; i--) System.out.println (attributeStack.get (i));
	}


	/**
	 * @param last the value resulting from the node processing
	 */
	public void endOfNode (Object last)
	{
		String called =
			JxrSymManager.getAssignment (attributes);
		if (called != null) symbols.put (called, last);
	}


}
