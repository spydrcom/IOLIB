
package net.myorb.gui.components;

import net.myorb.data.abstractions.BinaryStateMonitor;
import net.myorb.data.abstractions.SimpleUtilities;
import net.myorb.data.abstractions.Status;

import net.myorb.gui.BackgroundTask;

import javax.swing.SwingUtilities;
import javax.swing.ImageIcon;


import javax.swing.text.JTextComponent;
//import javax.swing.text.DefaultEditorKit;
import javax.swing.text.StyledEditorKit;

import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.BorderFactory;

import javax.swing.JViewport;
import javax.swing.JScrollPane;
import javax.swing.JScrollBar;

import javax.swing.RootPaneContainer;
import javax.swing.JComponent;

import javax.swing.JToggleButton;
import javax.swing.JButton;

import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
//import javax.swing.JEditorPane;
import javax.swing.JTextPane;

import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.JCheckBox;
import javax.swing.JMenuBar;

import javax.swing.JLabel;
import javax.swing.JPanel;

import java.awt.event.ActionListener;

import java.awt.LayoutManager;
import java.awt.GridLayout;
import java.awt.Dimension;
import java.awt.Container;
import java.awt.Component;
//import java.awt.Font;
import java.awt.Color;

import java.lang.reflect.InvocationTargetException;

import java.util.ArrayList;
import java.util.List;

/**
 * provide for encapsulation of simple swing display construction
 * @author Michael Druckman
 */
public class SimpleScreenIO
{


	/**
	 * import and rename Color class
	 */
	public static class Colour extends Color
	{
		Colour () { super (0); }
		private static final long serialVersionUID = 547619961625868610L;
	}


	/**
	 * R, G, and B set to same value produce shade of grey
	 * @param level the level for R, G, and B
	 * @return the created color
	 */
	public static Color greyScale (int level) { return new Color (level, level, level); }


	/**
	 * error handling helper
	 */
	public interface ScreenAssociation
	{
		/**
		 * @return swing component associated with error display
		 */
		JComponent getAssociatedComponent ();
	}


	/**
	 * object contains a value
	 * @param <T> the type of the value
	 */
	public interface Valued<T>
	{
		/**
		 * @return the value of specified type
		 */
		T getValue ();
	}


	/**
	 * can be displayed as text
	 */
	public interface Readable
	{
		String getTextRepresentation ();
	}


	/**
	 * the provision for all action call-backs.
	 * event data is discarded, actions are therefore assumed context free.
	 * action is allowed to throw any exception, environment for creation of error dialog is provided in other methods.
	 */
	public interface Callback extends Status.Localization
	{
		/**
		 * the action to perform at point of call back
		 * @throws Exception for any error, message will display in error dialog
		 */
		void executeAction () throws Exception;
	}


	/**
	 * collect lists of components
	 */
	public static class ComponentList extends ArrayList <JComponent>
	{ private static final long serialVersionUID = -8127514143966790854L; }


	public static class TextItemList extends SimpleUtilities.TextItems
	{ private static final long serialVersionUID = 4019704560526792302L; }


	/**
	 * pass on symbol import of Dimension objects
	 */
	public static class Dimensions extends Dimension
	{
		public Dimensions (int width, int height) { super (width, height); }
		private static final long serialVersionUID = 6166402997255517762L;
	}

	/**
	 * @param width size of object width
	 * @param height size of object height
	 * @return the Dimension object
	 */
	public static Dimensions wXh (int width, int height) { return new Dimensions (width, height); }


	/**
	 * equivalent to Component for objects of the Simple class
	 */
	public interface Widget { Component toComponent (); }

	/**
	 * wrap component as widget
	 * @param component any AWT component
	 * @return a widget wrapper for the component
	 */
	public static Widget widgetFor (Component component)
	{
		return new Widget ()
		{
			public Component toComponent () { return component; }
		};
	}

	/**
	 * equivalent to JComponent for objects of the Simple class
	 */
	public interface JWidget extends Widget { JComponent toJComponent (); }

