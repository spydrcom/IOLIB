
package net.myorb.gui.graphics;

import net.myorb.gui.components.SimpleScreenIO;

import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

/**
 * manage arrow key navigation from center location
 *  to any of 8 locations around the center
 * @author Michael Druckman
 */
public class NAV extends SimpleScreenIO implements KeyListener
{


	/**
	 * the directions of motion
	 */
	public enum Direction {UP, RIGHT, DOWN, LEFT}


	/**
	 * the locations in the display matrix
	 */
	public enum Location
	{

		LU (-1,  1,  3, -1), CU (-1,  2,  4,  0), RU (-1, -1,  5,  1),		// U = UP,   M = MIDDLE, D = DOWN
		LM ( 0,  4,  6, -1), CM ( 1,  5,  7,  3), RM ( 2, -1,  8,  4),		// L = LEFT, C = CENTER, R = RIGHT
		LD ( 3,  7, -1, -1), CD ( 4,  8, -1,  6), RD ( 5, -1, -1,  7)
		;

		/**
		 * @param u the UP destination
		 * @param r the TIGHT destination
		 * @param d the DOWN destination
		 * @param l the LEFT destination
		 */
		private Location (int u, int r, int d, int l)
		{ destination = new int[]{u, r, d, l}; }
		private int destination [];

		/**
		 * @param direction the direction to move
		 * @return the destination location, or NULL for ILLEGAL move
		 */
		public Location move (Direction direction)
		{
			int to = destination [direction.ordinal ()];
			if (to < 0) return null; else return Location.values () [to];
		}

		/**
		 * @return an object that has become associated with this enum constant
		 */
		public Object getAssociatedWith () { return associatedWith; }
		public void setAssociatedWith (Object associatedWith)
		{ this.associatedWith = associatedWith; }
		private Object associatedWith;

	}


	/**
	 * @param code one of 4 arrow key codes
	 * @return the direction for the 4 codes, or null
	 */
	public static Direction forCode (int code)
	{
		switch (code)
		{
			case 38: return Direction.UP;
			case 37: return Direction.LEFT;
			case 39: return Direction.RIGHT;
			case 40: return Direction.DOWN;
			default: return null;
		}
	}


	/**
	 * @param images an image per Location [0..9]
	 */
	public void connect (Image[] images)
	{
		for (int i = 0; i < 9; i++)
		{
			Location.values () [i].setAssociatedWith (images[i]);
			trace (i, images);
		}
	}


	/**
	 * show matrix as built
	 * @param i the index of the item added
	 * @param images the full list of images being added
	 */
	void trace (int i, Image[] images)
	{
		if ( ! TRACING ) return;
		String named = Location.values () [i].toString ();
		System.out.println ("pos: " + i + " named: " + named + " img: " + images[i]);
	}
	private boolean TRACING = false;


	/**
	 * @return the image widget in the GUI display
	 */
	public Image getShowing () { return onDisplay; }

	/**
	 * @param showing the image widget in the GUI display
	 */
	public void setShowing (Image showing) { onDisplay = showing; }
	private Image onDisplay;


	/**
	 * @return the image associated with current location
	 */
	public Image currentImage ()
	{ return (Image) position.getAssociatedWith (); }

	/**
	 * @param next the location becoming current
	 */
	public void setCurrentImage (Location next)
	{ position = next; onDisplay.setIcon (currentImage ().getIcon ()); }
	private Location position;


	/* (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
	 */
	@Override public void keyPressed (KeyEvent event)
	{
		Location to; Direction d;
		int code = event.getKeyCode ();						// the code for the key pressed
		if ((d = forCode (code)) == null) return;			// the direction indicated by the code
		if ((to = position.move (d)) != null)				// the destination for the requested move
		{ setCurrentImage (to); }							// update image based on new location
	}


	// unused KeyListener functionality
	@Override public void keyReleased (KeyEvent event) {}
	@Override public void keyTyped (KeyEvent event) {}


	/**
	 * simplest frame configuration for using NAV
	 * @param title the title for the frame with NAV functionality
	 * @param images the 9 images of the navigated view
	 */
	public void show (String title, Image[] images)
	{
		setShowing (new Image ());							// Image that will show current selection
		connect (images); setCurrentImage (Location.CM);	// set image array and show center/middle as default
		Frame f = new Frame (getShowing (), title);			// show selected image in frame
		((java.awt.Component)f.show ()).addKeyListener (this);					// apply key listener
	}


}

