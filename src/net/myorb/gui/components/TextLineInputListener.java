
package net.myorb.gui.components;

import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

import javax.swing.JComponent;

/**
 * provide for key listener that will parse text lines
 * @author Michael Druckman
 */
public class TextLineInputListener
	extends SimpleScreenIO implements KeyListener
{

	/**
	 * an object prepared to receive and process text
	 */
	public interface TextLineProcessor
	{
		/**
		 * @param line the text to be processed
		 */
		void process (String line);
	}

	/* (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
	 */
	public void keyTyped (KeyEvent e)
	{
		char c;
		if ((c = e.getKeyChar ()) == '\n')
		{ processor.process (line); line = ""; }
		else { line = line + c; }
	}

	/**
	 * @param text data to be appended to line
	 */
	public void append (String text)
	{
		line = line + text;
	}

	/* (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
	 */
	public void keyPressed (KeyEvent e) {}
	public void keyReleased (KeyEvent e) {}
	protected String line = "";

	public TextLineInputListener
	(TextLineProcessor processor)
	{ this.processor = processor; }
	protected TextLineProcessor processor;

	public TextLineInputListener
	(TextLineProcessor processor, JComponent c)
	{ this (processor); c.addKeyListener (this); }

}
