
package net.myorb.gui.editor;

import net.myorb.gui.editor.model.*;

public interface SnipToolScanner
{

	void updateSource
	(StringBuffer source, int position);

	int getLastSourcePosition ();

	SnipToolToken getToken ();

	int getDefaultStyleCode ();

}