	/**
	 * wrap JComponent as widget
	 * @param component any javax component
	 * @return a widget wrapper for the component
	 */
	public static JWidget jWidgetFor (Component component)
	{
		return new JWidget ()
		{
			public Component toComponent () { return component; }
			public JComponent toJComponent () { return (JComponent) component; }
		};
	}
	public static JWidget jWidgetFor (Widget widget)
	{ return jWidgetFor (widget.toComponent ()); }

	/**
	 * @param aWidget a widget that holds a JComponent
	 * @param to the text for the tool tip
	 */
	public static void setToolTipText (Widget aWidget, String to)
	{
		jWidgetFor (aWidget).toJComponent ().setToolTipText (to);
	}


	/**
	 * Visibility altered on component in thread
	 */
	public static class VisibilityModifier implements Runnable
	{
		public void run () { component.setVisible (to); }
		public VisibilityModifier (Component component, boolean setTo)
		{ this.component = component; this.to = setTo; }
		Component component; boolean to;
	}


	/**
	 * @param ofWidget the widget to be modified
	 * @param width the width preferred for this component
	 * @param height the preferred height
	 */
	public static void setPreferredSize (Widget ofWidget, int width, int height)
	{
		setPreferredSize (ofWidget, new Dimension (width, height));
	}
	public static void setPreferredSize (Widget ofWidget, Dimension size)
	{
		ofWidget.toComponent ().setPreferredSize (size);
	}


	/**
	 * widget for display of graphic image
	 */
	public static class Image extends JLabel implements Widget
	{
		public Image () {}
		public JComponent toComponent () { return this; }
		public Image (String path) { super (iconFor (path)); }
		public Image (javax.swing.Icon content) { super (content); }
		public static javax.swing.ImageIcon iconFor (String path) { return new javax.swing.ImageIcon (path); }
		public void changeTo (javax.swing.Icon content) { this.setIcon (content); }
		private static final long serialVersionUID = -7741959665020441814L;
	}


	/* encapsulate the use of JComponent types */

	public static class Scrolling extends JScrollPane implements Widget
	{
		public void addToViewport (Widget widget)
		{ getViewport ().add (widget.toComponent ()); }
		public JComponent toComponent () { return this; }
		public JViewport getView () { return getViewport (); }

		public Widget getContents ()
		{
			return (Widget) getView ().getComponents ()[0];
		}

		public void configure (int width, int height)
		{
			setPreferredSize ( wXh (width, height) );
			setHorizontalScrollBarPolicy (HORIZONTAL_SCROLLBAR_AS_NEEDED);
			setVerticalScrollBarPolicy (VERTICAL_SCROLLBAR_AS_NEEDED);
		}

		public Scrolling (Widget widget) { addToViewport (widget); }
		private static final long serialVersionUID = 7136003450613049727L;
	}

	/**
	 * reposition scroll pane at last entry
	 */
	public static class ScrollDown implements Runnable
	{
		public void run ()
		{
			Container viewport = panel.getParent ();
			JScrollPane scrollPane =  (JScrollPane) viewport.getParent ();
			JScrollBar vertical = scrollPane.getVerticalScrollBar ();
			vertical.setValue (vertical.getMaximum ());
		}
		ScrollDown (JPanel panel) { this.panel = panel; }
		JPanel panel;
	}

	/**
	 * post a contribution to a scroll
	 */
	public static class PostToScrollingPanel implements Runnable
	{
		public void run ()
		{
			panel.setVisible (false); panel.add (component);
			try { SwingUtilities.invokeAndWait (new ScrollDown (panel)); }
			catch (InvocationTargetException e) { e.printStackTrace (); }
			catch (InterruptedException e) { e.printStackTrace (); }
			panel.setVisible (true);
		}
		public PostToScrollingPanel (Component component, JPanel to)
		{ this.component = component; this.panel = to; }
		Component component; JPanel panel;
	}
	public static void postToScrollingPanel (Component component, JPanel to)
	{ new Thread (new PostToScrollingPanel (component, to)).start (); }

