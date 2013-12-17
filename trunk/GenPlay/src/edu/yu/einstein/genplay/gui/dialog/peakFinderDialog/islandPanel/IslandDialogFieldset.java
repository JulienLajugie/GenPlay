/*******************************************************************************
 *     GenPlay, Einstein Genome Analyzer
 *     Copyright (C) 2009, 2011 Albert Einstein College of Medicine
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *     
 *     Authors:	Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *     			Nicolas Fourel <nicolas.fourel@einstein.yu.edu>
 *     Website: <http://genplay.einstein.yu.edu>
 *******************************************************************************/
package edu.yu.einstein.genplay.gui.dialog.peakFinderDialog.islandPanel;

import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import edu.yu.einstein.genplay.core.operation.binList.peakFinder.IslandFinder;


/**
 * This panel allow to define a fieldset with title and some common attributes.
 * 
 * @author Nicolas
 * @version 0.1
 */
abstract class IslandDialogFieldset extends JPanel {
	
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
	IslandDialogFieldset (String title, IslandFinder island) {
		super();
		this.island = island;
		this.setBorder(BorderFactory.createTitledBorder(title));
	}

	/**
	 * The number of row allow to calculate a good fieldset height
	 * Panel size are set
	 * @param rows	total number of row in the fieldset
	 */
	protected void setRows(int rows) {
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
	protected int getFieldsetHeight() {
		return fieldsetHeight;
	}
	
	protected Dimension getFieldsetSize() {
		return this.getSize();
	}
	
	protected IslandFinder getIsland() {
		return island;
	}
}
