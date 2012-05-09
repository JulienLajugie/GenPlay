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
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.editors;

import javax.swing.JPanel;

import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFHeaderType.VCFHeaderType;
import edu.yu.einstein.genplay.core.multiGenome.VCF.filtering.IDFilterInterface;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public interface IDEditor {

	
	/**
	 * Creates and return the panel putting all necessary elements for selecting the filter.
	 * @return the panel
	 */
	public JPanel updatePanel ();
	
	
	/**
	 * Enable/Disable the panel
	 * @param b true if the panel must be enabled, false otherwise 
	 */
	public void setEnabled (boolean b);
	
	
	/**
	 * @return true if the panel is activated, false otherwise
	 */
	public boolean isEnabled ();
	
	
	/**
	 * Sets the ID of the filter
	 * @param header the ID
	 */
	public void setHeaderType (VCFHeaderType header);
	
	
	/**
	 * @return the ID
	 */
	public VCFHeaderType getHeaderType ();
	
	
	/**
	 * @return the filter
	 */
	public IDFilterInterface getFilter ();
	
	
	/**
	 * Initializes the panel using an existing filter
	 * @param filter the filter
	 */
	public void initializesPanel (IDFilterInterface filter);
	
	
	/**
	 * @return a list of error gathered in one String
	 */
	public abstract String getErrors ();
	
}
