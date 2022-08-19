
package net.myorb.data.abstractions;

import java.util.List;

/**
 * common description of parameter lists
 * @author Michael Druckman
 */
public class Parameters extends SimpleUtilities.ListOfNames
{


	public Parameters () {}
	public Parameters (String name) { this (new String[]{name}); }
	public Parameters (String [] names) { this (SimpleUtilities.toList (names)); }
	public Parameters (List <String> names)
	{ this.addAll (names); }


	/**
	 * parenthesis enclosed
	 * @param annotated TRUE = use annotated names
	 * @return the list formatted as a profile
	 */
	public StringBuffer formatProfile (boolean annotated)
	{
		return new StringBuffer ("(")
		.append (formatNameList (annotated))
		.append (")");
	}
	public String getAnnotatedProfile () { return formatProfile (true).toString (); }
	public String getProfile () { return formatProfile (false).toString (); }


	/**
	 * basic comma separated list
	 * @param annotated TRUE = use annotated names
	 * @return the list formatted as comma separated names
	 */
	public StringBuffer formatNameList (boolean annotated)
	{
		StringBuffer buffer = new StringBuffer ();
		formatParameterList (this, annotated, buffer);
		return buffer;
	}
	public String getAnnotatedNameList () { return formatNameList (true).toString (); }
	public String getNameList () { return formatNameList (false).toString (); }


	/**
	 * for simple single parameter functions
	 * @return the name of the single parameter
	 * @throws RuntimeException when more or less than 1 parameter
	 */
	public String getSingletonParameterName () throws RuntimeException
	{
		if (size () != 1)
		{ throw new RuntimeException ("Function profile is not consistent with singleton function"); }
		return this.get (0);
	}


	/**
	 * comma separated formatting
	 * @param names the list of names
	 * @param withNotations TRUE = use ConventionalNotations when appropriate
	 * @param buffer the buffer collecting the formatted text
	 */
	public void formatParameterList
		(
			List <String> names,
			boolean withNotations,
			StringBuffer buffer
		)
	{
		if (names.size () != 0)
		{
			buffer.append
				(formatParameter (names.get (0), withNotations));
			// bug fix 8/19/2022: original source just started with names[0] absent withNotations
			for (int i = 1; i < names.size (); i++)
			{
				buffer.append (", ").append
				(
					formatParameter (names.get (i), withNotations)
				);
			}
		}
	}


	/**
	 * formatting for a single parameter name.
	 *  extending methods override for notations.
	 * @param name the name of the parameter to format
	 * @param withNotations TRUE = convert to notation when available
	 * @return the formatted parameter name
	 */
	public String formatParameter (String name, boolean withNotations)
	{
		return name;
	}


	private static final long serialVersionUID = -6184873665662459921L;


}
