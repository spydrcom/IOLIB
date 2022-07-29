
package net.myorb.gui.javaedit;

import javax.swing.text.ViewFactory;

import java.awt.Graphics;
import java.awt.Shape;

import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.Position.Bias;
import javax.swing.text.View;

public class SnipToolView implements ViewFactory
{

	public View create (Element elem)
	{
		return null;
	}

}

class ElementView extends View
{

	public ElementView (Element elem)
	{
		super(elem);
	}

	/* (non-Javadoc)
	 * @see javax.swing.text.View#getPreferredSpan(int)
	 */
	public float getPreferredSpan (int axis)
	{
		return 0;
	}

	/* (non-Javadoc)
	 * @see javax.swing.text.View#paint(java.awt.Graphics, java.awt.Shape)
	 */
	public void paint (Graphics g, Shape allocation)
	{
	}

	/* (non-Javadoc)
	 * @see javax.swing.text.View#viewToModel(float, float, java.awt.Shape, javax.swing.text.Position.Bias[])
	 */
	public int viewToModel (float x, float y, Shape a, Bias[] biasReturn)
	{
		return 0;
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.text.View#modelToView(int, java.awt.Shape, javax.swing.text.Position.Bias)
	 */
	public Shape modelToView (int pos, Shape a, Bias b) throws BadLocationException
	{
		return null;
	}

}

