
package net.myorb.gui.components;

import net.myorb.data.font.TTF;

import net.myorb.data.abstractions.Status;
import net.myorb.data.unicode.CrossReference;
import net.myorb.data.unicode.Nomenclature;
import net.myorb.data.unicode.BlockIndex;
import net.myorb.data.unicode.Toolkit;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.KeyEvent;

import java.awt.GraphicsEnvironment;
import java.awt.Component;
import java.awt.Font;

import java.util.ArrayList;
import java.util.List;

/**
 * display 16x16 block of Unicode characters
 * @author Michael Druckman
 */
public class UnicodeCharacterBlock extends SimpleScreenIO
{


	/**
	 * callback for selection from display
	 */
	public interface Selection
	{
		/**
		 * selection of a character passes character to handler
		 * @param c the selected character
		 */
		void unicodeCharacterSelected (char c);
	}
	public void setSelectionHandler (Selection handler) { this.handler = handler; }
	protected Selection handler = null;


	/**
	 * @param base the base of the display
	 * @param handler a handler for selection
	 */
	public UnicodeCharacterBlock (int base, Selection handler)
	{
		this (base); setSelectionHandler (handler);
	}
	public UnicodeCharacterBlock (int base)
	{
		Grid grid = new Grid (17, 17);										// 17x17 cells for 16x16 matrix plus row/column headers
		display (new Panel (grid));											// prepare and display matrix of zero based Unicode block
		goTo (base);														// shift block to base requested
	}
	public UnicodeCharacterBlock () {}


	/*
	 * display construction
	 */


	/**
	 * build and show matrix of 16x16 characters
	 * @param p the panel being constructed
	 */
	public void display (Panel p)
	{
		tabulate (p);														// fill in the matrix
		showBlock (p);
	}
	protected void tabulate (Panel p)
	{
		p.add (new Label ("  \\"));											// row/column header intersection
		addColumnHeaders (p);
		addRows (p);
	}
	protected void addColumnHeaders (Panel p)								// column header row
	{
		Label header;
		for (int i = 0; i < 16; i++)
		{
			p.add (header = Toolkit.hexLabelFor (i));
			header.setForeground (Colour.BLUE);
		}
	}
	protected void addRows (Panel p)										// matrix of symbols
	{
		int n = 0, remaining = 0;											// n = next HEX_SQ marker value
		for (int c = 0; c < HEX_THIRD_OFFSET; c++)
		{
			if (--remaining <= 0)											// detect start of row with count down
			{ n = post (p, n); remaining = HEX_SECOND_OFFSET; }				// special processing for HEX_CUBED mark
			addCell (Toolkit.labelFor (c), Toolkit.toHex (c), p);
		}
	}
	protected void addCell
	(Label cellDisplay, String cellValue, Panel toPanel)
	{
		toPanel.add (cellDisplay);											// label with text
		cellDisplay.setToolTipText (cellValue);								//  and tool-tip showing value
		cellDisplay.addMouseListener (selectionListener);					// one listener for all cells
		chooseFontSizeFor (cellDisplay);									// check for available fonts
		cells.add (cellDisplay);											// keep list for EZ updates
	}
	protected void chooseFontSizeFor (Label cellDisplay)
	{
		Toolkit.useAvailableFontOrResize
		(
			cellDisplay, BLOCK_FONT, BLOCK_FONT_SZ
		);
	}
	protected List<Label> cells = new ArrayList<Label>();


	/*
	 * 	maintenance for century marker and other row headers (decade markers)
	 * 
	 * 		marker mask (non-HEX_THIRD) = 0x00F0		offset = 0x0010
	 * 			  HEX_THIRD marker mask = 0x0F00		offset = 0x0100
	 * 
	 */


	public static final int
		HEX_THIRD_MASK  = 0x0F00, HEX_THIRD_OFFSET  = 0x0100,				// 3rd digit of value (as in 16^2)
		HEX_SECOND_MASK = 0x00F0, HEX_SECOND_OFFSET = 0x0010;				// 2nd digit of value (as in 16^1)

	public static final int
		HEX_THIRD_NUMBER_MASK = 0x00FF,										// low 2 nibbles constitute item number within HEX_THIRD
		HEX_SECOND_ALIGNMENT_MASK = 0xFFF0;									// low nibble 0 => HEX_SECOND 0 alignment


