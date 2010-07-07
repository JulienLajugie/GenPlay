/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.dialog;

import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import yu.einstein.gdp2.core.filter.IslandFinder;

/**
 * This panel allow to define a fieldset with title and some common attributes.
 * 
 * @author Nicolas
 * @version 0.1
 */
public abstract class IslandDialogFieldset extends JPanel {
	
	private static final long serialVersionUID = 8769389246423162454L;
	
	private final IslandFinder island;
	
	protected static final int LINE_HEIGHT = 25;				// line height
	protected static final int LINE_TOP_INSET_HEIGHT = 5;		// top inset for line
	protected static final int LINE_BOTTOM_INSET_HEIGHT = 5;	// bottom inset for line
	protected static final int FIELDSET_WIDTH = 350;			// fieldset width
	private int fieldsetHeight;									// fieldset height
	
	/**
	 * Constructor for IslandDialogFieldset
	 * @param title		fieldset title
	 * @param island	IslandFinder object to set some information
	 */
	public IslandDialogFieldset (String title, IslandFinder island) {
		super();
		this.island = island;
		this.setBorder(BorderFactory.createTitledBorder(title));
	}

	/**
	 * The number of row allow to calculate a good fieldset height
	 * Panel size are set
	 * @param rows	total number of row in the fieldset
	 */
	public void setRows(int rows) {
		this.fieldsetHeight = rows * (IslandDialogFieldset.LINE_HEIGHT + 
										IslandDialogFieldset.LINE_TOP_INSET_HEIGHT +
										IslandDialogFieldset.LINE_BOTTOM_INSET_HEIGHT);
		this.fieldsetHeight = (int)Math.round(this.fieldsetHeight * 1.3);
		Dimension d = new Dimension (IslandDialogFieldset.FIELDSET_WIDTH, this.fieldsetHeight);
		this.setSize(d);
		this.setMinimumSize(d);
		this.setPreferredSize(d);
	}
	
	//Getters
	public int getFieldsetHeight() {
		return fieldsetHeight;
	}
	
	public Dimension getFieldsetSize() {
		return this.getSize();
	}
	
	public IslandFinder getIsland() {
		return island;
	}
}