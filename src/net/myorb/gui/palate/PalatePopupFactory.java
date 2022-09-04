
package net.myorb.gui.palate;

import net.myorb.gui.components.MenuListFactory;

/**
 * an implementation of the standard factory
 * @author Michael Druckman
 */
class PalatePopupFactory extends MenuListFactory
{
	PalatePopupFactory (PalateEditor editor)
	{
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
		editor.editPalate (PalateToolCommands.recognize (command), item);
	}
	PalatePopupProcessor (PalateEditor editor) { this.editor = editor; }
	protected PalateEditor editor;
}

