
package net.myorb.gui.graphics;

import net.myorb.gui.components.SimpleScreenIO;
import net.myorb.gui.components.MenuListFactory;
import net.myorb.gui.components.MenuManager;

import javax.swing.border.LineBorder;

import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;

import java.awt.Dimension;
import java.awt.Color;

/**
 * swing displays of color lists
 * @author Michael Druckman
 */
public class ColorDisplays extends SimpleScreenIO
{


	/**
	 * construct display panel for a palate
	 * @param name the name of the color to show
	 * @param comment a comment to append to the name
	 * @param tip a tip to show as tool tip on mouse over
	 * @param listener a mouse listener to add to the display cell
	 * @return a panel holding the display
	 */
	public static Panel getDisplayFor
	(String name, String comment, String tip, MouseListener listener)
	{
		return getDisplayFor (name, comment, tip, ColorNames.systemColorMap, listener);
	}


	/**
	 * construct display panel for a palate
	 * @param name the name of the color to show
	 * @param comment a comment to append to the name
	 * @param tip a tip to show as tool tip on mouse over
	 * @param colorMap the color map to use for the display
	 * @param listener a mouse listener to add to the display cell
	 * @return a panel holding the display
	 */
	public static Panel getDisplayFor
		(
			String name, String comment, String tip,
			ColorNames.ColorMap colorMap,
			MouseListener listener
		)
	{
		Panel cell = new Panel ();
		cell.setBorder (new LineBorder (Color.WHITE, 2));
		Color color = colorMap.get (name); int rgb = color.getRGB ();
		String code = Integer.toHexString (rgb & ColorNames.RGB_MASK).toUpperCase ();
		Label l = new Label (name + " [" + code + "] " + comment); cell.setBackground (color);
		if (listener != null) { cell.addMouseListener (listener); l.addMouseListener (listener); }
		int g = color.getGreen () / 32, b = color.getBlue () / 32;
		if (g < 4 || b < 4) l.setForeground (Colour.WHITE);
		if (tip != null) l.setToolTipText (tip);
		l.setName (name); cell.setName (name);
		cell.add (l);
		return cell;
	}


	/**
	 * @param names the list of color names
	 * @param menuFactory the factory for pop-up menu generation
	 * @param map a map of names to the colors
	 * @param p the panel to be appended
	 */
	public static void addToPalettePanel
		(
			ColorNames.ColorList names,
			MenuListFactory menuFactory,
			ColorNames.ColorMap map,
			Panel p
		)
	{
		Panel d;
		int rgb, prv = -1, item = 0;

		for (String name : names)
		{
			if ((rgb = RGBfor (name, map)) == prv) continue;

			p.add
			(
				d = getDisplayFor
				(
					name, "", null, map,
					menuFactory != null? null:
					getPaletteMouseListener (map)
				)
			);

			if (menuFactory != null)
			{
				MenuManager.addPopup (menuFactory.getActionList (item++), d);
			}

			prv = rgb;
		}
	}
	static int RGBfor (String name, ColorNames.ColorMap map)
	{
		return map.get (name).getRGB ();
	}


	/**
	 * @param names the list of color names
	 * @param menuFactory the factory for pop-up menu generation
	 * @param map a map of names to the colors
	 * @return the panel being built
	 */
	public static Panel paletteColumnPanel
		(
			ColorNames.ColorList names,
			MenuListFactory menuFactory,
			ColorNames.ColorMap map
		)
	{
		Panel p = startGridPanel (null, 0, 1);
		addToPalettePanel (names, menuFactory, map, p);
		return p;
	}


	/**
	 * @param names the list of color names
	 * @param menuFactory the factory for pop-up menu generation
	 * @param map a map of names to the colors
	 */
	public static void showPaletteColumn
		(
			ColorNames.ColorList names,
			MenuListFactory menuFactory,
			ColorNames.ColorMap map
		)
	{
		Frame f = new Frame
			(
				new Scrolling
				(paletteColumnPanel (names, menuFactory, map)),
				"Palate"
			);
		f.showAndExit ();
	}


	/**
	 * @param CL list of color names
	 * @param map color object for given name
	 * @return grid panel for display
	 */
	public static Panel palettePanelFor
		(ColorNames.ColorList CL, ColorNames.ColorMap map)
	{
		Panel p = startGridPanel ( null, 0, 8 );
		addToPalettePanel ( CL, null, map, p );
		return p;
	}


	/**
	 * show all colors ordered by RGB code
	 */
	public static void showFullPalette ()
	{
		Frame f = new Frame
			(fullPalettePanel (), "Full Palette");
		f.showAndExit ();
	}
	public static Panel fullPalettePanel ()
	{
		return palettePanelFor
		(
			ColorNames.byCode (),
			ColorNames.systemColorMap
		);
	}


