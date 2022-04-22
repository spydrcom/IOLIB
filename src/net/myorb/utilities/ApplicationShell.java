
package net.myorb.utilities;

import net.myorb.data.abstractions.SimpleStreamIO;
import net.myorb.data.notations.json.*;

import java.io.IOException;

import java.util.HashMap;
import java.util.Set;

/**
 * utilize Windows applications
 * @author Michael Druckman
 */
public class ApplicationShell
{


	/**
	 * map from concept to mechanism (i.e. file type to viewing app)
	 */
	public static class Associations extends HashMap <String, String>
	{ private static final long serialVersionUID = 2015903925025067575L; }


	/**
	 * extended exception for tracking shell errors
	 */
	public static class ShellError extends ReductionError
	{
		public ShellError (String message) { super (message); }
		private static final long serialVersionUID = -3284246447205992353L;
	}


	/**
	 * fork command shell
	 * @param command the command to execute
	 * @throws IOException for IO errors
	 */
	public static void executeInShell (String command) throws IOException
	{
		//  System.out.println (command);
		Runtime.getRuntime ().exec (command);
	}


	/**
	 * open content is specific application
	 * @param executable the entity to execute for this request
	 * @param content the full path to the content to operate on
	 * @throws ShellError for command execution errors
	 * @throws IOException for any IO errors
	 */
	public static void executeInShell (String executable, String content) throws ShellError, IOException
	{
		if (executable == null)
			throw new ShellError ("No executable found for application");
		executeInShell (executable + " " + content);
	}

	/**
	 * find executable for application
	 * @param app the name of the application
	 * @param content the full path to the content
	 * @throws ShellError for command execution errors
	 * @throws IOException for any IO errors
	 */
	public static void commandFor (String app, String content) throws ShellError, IOException
	{
		if (app == null)
			throw new ShellError ("No application assigned for file type");
		executeInShell (APPS.get (app), content);
	}
	static Associations APPS = new Associations ();

	/**
	 * @return Set of names of configured Apps
	 */
	public static Set<String> getApps () { return APPS.keySet (); }

	/**
	 * @param appNamed name of APP to be excluded
	 */
	public static void exclude (String appNamed)
	{
		APPS.remove (appNamed);
	}

	/**
	 * find application bound to type
	 * @param type the type of content to display
	 * @param content the path to the content to show in viewer
	 * @throws ShellError for command execution errors
	 * @throws IOException for any IO errors
	 */
	public static void showForType (String type, String content) throws ShellError, IOException
	{
		commandFor (BIND.get (type.toLowerCase ()), content);
	}
	static Associations BIND = new Associations ();

	/**
	 * show in application specified for type in configuration
	 * @param content the path to the content to be displayed in viewer
	 * @throws ShellError for command execution errors
	 * @throws IOException for any IO errors
	 */
	public static void showForType (String content) throws ShellError, IOException
	{
		int dot = content.lastIndexOf ('.');
		if (dot < 0) throw new ShellError ("No file type for content");
		showForType (content.substring (dot + 1), content);
	}


	/*
	 * configure applications and file types that are to be mapped to specific applications
	 */

	static
	{
		try
		{
			JsonSemantics.JsonObject cfg;
			SimpleStreamIO.TextSource source = JsonReader.getFileSource ("/links/cfg.json");
			cfg = (JsonSemantics.JsonObject) JsonReader.readFrom (source);

			JsonSemantics.JsonArray
				apps = (JsonSemantics.JsonArray) cfg.getMember ("APPS"),
				bind = (JsonSemantics.JsonArray) cfg.getMember ("BIND");
			for (JsonSemantics.JsonValue v : apps) { config (APPS, v, "Name", "Run"); }
			for (JsonSemantics.JsonValue v : bind) { config (BIND, v, "Type", "With"); }
		}
		catch (Exception e) { e.printStackTrace (); }
	}

	/**
	 * add configuration item to appropriate map
	 * @param map the map that this item should be added to
	 * @param v the JSON value that is a configuration object record
	 * @param from the member name of the FROM side of the mapping
	 * @param to the member name of the TO side of the mapping
	 */
	public static void config
		(
			HashMap<String,String> map,
			JsonSemantics.JsonValue v,
			String from, String to
		)
	{
		JsonSemantics.JsonObject o = (JsonSemantics.JsonObject) v;
		String f = o.getMemberString (from), t = o.getMemberString (to);
		map.put (f, t);
	}


}