	/**
	 * JPanel child object
	 */
	public static class Panel extends JPanel implements Widget
	{
		public Panel () {}
		public JComponent toComponent () { return this; }
		public Panel (LayoutManager manager) { super (manager); }
		public Scrolling scrolling () { return new Scrolling (this); }
		public void addWidget (Widget widget) { add (widget.toComponent ()); }
		private static final long serialVersionUID = -6047606259416468794L;
	}


	/**
	 * grid layout manager
	 */
	public static class Grid extends GridLayout
	{
		public Grid (int rows, int columns) { super (rows, columns); }
		private static final long serialVersionUID = -2605043561085487016L;
	}


	/**
	 * text editing component
	 */
	public static class SnipEditor extends JTextPane implements Widget
	{

		public static class SnipEditorModel
			extends StyledEditorKit				//DefaultEditorKit
		{ private static final long serialVersionUID = -4369234614973084001L; }

		public SnipEditor (SnipEditorModel model)
		{
			this ();
			String contentType = model.getContentType ();
			setEditorKitForContentType (contentType, model);
			setContentType (contentType);
		}

		public SnipEditor ()
		{
			setBackground (Color.white);
			//setEditable (true);
		}

		/* (non-Javadoc)
		 * @see net.myorb.gui.components.SimpleScreenIO.Widget#toComponent()
		 */
		public Component toComponent() { return this; }
		private static final long serialVersionUID = -5533604189898405770L;
	}


	/* tabbed panel objects */


	/**
	 * JTabbedPane child object
	 */
	public static class TabbedPanel extends JTabbedPane
			implements Valued<Integer>, Widget
	{

		public interface Builder
		{
			void constructTabbedPanel (TabbedPanel p);
		}

		/**
		 * @param name the name of the new tab
		 * @param tab the options panel to place in tab
		 */
		public void addTab (String name, TabPanel tab) { add (name, last = tab); }

		public void addWidget (String text, Widget widget) { add (text, widget.toComponent ()); }

		public JComponent toComponent () { return this; }

		/**
		 * @return the incremented index of the last tab
		 */
		public int nextIndex ()
		{ return last == null? 0: last.getTabIndex () + 1; }
		private TabPanel last = null;

		/* (non-Javadoc)
		 * @see net.myorb.lotto.gui.components.SimpleScreenIO.Valued#getValue()
		 */
		public Integer getValue () { return this.getSelectedIndex (); }

		private static final long serialVersionUID = 7831216448382358840L;
	}

	/**
	 * panel to use as tab
	 */
	public static class TabPanel extends Panel
	{

		/**
		 * get/set tab header
		 */
		public interface TabHeaderAccess
		{
			/**
			 * @param t tab panel header
			 */
			void setTabHeader (TabPanel t);

			/**
			 * @return tab panel header
			 */
			TabPanel getTabHeader ();
		}

		/**
		 * add components to tab panel
		 */
		public interface Builder extends TabHeaderAccess
		{
			/**
			 * @param p panel displaying tab contents
			 */
			void constructTab (TabPanel p);
		}

		/**
		 * @param parent the parent panel
		 * @param grid the grid layout configuration to use
		 */
		public TabPanel (TabbedPanel parent, Grid grid) { this (parent); setLayout (grid); }
		public TabPanel (TabbedPanel parent) { this.parent = parent; this.tabIndex = parent.nextIndex (); }
		private TabbedPanel parent;

		/**
		 * @return TRUE = this tab currently selected
		 */
		public boolean isSelected () { return parent.getSelectedIndex () == tabIndex; }

		/**
		 * change selection to this tab
		 */
		public void select () { parent.setSelectedIndex (tabIndex); }

		/**
		 * @return index assigned to this tab
		 */
		public int getTabIndex () { return tabIndex; }
		private int tabIndex;

		private static final long serialVersionUID = -5815434633083748003L;
	}

