
package net.myorb.data.abstractions;

import net.myorb.utilities.Mapping.*;
import net.myorb.utilities.*;

import java.util.*;

/**
 * abstract description of a Directory
 * @author Michael Druckman
 */
public class Directory
{


	/*
	 * collection of data across children
	 */

	protected Accounting.Summary summary = new Accounting.Summary ();
	public Accounting.Summary getSummary () { return summary; }


	/**
	 * description of element groups 
	 */
	public static class GroupItems extends ArrayList < Element >
		implements Mapping.IdentifiedGroup < String >
	{

		public String summary ()
		{ return Accounting.scaled (aggregate); }
		protected long aggregate = 0;

		/* (non-Javadoc)
		 * @see net.myorb.utilities.Mapping.IdentifiedGroup#include(net.myorb.utilities.Mapping.IdentifiedItem)
		 */
		public void include (IdentifiedItem < String > item)
		{ Element ele = (Element) item; this.add ( ele ); aggregate += ele.getSize (); }

		private static final long serialVersionUID = 8054716509573520478L;
	}


	/**
	 * @param group name of the group type
	 * @param items the list of child elements
	 * @param parent the representation of the parent object
	 * @return the description text
	 */
	static String describeHeiarchy (String group, GroupItems items, Element parent)
	{
		boolean hasChild = false;
		StringBuffer description = new StringBuffer ("\r\n");
		StringBuffer children = new StringBuffer ("\r\n");

		if ( ! ">>>".equals (group) )
		{
			description
				.append ("\t").append (group).append (items.summary ())
				.append (" (").append (items.size ()).append (" entries) ")
				.append("\r\n");
		}

		for (Element item : items)
		{
			if (item.isDirectory)
			{
				children.append (item);
				hasChild = true;
			}
			else description.append (item);
		}

		if (hasChild)
		{
			children.append ("\r\n");
			children.append (parent.qName ()).append ("\r\n");
			children.append (description).append ("\r\n");
			return children.toString ();
		}

		return description.toString ();
	}


	/**
	 * grouping of typed object to the name of the type
	 */
	public class TypedGroups extends Mapping.OneToMany < String >
	{

		/**
		 * @param group name of the group
		 * @param parent the parent object of the grouped items
		 * @return description of the group contents
		 */
		String describe (String group, Element parent)
		{
			return describeHeiarchy (group, (GroupItems) get (group), parent);
		}

		/**
		 * describe the contents of a group
		 * @param parent the parent object of the group
		 * @return the description of the group contents
		 */
		public String describeAll (Element parent)
		{
			StringBuffer description = new StringBuffer ("\r\n");

			for (String group : keySet ())
			{
				description.append (describe (group, parent)).append ("\r\n");
			}

			return description.toString ();
		}

		/**
		 * @param item the item to be added
		 */
		public void addUnit (Element item)
		{
			summary.getItemsFor (item.getKey ())
					.accountFor (item);
			this.include (item);
		}

		/* (non-Javadoc)
		 * @see net.myorb.utilities.Mapping.Association#allocate()
		 */
		public GroupItems allocate () { return new GroupItems (); }

		private static final long serialVersionUID = 3696780844501601711L;
	}


	/**
	 * description of individual elements
	 */
	public class Element
		implements Mapping.IdentifiedItem < String >, Accounting.Estimation
	{

		/**
		 * @return description of contents of group
		 */
		String enumerate ()
		{
			if (contents == null || contents.size () == 0) return "";
			return contents.describeAll (this);
		}
		protected TypedGroups contents = null;

		/**
		 * @return object collecting child elements
		 */
		public TypedGroups addStructure ()
		{
			contents = new TypedGroups ();
			isDirectory = true;
			return contents;
		}

		/**
		 * @return formatted qualified name
		 */
		public String qName ()
		{
			StringBuffer formatted = new StringBuffer ()
				.append (parent).append (":").append (name);
			return formatted.toString ();
		}
		public String getParent () { return parent; }
		public void setParent (String parent) { this.parent = parent; }
		public void setName (String name) { this.name = name; }
		public String getName () { return this.name; }
		protected String name, type, parent;

		/* (non-Javadoc)
		 * @see net.myorb.FSTree.Accounting.Estimation#getSize()
		 */
		public long getSize () { return size; }
		public void setSize (long size) { this.size = size; }
		protected long size = 0;

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		public String toString ()
		{
			if ( ! isDirectory ) return "\r\n\t\t" + name;
			return "\r\n" + parent + ":" + name + enumerate ();
		}
		public boolean isStructured () { return isDirectory; }
		protected boolean isDirectory = false;

		/* (non-Javadoc)
		 * @see net.myorb.utilities.Mapping.IdentifiedItem#setKey(java.lang.Object)
		 */
		public void setKey (String text) { this.type = text; }

		/* (non-Javadoc)
		 * @see net.myorb.utilities.Mapping.IdentifiedItem#getKey()
		 */
		public String getKey () { return type; }

	}


}

