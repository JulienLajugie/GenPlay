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
package edu.yu.einstein.genplay.core.multiGenome.VCF.VCFHeaderType;

import java.io.Serializable;

import edu.yu.einstein.genplay.dataStructure.enums.VCFColumnName;


/**
 * This class declares methods required for VCF type field:
 * - ALT
 * - FILTER
 * @author Nicolas Fourel
 * @version 0.1
 */
public interface VCFHeaderType extends Serializable {


	/** Number maximum of elements an ID can have */
	public static final int ELEMENT_LIMIT = 10;


	/**
	 * @return a line formatted as the native line from the VCF file
	 */
	public String getAsOriginalLine();


	/**
	 * @return the columnCategory
	 */
	public VCFColumnName getColumnCategory();


	/**
	 * @return the description
	 */
	public String getDescription();


	/**
	 * @return the id
	 */
	public String getId();


	/**
	 * @param columnCategory the id to set
	 */
	public void setColumnCategory(VCFColumnName columnCategory);


	/**
	 * @param description the description to set
	 */
	public void setDescription(String description);


	/**
	 * @param id the id to set
	 */
	public void setId(String id);

}