	/**
	 * access to tab panel object from builder
	 */
	public static class TabHeader
	{
		public void setTabHeader (TabPanel t) { tabHeader = t; }
		public TabPanel getTabHeader () { return tabHeader; }
		private TabPanel tabHeader;
	}


	/* simple labels */


	/**
	 * JLabel child object
	 */
	public static class Label extends JLabel
	   implements Valued<String>, Readable, Widget
	{
		public Label (String text) { super (text); }
		public Label (ImageIcon icon) { super (icon); }
		public JComponent toComponent () { return this; }
		public String getValue () { return getText (); }
		public String getTextRepresentation () { return getValue (); }
		public Label (String text, int alignment) { super (text, alignment); }
		private static final long serialVersionUID = -1971436064810744614L;
	}


	/* action execution */


	/**
	 * JButton child object
	 */
	public static class Button extends JButton implements Widget
	{
		public Button (String text) { super (text); }
		public JComponent toComponent () { return this; }
		private static final long serialVersionUID = -4697077904356725894L;
	}


	/* binary input */


	/**
	 * JToggleButton child object
	 */
	public static class Toggle extends JToggleButton implements Valued<Boolean>, Readable, Widget,
		BinaryStateMonitor.BinaryMonitorEvaluation, BinaryStateMonitor.BinaryMonitoredComponent
	{
		private static final long serialVersionUID = 5146494438570194062L;
		public Toggle (String text) { setText (text); addChangeListener (stateMonitor = new BinaryStateMonitor (this)); }
		public Toggle (String text, boolean initially) { this (text); setSelected (initially); }
		public String getTextRepresentation () { return getValue ().toString (); }
		public BinaryStateMonitor getMonitor () { return stateMonitor; }
		public Boolean getCurrentState () { return getValue (); }
		public Boolean getValue () { return isSelected (); }
		public JComponent toComponent () { return this; }
		private BinaryStateMonitor stateMonitor;
	}


	/**
	 * JCheckBox child object
	 */
	public static class CheckBox extends JCheckBox implements Valued<Boolean>, Readable, Widget,
		BinaryStateMonitor.BinaryMonitorEvaluation, BinaryStateMonitor.BinaryMonitoredComponent
	{
		private BinaryStateMonitor stateMonitor;
		public CheckBox (String text) { this (text, false); }
		public CheckBox () { addChangeListener (stateMonitor = new BinaryStateMonitor (this)); }
		public CheckBox (String text, boolean initially) { this (); setSelected (initially); setText (text); }

		public String getTextRepresentation () { return getValue ().toString (); }
		public CheckBox (boolean initially) { this (); setSelected (initially); }
		private static final long serialVersionUID = 4206849095606435898L;

		public BinaryStateMonitor getMonitor () { return stateMonitor; }
		public Boolean getCurrentState () { return getValue (); }
		public Boolean getValue () { return isSelected (); }
		public JComponent toComponent () { return this; }
	}


	/* value input */


	/**
	 * JTextField child object
	 */
	public static class Field extends JTextField
		implements Valued<String>, Readable, Widget
	{
		public Field () { super (); }
		public Field (int size) { super (size); }
		public JComponent toComponent () { return this; }
		public String getValue () { return getText (); }
		public String getTextRepresentation () { return getValue (); }
		private static final long serialVersionUID = -7866765746320952001L;
	}


	/**
	 * JComboBox child object
	 */
	public static class ComboBox<T> extends JComboBox<T>
		implements Valued<T>, Readable, Widget
	{
		public ComboBox () {}
		public void addItems (T[] items)
		{ addItems (SimpleUtilities.toList (items)); }
		public JComponent toComponent () { return this; }

		public T getValue () { return getItemAt (getSelectedIndex ()); }
		public String getTextRepresentation () { return getValue ().toString (); }
		public void addItems (List<T> items) { for (T t : items) addItem (t); }
		private static final long serialVersionUID = -1622051517254472781L;

		public ComboBox (List<T> items) { this (); addItems (items); }
		public ComboBox (T[] items) { this (); addItems (items); }
	}


