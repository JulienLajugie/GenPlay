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

import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFile.VCFFile;
import edu.yu.einstein.genplay.core.multiGenome.data.display.variant.Variant;


/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public abstract class MGFilter {

	protected FilterInterface 		filter;				// the filter


	/**
	 * Generate/Update/Initialize the filter
	 */
	public void generateFilter () {}


	/**
	 * @param filter the filter to set
	 */
	public void setFilter(FilterInterface filter) {
		this.filter = filter;
	}


	/**
	 * @return the filter
	 */
	public FilterInterface getFilter() {
		return filter;
	}


	/**
	 * @param variant the variant
	 * @return	its index on the boolean filter list
	 */
	public abstract int getVariantIndex (Variant variant);


	/**
	 * @param variant the variant
	 * @return true if the variant is valid, false otherwise
	 */
	public abstract boolean isVariantValid (Variant variant);


	/**
	 * @param variantIndex the index of the variant
	 * @return true if the variant is valid, false otherwise
	 */
	public abstract boolean isVariantValid (int variantIndex);


	@Override
	public abstract boolean equals(Object obj);


	/**
	 * Creates a clone of the current {@link MGFilter}.
	 * This method is supposed to be use for the Multigenome Properties Dialog.
	 * 
	 * For {@link VCFFilter}:
	 * Technically, the {@link VCFFile} is not duplicated since we want to keep the original ones and never create new ones after the creation of the project.
	 * The boolean list is also not duplicated because of memory purpose. If a new filter is created, generating it will create the right boolean list anyway!
	 * 
	 * @return a clone of the current object
	 */
	public abstract MGFilter getDuplicate ();


	/**
	 * Shows information about the MG filter
	 */
	public abstract void show ();


}
