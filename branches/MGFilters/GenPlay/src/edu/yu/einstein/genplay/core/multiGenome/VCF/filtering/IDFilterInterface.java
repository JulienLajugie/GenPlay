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
package edu.yu.einstein.genplay.core.multiGenome.VCF.filtering;

import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFHeaderType.VCFHeaderType;
import edu.yu.einstein.genplay.core.multiGenome.display.variant.VariantInterface;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public interface IDFilterInterface {
	
	
	/**
	 * @return the ID of the filter
	 */
	public VCFHeaderType getID ();
	
	
	/**
	 * Sets the ID of the filter
	 * @param id the ID
	 */
	public void setID (VCFHeaderType id);
	
	
	/**
	 * Sets the category of the filter:
	 * - ALT
	 * - QUAL
	 * - FILTER
	 * - INFO
	 * - FORMAT
	 * @param category the ID category
	 */
	public void setCategory (String category);
	
	
	/**
	 * @return the category of the filter
	 */
	public String getCategory ();
	
	
	/**
	 * Gives a string for display use of the filter
	 * @return a string
	 */
	public String toStringForDisplay ();
	
	
	/**
	 * Checks for all errors
	 * @return the string of errors if exists or null otherwise
	 */
	public String getErrors ();

	
	/**
	 * Checks if the given variant is valid according to the filter
	 * @param variant the variant
	 * @return true if it is valid, false otherwise;
	 */
	public boolean isValid (VariantInterface variant);
	
	
	/**
	 * Checks if the object (purpose of the filter) is valid according to the filter
	 * @param value the object, value from the variant information
	 * @return true if it is valid, false otherwise;
	 */
	public boolean isValid (Object value);
	
	
	/**
	 * Equals method
	 * @param obj
	 * @return true if the parameter is equal to the instance
	 */
	public boolean equals(Object obj);
}
