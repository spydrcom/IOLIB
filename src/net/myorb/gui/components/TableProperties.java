
package net.myorb.gui.components;

import java.awt.Dimension;

/**
 * properties required for use in table adapter
 * @author Michael Druckman
 */
public interface TableProperties
{

	/**
	 * @return title for the table display frame
	 */
	String getTitle ();

	/**
	 * @return column headers for table
	 */
	String[] getColumns ();

	/**
	 * @return matrix of table cells
	 */
	Object[][] getCells ();

	/**
	 * @return a menu of actions that form a popup
	 */
	Menu getMenu ();

	/**
	 * @return WxH dimensions for display area
	 */
	Dimension getDisplayArea ();

	/**
	 * call-back method for double click on table
	 * @param forRowNumber row number where double click was accepted
	 */
	void doDoubleClick (int forRowNumber);

}

