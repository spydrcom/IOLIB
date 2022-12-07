
package net.myorb.data.abstractions;

import net.myorb.data.notations.json.JsonLowLevel.JsonValue;

/**
 * enable transport for objects able to be represented
 * @author Michael Druckman
 */
public class Portable
{

	/**
	 * use JSON as the transport format
	 * @param <T> the data type being transported
	 */
	public interface AsJson <T>
	{

		/**
		 * describe object to be transported as JSON
		 * @param from the source object to be transported
		 * @return the JSON representation for this object
		 */
		JsonValue toJson (T from);

		/**
		 * reproduce a transported object from the JSON representation
		 * @param representation the JSON representation for this object
		 * @return the object restored from the JSON representation
		 */
		T fromJson (JsonValue representation);

	}

}
