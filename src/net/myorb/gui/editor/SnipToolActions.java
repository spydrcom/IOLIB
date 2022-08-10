
package net.myorb.gui.editor;

import javax.swing.text.JTextComponent;
import java.awt.event.ActionListener;

/**
 * implementation of the editor actions
 * @author Michael Druckman
 */
public class SnipToolActions extends SnipToolProcessing
{


	/**
	 * @param properties the properties passed from upper layers
	 */
	public void provideAccess  (SnipToolPropertyAccess properties)
	{
		this.properties = properties;
	}
	protected SnipToolPropertyAccess properties;


	/**
	 * use environment control to get execution processor
	 * @param source source of command to execute in CalcLib processor
	 */
	public void process (String source)
	{
		properties.execute (source);
	}


	/**
	 * identify from which text to be edited is drawn
	 * @param source the component holding original source
	 */
	public void setSource (JTextComponent source)
	{
		this.source = source;
	}
	public JTextComponent getSource () { return source; }
	protected JTextComponent source;


	/**
	 * get access to APP top layer functionality
	 * @param tool the snip tool class using these actions
	 */
	public void connectTool (SnipTool tool)
	{
		this.tool = tool;
	}
	protected SnipTool tool;


	/*
	 * Open and Edit action items
	 */

	/**
	 * change name on tab
	 * @return action for feature
	 */
	public ActionListener getNameAction ()
	{
		return (e) -> { setToRequestedName (); };
	}

	/**
	 * read file contents into current tab
	 * - file name taken from file selected in table
	 * - user GUI request made for file name when nothing selected
	 * @return action for feature
	 */
	public ActionListener getOpenAction ()
	{
		return (e) -> { open (properties); };
	}

	/**
	 * save tab contents to file
	 * - user GUI request specifies file name
	 * - tab name is also changed to user specified name
	 * @return action for feature
	 */
	public ActionListener getSaveAsAction ()
	{
		return (e) -> { saveAs (properties); };
	}

	/**
	 * save tab contents to file given tab name
	 * @return action for feature
	 */
	public ActionListener getSaveAction ()
	{
		return (e) -> { save (properties); };
	}

	/**
	 * NOT implemented
	 * @return action for feature
	 */
	public ActionListener getCopyAction ()
	{
		return (e) ->
		{
			throw new RuntimeException ("NOT implemented");
		};
	}

	/**
	 * eXpand to full screen
	 * @return action for feature
	 */
	public ActionListener getExpandAction ()
	{
		return (e) -> { expand (properties); };
	}

	/**
	 * analyZe content in full screen
	 * @return action for feature
	 */
	public ActionListener getAnalyzeAction ()
	{
		return (e) -> { analyze (properties); };
	}


	/*
	 * Execute action items
	 */

	/**
	 * @param line a text line to execute
	 */
	public void exec (String line)
	{
		source.setText (source.getText () + line);
		process (line);
	}

	/**
	 * execute a block of text
	 * @param text the block of text lines
	 * @param sep a separation character for breaking the lines
	 */
	public void exec (String text, String sep)
	{
		//SnipToolSupport.dump (text);
		String[] lines = text.split (sep);
		for (String line : lines) exec (line + "\r\n");
	}

	/**
	 * execute full contents of editor
	 * @return the action implementation
	 */
	public ActionListener getExecAllAction ()
	{
		return (e) ->
		{
			exec (getTextContainer ().getText (), "\r");
		};
	}

	/**
	 * execute selected contents of editor
	 * @return the action implementation
	 */
	public ActionListener getExecSelectedAction ()
	{
		return (e) ->
		{
			exec (getTextContainer ().getSelectedText (), "\n");
		};
	}


}

