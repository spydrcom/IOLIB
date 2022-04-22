
package net.myorb.jxr;

import net.myorb.data.abstractions.ExpressionTokenEvaluation;
import net.myorb.data.abstractions.ExpressionTokenParser;
import net.myorb.data.abstractions.Stack;

import org.xml.sax.Attributes;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import java.util.ArrayList;

/**
 * primitive types for
 *  Java XML Reflection parser
 * @author Michael Druckman
 */
public class JxrPrimitives
{


	/**
	 * a stack for attribute hash tables supporting nesting of XML nodes
	 */
	public static class AttributeStack extends Stack<JxrSymManager.AttributeHash>
	{

		/**
		 * @param attributes a collections of XML attributes taken from an XML element
		 */
		public void push (Attributes attributes)
		{ push (new JxrSymManager.AttributeHash (attributes)); }

		private static final long serialVersionUID = 2519215449937070229L;

	}


	/**
	 * a list for objects
	 */
	public static class ObjectList extends ArrayList<Object>
	{

		Object[] objects = null;

		/**
		 * @return an array holding the class description objects of the object from the list
		 */
		public Class<?>[] getTypes ()
		{
			objects = this.toArray ();
			Class<?>[] types = new Class<?>[objects.length];
			for (int i=0; i<objects.length; i++)
			{
				if (objects[i] == null)
					types[i] = Object.class;
				else types[i] = objects[i].getClass ();
			}
			return types;
		}

		/**
		 * @param n the number of the object
		 * @return the Nth object from the list
		 */
		public Object nth (int n) { return this.get (n-1); }

		/**
		 * @return the first object from the list
		 */
		public Object first () { return this.get (0); }

		/**
		 * @return the text of the first object
		 */
		public String firstText () { return this.first ().toString (); }

		/**
		 * @return the list converted to an array
		 */
		public Object[] asArray () { return objects!=null ? objects : this.toArray (); }

		private static final long serialVersionUID = -1692830134425503971L;

	}

	/**
	 * a stack for object lists
	 */
	public static class ObjectStack extends Stack<ObjectList>
	{

		/**
		 * push a new empty list to top of stack
		 */
		public void push () { push (new ObjectList ()); }

		private static final long serialVersionUID = -8965035669350326552L;

	}


	/**
	 * a map of names to objects
	 */
	public static class SymbolTable extends JxrSymManager.SymbolHash
	{

		public static final SymbolTable EMPTY = new SymbolTable ();

		SymbolTable (JxrSymManager.SymbolHash imports)
		{
			this (); if (imports != null) this.putAll (imports);
		}

		SymbolTable ()
		{
			put ("T", Boolean.TRUE);
			put ("F", Boolean.FALSE);
		}

		private static final long serialVersionUID = -5658509369297571572L;

	}


	/**
	 * @param parameters the text description of the parameters
	 * @param symbols the symbol table to use to resolve references
	 * @param objects the list collecting parameter objects
	 */
	public static void getParameterObjects
		(String parameters, SymbolTable symbols, ObjectList objects)
	{
		ExpressionTokenParser.TokenList tokens =
			ExpressionTokenParser.parse (new StringBuffer (parameters));
		ExpressionTokenEvaluation.evaluate (tokens, symbols, objects);
	}


	/**
	 * combine attributes to build implied class-path
	 * @param attributes the attributes read from node start
	 * @param symbols the symbol table to use to resolve references in using text
	 * @return the list of objects represented by nodes or using attribute
	 */
	public static ObjectList getParameterObjects
		(JxrSymManager.AttributeHash attributes, SymbolTable symbols)
	{
		String parameter;
		ObjectList objects = new ObjectList ();
		if ((parameter = JxrSymManager.getParameter (attributes)) != null)
		{ getParameterObjects (parameter, symbols, objects); }
		return objects;
	}


	/**
	 * @param attributes the attribute has from the XML node holding text parameters
	 * @param symbols the symbol table to use to resolve references
	 * @param objects the list collecting parameter objects
	 * @return the collected parameter objects
	 */
	public static ObjectList getParameterObjects
		(JxrSymManager.AttributeHash attributes, SymbolTable symbols, ObjectList objects)
	{
		if (objects.size () == 0)
		{ return getParameterObjects (attributes, symbols); }
		return objects;
	}


