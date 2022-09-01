
package net.myorb.gui.palate;

import net.myorb.gui.graphics.ColorDisplays;
import net.myorb.gui.graphics.ColorPropertiesProcessor;
import net.myorb.gui.components.SimpleScreenIO;

/**
 * the palate tool component for application GUI management
 * @author Michael Druckman
 */
public class PalateTool extends SimpleScreenIO.Panel
{

	public PalateTool ()
	{
		ColorPropertiesProcessor.processColorList ();
		this.add (ColorDisplays.getGBmatrixPanel ());
	}
	private static final long serialVersionUID = -5619629168067179759L;

}
