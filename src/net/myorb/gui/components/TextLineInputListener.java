
package net.myorb.gui.components;

import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

import javax.swing.JTextArea;
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


	/*
	 * connect with display component
	 */

	/**
	 * connect to a parent text component
	 * @param parent the text component using this as input
	 */
	public void setParent (JTextArea parent)
	{
		this.parent = parent;
	}
	protected JTextArea parent = null;

	/**
	 * add newline to display
	 */
	public void updateParent ()
	{
		if (parent != null) parent.append ("\n");
	}


	/*
	 * command line processing
	 */

	/**
	 * @param text data to be appended to line
	 */
	public void append (String text) { line.append (text); }
	protected StringBuffer line;

	/**
	 * ignore last key entered
	 */
	public void processBS ()
	{
		line.setLength (line.length () - 1);
	}

	/**
	 * ignore key input in ISO CTRL ranges
	 * @param c character being processed
	 */
	public void appendNonISO (char c)
	{
		if (Character.isISOControl (c)) return;
		line.append (c);
	}

	/**
	 * process collected line text
	 */
	public void processLine ()
	{
		updateParent ();
		processor.process (line.toString ());
		line = new StringBuffer ();
	}


	/*
	 * implementation of KeyListener
	 */

	/* (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
	 */
	public void keyTyped (KeyEvent e)
	{
		char c;
		switch (c = e.getKeyChar ())
		{
			case '\b':	processBS ();		break;	// backspace
			case '\n':	processLine ();		break;	// newline
			default:	appendNonISO (c);
		}
	}

	/* (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
	 */
	public void keyPressed (KeyEvent e) {}
	public void keyReleased (KeyEvent e) {}


	/*
	 * constructors
	 */

	/**
	 * @param processor for command line actions
	 */
	public TextLineInputListener (TextLineProcessor processor)
	{ this.processor = processor; line = new StringBuffer (); }
	protected TextLineProcessor processor;

	/**
	 * @param processor for command line actions
	 * @param c component that will use this as KeyListener
	 */
	public TextLineInputListener
	(TextLineProcessor processor, JComponent c)
	{ this (processor); c.addKeyListener (this); }

}
