
package net.myorb.gui.components;

import net.myorb.data.abstractions.HtmlTable;
import net.myorb.data.abstractions.SimpleStreamIO;

/**
 * display a column of components in scrolling frame
 * @author Michael Druckman
 */
public class RenderingDisplay extends SimpleScreenIO
{


	/**
	 * no rendering frame required
	 */
	public RenderingDisplay () {}


	/**
	 * @param title a title for the frame
	 * @param width the width for the initial display
	 * @param height the height for the initial display
	 */
	public RenderingDisplay (String title, int width, int height)
	{
		createRenderingPanel ();
		f = new WidgetFrame (getRenderingPanel (), title);
		f.setIcon ("images/icon-render.gif");
		f.showOrHide (width, height);
	}
	public Frame getFrame () { return f; }
	protected Frame f = null;


	/**
	 * allocate a new display object
	 *  with rendering panel prepared for use by renderers
	 * @return a new instance of the RenderingDisplay
	 */
	public static RenderingDisplay newRenderingDisplayPanel ()
	{
		RenderingDisplay display = new RenderingDisplay ();
		display.createRenderingPanel ();
		return display;
	}


	/**
	 * create panel that rendering operations will use
	 */
	public void createRenderingPanel ()
	{ (p = new Panel ()).setBackground (Colour.WHITE); }
	protected Panel p = null;


	/**
	 * @return the panel generated for this display
	 */
	public Widget getRenderingPanel ()
	{
		return p.scrolling ();
	}


	/**
	 * @param widget a component to add to column
	 */
	public void addWidget (Widget widget)
	{
		//addComponent (widget.toComponent ());
		hideRenderFrame (); p.addWidget (widget); showRenderFrame ();
	}


//	/**
//	 * original version using swing or AWT components.
//	 *  since replaced with SimpleStreamIO layer over swing.
//	 *  Panel addWidget replaces simple add, see addWidget above
//	 * @param component a general component to add to column
//	 */
//	public void addComponent (java.awt.Component component)
//	{
//		hideRenderFrame (); p.add (component); showRenderFrame ();
//	}


	/**
	 * @param markup text of markup to place in label component
	 */
	public void addMarkup (String markup)
	{
		addWidget (newComponentFor (markup));
	}


	/**
	 * @param markup an HTML table markup object
	 */
	public void addComponent (HtmlTable markup)
	{
		addMarkup (markup.toString ());
	}


	/**
	 * @param markup an HTML table markup object
	 * @param withMenu a menu to be added to component
	 * @return THIS rendering object
	 */
	public RenderingDisplay addComponent (HtmlTable markup, Menu withMenu)
	{
		Widget label =
			newComponentFor (markup.toString ());
		withMenu.addAsPopupToWidget (label);
		addWidget (label);
		return this;
	}


	/**
	 * render HTML document with Open With menu connected
	 * @param markup HTML object being rendered
	 */
	public void addComponentWithMenu (HtmlTable markup)
	{
		addComponent (markup, new OpenWithMenu ());
	}


	/**
	 * @param markup text to be held in component
	 * @return the component for the display
	 */
	public Widget newComponentFor (String markup)
	{
		return widget = new Label (markup, Label.CENTER);
	}


	/**
	 * the component label
	 *  will have been set with HTML in the text property
	 * @return the text being displayed in the component
	 */
	public String getComponentText ()
	{ return widget.getText (); }
	protected Label widget;


	/**
	 * prepare text as source to OpenWith functions
	 * @return stream access to component text
	 */
	public SimpleStreamIO.TextSource getComponentTextSource ()
	{
		return new SimpleStreamIO.SingleLineSource (getComponentText ());
	}


	/**
	 * save content to TEMP file and return path
	 * @return path to HTML TEMP file holding content
	 * @throws Exception for any errors
	 */
	public String getComponentTempFilePath () throws Exception
	{
		return SimpleStreamIO.saveToTempFile
		(getComponentTextSource (), "DOC", ".html")
		.getAbsolutePath ();
	}


	/**
	 * show frame
	 */
	public void showRenderFrame ()
	{
		if (f != null) f.forceToScreen ();
	}


	/**
	 * hide frame
	 */
	public void hideRenderFrame ()
	{
		if (f != null) f.removeFromScreen ();
	}


	/**
	 * menu allows external APP access to HTML documents
	 */
	public class OpenWithMenu extends Menu
		implements AppAccessMenuManager.ResourceAccess
	{
		/* (non-Javadoc)
		 * @see net.myorb.gui.components.AppAccessMenuManager.ResourceAccess#getSelectedPath()
		 */
		public String getSelectedPath () throws Exception { return getComponentTempFilePath (); }
		public OpenWithMenu () { AppAccessMenuManager.addToMenu (this, this); }
		private static final long serialVersionUID = 5171280780578361803L;
	}


}