	/**
	 * century marker treatment
	 * @param p panel being built
	 * @param n value of row base
	 * @return next marker value
	 */
	protected int post (Panel p, int n)
	{
		Label markerLabel = newRowHeaderFor (p);
		int nextMarker = (n + HEX_SECOND_OFFSET) & HEX_THIRD_NUMBER_MASK;	// increment decade but mask off HEX_THIRD giving offset
		if (n == 0) centuryMarkerAlignment (markerLabel);					// check for alignment and set label value
		else Toolkit.setHexText (markerLabel, n);
		return nextMarker;
	}
	protected Label newRowHeaderFor (Panel p)								// prepare new label for use as row header in matrix panel
	{
		Label header;
		p.add (header = new Label (""));									// add empty label to matrix, text to be set later
		header.setForeground (Colour.BLUE);
		rowHeaders.add (header);											// keep as header label for this row
		return header;
	}
	protected List<Label> rowHeaders = new ArrayList<Label>();


	/**
	 * set century marker with aligned 24-bit value
	 */
	protected void centuryMarkerAlignment ()
	{ Toolkit.setHexText (centuryMarker, getNextAlignedBlockBase ()); }
	protected void centuryMarkerAlignment (Label label) { centuryMarker = label; centuryMarkerAlignment (); }
	protected Label centuryMarker;


	/*
	 * row marker maintenance and century/decade alignment
	 */


	/**
	 * adjust headers on goTo request
	 * @param base the base code for the block
	 */
	protected void adjustRowHeaders (int base)
	{
		alignBlock (base);
		int header = base;

		for (Label l : rowHeaders)
		{
			int offset = header & HEX_SECOND_MASK;

			if (offset == 0)
			{
				offset = header & HEX_THIRD_MASK;
				centuryMarker = l;
			}

			Toolkit.setHexText (l, offset);
			header += HEX_SECOND_OFFSET;
		}
	}


	/**
	 * adjust for non-00 alignment
	 * @param base the base code for the block
	 */
	protected void alignBlock (int base)
	{
		this.nextBlock = 0;
		if ((base & HEX_THIRD_NUMBER_MASK) != 0)
		{ this.nextBlock = HEX_THIRD_OFFSET; }								// when base is not 00 aligned century mark is mid block
	}
	protected int getNextAlignedBlockBase ()
	{ return (base + nextBlock) & HEX_THIRD_MASK; }
	protected int nextBlock = 0;
	protected int base = 0;


	/**
	 * request a Unicode value from the user
	 * @param relatedTo the panel generating the request
	 * @return the text entered by the user at the prompt
	 * @throws Alert for canceled request or illegal value
	 */
	protected static int getNewBase (Panel relatedTo) throws Alert
	{
		int value = 0;
		String text = requestTextInput (relatedTo, "New Page Base (Hex)", "Change Unicode Block");
		try { value = Toolkit.parseHex (text); } catch (Exception e) { Toolkit.parserAlert (relatedTo); }
		return value;
	}


	/**
	 * select page for code and notify user
	 * @param name the symbol used in the lookup
	 * @param relatedTo the panel generating the request
	 * @param info the information block for the character
	 * @throws Alert info alert for notification
	 */
	protected void symbolAlert
	(String name, Panel relatedTo, Nomenclature.BasicInformation info)
	throws Alert
	{
		int code;
		goTo (code = info.code);
		Toolkit.checkCodeSupported (code, BLOCK_FONT, relatedTo);
		Status status = Toolkit.localInfo (relatedTo).setTitle ("Symbol Processed");
		alert (status.setMessage ("Symbol '" + name + "' found, code " + info.toString ()));
	}


	/**
	 * use code related to symbol
	 */
	protected void lookupSymbol ()
	{
		try
		{
			Nomenclature.BasicInformation info = null;
			Panel localContext = getBlockDisplayPanel ();
			String text = requestTextInput (localContext, "Locate Symbol", "Unicode Symbol Find");
			try { info = Nomenclature.lookup (text); } catch (Exception e) { Toolkit.symbolLookupFailed (text, localContext, e); }
			if (info == null) Toolkit.symbolNotFound (text, localContext);
			symbolAlert (text, localContext, info);
		}
		catch (Alert alert) { alert.presentDialog (); }
	}


	/*
	 * page navigation
	 */


