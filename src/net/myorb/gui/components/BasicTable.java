
package net.myorb.gui.components;

import java.awt.Dimension;

/**
 * abstraction for table size
 * @author Michael Druckman
 */
public abstract class BasicTable extends TableAdapter implements TableProperties
{
	/* (non-Javadoc)
	 * @see net.myorb.lotto.gui.components.TableProperties#getDisplayArea()
	 */
	public Dimension getDisplayArea () { return new Dimension (W, H); }
	public void changeDimensions (int H, int W) { this.H = H; this.W = W; }
	private int H = 400, W = 800;
}

