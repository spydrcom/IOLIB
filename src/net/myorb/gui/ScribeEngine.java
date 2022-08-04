
package net.myorb.gui;

import net.myorb.gui.components.text.TextSource;

import javax.swing.text.Style;
import javax.swing.text.Segment;
import javax.swing.text.TabExpander;

import javax.swing.text.BadLocationException;
import javax.swing.text.Utilities;

import java.awt.Graphics;

import java.awt.Color;
import java.awt.Font;

/**
 * a container for objects needed to draw text
 * @author Michael Druckman
 */
public class ScribeEngine
{


	/**
	 * style attributes to apply to text
	 */
	public interface Attribution
	{

		/**
		 * @param styleCode the code that identifies a style
		 * @return the style that matches the code
		 */
		Style getStyleFor (int styleCode);

		/**
		 * @param styleCode the code that identifies a color
		 * @return the identified color
		 */
		Color getColorFor (int styleCode);

		/**
		 * @param styleCode the code that identifies a font
		 * @return the identified font
		 */
		Font getFontFor (int styleCode);
	}


	/**
	 * build a container that holds the graphic object and position for that structure
	 * @param x the x coordinate within the Graphic to start the draw of the text
	 * @param y the y coordinate within the Graphic to start the draw of the text
	 * @param tabExpander the tab expander assigned for the render in the caller
	 * @param graphcs the Graphic being constructed for the render
	 * @param attribution the style context manager
	 */
	public ScribeEngine
		(
			int x, int y, TabExpander tabExpander,
			Graphics graphcs, Attribution attribution
		)
	{
		this.graphcs = graphcs;
		this.tabExpander = tabExpander;
		this.attribution = attribution;
		this.text = new Segment ();
		this.x = x; this.y = y;
	}
	protected TabExpander tabExpander;
	protected Graphics graphcs;			// the graphics object being drawn
	protected Segment text;				// text buffer for segmented read
	protected int x, y;					// AWT graphics coordinates


	/**
	 * draw a range in model coordinates
	 * @param source a source for text data
	 * @param start the start in model coordinates
	 * @param end the end of the range in model coordinates
	 * @param styleCode the code describing the style to use for draw
	 * @return the x coordinate of end point within the Graphic
	 * @throws BadLocationException for the text draw layer
	 */
	public int draw
		(
			TextSource source, 		// source of content
			int start, int end,		// document model coordinates
			int styleCode			// context assigned code
		)
	throws BadLocationException
	{
		getSegment (source, start, end);
		return x = draw (start, styleCode);
	}


	/**
	 * get document text found in range
	 * @param source access to the source text
	 * @param from start position of range of interest
	 * @param to the end position for the range of interest
	 * @return Segment holding content found in specified range
	 * @throws BadLocationException for segment errors
	 */
	public Segment getSegment
		(TextSource source, int from, int to)
	throws BadLocationException
	{
		source.getText (from, to - from, text);
		return text;
	}


	/**
	 * use text Utilities to draw text into graphics
	 * @param mark the start point describing the location within the model
	 * @param styleCode the style to use which identifies the font and color
	 * @return the x coordinate within the Graphic ending the draw of the text
	 * @throws BadLocationException for the tabbed text draw layer
	 */
	public int draw
		(
			int mark,
			int styleCode
		)
	throws BadLocationException
	{
		attribute (graphcs, styleCode);

		return Utilities.drawTabbedText
		(
			text, x, y, graphcs,
			tabExpander, mark	
		);
	}


	/**
	 * apply a style code to a Graphic being rendered
	 * @param g the Graphic being constructed for the render
	 * @param styleCode the code that identifies a style
	 */
	public void attribute 
		(
			Graphics g,
			int styleCode
		)
	{
		g.setColor (attribution.getColorFor (styleCode));
		g.setFont (attribution.getFontFor (styleCode));
	}
	protected Attribution attribution;


}

