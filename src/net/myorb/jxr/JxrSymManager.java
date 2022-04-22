
package net.myorb.jxr;

import org.xml.sax.Attributes;

import java.util.HashMap;
import java.util.HashSet;

/**
 * manager for JXR keyword symbols
 * @author Michael Druckman
 */
public class JxrSymManager
{


	/**
	 * mapping of name to any kind of object
	 */
	public static class SymbolHash extends HashMap<String,Object>
	{

		/**
		 * @param named the name of the attribute holding the name of the object
		 * @param from the hash of attributes that holds the name of the object
		 * @return the object named in the attribute
		 */
		public Object getObject (String named, AttributeHash from)
		{
			if ( ! from.containsKey (named) ) return null;
			return this.get (from.get (named));
		}

		/**
		 * @param named the name of the attribute holding the name of the object
		 * @param from the hash of attributes that holds the name of the object
		 * @return the text of the object named in the attribute
		 */
		public String getText (String named, AttributeHash from)
		{
			Object o = getObject (named, from);
			if (o == null) return null;
			return o.toString ();
		}

		/**
		 * @param from the hash of attributes from the nodes
		 * @return the object referred to by the name in the reference
		 */
		public Object getReference (AttributeHash from)
		{
			return this.get (JxrSymManager.getReference (from, this));
		}

		/**
		 * combine attributes to build implied class-path
		 * @param attributes the attribute object from the XML node
		 * @return the text of the class-path
		 */
		public String getClasspathFrom (AttributeHash attributes)
		{
			return getClasspathUsing (this, attributes);
		}

		/**
		 * add a list of named symbols into a hash
		 * @param from the text list of symbol names to be added
		 * @param table a hash of symbols being built
		 */
		public void addTo (String from, SymbolHash table)
		{
			addSymbolsTo (from, this, table);
		}

		/**
		 * a hash of symbols named in USING attribute
		 * @param from the hash of attributes from the node
		 * @return a new hash or NULL for no symbols
		 */
		public JxrSymManager.SymbolHash getHashFor (AttributeHash from)
		{
			return JxrSymManager.getHashFor (from, this);
		}

		/**
		 * a hash of symbols named in USING attribute
		 * @param from the hash of attributes from the node
		 * @return a new hash with USING and REFERENCE copies
		 */
		public SymbolHash copyHashFor (AttributeHash from)
		{
			return JxrSymManager.copyHashFor (from, this);
		}

		private static final long serialVersionUID = -8429804711180425529L;

	}


	/**
	 * a hash for mapping attribute names to values
	 */
	public static class AttributeHash extends HashMap<String,String>
	{
		public AttributeHash (Attributes attributes)
		{
			for (int i = 0; i < attributes.getLength (); i++)
			{ put (attributes.getQName (i), attributes.getValue (i)); }
		}
		private static final long serialVersionUID = -8420325346014079989L;
	}


	/**
	 * parse a space-delimited list of names
	 * @param symbols the name of symbols separated by spaces
	 * @return the symbols names as a string array
	 */
	public static String[] spaceDelimited (String symbols)
	{
		return symbols.split (" ");
	}


	/**
	 * @param attributes hash of node attributes
	 * @return the text of the USING attribute
	 */
	public static String getUsing (AttributeHash attributes)
	{
		return attributes.get (USING);
	}
	static final String USING = "using";


	/**
	 * @param attributes hash of node attributes
	 * @return the text of the SYMBOLS attribute
	 */
	public static String getSymbols (AttributeHash attributes)
	{
		return attributes.get (SYMBOLS);
	}
	static final String SYMBOLS = "symbols";


	/**
	 * @param attributes hash of node attributes
	 * @return the text of the NAMED attribute
	 */
	public static String getNamed (AttributeHash attributes)
	{
		return attributes.get (NAMED);
	}
	static final String NAMED = "named";


	/**
	 * @param names the name of symbols to add to hash
	 * @param symbols a symbol table object for name-value lookup
	 * @param hash table to be appended with named symbols
	 */
	public static void addSymbolsTo
		(
			String names,
			SymbolHash symbols,
			SymbolHash hash
		)
	{
		for (String item : spaceDelimited (names))
		{ hash.put (item, symbols.get (item)); }
	}


	/**
	 * identify an object that will generate symbols
	 */
	public interface SymReader
	{
		/**
		 * @param parameters a hash of symbols used by reader
		 * @throws Exception for errors in symbol generation
		 */
		public void copyReadSymbols
		(
			SymbolHash parameters
		)
		throws Exception;
	}