	/*
	 * a frame type that is dedicated to Panel root components
	 */
	public static class Frame extends DisplayFrame
	{
		public Frame () {}

		public Frame (Component component, String title)
		{
			this.title = title;
			this.component = component;
		}

		public Panel getRoot () { return (Panel) component; }
		public interface KeyboardListener extends InputListener {}
		public Frame (Panel panel, String title, JMenuBar menuBar)
		{ this (panel, title); this.menuBar = menuBar; }
	}
	public static class WidgetFrame extends Frame
	{
		public WidgetFrame (Widget widget, String title)
		{
			super (widget.toComponent (), title);
		}
	}

	/**
	 * hide a frame using the event queue
	 * @param frame the frame to be hidden
	 */
	public static void hide (Component frame)
	{
		SwingUtilities.invokeLater (new VisibilityModifier (frame, false));
	}

	/**
	 * treat frame content as widget
	 * @return the frame component
	 */
	public static Widget getLastFrameAsWidget ()
	{
		RootPaneContainer pane =
				DisplayFrame.removeLast ();
		Container frame = GuiToolkit.asContainer (pane);
		Widget widget = widgetFor (frame.getComponent (0)); hide (frame);
		return widget;
	}


	/*
	 * functionality specific to panel types.
	 *  helpers made available for Panel construction
	 *  with No Layout, Generic Layout, or Grid Layout.
	 * also simple line border method. 
	 */


	/**
	 * build a child panel
	 * @param parent the parent to contain this new child
	 * @return the new child panel
	 */
	public static Panel startPanel (Panel parent)
	{
		Panel p = new Panel ();
		if (parent != null) parent.add (p);
		return p;
	}

	/**
	 * child Panel with addition of layout manager
	 * @param parent the parent to contain this new child
	 * @param layout the layout manager for the new panel
	 * @return the new child panel
	 */
	public static Panel startPanel (Panel parent, LayoutManager layout)
	{
		Panel p = startPanel (parent);
		p.setLayout (layout);
		return p;
	}

	/**
	 * child Panel with addition of grid layout manager
	 * @param parent the parent to contain this new child
	 * @param rows the row count for the grid layout
	 * @param columns the column count
	 * @return the new child panel
	 */
	public static Panel startGridPanel (Panel parent, int rows, int columns)
	{
		return startPanel (parent, new Grid (rows, columns));
	}

	/**
	 * build 2 element grid
	 * @param left the left side component
	 * @param right the right side component
	 * @return the line bordered grid
	 */
	public static Panel sideBySidePanel (Component left, Component right)
	{
		Panel p = startGridPanel (null, 1, 2);
		setBorder (p); p.add (left); p.add (right);
		return p;
	}
	public static Panel sideBySidePanel (Widget left, Widget right)
	{
		return sideBySidePanel (left.toComponent (), right.toComponent ());
	}

	/**
	 * add inset of empty space border
	 * @param p the panel being bordered
	 * @param edgeSize the border size of each edge
	 * @return the bordered panel
	 */
	public static Panel wsBordered (Panel p, int edgeSize)
	{
		int e = edgeSize;
		Panel outer = new Panel ();
		Border border = new EmptyBorder (e, e, e, e);
		outer.add (p); outer.setBorder (border);
		return outer;
	}

	/**
	 * surround panel with line border
	 * @param p panel to have borders added
	 */
	public static void setBorder (Panel p)
	{
		p.setBorder (BorderFactory.createLineBorder (Color.black));
	}


	/* tabbed panels and the tabs within */


	/**
	 * tabbed panel
	 * @param parent panel being built
	 * @return the new tabbed panel
	 */
	public static TabbedPanel addTabbedPanel (Panel parent)
	{
		TabbedPanel tabs = new TabbedPanel ();
		parent.add (tabs);
		return tabs;
	}
	public static TabbedPanel addTabbedPanel (Panel parent, TabbedPanel.Builder builder)
	{
		TabbedPanel t = addTabbedPanel (parent);
		builder.constructTabbedPanel (t);
		return t;
	}