	/**
	 * change the focus point of the block display
	 * @param offset change to cell values
	 */
	public void pg_shift (int offset)
	{
		int check = base + offset;
		if (check < 0) return; else base = check;

		adjustRowHeaders (base);
		int cell; String tip;

		for (Label l : cells)
		{
			cell = Toolkit.charFrom (l) + offset;
			tip = Toolkit.toHex (cell);

			if (BLOCK_FONT != null)
			{
				l.setFont (BLOCK_FONT);
				if (BLOCK_FONT.canDisplay (cell))
				{
					Nomenclature.BasicInformation info = Nomenclature.getInformation (cell);
					if (info != null) tip += " [" + info.description + "]";
				}
				else { tip += " not in font"; }
			}

			l.setText (Toolkit.stringFor (cell));
			l.setToolTipText (tip);
		}

		setBlockDisplayTitle
		(titleFor ((char) base, BLOCK_FONT_SZ, "Unicode Block"));
		centuryMarkerAlignment ();
	}
	public void pg_up () { pg_shift (HEX_THIRD_OFFSET); }
	public void pg_down () { pg_shift (-HEX_THIRD_OFFSET); }


	/**
	 * change block base to arbitrary value
	 * @param newBase the value of first character on new page
	 */
	public void goTo (int newBase)
	{
		try
		{
			if (newBase > 0x10000)
				Toolkit.useBMPlaneOnly (getBlockDisplayPanel ());
			pg_shift ((newBase & HEX_SECOND_ALIGNMENT_MASK) - base); 		// force row alignment
		}
		catch (Alert alert) { alert.presentDialog (); }
	}

	/**
	 * change block base to value entered by user
	 */
	public void goTo ()
	{
		try { goTo (getNewBase (getBlockDisplayPanel ())); }
		catch (Alert alert) { alert.presentDialog (); }
	}


	/*
	 * block display frame management
	 */


	/**
	 * show the block display
	 * @param p panel holding formatted block display
	 */
	protected void showBlock (Panel p)
	{
		(blockDisplayFrame = showAndExit (p, "INIT...", 500, 500))			// show matrix panel in frame using title and panel
		.setInputListener (blockNavigation);								// allow key stroke input over entire frame
	}
	protected Frame blockDisplayFrame;

	/**
	 * @param title new title to be displayed
	 */
	protected void setBlockDisplayTitle (String title)
	{ blockDisplayFrame.setTitle (title); }

	/**
	 * @return display panel found at root of frame
	 */
	protected Panel getBlockDisplayPanel ()
	{ return blockDisplayFrame.getRoot (); }

	/**
	 * hide frame from user 
	 */
	protected void hideBlockDisplayFrame ()
	{ blockDisplayFrame.dispose (); }


	/*
	 * CrossReference object management
	 */


	/**
	 * show tabulated index
	 */
	protected void showIndex ()
	{
		activeReferences.add (new BlockIndex.Table (BLOCK_FONT, this));
	}

	/**
	 * show tabulated symbol dictionary
	 */
	protected void showDictionary ()
	{
		activeReferences.add (new Nomenclature.Dictionary (BLOCK_FONT, this));
	}

	/**
	 * dispose of CrossReference displays when code block becomes hidden
	 */
	protected void releaseTables ()
	{ releaseAll (); activeReferences.clear (); }
	protected void releaseAll () { for (CrossReference ref : activeReferences) ref.release (); }
	protected List<CrossReference> activeReferences = new ArrayList<CrossReference>();


	/*
	 * key and mouse adapters for block display
	 */


	/**
	 * keyboard input adapter for block display
	 */
	protected Frame.KeyboardListener blockNavigation = new Frame.KeyboardListener ()
	{
		/* (non-Javadoc)
		 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
		 */
		public void keyPressed (KeyEvent e)
		{
			int code;
			switch (code = e.getKeyCode ())
			{
				case 0x0047: goTo (); break;							// G
				case 0x0027: pg_up (); break;							// RIGHT ARROW
				case 0x0025: pg_down (); break;							// LEFT ARROW
				case 0x0049: showIndex (); break;						// I
				case 0x0053: lookupSymbol (); break;					// S
				case 0x0044: showDictionary (); break;					// D
				case 0x0026: adjustBlockPs (1); break;					// UP ARROW
				case 0x0028: adjustBlockPs (-1); break;					// DOWN ARROW
				case 0x004C: loadFont (e.getComponent ()); break;		// L
				case 0x0046: changeFont (e.getComponent ()); break;		// F
				default: System.out.println (Toolkit.toHex (code));
			}
		}
		public void keyReleased (KeyEvent e) {}
		public void keyTyped (KeyEvent e) {}
	};