	/**
	 * @param named name of field to be read
	 * @param state the stacks that maintain the processing state
	 * @return the identified field object
	 * @throws Exception for any errors
	 */
	public static Object getFieldObject
		(String named, JxrProcessingState state)
	throws Exception
	{
		checkReference (named == null); // name is required
		// look for class-path description in attributes, static field
		String classpath = JxrSymManager.getClassPath (state.attributes, state.symbols);
		if (classpath != null) return Class.forName (classpath).getField (named).get (null);
		// field must otherwise be a member of a class instance found as singleton parameter
		return getFieldObjectFromInstance (named, state.getParameterObjectsFromAttributes ());
	}

	/**
	 * @param named name of field to be read
	 * @param objects the parameters listed for this invoking node
	 * @return the object holding the value of the named field
	 * @throws Exception for access violation (listed below)
	 */
	public static Object getFieldObjectFromInstance
		(String named, ObjectList objects)
	throws Exception
	{
		checkReference (objects == null || objects.size () != 1);
		return useObjectReference (named, objects.get (0));
	}

	/**
	 * @param named name of field to be read
	 * @param parent object instance holding named field
	 * @return the object holding the value of the named field
	 * @throws IllegalArgumentException  for illegal argument
	 * @throws IllegalAccessException for illegal access
	 * @throws NoSuchFieldException for no such field
	 * @throws SecurityException for security issue
	 */
	public static Object useObjectReference
		(
			String named, Object parent
		)
	throws IllegalArgumentException, IllegalAccessException,
		NoSuchFieldException, SecurityException
	{
		Class<?> c = parent.getClass ();
		return c.getField (named).get (parent);
	}

	/**
	 * throw exception when
	 *  referenced object cannot be identified
	 * @param test a boolean indicating an error state
	 * @throws RuntimeException when test evaluates TRUE
	 */
	public static void checkReference (boolean test) throws RuntimeException
	{
		if (test) throw new RuntimeException ("Unable to identify referenced object");
	}


	/**
	 * @param state the stacks that maintain the processing state
	 * @return the newly constructed object
	 */
	public static Object doConstruct
		(
			JxrProcessingState state
		)
	{
		try
		{
			Class<?> classDescriptor = Class.forName
				(JxrSymManager.identifyClass (state.attributes, state.symbols));
			ObjectList constructorParameters = state.getParameterObjectsFromAttributes ();
			return doConstruct (classDescriptor, constructorParameters);
		}
		catch (Exception e)
		{
			if (JxrReport.VERBOSE_ERROR_TRACE) e.printStackTrace ();
			return null;
		}
	}


	/**
	 * @param classDescriptor the description of the class to instance
	 * @param constructorParameters objects holding parameters for constructor
	 * @param state the stacks that maintain the processing state
	 * @throws Exception for errors on new instances
	 */
	public static void doPopulate
		(
			Class<?> classDescriptor,
			ObjectList constructorParameters,
			JxrProcessingState state
		)
	throws Exception
	{
		String listName, nameList =
			JxrSymManager.getAssignment (state.attributes);
		for (String name : JxrSymManager.spaceDelimited (nameList))
		{ state.symbols.put (name, doConstruct (classDescriptor, constructorParameters)); }
		if ((listName = JxrSymManager.getListName (state.attributes)) != null)
		{ state.symbols.put (listName, nameList); }
	}


	/**
	 * set symbols to new class instances
	 * @param state the stacks that maintain the processing state
	 */
	public static void doPopulate
		(
			JxrProcessingState state
		)
	{
		try
		{
			Class<?> classDescriptor = Class.forName
				(JxrSymManager.identifyClass (state.attributes, state.symbols));
			ObjectList constructorParameters = state.getParameterObjectsFromAttributes ();
			doPopulate (classDescriptor, constructorParameters, state);
		}
		catch (Exception e)
		{
			if (JxrReport.VERBOSE_ERROR_TRACE) e.printStackTrace ();
		}
	}


	/**
	 * @param classDescriptor description of class identified by attributes
	 * @param constructorParameters objects holding parameters for constructor
	 * @return the newly constructed object based on the class descriptor
	 * @throws Exception for illegal access
	 */
	public static Object doConstruct
		(
			Class<?> classDescriptor, ObjectList constructorParameters
		)
	throws Exception
	{
		if (constructorParameters.size () == 0) return classDescriptor.newInstance ();
		Constructor<?> c = findUniqueConstructor (classDescriptor, constructorParameters);
		if (c == null) c = classDescriptor.getConstructor (constructorParameters.getTypes ());
		return c.newInstance (constructorParameters.asArray ());
	}


	/**
	 * @param objectClass description of object referenced
	 * @param constructorParameters objects holding parameters for constructor
	 * @return the constructor description object found
	 */
	public static Constructor<?> findUniqueConstructor
		(Class<?> objectClass, ObjectList constructorParameters)
	{
		Constructor<?> result = null;
		int parameterCount = constructorParameters.size ();
		for (Constructor<?> c : objectClass.getConstructors ())
		{
			if (c.getParameterCount () == parameterCount)
			{
				if (result != null) return null;
				result = c;
			}
		}
		return result;
	}