	/**
	 * tab on tabbed panel
	 * @param parent tabbed panel being built
	 * @param title the title for the tab
	 * @return the panel for the tab
	 */
	public static TabPanel addTab (TabbedPanel parent, String title)
	{
		TabPanel tab = new TabPanel (parent);
		parent.addTab (title, tab);
		return tab;
	}
	public static TabPanel addTab (TabbedPanel parent, String title, TabPanel.Builder builder)
	{
		TabPanel t = addTab (parent, title);
		builder.constructTab (t);
		builder.setTabHeader (t);
		return t;
	}


	/* specific to screen components */


	/**
	 * JLabel component
	 * @param parent panel being built
	 * @param text the text to display in label
	 * @return the label added
	 */
	public static Label addLabel (Panel parent, String text)
	{
		Label l;
		parent.add (l = new Label (text));
		return l;
	}


	/**
	 * JTextField component (empty)
	 * @param parent panel being built
	 * @param size the size of the text field to construct
	 * @return the text field object added to panel
	 */
	public static Field addField (Panel parent, int size)
	{
		Field f;
		parent.add (f = new Field (size));
		return f;
	}
	/**
	 * add a labeled text fields to panel
	 * @param parent the panel being built
	 * @param label the text of the label to be used
	 * @return the text field component added to the panel
	 */
	public static Field addField (Panel parent, String label)
	{
		Field field;
		parent.add (new Label (label, JLabel.CENTER));
		parent.add (field = new Field ());
		return field;
	}
	public static Field addField (Panel parent, String text, String withDefault)
	{
		Field field = addField (parent, text);
		field.setText (withDefault);
		return field;
	}


	/**
	 * JTextField component with text
	 * @param parent the panel being built
	 * @param size the size of the text field to construct
	 * @param text initial value to place in new text field
	 * @return the text field object added to panel
	 */
	public static Field addField (Panel parent, int size, String text)
	{
		Field f = addField (parent, size);
		f.setText (text);
		return f;
	}


	/**
	 * provide options drop-down for field
	 * @param field the field component to be modified
	 * @param options the text of the options
	 */
	public static void addFieldMenu (Field field, String[] options)
	{
		new FieldMenu<String> (field, SimpleUtilities.toList (options));
	}


	/**
	 * JButton component
	 * @param parent panel being built
	 * @param text the text of button to be added
	 * @param callback action to be connected to button
	 * @return the button added to panel
	 */
	public static Button addButton (Panel parent, String text, Callback callback)
	{
		Button b;
		parent.add (b = new Button (text));
		b.addActionListener (actionFor (callback));
		return b;
	}


	/**
	 * JCheckBox component
	 * @param parent panel being built
	 * @param text the text of box to be added
	 * @param initially the initial value of the box (TRUE = checked)
	 * @return the box added to panel
	 */
	public static CheckBox addCheckBox (Panel parent, String text, boolean initially)
	{
		CheckBox box;
		parent.add (box = new CheckBox (initially));
		box.setText (text);
		return box;
	}


	/**
	 * JComboBox component
	 * @param <T> component type
	 * @param parent panel being built
	 * @return the combo added to panel
	 */
	public static <T> ComboBox<T> addCombo (Panel parent)
	{
		ComboBox<T> box;
		parent.add (box = new ComboBox<T> ());
		return box;
	}
	public static <T> ComboBox<T> addCombo (Panel parent, T[] items)
	{
		ComboBox<T> combo = addCombo (parent);
		combo.addItems (items);
		return combo;
	}
	public static <T> ComboBox<T> addCombo (Panel parent, List<T> items)
	{
		ComboBox<T> combo = addCombo (parent);
		combo.addItems (items);
		return combo;
	}


	/**
	 * treat object as text component
	 * @param component the object to be used
	 * @return the converted object
	 */
	public static JTextComponent asTextComponent (Object component)
	{
		return (JTextComponent) component;
	}


	/* user dialog for screen input requests */


