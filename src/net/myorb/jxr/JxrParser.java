
package net.myorb.jxr;

import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Java XML Reflection parser
 * @author Michael Druckman
 */
public class JxrParser extends JxrProcessingState
	implements JxrSymManager.SymReader
{


	/**
	 * construct with empty symbol table
	 */
	JxrParser () {}

	/**
	 * @param symbols initialize symbol table from parent process
	 */
	JxrParser (JxrSymManager.SymbolHash symbols)
	{
		if (symbols != null) include (symbols);
	}


	/**
	 * @param path the path to XML source
	 * @return a hash of symbols parsed from the source
	 * @throws Exception for error conditions
	 */
	public static SymbolTable read (String path) throws Exception
	{
		return read (new FileInputStream (path), null);
	}

	/**
	 * @param path the path to XML source
	 * @param symbols initial symbols passed from parent
	 * @return a hash of symbols parsed from the source
	 * @throws Exception for error conditions
	 */
	public static SymbolTable read
		(String path, JxrSymManager.SymbolHash symbols)
	throws Exception
	{
		JxrReport.pushSource (path, symbols);
		SymbolTable result = read (new FileInputStream (path), symbols);
		JxrReport.popSource (path);
		return result;
	}

	/**
	 * @param source an XML input stream for the parser
	 * @param symbols initial symbols passed from parent
	 * @return a hash of symbols parsed from the source
	 * @throws Exception for error conditions
	 */
	public static SymbolTable read
		(InputStream source, JxrSymManager.SymbolHash symbols)
	throws Exception
	{
		try
		{
			JxrParser jxrParser = new JxrParser (symbols);
			JxrJaxHandler jaxHandler = new JxrJaxHandler (jxrParser);
			return jaxHandler.parse (source).getExportedSymbols ();
		}
		catch (JxrJaxHandler.ExtendedSAXException e)
		{
			if (JxrReport.VERBOSE_ERROR_TRACE) System.out.println (e.toString ());
			throw new Exception ("Terminated with error", e);
		}
	}


	/**
	 * iterate over enumeration list expanding named macro
	 */
	public void expandMacroEnumeration ()
	{
		try { JxrSymManager.expandMacroEnumeration (attributes, symbols, this); }
		catch (Exception e) { throw new RuntimeException ("Unable to expand macro"); }
	}


	/**
	 * open XML as a nested script accepting parameters from parent symbols.
	 *  the symbol called references will pass as $BEAN$ to macro script for GET/SET operations.
	 *  the complete set of attributes will be treated as a symbol called $ATTRS$ for the passedBy mechanism.
	 */
	public void prepareMacroCall ()
	{
		try { copyReadSymbols (symbols.copyHashFor (attributes)); }
		catch (Exception e) { throw new RuntimeException ("Unable to process macro"); }
	}


	/**
	 * read and process XML at specified path
	 * @param parameters a hash of symbols taken from USING attribute
	 * @throws Exception for any error in read source
	 */
	public void copyReadSymbols
		(
			JxrSymManager.SymbolHash parameters
		)
	throws Exception
	{
		symbols.putAll
		(
			read
			(
				getSourcePath (), parameters
			)
		);
	}

	/**
	 * identify the path to the source
	 * @return the path to the source as identified
	 * @throws RuntimeException when path not found
	 */
	public String getSourcePath () throws RuntimeException
	{
		if (attributes.containsKey (INDIRECT))
		{ return symbols.get (attributes.get (INDIRECT)).toString (); }
		if (attributes.containsKey (PATH)) { return attributes.get (PATH); }
		throw new RuntimeException ("No source identified");
	}
	static final String PATH = "path", INDIRECT = "indirect";


	/**
	 * open XML as a nested script accepting parameters from parent symbols
	 */
	public void doRead ()
	{
		try { copyReadSymbols (symbols.getHashFor (attributes)); }
		catch (Exception e) { throw new RuntimeException ("Unable to read file"); }
	}


	/**
	 * process node by specified type
	 * @param typeOfNode the enumerated node type
	 * @return the object to be pushed on the object stack
	 */
	public Object processNode
		(
			JxrJaxHandler.Types typeOfNode
		)
	{
		pop ();
		Object last = null;

		switch (typeOfNode)
		{

			/*
			 * parse alternate source
			 */
			case READ:
				doRead ();
				break;
			case MACRO:
				prepareMacroCall ();
				break;
			case EXPAND:
				expandMacroEnumeration ();
				break;

			/*
			 * place constant object on top of stack
			 */
			case TRUE:
				top.add (Boolean.TRUE);
				break;
			case FALSE:
				top.add (Boolean.FALSE);
				break;
			case INT:
				top.add (last = new Integer (getLastPoppedText ()));
				break;
			case FLOAT:
				top.add (last = new Float (getLastPoppedText ()));
				break;
			case STRING:
				top.add (last = new String (getLastPoppedText ()));
				break;

			/*
			 * place referenced object on top of stack
			 */
			case REFERTO:
				top.add (last = getReference ());
				break;

			/*
			 * place computed object on top of stack
			 */
			case EVALUATE:
				top.add (last = doCall (this));
				break;
			case ARRAY:
				top.add (last = doArray (this));
				break;
			case NEW:
				top.add (last = doConstruct (this));
				break;
			case FIELD:
				top.add (last = doField (this));
				break;
			case SUBSTRING:
				top.add (last = doSubstring (this));
				break;
			case EQUALS:
				top.add (last = doEquals (this));
				break;

			/*
			 * describe Java objects
			 */
			case PACKAGE:
				last = getFirstFromLastPopped ();
				break;
			case CLASS:
				last = getClasspathFromAttributes ();
				break;

			/*
			 * construct Java object group
			 */
			case POPULATE:
				doPopulate (this);
				break;

			/*
			 * invoke method on Java object
			 */
			case CALL:
				last = doCall (this);
				break;

			/*
			 * operations on Java bean object
			 */
			case GET:
				doGet (this);
				break;
			case IS:
				doIs (this);
				break;
			case SET:
				doSet (this);
				break;
			case TEXT:
				doText (this);
				break;
			case IF:	//TODO: currently unimplemented
				break;
			case IFNOT:
				break;

			/*
			 * modify symbol visibility
			 */
			case COMMON:
				addSymbolsToCommon ();
				break;
			case EXPORT:
				addParametersFromAttributes (getExports ());
				break;
			case EXPORTALL:
				addAllSymbolsTo (getExports ());
				break;

			/*
			 * sundry operators
			 */
			case TABULATE:
				JxrReport.dump (symbols);
				break;
			case PRINT:
				System.out.println (getFirstFromLastPopped ());
				break;
			case SAVEAS:
				last = getFirstFromLastPopped ();
				break;

			/*
			 * operators with no processing effects
			 */
			case VOID:
				break;

			case BEAN:
				traceBean ();
				break;

			/*
			 * end of source, check stack status
			 */
			case JXR:
				stackCheck ();
				break;

		}

		return last;
	}


	public void traceBean ()
	{
		JxrReport.trace
		(
			JxrReport.BEAN_TRACE, "BEAN", true,
			attributes.get ("references")
		);
	}


}

