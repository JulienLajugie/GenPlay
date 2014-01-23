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
package edu.yu.einstein.genplay.core.multiGenome.filter;

import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFLine;
import edu.yu.einstein.genplay.core.multiGenome.data.display.variant.Variant;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public interface FilterInterface {

	/**
	 * Equals method
	 * @param obj
	 * @return true if the parameter is equal to the instance
	 */
	@Override
	public boolean equals(Object obj);


	/**
	 * @return a description of the filter
	 */
	public String getDescription ();


	/**
	 * @return a duplicate of the current object
	 */
	public FilterInterface getDuplicate ();


	/**
	 * Checks for all errors
	 * @return the string of errors if exists or null otherwise
	 */
	public String getErrors ();


	/**
	 * @return the name of the filter
	 */
	public String getName ();


	/**
	 * Checks if the object (purpose of the filter) is valid according to the filter
	 * @param variant the variant
	 * @return true if it is valid, false otherwise;
	 */
	public boolean isValid (Variant variant);


	/**
	 * Checks if the object (purpose of the filter) is valid according to the filter
	 * @param line the line of a VCF
	 * @return true if it is valid, false otherwise;
	 */
	public boolean isValid (VCFLine line);


	/**
	 * Gives a string for display use of the filter
	 * @return a string
	 */
	public String toStringForDisplay ();

}