	/**
	 * request text response from user
	 * @param component associated with request
	 * @param request text of the request message
	 * @param title title for the request frame display
	 * @param defaultValue optional default value for response
	 * @return text from user or NULL for CANCEL
	 */
	public static Object requestInput
	(Object component, String request, String title, String defaultValue)
	{
		return JOptionPane.showInputDialog
		(
			doAssociation (component),
			request, title, JOptionPane.PLAIN_MESSAGE,
			null, null, defaultValue
		);
	}
	public static Object requestInput
	(Object component, String request, String title)
	{ return requestInput (component, request, title, ""); }

	/**
	 * request selection from choice list
	 * @param component associated with request
	 * @param request text of the request message
	 * @param title title for the request frame display
	 * @param choices an array of objects providing choice list
	 * @param defaultValue optional default value for response
	 * @return text from user or NULL for CANCEL
	 */
	public static Object requestInput
	(Object component, String request, String title, Object[] choices, String defaultValue)
	{
		return JOptionPane.showInputDialog
		(
			doAssociation (component),
			request, title, JOptionPane.PLAIN_MESSAGE,
			null, choices, defaultValue
		);
	}

	/**
	 * force text response or throw exception
	 * @param response response return from screen component
	 * @param from component reference to dialog causing status update
	 * @return the text of the response when made available
	 * @throws Alert for null response found
	 */
	public static String checkForText (Object response, Object from) throws Alert
	{ if (response == null) requestCanceled (from); return response.toString (); }

	public static String requestTextInput (Object component, String request, String title) throws Alert
	{ return checkForText (requestInput (component, request, title), component); }

	public static String requestTextInput
	(Object component, String request, String title, String defaultValue) throws Alert
	{ return checkForText (requestInput (component, request, title, defaultValue), component); }

	public static Number requestNumericInput
	(Object component, String request, String title, String defaultValue) throws Alert
	{
		try { return Double.parseDouble (requestTextInput (component, request, title, defaultValue)); }
		catch (NumberFormatException e) { alertError ("Invalid number"); return null;}
	}


	/* user dialog for execution status information */


	/**
	 * @return a Status object with Level set to ERROR
	 */
	public static Status newErrorStatus () { return Status.newErrorStatus (); }


	/**
	 * @param status information to be displayed
	 */
	public static void presentToUser (Status status)
	{
		JOptionPane.showMessageDialog
		(
			doAssociation (status.getComponent ()),
			status.getMessage (), status.getTitle (),
			LEVEL_MAP.get (status.getLevel ())
		);
	}

	static private java.util.HashMap<Status.Level, Integer>
		LEVEL_MAP = new java.util.HashMap<Status.Level, Integer>();
	static
	{
		LEVEL_MAP.put (Status.Level.ERROR, JOptionPane.ERROR_MESSAGE);
		LEVEL_MAP.put (Status.Level.INFO, JOptionPane.INFORMATION_MESSAGE);
		LEVEL_MAP.put (Status.Level.WARN, JOptionPane.WARNING_MESSAGE);
		LEVEL_MAP.put (Status.Level.PLAIN, JOptionPane.PLAIN_MESSAGE);
	}


	/**
	 * throwable object holding full status
	 */
	public static class Alert extends Exception
	{

		/**
		 * @param status the description to be conveyed
		 */
		public Alert (Status status)
		{
			super (status.getMessage ());
			status.linkCause (this);
			this.status = status;
		}
		private Status status;

		/**
		 * @param localization properties used to fill in Status
		 */
		public void presentDialog (Status.Localization localization)
		{ status.fillLocalization (localization); presentDialog (); }
		public void presentDialog () { presentToUser (status); }

		private static final long serialVersionUID = 1356928520842168699L;
	}


	/**
	 * simple error state with message only
	 * @param message the text of the message to display
	 * @throws Alert the Alert wrapper for the error status
	 */
	public static void alertError (String message) throws Alert { alert (message, Status.Level.ERROR); }
	public static void alertWarning (String message) throws Alert { alert (message, Status.Level.WARN); }
	public static void alertInfo (String message) throws Alert { alert (message, Status.Level.INFO); }