	/**
	 * color list built for Temperature model
	 */
	public static void showTempPalette ()
	{
		Frame f = new Frame
			( tempPalettePanel (), "Temperature Palette" );
		f.showAndExit ();
	}
	public static Panel tempPalettePanel ()
	{
		ColorNames.ColorMap map; ColorNames.ColorList CL;

		ColorNames.fillTemperatureMap
		(
			CL = new ColorNames.ColorList (),
			map = new ColorNames.ColorMap ()
		);

		return palettePanelFor ( CL, map );

	}


	/**
	 * color list built by hue
	 */
	public static void showHSBPalette ()
	{
		Frame f = new Frame
			( hsbPalettePanel (), "HSB Palette" );
		f.showAndExit ();
	}
	public static Panel hsbPalettePanel ()
	{
		ColorNames.ColorList CL;
		dumpHSB ( CL = ColorNames.buildHsbMap () );
		return palettePanelFor ( CL, ColorNames.hsbAlgorithmMap );
	}
	public static void dumpHSB (ColorNames.ColorList CL)
	{
		for (String name : CL)
		{
			int rgb = RGBfor ( name, ColorNames.hsbAlgorithmMap );
			String rep = Integer.toHexString ( rgb ).toUpperCase ();
			System.out.println ( rep.substring (2) );
		}
	}


	/**
	 * @param map the map of names to colors
	 * @return a listener that fields Palate requests
	 */
	public static MouseListener getPaletteMouseListener (ColorNames.ColorMap map)
	{
		return new MouseListener ()
		{
			/* (non-Javadoc)
			 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
			 */
			public void mouseClicked (MouseEvent event)
			{
				//System.out.println (event.getComponent ().getName ());
				addToPalette (event.getComponent ().getName (), map);
			}
			public void mousePressed (MouseEvent event) {}
			public void mouseReleased (MouseEvent event) {}
			public void mouseEntered (MouseEvent event) {}
			public void mouseExited (MouseEvent event) {}
		};
	}


	/**
	 * @param name the name of a color
	 * @param map the map of name to color
	 */
	public static void addToPalette (String name, ColorNames.ColorMap map)
	{
		if (palette == null)
		{
			palette = new Frame
				(new Scrolling (paletteList), "Palette");
			palette.showOrHide (new Dimension (300, 600));
		}
		else
		{
			palette.removeFromScreen ();
		}

		nameList.add (name); System.out.println (nameList);
		paletteList.add (getDisplayFor (name, "", null, map, null));
		palette.forceToScreen ();
	}
	static Panel paletteList = startGridPanel (null, 0, 1);
	static ColorNames.ColorList nameList = new ColorNames.ColorList ();
	static Frame palette = null;


	/**
	 * show arbitrary group of colors
	 * @param group the list of names in the group
	 */
	public static void showGroup (ColorNames.ColorList group)
	{
		//System.out.println (group);
		Panel p = startGridPanel (null, 0, 8);

		for (String name : group)
		{
			p.add
			(
				getDisplayFor
				(
					name, "", null,
					getPaletteMouseListener (ColorNames.systemColorMap)
				)
			);
		}

		Frame f = new Frame (p, "Colors " + group);
		f.showOrHide ();
	}


	/**
	 * @param group the group to display on click event
	 * @return a listener that will show a group
	 */
	public static MouseListener showGroupMouseListener (final ColorNames.ColorList group)
	{
		return new MouseListener ()
		{
			/* (non-Javadoc)
			 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
			 */
			public void mouseClicked (MouseEvent event)
			{
				showGroup (group);
			}
			public void mousePressed (MouseEvent event) {}
			public void mouseReleased (MouseEvent event) {}
			public void mouseEntered (MouseEvent event) {}
			public void mouseExited (MouseEvent event) {}
		};
	}


	/**
	 * show green/blue matrix
	 */
	public static void showGBmatrix ()
	{
		Frame f = new Frame (getGBmatrixPanel (), "GB Matrix");
		f.showAndExit ();
	}
	public static Panel getGBmatrixPanel ()
	{
		Label l;
		Panel p = startGridPanel (null, 8, 8);
		ColorNames.ColorList[][] gbm = ColorNames.gbMatrix ();

		for (int g = 0; g < 8; g++)
		{
			for (int b = 0; b < 8; b++)
			{
				Panel cell;  int count;
				ColorNames.ColorList list = gbm[g][b];

				if ((count = list.size ()) > 0)
				{
					cell = getDisplayFor
					(
						list.get (0), " : " + count, list.toString (),
						showGroupMouseListener (list)
					);
				}
				else
				{
					cell = new Panel ();
					l = new Label ("EMPTY");
					cell.add (l);
				}

				p.add (cell);
			}
		}

		return p;
	}


}

