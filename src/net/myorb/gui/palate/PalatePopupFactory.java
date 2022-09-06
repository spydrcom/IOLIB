
package net.myorb.gui.palate;

import net.myorb.gui.components.MenuListFactory;

/**
 * an implementation of the standard factory
 * @author Michael Druckman
 */
class PalatePopupFactory extends MenuListFactory
{
	/**
	 * menu items from the pop-up invoke palate editor commands
	 * @param editor the palate editor that implements the commands
	 */
	PalatePopupFactory (PalateEditor editor)
	{
		// the names are coming from the enum of editor commands
		// - the palate processor accepts the commands and passes on to the editor
		super (PalateToolCommands.getNames (), new PalatePopupProcessor (editor));
	}
}


/**
 * the processor for the standard factory
 */
class PalatePopupProcessor implements MenuListFactory.Processor
{
	/* (non-Javadoc)
	 * @see net.myorb.gui.components.MenuListFactory.Processor#process(java.lang.String, int)
	 */
	public void process (String command, int item)
	{
		// the processor recognizes the editor commands
		// - recognized commands are passed to the editor for action
		editor.editPalate (PalateToolCommands.recognize (command), item);
	}
	PalatePopupProcessor (PalateEditor editor) { this.editor = editor; }
	protected PalateEditor editor;
}

