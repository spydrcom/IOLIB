
package net.myorb.gui.components;

import net.myorb.data.abstractions.SimpleUtilities;
import net.myorb.data.io.TextAreaStreams;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Writer;

/**
 * extended text area component.
 *  display can be accessed as stream or writer
 * @author Michael Druckman
 */
public class StreamDisplay extends DisplayFrame
{


	/**
	 * specifically extended for properties of a stream
	 */
	public static class StreamProperties extends SimpleUtilities.PropertiesMap
	{ private static final long serialVersionUID = 6821569656002463222L; }


	/**
	 * map access object
	 * @param map the properties map
	 * @param name the name of the console
	 * @param textArea the component providing display
	 * @param scrollPane the scroll object for the display
	 */
	public static void setProperties
		(
			StreamProperties map, String name,
			JTextArea textArea, JScrollPane scrollPane
		)
	{
		TextAreaStreams streams =
			new TextAreaStreams (textArea, scrollPane);
		map.put (name + "$WRITER", streams.getOutputWriter ());
		map.put (name, streams.getOutputStream ());
		map.put (name + "$AREA", textArea);
	}

	/**
	 * display the console
	 * @param console the scroll for the area
	 * @param title a title for the frame
	 * @param size square size of frame
	 */
	public static void showConsole
		(JScrollPane console, String title, int size)
	{
		JPanel panel = new JPanel ();
		panel.setLayout (new GridLayout ());
	
		panel.setPreferredSize
		(new Dimension (size, size));
		panel.add (console);
	
		new DisplayFrame (panel, title).show ();
	}

	/**
	 * @param name a name for this console
	 * @param map a map of properties that can access this console
	 * @return the scroll component for this area
	 */
	public static JScrollPane displayArea (String name, StreamProperties map)
	{
		JTextArea
		console = new JTextArea ();
		console.setForeground (Color.BLACK);
		JScrollPane scrollPane = new JScrollPane (console);
		if (name != null) setProperties (map, name, console, scrollPane);
		return scrollPane;
	}
	
	/**
	 * @param color new color for area
	 * @param name the name of the console
	 * @param map the properties map
	 */
	public static void setAreaColorTo (Color color, String name, StreamProperties map)
	{
		getAreaFor (name, map).setForeground (color);
	}

	/**
	 * find named area in map
	 * @param name the name of the console
	 * @param map the properties map
	 * @return the area found
	 */
	public static JTextArea getAreaFor (String name, StreamProperties map)
	{
		try { return (JTextArea) map.get (name + "$AREA"); }
		catch (Exception e) { e.printStackTrace(); return null; }
	}

	/**
	 * @param name the name of the console
	 * @param map the properties map for console
	 * @return print writer for console
	 */
	public static PrintWriter getWriterFor (String name, StreamProperties map)
	{
		try { return new PrintWriter ((Writer) map.get (name + "$WRITER")); }
		catch (Exception e) { e.printStackTrace(); return null; }
	}

	/**
	 * @param name the name of the console
	 * @param map the properties map for console
	 * @return print stream for console
	 */
	public static PrintStream getStreamFor (String name, StreamProperties map)
	{
		try { return new PrintStream ((OutputStream) map.get (name), true, "UTF-8"); }
		catch (Exception e) { e.printStackTrace(); return null; }
	}

	/**
	 * @param name the name of the console
	 * @param map the properties map for console
	 */
	public static void setStreamTo (String name, StreamProperties map)
	{
		System.setOut (getStreamFor (name, map));
	}

	/**
	 * @param title a title for the frame
	 * @param size the square size of frame
	 */
	public static void showConsole (String title, int size)
	{
		showConsole (displayArea (null, null), title, size);
	}

	/**
	 * @param title a title for the frame
	 * @param map the properties map for console
	 * @param size the square size of frame
	 * @return a stream for the console
	 */
	public static PrintStream showConsole
	(String title, StreamProperties map, int size)
	{
		String name = title + "$STREAM";
		showConsole (displayArea (name, map), title, size);
		return getStreamFor (name, map);
	}

}