	/**
	 * @param objectClass description of object referenced
	 * @param methodName name of method to be called
	 * @param parameters number of parameters
	 * @param ignoreCount allow varying counts
	 * @return the method description object
	 */
	public static Method findUniqueMethod
	(Class<?> objectClass, String methodName, int parameters, boolean ignoreCount)
	{
		Method result = null;

		for (Method m : objectClass.getMethods ())
		{
			if
				(
					m.getName ().equals (methodName) &&
					(m.getParameterCount () == parameters || ignoreCount)
				)
			{
				if (result != null) return null;
				result = m;
			}
		}

		return result;
	}


	/**
	 * @param objectClass description of object referenced
	 * @param methodName name of method to be called in referenced object
	 * @param parameterTypes types of parameters in method profile
	 * @param ignoreCount profile accepts varying parameter counts
	 * @return the description object for method found
	 * @throws Exception for reflection errors
	 */
	public static Method findMethod
		(
			Class<?> objectClass, String methodName,
			Class<?>[] parameterTypes, boolean ignoreCount
		)
	throws Exception
	{
		Method m = findUniqueMethod
			(objectClass, methodName, parameterTypes.length, ignoreCount);
		if (m == null) m = objectClass.getMethod (methodName, parameterTypes);
		return m;
	}


	/**
	 * @param state the stacks that maintain the processing state
	 * @return the result returned from the call
	 */
	public static Object doCall
		(
			JxrProcessingState state
		)
	{
		String classpath = JxrSymManager.getClassPath (state.attributes, state.symbols);
		if (classpath != null) return doStaticCall (state, classpath);
		else return doObjectCall (state);
	}


	/**
	 * call to static method rather than instance
	 * @param state the stacks that maintain the processing state
	 * @param classPath the full classPath for the object being called
	 * @return the result returned from the call
	 */
	public static Object doStaticCall
		(
			JxrProcessingState state, String classPath
		)
	{
		try
		{
			Class<?> c = Class.forName (classPath);
			return makeCall (state, null, c);
		}
		catch (Exception e)
		{
			if (JxrReport.VERBOSE_ERROR_TRACE) e.printStackTrace();
			throw new RuntimeException ("Class not found: " + classPath, e);
		}
	}


	/**
	 * call instance method of class
	 * @param state the stacks that maintain the processing state
	 * @return the result returned from the call
	 */
	public static Object doObjectCall
		(
			JxrProcessingState state
		)
	{
		Object o = state.symbols.getReference (state.attributes);
		return makeCall (state, o, o.getClass ());
	}


	/**
	 * @param state the stacks that maintain the processing state
	 * @param object the object for the call when not a static call
	 * @param classDescription the Class object for the call
	 * @return the result returned from the call
	 */
	static Object makeCall
		(
			JxrProcessingState state, Object object, Class<?> classDescription
		)
	{
		try
		{
			boolean multi =
				state.attributes.containsKey ("varying");
			ObjectList p = state.getParameterObjectsFromAttributes ();
			String methodName = JxrSymManager.getMethod (state.attributes, state.symbols);
			Method m = findMethod (classDescription, methodName, p.getTypes (), multi);
			return m.invoke (object, getParameterArray (p.asArray (), multi));
		}
		catch (Exception e) { throw new RuntimeException ("Unable to execute call", e); }
	}
	static Object[] getParameterArray (Object[] p, boolean multi)
	{ return multi ? new Object[]{p} : p; }


	/**
	 * @param state the stacks that maintain the processing state
	 * @return the object array of the listed parameter objects
	 */
	public static Object doArray
		(
			JxrProcessingState state
		)
	{
		return state.getParameterObjectsFromAttributes ().toArray ();
	}


	/**
	 * @param state the stacks that maintain the processing state
	 * @return the object array of the listed parameter objects
	 */
	public static Object doField
		(
			JxrProcessingState state
		)
	{
		String named = JxrSymManager.getNamed (state.attributes);
		try { return getFieldObject (named, state); }
		catch (Exception e)
		{
			throw new RuntimeException ("Unable to reference field " + named);
		}
	}