	/**
	 * perform macro expansion for included symbols
	 * @param attributes the hash of attributes from the node
	 * @param symbols a symbol table object for name-value lookup
	 * @param reader an object implementing the symbol copier
	 * @throws Exception for errors in macro expansion
	 */
	public static void expandMacroEnumeration
		(
			AttributeHash attributes,
			SymbolHash symbols,
			SymReader reader
		)
	throws Exception
	{
		String
			redirection = getReference (attributes),
			including = getInclusionList (attributes, symbols);
		HashSet<String> excluding = getExclusionList (attributes);
		SymbolHash parameters = symbols.copyHashFor (attributes);
		addSymbolsTo (including, symbols, parameters);

		expandMacroEnumeration
		(
			spaceDelimited (including), excluding,
			parameters, redirection,
			reader
		);
	}


	/**
	 * @param enumeratedList a list of symbol names that are to be included
	 * @param excluding a hash of symbol names that are to be excluded
	 * @param parameters a hash of symbols to be passed to macro script
	 * @param redirection the symbol name expected by the macro
	 * @param reader an object implementing the symbol copier
	 * @throws Exception for errors in macro expansion
	 */
	public static void expandMacroEnumeration
		(
			String[] enumeratedList,
			HashSet<String> excluding,
			SymbolHash parameters,
			String redirection,
			SymReader reader
		)
	throws Exception
	{
		for (String item : enumeratedList)
		{
			if ( ! excluding.contains (item) )
			{
				parameters.put (redirection, item);
				addToHash (parameters, redirection, item);
				reader.copyReadSymbols (parameters);
			}
		}
	}


	/**
	 * @param attributes the hash of attributes from the node
	 * @return the list of items to be excluded from macro expansion
	 */
	public static HashSet<String> getExclusionList
	(AttributeHash attributes)
	{
		HashSet<String> excluded = new HashSet<String> ();
		if (attributes.containsKey (EXCLUDING))
		{
			String list = attributes.get (EXCLUDING);
			for (String item : spaceDelimited (list))
			{ excluded.add (item); }
		}
		return excluded;
	}


	/**
	 * @param attributes the hash of attributes from the node
	 * @param symbols a symbol table object for name-value lookup
	 * @return the list of items to be included in macro expansion
	 */
	public static String getInclusionList
	(AttributeHash attributes, SymbolHash symbols)
	{
		String listName;
		if ((listName = attributes.get (INCLUSION_LIST)) != null)
		{ return symbols.get (listName).toString (); }
		else { return attributes.get (INCLUDING); }
	}


	/**
	 * save the list specified in a POPULATE to be used in EXPAND
	 * @param attributes the hash of attributes from the node
	 * @return the name of the specified list
	 */
	public static String
	getListName (AttributeHash attributes)
	{ return attributes.get (SAVE_LIST_AS); }

	static final String INCLUDING = "including",
	INCLUSION_LIST = "inclusionList",
	SAVE_LIST_AS = "saveListAs",
	EXCLUDING = "excluding";


	/**
	 * hash symbols to be passed to nested script
	 * @param names the names of symbols to share with child
	 * @param symbols a symbol table object for name-value lookup
	 * @return the hash of shared symbols
	 */
	public static SymbolHash getSymbolsFor
	(String names, SymbolHash symbols)
	{
		if (names == null) return null;
		SymbolHash passing = new SymbolHash ();
		for (String n : spaceDelimited (names))
		{ passing.put (n, symbols.get (n)); }
		return passing;
	}


	/**
	 * a hash of symbols named in USING attribute
	 * @param attributes the hash of attributes from the node
	 * @param symbols a symbol table object for name-value lookup
	 * @return a new hash or NULL for no symbols
	 */
	public static SymbolHash getHashFor
	(AttributeHash attributes, SymbolHash symbols)
	{ return getSymbolsFor (getUsing (attributes), symbols); }


	/**
	 * @param names the symbol names to be included in the share
	 * @param symbols a symbol table object for name-value lookup
	 * @return hash of named symbols
	 */
	public static SymbolHash getHashFor (String names, SymbolHash symbols)
	{
		if (names == null)
			return new SymbolHash();
		else return getSymbolsFor (names, symbols);
	}


	/**
	 * prepare content to be passed to a macro
	 * @param attributes the hash of attributes from the node
	 * @param symbols a symbol table object for name-value lookup
	 * @param names the symbol names to be included in the share
	 * @return the hash of symbols to be shared
	 */
	public static SymbolHash getSymbolsFor
		(
			AttributeHash attributes,
			SymbolHash symbols,
			String names
		)
	{
		SymbolHash
		passing = getHashFor (names, symbols);
		if (attributes.containsKey (REFERENCES))
		{ passing.put (BEAN, symbols.get (attributes.get (REFERENCES))); }
		passing.put (ATTR, attributes);
		return passing;
	}
	static final String BEAN = "$BEAN$", REFERENCES = "references";


	/**
	 * @param attributes the hash of attributes from the node
	 * @param symbols a symbol table object for name-value lookup
	 * @return a new hash with USING and REFERENCE copies
	 */
	public static SymbolHash copyHashFor
	(AttributeHash attributes, SymbolHash symbols)
	{ return getSymbolsFor (attributes, symbols, getUsing (attributes)); }


