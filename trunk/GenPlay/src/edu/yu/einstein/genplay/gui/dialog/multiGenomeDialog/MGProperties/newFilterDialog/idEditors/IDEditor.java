/*******************************************************************************
 * GenPlay, Einstein Genome Analyzer
 * Copyright (C) 2009, 2014 Albert Einstein College of Medicine
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * Authors: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *          Nicolas Fourel <nicolas.fourel@einstein.yu.edu>
 *          Eric Bouhassira <eric.bouhassira@einstein.yu.edu>
 * 
 * Website: <http://genplay.einstein.yu.edu>
 ******************************************************************************/
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.MGProperties.newFilterDialog.idEditors;

import javax.swing.JPanel;

import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFHeaderType.VCFHeaderType;
import edu.yu.einstein.genplay.core.multiGenome.filter.VCFID.IDFilterInterface;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public interface IDEditor {


	/**
	 * @return a list of error gathered in one String
	 */
	public abstract String getErrors();


	/**
	 * @return the filter
	 */
	public IDFilterInterface getFilter();


	/**
	 * @return the ID
	 */
	public VCFHeaderType getHeaderType();


	/**
	 * Initializes the panel using an existing filter
	 * @param filter the filter
	 */
	public void initializesPanel(IDFilterInterface filter);


	/**
	 * @return true if the panel is visible, false otherwise
	 */
	public boolean isVisible();


	/**
	 * Sets the ID of the filter
	 * @param header the ID
	 */
	public void setHeaderType(VCFHeaderType header);


	/**
	 * Show/Hide the panel
	 * @param b true if the panel must be visible, false otherwise
	 */
	public void setVisible(boolean b);


	/**
	 * Creates and return the panel putting all necessary elements for selecting the filter.
	 * @return the panel
	 */
	public JPanel updatePanel();

}
