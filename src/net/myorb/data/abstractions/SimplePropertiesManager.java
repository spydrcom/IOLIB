
package net.myorb.data.abstractions;

import net.myorb.data.abstractions.CommonCommandParser;

import java.util.HashMap;
import java.util.List;

import java.io.File;

/**
 * manage lists of properties
 * @author Michael Druckman
 */
public class SimplePropertiesManager
{


	/**
	 * validation maintenance for properties in cache
	 */
	public interface Maintenance
	{
		/**
		 * @param entry the directory entry referenced
		 * @param property the name of the property to remove from cache
		 */
		void invalidate (String entry, String property);
	}


	/**
	 * values connected to properties
	 */
	public static class PropertyValueList extends CommonCommandParser.TokenList
	{
		public PropertyValueList () {}
		public PropertyValueList (List <CommonCommandParser.TokenDescriptor> tokens) { addAll (tokens); }
		private static final long serialVersionUID = 6519091306591149898L;
	}


	/**
	 * the declaration of the directory
	 */
	public static class PropertiesMap
		extends HashMap <String, PropertyValueList>
	{ private static final long serialVersionUID = 4727087738535235565L; }
	private static final HashMap <String, PropertiesMap> directory = new HashMap <> ();


	/**
	 * @param entryName the name of the map
	 * @return the named map
	 */
	public static PropertiesMap pget (String entryName)
	{
		String ucName = entryName.toUpperCase ();
		PropertiesMap entry = directory.get (ucName);

		if (entry == null)
		{
			entry = new PropertiesMap ();
			directory.put (ucName, entry);
		}

		return entry;
	}


	/**
	 * @param entryName the name of the map
	 * @param propertyName the name of the property
	 * @return the value of the property
	 */
	public static PropertyValueList
		pget (String entryName, String propertyName)
	{
		return pget (entryName).get (propertyName);
	}


	/**
	 * @param values the value list for the property
	 * @param index the index into the list
	 * @return the text value
	 */
	public static String pgetText (PropertyValueList values, int index)
	{
		CommonCommandParser.TokenDescriptor token = values.get (index);
		if (token.getTokenType () == CommonCommandParser.TokenType.IDN) return token.getTokenImage ();
		return CommonCommandParser.stripQuotes (token.getTokenImage ());
	}


	/**
	 * @param values the value list for the property
	 * @param index the index into the list
	 * @return the numeric value
	 */
	public static Number pgetNumber (PropertyValueList values, int index)
	{
		return values.get (index).getTokenValueAsCoded ();
	}


	/**
	 * @param entry the name of the map
	 * @param property the name of the property
	 * @param values the tokens to set as the property values
	 */
	public static void pset
	(String entry, String property, CommonCommandParser.TokenList values)
	{
		pget (entry).put (property, new PropertyValueList (values));
	}
	public static void pset (CommonCommandParser.TokenList tokens)
	{
		String entry = tokens.remove (0).getTokenImage ();
		String property = tokens.remove (0).getTokenImage ();
		pset (entry, property, tokens);
	}


	/**
	 * remove named property from map
	 * @param entry the name of the map
	 * @param property the name of the property
	 */
	public static void pdel (String entry, String property)
	{
		if (maintenance != null)
			maintenance.invalidate (entry, property);
		pget (entry).remove (property);
	}
	public static void pdel (CommonCommandParser.TokenList tokens)
	{
		String entry = tokens.remove (0).getTokenImage ();
		String property = tokens.remove (0).getTokenImage ();
		pdel (entry, property);
	}
	public static void setmaintenance
	(Maintenance manager) { maintenance = manager; }
	static Maintenance maintenance = null;


	/**
	 * clear all properties of named map
	 * @param entry the name of the map
	 */
	public static void pclr (String entry) { directory.remove (entry); }
	public static void pclr (CommonCommandParser.TokenList tokens)
	{ pclr (tokens.remove (0).getTokenImage ()); }


	/**
	 * save JSON file of properties
	 * @param entry the name of the map
	 * @param toFile the file to save to
	 */
	public static void psave (String entry, File toFile)
	{
		throw new RuntimeException ("Unimplemented feature: psave");
	}


	/**
	 * read JSON file of properties
	 * @param entry the name of the map
	 * @param fromFile the file to read from
	 */
	public static void pload (String entry, File fromFile)
	{
		throw new RuntimeException ("Unimplemented feature: pload");
	}


	/*
	 * description of objects configured with properties
	 */


	/**
	 * a cache for objects configured with properties
	 * @param <T> the type of objects placed/found in the cache
	 * @param <K> the kind of the objects, a sub-type of the type
	 */
	public static class Cache <T, K>
	{

		/**
		 * a factory definition for cache item creators
		 * @param <T> the type of objects to be built for the cache
		 * @param <K> the kind of the objects, a sub-type of the type
		 */
		public interface ObjectFactory <T, K>
		{
			T newInstance (PropertyValueList tokens, K kind);
		}

		/**
		 * @param named the name of the property
		 * @param kind the kind that identifies the flavor of the property
		 * @return the object being sought, from cache or factory
		 */
		public T lookup (String named, K kind)
		{
			PropertyValueList propertyValues;

			if (allocated.containsKey (named)) return allocated.get (named);
			else if ((propertyValues = pget (entryType, named)) == null)
			{ propertyValues = defaultPropertiesFor (named); }

			T newInstance =
				factory.newInstance (propertyValues, kind);
			allocated.put (named, newInstance);
			return newInstance;
		}
		private HashMap <String, T> allocated = new HashMap <> ();

		/**
		 * remove named from cache
		 * @param named the name of the property
		 */
		public void invalidate (String named)
		{
			allocated.remove (named);
		}

		/**
		 * @param named the name of the property to build default for
		 * @return the new instance of the object configured with default properties
		 */
		PropertyValueList defaultPropertiesFor (String named)
		{
			String defaultSettings = defaultPropertiesList.get (named);

			if (defaultSettings == null)
			{
				throw new RuntimeException ("No default settings: " + named);
			}

			return new PropertyValueList
			(
				CommonCommandParser.parseCommon (new StringBuffer (defaultSettings), segments)
			);
		}

		/**
		 * @param name the name of the object to default for
		 * @param useValue the text of the properties to use as default
		 */
		public void addDefaultFor
			(String name, String useValue) { defaultPropertiesList.put (name, useValue); }
		private HashMap <String, String> defaultPropertiesList = new HashMap <> ();

		/**
		 * @param factory a factory object that will construct objects for this cache
		 */
		public void setFactory
			(ObjectFactory<T,K> factory) { this.factory = factory; }
		private ObjectFactory<T,K> factory;

		/**
		 * @param entryType the properties directory entry type
		 * @param segments the token segments used in the parser
		 */
		public Cache
			(
				String entryType, CommonCommandParser.SpecialTokenSegments segments
			)
		{
			this.entryType = entryType; this.segments = segments;
		}
		private CommonCommandParser.SpecialTokenSegments segments;
		private String entryType;
	}


}