	/**
	 * @param status the status describing the alert
	 * @throws Alert an Alert instance providing access to status
	 */
	public static void alert (Status status) throws Alert { throw new Alert (status); }
	public static void	alert (String message, Status.Level level) throws Alert
	{ alert (Status.newInstance (level).setMessage (message)); }

	/**
	 * generic exception processing
	 * @param basis the exception being processed
	 * @throws Alert an encapsulation of specified exception
	 */
	public static void alertBasedOn (Exception basis) throws Alert
	{
		String message = basis.getMessage ();
		if (message == null) message = "Unhandled exception has been caught: " + basis.getClass ().getName ();
		alert (Status.newInstance (Status.Level.ERROR).setMessage (message).setCause (basis));
	}


	/**
	 * generate alert for GUI components generating errors
	 * @param component the component where interaction generated error
	 * @param message the text of the message to display in dialog for this error
	 * @param title the text of the title to be used for the displayed dialog
	 * @throws Alert an instance of Alert providing access to status
	 */
	public static void componentError
		(Object component, String message, String title)
	throws Alert
	{
		alert (Status.genError (component, message, title));
	}


	/**
	 * common INFO dialog for canceled requests
	 * @param from component reference to dialog causing status update
	 * @throws Alert an instance of Alert providing access to status
	 */
	public static void requestCanceled (Object from) throws Alert
	{
		alert (Status.newInstance (Status.Level.INFO).setMessage ("Request canceled").setComponent (from));
	}


	/*
	 * ScreenAssociation algorithm allowing choice of implementation.
	 * associated object may be swing component in which case object is cast to JComponent.
	 * alternatively object may implement ScreenAssociation allowing one extra indirection level to get swing component.
	 */


	/**
	 * determine screen component to use
	 * @param object object associated with error
	 * @return component to use in dialog
	 */
	public static JComponent doAssociation (Object object)
	{
		if (object instanceof ScreenAssociation)
			return ((ScreenAssociation) object).getAssociatedComponent ();
		else return (JComponent) object;
	}
	public static JComponent doCallbackAssociation (Callback callback)
	{ return doAssociation (callback.getAssociatedComponent ()); }


	/*
	 * use DisplayFrame class to present a Panel to the screen
	 */


	/**
	 * show frame containing panel
	 * @param component the component to be displayed
	 * @param title the title for the frame
	 * @param w the preferred width
	 * @param h the height
	 * @return the frame
	 */
	public static Frame show (Widget component, String title, int w, int h)
	{
		Frame frame;
		setPreferredSize (component, w, h);
		(frame = new WidgetFrame (component, title)).show ();
		return frame;
	}
	public static Frame showAndExit (Widget component, String title, int w, int h)
	{
		Frame frame;
		setPreferredSize (component, w, h);
		(frame = new WidgetFrame (component, title)).showAndExit ();
		return frame;
	}


	/*
	 * ActionListener wrapper for Callback objects.
	 * SimpleCallback class provides the wrapper.
	 */


	/**
	 * translate swing action to a Callback
	 * @param callback the callback to treat as a swing action
	 * @return the action wrapper for the callback
	 */
	public static ActionListener actionFor (Callback callback)
	{ return new SimpleCallback (callback); }


	/**
	 * use default handler when GUI actions are unlikely to generate exceptions
	 */
	public static class CallbackWithDefaultHandler
		extends SimpleCallback.DefaultForUnexpectedError
	{}


	/*
	 * wrap time consuming tasks in SwingWorker control objects
	 * - this prevents screen functionality from time starvation
	 */

	/**
	 * start a task in the background using a SwingWorker
	 * @param task a Runnable object wrapping the task to be run
	 * @return the SwingWorker for the new task
	 */
	public static BackgroundTask startBackgroundTask (Runnable task)
	{
		BackgroundTask BG = new BackgroundTask (task);
		BG.startTask ();
		return BG;
	}


}


