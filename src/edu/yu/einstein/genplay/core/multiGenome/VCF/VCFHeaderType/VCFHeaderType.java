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
 *     Author: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *     Website: <http://genplay.einstein.yu.edu>
 *******************************************************************************/
package edu.yu.einstein.genplay.core.multiGenome.VCF.VCFHeaderType;

import java.io.Serializable;


/**
 * This class declares methods required for VCF type field:
 * - ALT
 * - FILTER
 * @author Nicolas Fourel
 * @version 0.1
 */
public interface VCFHeaderType extends Serializable {

	
	/**
	 * @return the id
	 */
	public String getId();

	
	/**
	 * @param id the id to set
	 */
	public void setId(String id);
	
	
	/**
	 * @return the description
	 */
	public String getDescription();

	
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description);
	
	
}