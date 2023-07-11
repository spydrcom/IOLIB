
package net.myorb.gui.graphics.markets.data;

import net.myorb.gui.components.SimpleScreenIO;

import net.myorb.data.abstractions.CommonDataStructures.*;

/**
 * display object for market data
 * @author Michael Druckman
 */
public class HistoricalData extends SimpleScreenIO
{


	public static class LabelMap extends SymbolicMap <Label>
	{ private static final long serialVersionUID = 6375712551609853095L; }

	public static class ValueMap extends SymbolicMap <Double>
	{ private static final long serialVersionUID = -2608452612522234172L; }


	/**
	 * @param titles the names of fields to be shown
	 */
	public HistoricalData (String[] titles)
	{
		this ();
		this.titles = titles;
		for (String title : titles)
		{ addItem (title); }
	}
	public HistoricalData () { initDisplay ();  }
	protected ValueMap valueMap = new ValueMap ();
	protected LabelMap itemMap = new LabelMap ();
	protected String[] titles;


	/**
	 * set/reset display to empty panel
	 */
	public void initDisplay () { display = startGridPanel (null, 0, 2);}
	public Panel getDisplayPanel () { return display; }
	protected Panel display;


	/**
	 * @param title name of a field to be added
	 */
	public void addItem (String title)
	{
		if (itemMap.containsKey (title)) return;
		addItem (new Label (title), new Label (""));
	}
	public void addItem (Label title, Label dataItem)
	{
		display.add (title);
		itemMap.put (title.getText (), dataItem);
		display.add (dataItem);
	}


	/**
	 * @param name the name of a field
	 * @param value the new (text) value to be shown
	 */
	public void set (String name, String value)
	{
		Label field;
		if ((field = itemMap.get (name)) == null) return;
		field.setText (value);
	}

	/**
	 * @param name the name of a field
	 * @param value the new (numeric) value to be shown
	 */
	public void set (String name, Number value)
	{
		if (value instanceof Long)
		{ set (name, value.toString ()); return; }
		set (name, String.format ("%8.2f", value.doubleValue ()));
		valueMap.put (name, value.doubleValue ());
	}


	/**
	 * @return an array of names of support levels
	 */
	public String [] getTypes ()
	{ return valueMap.keySet ().toArray (new String[]{}); }

	/**
	 * @param type name of a type of support
	 * @return the value for the named item
	 */
	public double getValue (String type) { return valueMap.get (type); }

	/**
	 * @return name-value pairs of values captured
	 */
	public ValueMap getValues () { return valueMap; }


	/**
	 * @return a duplicate data object
	 */
	public HistoricalData copy ()
	{
		return new HistoricalData (titles);
	}

	/**
	 * remove frame from display
	 */
	public void hide ()
	{
		if (frame != null) frame.dispose ();
	}

	/**
	 * show frame on screen
	 * @param title the title for the frame
	 */
	public void show (String title)
	{
		frame = new WidgetFrame (display, title);
		frame.showAndExit ();
	}

	/**
	 * alter display tree prior to show
	 */
	public void prep () {}
	protected Frame frame;

}
