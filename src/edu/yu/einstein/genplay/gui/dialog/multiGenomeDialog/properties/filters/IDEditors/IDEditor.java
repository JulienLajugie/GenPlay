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
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.filters.IDEditors;

import javax.swing.JPanel;

import edu.yu.einstein.genplay.core.enums.VCFColumnName;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFHeaderType.VCFHeaderType;
import edu.yu.einstein.genplay.core.multiGenome.VCF.filtering.IDFilterInterface;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public interface IDEditor {

	
	/**
	 * Initializes the panel putting all necessary elements for selecting the filter.
	 * @param panel the panel
	 */
	public void updatePanel (JPanel panel);
	
	
	/**
	 * Sets the ID of the filter
	 * @param id the ID
	 */
	public void setID (VCFHeaderType id);
	
	
	/**
	 * @return the ID
	 */
	public VCFHeaderType getID ();
	
	
	/**
	 * Sets the category of the filter:
	 * - ALT
	 * - QUAL
	 * - FILTER
	 * - INFO
	 * - FORMAT
	 * @param category the ID category
	 */
	public void setCategory (VCFColumnName category);
	
	
	/**
	 * @return the category of the editor
	 */
	public VCFColumnName getCategory ();
	
	
	/**
	 * @return the filter
	 */
	public IDFilterInterface getFilter ();
	
	
	/**
	 * Initializes the panel using an existing filter
	 * @param filter the filter
	 */
	public void initializesPanel (IDFilterInterface filter);
	
}
