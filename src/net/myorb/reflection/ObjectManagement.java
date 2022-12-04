
package net.myorb.reflection;

import java.lang.reflect.Constructor;
import java.util.ArrayList;

/**
 * primitive types for Reflection processing
 * @author Michael Druckman
 */
public class ObjectManagement
{


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


}

