
package net.myorb.jxr;

import net.myorb.gui.components.SimplePopupRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * popUp drop-down for configuration script selection JXR
 * @author Michael Druckman
 */
public class JxrScriptChoice
{

	/**
	 * a Preparation / Conclusion for the script execution
	 */
	public interface Steps
	{
		/**
		 * do Preparation requirements
		 */
		void doSteps ();
	}

	public static Steps preparation = null, conclusion = null;

	/**
	 * show popUp for selection
	 */
	public static void popUp ()
	{
		new OptionPopup ();
		if (preparation != null)
		{ preparation.doSteps (); }
	}

	static ArrayList<NameValuePair> scripts = new ArrayList<> ();

	/**
	 * @param description a description to show for the script
	 * @param path the path to the script to be executed on choice
	 */
	public static void addScript (String description, String path)
	{
		scripts.add (new NameValuePair (description, path));
	}

	/**
	 * @param path path to XML source
	 * @return symbols exported by script
	 */
	public static final JxrPrimitives.SymbolTable 
				runScript (String path)
	{
		JxrPrimitives.SymbolTable symbols = null;
		try { symbols = JxrParser.read (path, globalSymbols); }
		catch (Exception e) { e.printStackTrace (); }
		if (conclusion != null) { conclusion.doSteps (); }
		return symbols;
	}
	static JxrPrimitives.SymbolTable globalSymbols = null;

	/**
	 * @param path the script holding symbols to be made global
	 */
	public static final void setGlobalSymbols (String path)
	{
		JxrPrimitives.SymbolTable stab = runScript (path);
		if (globalSymbols == null) globalSymbols = stab;
		else globalSymbols.putAll (stab);
	}

	/**
	 * empty the script list
	 */
	public static void resetScripts () {
		scripts.clear ();
	}

	/**
	 * @return the list of added scripts
	 */
	public static ArrayList<NameValuePair> getScripts () {
		return scripts;
	}

	/**
	 * @param scripts the list to be used for the popUp
	 */
	public static void setScripts (ArrayList<NameValuePair> scripts) {
		JxrScriptChoice.scripts = scripts;
	}

	/**
	 * @return the frame title set for the popUp
	 */
	public static String getFrameTitle () {
		return frameTitle;
	}

	/**
	 * @param frameTitle the frame title to be used for the popUp
	 */
	public static void setFrameTitle (String frameTitle) {
		JxrScriptChoice.frameTitle = frameTitle;
	}

	/**
	 * @return the notification to be shown on script execution
	 */
	public static String getNotification () {
		return notification;
	}

	/**
	 * @param notification the notification to be shown on script execution
	 */
	public static void setNotification (String notification) {
		JxrScriptChoice.notification = notification;
	}

	/**
	 * @return the field width for the popUp display
	 */
	public static int getFieldWidth () {
		return fieldWidth;
	}

	/**
	 * @param fieldWidth the field width for the popUp display
	 */
	public static void setFieldWidth (int fieldWidth) {
		JxrScriptChoice.fieldWidth = fieldWidth;
	}

	/**
	 * @return the frame height for the popUp display
	 */
	public static int getFrameHeight () {
		return frameHeight;
	}

	/**
	 * @param frameHeight the frame height for the popUp display
	 */
	public static void setFrameHeight (int frameHeight) {
		JxrScriptChoice.frameHeight = frameHeight;
	}

	/**
	 * @return the frame width for the popUp display
	 */
	public static int getFrameWidth () {
		return frameWidth;
	}

	/**
	 * @param frameWidth the frame width for the popUp display
	 */
	public static void setFrameWidth (int frameWidth) {
		JxrScriptChoice.frameWidth = frameWidth;
	}

	/**
	 * @param frameWidth the frame width for the popUp display
	 * @param frameHeight the frame height for the popUp display
	 * @param fieldWidth the field width for the popUp display
	 */
	public static void setPopUpDimensions
	(int frameWidth, int frameHeight, int fieldWidth)
	{
		setFrameWidth (frameWidth);
		setFrameHeight (frameHeight);
		setFieldWidth (fieldWidth);
	}

	static String frameTitle = "", notification = null;
	static int fieldWidth = 20, frameHeight = 100, frameWidth = 300;

}


/**
 * pair a display name with the configuration file path
 * @author Michael Druckman
 */
class NameValuePair
{
	NameValuePair (String name, String path)
	{ this.name = name; this.path = path;  }
	public String toString () { return name; }
	String getPath () { return path; }
	String name, path;
}


/**
 * instance of simple PopUp to use with XML driver
 * @author Michael Druckman
 */
class OptionPopup extends SimplePopupRequest<NameValuePair>
{

	/*
	 * description of the field
	 */

	/* (non-Javadoc)
	 * @see net.myorb.gui.components.SimplePopupRequest#getOptions()
	 */
	public List<NameValuePair> getOptions () { return JxrScriptChoice.scripts; }

	/* (non-Javadoc)
	 * @see net.myorb.gui.components.SimplePopupRequest#getFieldWidth()
	 */
	public int getFieldWidth () { return JxrScriptChoice.getFieldWidth (); }

	/*
	 * description of the frame
	 */

	/* (non-Javadoc)
	 * @see net.myorb.gui.components.SimplePopupRequest#getFrameTitle()
	 */
	public String getFrameTitle () { return JxrScriptChoice.getFrameTitle (); }

	/* (non-Javadoc)
	 * @see net.myorb.gui.components.SimplePopupRequest#getFrameHeight()
	 */
	public int getFrameHeight () { return JxrScriptChoice.getFrameHeight (); }

	/* (non-Javadoc)
	 * @see net.myorb.gui.components.SimplePopupRequest#getFrameWidth()
	 */
	public int getFrameWidth () { return JxrScriptChoice.getFrameWidth (); }

	/*
	 * feedback selection to user
	 */

	/* (non-Javadoc)
	 * @see net.myorb.gui.components.SimplePopupRequest#formatNotificationFor(java.lang.Object)
	 */
	public String formatNotificationFor (NameValuePair selectedItem) { return JxrScriptChoice.getNotification (); }

	/*
	 * identify selection to implementation
	 */

	/* (non-Javadoc)
	 * @see net.myorb.gui.components.SimplePopupRequest#setSelectedItem(java.lang.Object)
	 */
	public void setSelectedItem (NameValuePair item)
	{ JxrScriptChoice.runScript (item.getPath ()); }

}
