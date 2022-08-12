
package net.myorb.gui.components;

import net.myorb.gui.StyleManager;

import net.myorb.gui.editor.model.SnipToolToken;
import net.myorb.gui.editor.SnipToolPropertyAccess;
import net.myorb.gui.editor.SnipToolScanner;

import javax.swing.text.JTextComponent;
import javax.swing.text.BadLocationException;

import javax.swing.text.StyledDocument;
import javax.swing.text.Style;

/**
 * a display based on
 *  full token analysis and styled functionality
 * @author Michael Druckman
 */
public class SnipAnalyzer extends SnipFrame
{


	public static final String EOL = "\n", CRLF = "\r"+EOL;


	public SnipAnalyzer
		(
			SnipToolPropertyAccess properties
		)
	{
		super ();
		this.properties = properties;
		this.doc = getStyledDocument ();  
		this.styles = properties.newContext ();
		this.scanner = properties.getScanner ();
		this.scanner.trackWS ();
	}
	protected StyleManager styles;
	protected SnipToolPropertyAccess properties;
	protected SnipToolScanner scanner;
	protected StyledDocument doc;


	public SnipAnalyzer
		(
			JTextComponent source,
			SnipToolPropertyAccess properties
		)
	{
		this (properties);
		this.source = source;
		this.scanSource ();
	}
	protected StringBuffer content;


	/**
	 * @param text the source to add to the document
	 * @param style the attributed style to use for formatting
	 * @throws BadLocationException for errors in the model
	 */
	public void append
		(String text, Style style)
	throws BadLocationException
	{
		doc.insertString (doc.getLength (), text, style);
	}


	/**
	 * @param text the source to add to the document
	 * @param styleCode the code assigned by the style manager
	 * @throws BadLocationException for errors in the model
	 */
	public void append
		(String text, int styleCode)
	throws BadLocationException
	{
		append (text, styles.getStyleFor (styleCode));
	}


	/**
	 * scan a line of text with a token parser
	 * - tokens are appended as source into the document
	 * @throws Exception for errors in the model
	 */
	public void scanLine () throws Exception
	{
		SnipToolToken token;
		this.content = new StringBuffer (lines[line]);
		scanner.updateSource (content, 0);
		token = scanner.getToken ();

		while (token != null)
		{
			String text = token.toString ();
			append (text, token.getStyleCode ());
			token = scanner.getToken ();
		}

		append (CRLF, properties.getDefaultStyleCode ());
	}


	/**
	 * scan each line found in the source
	 */
	public void scanSource ()
	{
		try
		{
			splitSourceLines ();
			while (line < lines.length)
			{
				scanLine ();
				line++;
			}
		} catch (Exception e) { e.printStackTrace (); }
	}


	/**
	 * break the source into lines broken at EOL
	 */
	public void splitSourceLines ()
	{
		this.lines = source.getText ().split (EOL);
		this.line = 0;
	}
	protected String[] lines;
	protected int line;


	private static final long serialVersionUID = -7062749876634325115L;

}

