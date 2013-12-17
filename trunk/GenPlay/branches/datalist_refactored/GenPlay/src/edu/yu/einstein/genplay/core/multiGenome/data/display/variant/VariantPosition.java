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
package edu.yu.einstein.genplay.core.multiGenome.data.display.variant;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class VariantPosition {


	private Variant[] variants;


	/**
	 * Constructor of {@link VariantPosition}
	 */
	public VariantPosition () {
		setVariants(null);
	}


	/**
	 * Constructor of {@link VariantPosition}
	 * @param variants variants to set
	 */
	public VariantPosition (Variant[] variants) {
		setVariants(variants);
	}


	/**
	 * @param variants the {@link Variant} to set
	 */
	public void setVariants (Variant[] variants) {
		this.variants = variants;
	}


	/**
	 * @return the variants
	 */
	public Variant[] getVariants() {
		return variants;
	}

}
