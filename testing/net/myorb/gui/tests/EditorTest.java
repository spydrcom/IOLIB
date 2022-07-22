
package net.myorb.gui.tests;

import net.myorb.gui.components.SimpleScreenIO;

import javax.swing.*;
import java.awt.*;

public class EditorTest extends SimpleScreenIO {

	public static void main (String[] a)
	{
		JEditorPane editorPane = new JEditorPane();
		editorPane.setEditable(true);

		//Put the editor pane in a scroll pane.
		JScrollPane editorScrollPane = new JScrollPane(editorPane);
		editorScrollPane.setVerticalScrollBarPolicy(
		                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		editorScrollPane.setPreferredSize(new Dimension(250, 145));
		editorScrollPane.setMinimumSize(new Dimension(10, 10));

		new Frame (editorScrollPane, "HI").show();
	}
}