	/**
	 * mouse click adapter for character selection in block display
	 */
	protected MouseAdapter selectionListener = new MouseAdapter ()
	{
		/* (non-Javadoc)
		 * @see java.awt.event.MouseAdapter#mouseClicked(java.awt.event.MouseEvent)
		 */
		public void mouseClicked (MouseEvent e)
		{
			char c = Toolkit.charFrom ((Label) e.getComponent ());
			if (handler != null) handler.unicodeCharacterSelected (c);
			hideBlockDisplayFrame ();
		}
	};


	/*
	 * character enlargement display
	 */


	/**
	 * show enlarged character in small frame
	 * @param c the character to be displayed in enlargement
	 */
	public void showCharacter (char c)
	{
		Label label; Panel panel;
		(panel = new Panel ()).add (label = Toolkit.labelFor (c));
		Toolkit.useAvailableFontOrResize (label, ENLARGED_FONT, ENLARGED_FONT_SZ);
		show (panel, titleFor (c, ENLARGED_FONT_SZ, "Unicode"), 300, 200)
			.setInputListener (returnToBlock);
		addDescription (c, label);
		releaseTables ();
	}
	public void addDescription (char forCharacter, Label toLabel)
	{
		Nomenclature.BasicInformation info = Nomenclature.getInformation (forCharacter);
		if (info != null) toLabel.setToolTipText (info.description);
	}

	/**
	 * @param c character being described
	 * @param size the point size to show in title
	 * @param tag a description for the type of display
	 * @return formatted title for character display
	 */
	public String titleFor (char c, int size, String tag)
	{
		String title = tag + " 0x" + Toolkit.toHex (c),
				name = cells.get (0).getFont ().getName ();
		title += " (" + name + ", ps=" + size + ")";
		return title;
	}

	/**
	 * show plot frame for GLYF of character
	 * @param c the character to render GLYF for
	 * @throws Alert for exceptions raised by font parser
	 */
	public static void showGlyf (char c) throws Alert
	{
		if (trueTypeFontParser == null)
		{
			alertError ("GLYF only available for loaded TTF fonts");
		}
		else
		{
			try
			{
				trueTypeFontParser.drawGlyf (c);
			}
			catch (Exception e) { trace (e); }
		}
	}
	static void trace (Exception e) throws Alert
	{
		if (tracing) e.printStackTrace (); alertBasedOn (e);
	}
	static boolean tracing = false;

	/**
	 * keyboard input adapter for enlargement display
	 */
	protected Frame.KeyboardListener returnToBlock = new Frame.KeyboardListener ()
	{
		/* (non-Javadoc)
		 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
		 */
		public void keyPressed (KeyEvent e)
		{
			int code, adjustBy = 0;
			Label label = Toolkit.labelFrom (e.getComponent ());
			char c = Toolkit.charFrom (label);

			try
			{
				switch (code = e.getKeyCode ())
				{
					case 0x0025: show (c); return;
					case 0x0026: adjustBy = 8; break;
					case 0x0028: adjustBy = -8; break;
					case 0x0047: showGlyf (c); return;
					default: System.out.println (Toolkit.toHex (code)); return;
				}
			}
			catch (Alert alert) { alert.presentDialog ();}

			if (ENLARGED_FONT == null) return;
			
			GuiToolkit.setTitle
			(
				DisplayFrame.toFrame (e.getComponent ()),
				titleFor (c, ENLARGED_FONT_SZ, "Unicode")
			);
			adjustEnlargementPs (adjustBy); label.setFont (ENLARGED_FONT);
		}
		public void keyReleased (KeyEvent e) {}
		public void keyTyped (KeyEvent e) {}
	};


	/*
	 * font manipulation
	 */


	/**
	 * @return available fonts in array
	 */
	public static Font[] getLocalFonts ()
	{
		return GraphicsEnvironment.getLocalGraphicsEnvironment ().getAllFonts ();
	}


	/**
	 * @return list of available font names
	 */
	public static List<String> getLocalFontNames ()
	{
		List<String> names = new ArrayList<String>();
		for (Font f : getLocalFonts ()) names.add (f.getName ());
		return names;
	}