	/**
	 * @param state the stacks that maintain the processing state
	 * @return a new string with specified portion of parameter string
	 */
	public static Object doSubstring
		(
			JxrProcessingState state
		)
	{
		ObjectList parameters = state.getParameterObjectsFromAttributes ();
		if (parameters.size () != 3) throw new RuntimeException ("Parameters must be STRING START LENGTH");
		int starting = (Integer) parameters.nth (2), count = (Integer) parameters.nth (3);
		char[] chars = parameters.get (0).toString ().toCharArray ();
		return new String (chars, starting - 1, count);
	}


	/**
	 * @param state the stacks that maintain the processing state
	 * @return TRUE or FALSE per comparison result
	 */
	public static Object doEquals (JxrProcessingState state)
	{
		ObjectList parameters = new ObjectList ();
		getParameterObjects (JxrSymManager.getParameter (state.attributes), state.symbols, parameters);
		if (parameters.size () != 2) throw new RuntimeException ("Two components required for equals test");
		return parameters.get (0).equals (parameters.get (1));
	}


	/**
	 * process a GET method on a bean
	 * @param state the stacks that maintain the processing state
	 */
	public static void doGet (JxrProcessingState state)
	{
		String name = null;
		for (String dst : state.attributes.keySet ())
		{
			try { setSym (dst, "get", name = state.attributes.get (dst), state); }
			catch (Exception e) { invocationError ("get", name, e); }
		}
	}
	static Class<?>[] EMPTY = new Class<?>[]{};
	static Object[] NONE = new Object[]{};


	/**
	 * process a IS method on a bean
	 * @param state the stacks that maintain the processing state
	 */
	public static void doIs
		(
			JxrProcessingState state
		)
	{
		String name = null;
		for (String dst : state.attributes.keySet ())
		{
			try { setSym (dst, "is", name = state.attributes.get (dst), state); }
			catch (Exception e) { invocationError ("is", name, e); }
		}
	}


	/**
	 * @param sym the name of the symbol being created
	 * @param kind the kind (GET / IS) of operation to invoke
	 * @param name the name of the field in the bean instance to reference
	 * @param state the stacks that maintain the processing state
	 * @throws Exception for invocation errors
	 */
	public static void setSym
		(
			String sym, String kind, String name, JxrProcessingState state
		)
	throws Exception
	{
		Object instance = state.getFirstFromTOS ();
		Method m = findMethod (instance.getClass (), kind + name, EMPTY, false);
		state.symbols.put (sym, m.invoke (instance, NONE));
	}


	/**
	 * @param instance the bean object
	 * @param name the name of the field in the bean instance
	 * @param parameters the object parameters to the call
	 * @throws Exception for invocation errors
	 */
	public static void invoke
		(
			Object instance, String name, ObjectList parameters
		)
	throws Exception
	{
		findMethod
		(
			instance.getClass (), "set" + name,
			parameters.getTypes (), false
		)
		.invoke (instance, parameters.asArray ());
	}


	/**
	 * @param op the bean operation that failed
	 * @param name the name of the property being accessed
	 * @param e the exception caught
	 */
	public static void invocationError (String op, String name, Exception e)
	{
		if (JxrReport.VERBOSE_ERROR_TRACE) e.printStackTrace ();
		throw new RuntimeException ("Unable to invoke '" + op + "' for " + name);
	}


	/**
	 * process a SET method on a bean
	 * @param state the stacks that maintain the processing state
	 */
	public static void doSet (JxrProcessingState state)
	{
		for (String dst : state.attributes.keySet ())
		{
			try { invoke (state.getFirstFromTOS (), dst, parametersForSet (dst, state)); }
			catch (Exception e) { invocationError ("set", dst, e); }
		}
	}

	/**
	 * @param attribute the name of the attribute holding the value
	 * @param state the stacks that maintain the processing state
	 * @return the parameter as an object list
	 */
	public static ObjectList parametersForSet (String attribute, JxrProcessingState state)
	{
		ObjectList parameters = new ObjectList ();
		getParameterObjects (state.attributes.get (attribute), state.symbols, parameters);
		return parameters;
	}


	/**
	 * process a TEXT method on a bean.
	 *  this is an extension to the BEAN concept for TEXT constant values
	 * @param state the stacks that maintain the processing state
	 */
	public static void doText (JxrProcessingState state)
	{
		for (String dst : state.attributes.keySet ())
		{
			try { invoke (state.getFirstFromTOS (), dst, singleton (state.attributes.get (dst))); }
			catch (Exception e) { invocationError ("text", dst, e); }
		}
	}

	/**
	 * @param parameter the text of the parameter value
	 * @return an object list holding the parameter
	 */
	public static ObjectList singleton (String parameter)
	{
		ObjectList parameters = new ObjectList ();
		parameters.add (parameter);
		return parameters;
	}


}

