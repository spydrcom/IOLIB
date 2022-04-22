
package net.myorb.gui.tests;

import net.myorb.gui.components.GuiToolkit;
import net.myorb.gui.components.DisplayContainer;

import javax.swing.ImageIcon;

public class IconTest
{
	public static void main (String[] args)
	{
		ImageIcon icon = new ImageIcon ("images/logo.gif");
		DisplayContainer root = GuiToolkit.newDisplayContainer ("icon test");
		root.setIconImage(icon.getImage());
		GuiToolkit.centerAndShow(root);
	}
}