	/**
	 * @param name the name of an available font
	 * @param pointSize the point size to be used for enlargements
	 */
	public static void useFontCalled (String name, int pointSize)
	{
		try
		{
			BLOCK_FONT = new Font (name, Font.PLAIN, BLOCK_FONT_SZ);
			ENLARGED_FONT = BLOCK_FONT.deriveFont ((float) pointSize);
		}
		catch (Exception e) { e.printStackTrace (); }
	}


	/**
	 * present font list and use new selected font
	 * @param c component requesting load
	 */
	public void changeFont (Component c)
	{
		try
		{
			String name = checkForText (requestInput
			(c, "Choose Font Name", "Select Font", getLocalFontNames ().toArray (), "Dialog.plain"), c);
			useFontCalled (name, ENLARGED_FONT_SZ); pg_shift (0); BlockIndex.showBMPlaneIndex (BLOCK_FONT);
			trueTypeFontParser = null;
		}
		catch (Alert alert) { alert.presentDialog (); }
		catch (Exception e) { e.printStackTrace (); }
	}


	/**
	 * load TTF file
	 * @param c component requesting load
	 */
	public void loadFont (Component c)
	{
		try
		{
			String path = "fonts/";
			String name = checkForText (requestInput
			(c, "Choose Font File", "Select Font File", new java.io.File (path).list (), ""), c);
			useTrueTypeFont (path+name, ENLARGED_FONT_SZ); pg_shift (0); BlockIndex.showBMPlaneIndex (BLOCK_FONT);
		}
		catch (Alert alert) { alert.presentDialog (); }
		catch (Exception e) { e.printStackTrace (); }
	}


	/**
	 * @param path full path to TTF file
	 * @param pointSize the enlarged point size to use
	 */
	public static void useTrueTypeFont (String path, int pointSize)
	{
		try
		{
			java.io.File
			file = new java.io.File (path);
			trueTypeFontParser = TTF.newInstance (file);
			Font BASIC_FONT = Font.createFont (Font.TRUETYPE_FONT, file);
			BLOCK_FONT = BASIC_FONT.deriveFont ((float) BLOCK_FONT_SZ);
			ENLARGED_FONT = BASIC_FONT.deriveFont ((float) pointSize);
			trueTypeFontParser.getNameTable ().print ();
		}
		catch (Exception e) { e.printStackTrace (); }
	}
	static TTF trueTypeFontParser = null;


	public void adjustBlockPs (int by)
	{ if (BLOCK_FONT == null) return; BLOCK_FONT = resize (BLOCK_FONT, BLOCK_FONT_SZ += by); pg_shift (0); }
	public void adjustEnlargementPs (int by) { ENLARGED_FONT = resize (ENLARGED_FONT, ENLARGED_FONT_SZ += by); }
	public Font resize (Font f, int ps) { return f.deriveFont ((float) ps); }
	public static int BLOCK_FONT_SZ = 18, ENLARGED_FONT_SZ = 80;
	public static Font BLOCK_FONT, ENLARGED_FONT;


	/*
	 * application entry points
	 */


	/**
	 * 
	 * show selection page and enlarge the selected character
	 * 
	 * @param p
	 * 		[0] the HEX base value for the first shown page of characters
	 * 		[1] the point size for display
	 */
	public static void main (String... p)
	{
		int base = 0, pointSize = 80;
		if (p.length > 0) base = Toolkit.parseHex (p[0]);
		if (p.length > 1) pointSize = Integer.parseInt (p[1]);
		if (p.length > 2) useFontCalled (p[2], pointSize);
		show (base);
	}


	/**
	 * show a block of Unicode characters
	 * @param base the base value for the first shown page of characters
	 * @return access to the block object
	 */
	public static UnicodeCharacterBlock show (int base)
	{
		UnicodeCharacterBlock block;
		(block = new UnicodeCharacterBlock (base)).setSelectionHandler
		(
			new UnicodeCharacterBlock.Selection ()
			{
				/* (non-Javadoc)
				 * @see net.myorb.gui.components.UnicodeCharacterBlock.Selection#unicodeCharacterSelected(char)
				 */
				public void unicodeCharacterSelected (char c) { block.showCharacter (c); }
			}
		);
		return block;
	}


}