	/**
	 * @param from attributes from XML nodes
	 * @param symbols the symbol table used to resolve indirect references
	 * @return the attribute name found from the aliases list
	 */
	public static String getReference (AttributeHash from, SymbolHash symbols)
	{
		if (from.containsKey (INDIRECT_REFERENCE))
		{
			return symbols.getText (INDIRECT_REFERENCE, from);
		}
		else if (from.containsKey (PARAMETER_REFERENCE))
		{
			AttributeHash parameters = getAttributeHash (symbols);
			return parameters.get (from.get (PARAMETER_REFERENCE));
		}
		else return findAttribute (from, REFERENCE_ALIASES);
	}


	/**
	 * @param from the symbol table used to resolve indirect references
	 * @return the AttributeHash called ATTR in the symbol table
	 */
	public static AttributeHash getAttributeHash (SymbolHash from)
	{
		return (AttributeHash) from.get (ATTR);
	}
	static final String ATTR = "$ATTRS$";


	/**
	 * @param from the table holding the hash
	 * @param name the name to give to the new symbol
	 * @param item the text of the content to add to the symbol
	 */
	public static void addToHash (SymbolHash from, String name, String item)
	{
		getAttributeHash (from).put (name, item);
	}


	/**
	 * @param from the hash of attributes from the node
	 * @return the value of the INDIRECT_REFERENCE attribute
	 */
	public static String getReference (AttributeHash from)
	{
		return from.get (INDIRECT_REFERENCE);
	}


	/**
	 * decode PACKAGE and NAME as class-path
	 * combine attributes to build implied class-path
	 * @param symbols the symbol table to use to resolve the package reference
	 * @param attributes the attribute object from the XML node
	 * @return the text of the class-path
	 */
	public static String getClasspathUsing (SymbolHash symbols, AttributeHash attributes)
	{
		String classpath;
		if ((classpath = attributes.get (CLASSPATH)) != null) return classpath;
		String classname = attributes.get (NAME), packagePath = symbols.getText (PACKAGE, attributes);
		if (classname==null || packagePath==null) throw new RuntimeException ("Unable to determine Classpath");
		return packagePath + "." + classname;
	}
	static final String NAME = "name";


	/**
	 * decode CLASS attribute as name of declared class
	 * @param attributes the attribute object from the XML node
	 * @param symbols the symbol table to use to resolve the package reference
	 * @return the text of the class-path
	 */
	public static String getClassPath
	(AttributeHash attributes, SymbolHash symbols)
	{ return symbols.getText (CLASS, attributes); }


	/**
	 * @param attributes the attributes from the NEW node in the XML stream
	 * @param symbols the symbol table for resolving named object references
	 * @return the class-path identified by parameters
	 */
	public static String identifyClass
		(
			AttributeHash attributes, SymbolHash symbols
		)
	{
		String classpath;
		if ((classpath = getClassPath (attributes, symbols)) == null)
		{ classpath = getClasspathUsing (symbols, attributes); }
		return classpath;
	}


	/**
	 * @param attributes the attribute hash built from the XML nodes
	 * @param symbols the symbol map used to evaluate object references
	 * @return the name of the method to call
	 */
	public static String getMethod (AttributeHash attributes, SymbolHash symbols)
	{
		String methodName = symbols.getText (INDIRECT_NAME, attributes);
		if (methodName == null) methodName = attributes.get (METHOD);
		return methodName;
	}
	static final String INDIRECT_NAME = "methodNamedIn", METHOD = "method";


	/**
	 * @param from attributes from XML nodes
	 * @param aliases the list of aliases to be searched
	 * @return the attribute name found from the aliases list
	 */
	public static String findAttribute (AttributeHash from, String[] aliases)
	{
		for (String name : aliases)
		{ if (from.containsKey (name)) return from.get (name); }
		return null;
	}


	/**
	 * @param from attributes from XML nodes
	 * @return the attribute name found from the aliases list
	 */
	public static String getAssignment (AttributeHash from)
	{
		return findAttribute (from, ASSIGNMENT_ALIASES);
	}


	/**
	 * @param from attributes from XML nodes
	 * @return the attribute name found from the aliases list
	 */
	public static String getParameter (AttributeHash from)
	{
		return findAttribute (from, PARAMETER_ALIASES);
	}


	static final String
	REFERENCE_ALIASES [] =
	{
		"named", "object", "objectNamed", "objectCalled",
		"referredToAs", "savedAs"
	},
	ASSIGNMENT_ALIASES [] =
	{
		"called", "toBeNamed", "toBeCalled", "saveAs"
	},
	PARAMETER_ALIASES [] =
	{
		"parameter", "parameters", "references", "using"
	},
	INDIRECT_REFERENCE = "indirectUsing",
	PARAMETER_REFERENCE = "passedAs",
	CLASSPATH = "classpath",
	PACKAGE = "package",
	CLASS = "class";


}

