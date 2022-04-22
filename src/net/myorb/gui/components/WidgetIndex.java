
package net.myorb.gui.components;

import javax.swing.RootPaneContainer;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;

import java.awt.Dimension;
import java.awt.Component;
import java.awt.Point;

import java.util.HashMap;

/**
 * map the components of a Widget.
 *  and collect the properties of those components
 * @author Michael Druckman
 */
public class WidgetIndex extends HashMap <String, Component>
{

	/**
	 * empty base class for descriptions of settings
	 */
	public static class Settings {}

	/**
	 * settings described with simple integers
	 */
	public static class NumericSettings extends Settings
	{
		public NumericSettings (int value)
		{ this.value = new int [] {value}; }
		public NumericSettings (int [] value)
		{ this.value = value; }
		int [] value;
	}
	public static int [] multiple (Settings settings) { return ((NumericSettings) settings).value; }
	public static int simple (Settings settings) { return ((NumericSettings) settings).value[0]; }

	/**
	 * settings that requires size and location
	 */
	public static class PositionalSettings extends Settings
	{
		public PositionalSettings (Dimension dimension, Point location)
		{ this.dimension = dimension; this.location = location; }
		Dimension dimension; Point location;
	}

	/**
	 * settings specific to table objects
	 */
	public static class TableSettings extends Settings
	{
		public TableSettings (int size, int [] selected)
		{ this.size = size; this.selected = selected; }
		int size, selected [];
	}

	/**
	 * get a settings object that describes specified component
	 * @param component the component to be described
	 * @return a settings object or NULL for none
	 */
	public static Settings getSettings (Component component)
	{
		if (component instanceof JTable)
		{
			JTable table = (JTable) component;
			int rows = table.getRowCount (), selected [] = table.getSelectedRows ();
			return new TableSettings (rows, selected);
		}
		if (component instanceof JSplitPane)
		{
			return new NumericSettings ( ((JSplitPane) component).getDividerLocation () );
		}
		if (component instanceof JScrollPane)
		{
			return new NumericSettings ( ((JScrollPane) component).getVerticalScrollBar ().getValue () );
		}
		if (component instanceof RootPaneContainer)
		{
			return new PositionalSettings (component.getSize (), component.getLocation ());
		}
		return null;
	}

	/**
	 * apply settings to a components where appropriate
	 * @param component the component to be configured from the settings
	 * @param settings a settings object or NULL for none
	 */
	public static void applySettings (Component component, Settings settings)
	{
		if (settings == null || component == null) return;
		if (component instanceof JTable) apply (settings, (JTable) component);
		else if (component instanceof JSplitPane) apply (settings, (JSplitPane) component);
		else if (component instanceof JScrollPane) apply (settings, (JScrollPane) component);
		else if (component instanceof RootPaneContainer) apply (settings, component);
	}

	/**
	 * apply settings to a frame
	 * @param settings the settings to be applied
	 * @param to the component to configure
	 */
	public static void apply (Settings settings, Component to)
	{
		PositionalSettings positionalSettings = (PositionalSettings) settings;
		to.setLocation (positionalSettings.location);
		to.setSize (positionalSettings.dimension);
	}

	/**
	 * apply settings to a table
	 * @param settings the settings to be applied
	 * @param to the component to configure
	 */
	public static void apply (Settings settings, JTable to)
	{
		TableSettings tableSettings = (TableSettings) settings;
		
		if (tableSettings.size == to.getRowCount ())
		{
			for (int index : tableSettings.selected)
			{
				to.addRowSelectionInterval (index, index);
			}
		}
	}

	/**
	 * apply settings to a scroll pane
	 * @param settings the settings to be applied
	 * @param to the component to configure
	 */
	public static void apply (Settings settings, JScrollPane to)
	{ to.getVerticalScrollBar ().setValue (simple (settings)); }

	/**
	 * apply settings to a split
	 * @param settings the settings to be applied
	 * @param to the component to configure
	 */
	public static void apply (Settings settings, JSplitPane to)
	{ to.setDividerLocation (simple (settings)); }

	/**
	 * collect the settings of properties of a component set
	 */
	@SuppressWarnings ("serial")
	public static class Properties extends HashMap <String, Settings>
	{}

	/**
	 * @return a Properties object holding Settings for each component
	 */
	public Properties capturePropertySettings ()
	{
		Properties properties = new Properties ();
		for (String name : this.keySet ())
		{
			Settings settings = getSettings (this.get (name));
			if (settings != null) properties.put (name, settings);
		}
		return properties;
	}

	/**
	 * @param properties a Properties object holding Settings for each component
	 */
	public void applyPropertySettings (Properties properties)
	{
		for (String name : properties.keySet ())
		{
			applySettings (this.get (name), properties.get (name));
		}
	}

	private static final long serialVersionUID = -1371157264958492391L;

}
