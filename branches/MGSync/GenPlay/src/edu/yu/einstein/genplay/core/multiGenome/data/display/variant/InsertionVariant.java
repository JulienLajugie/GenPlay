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

import edu.yu.einstein.genplay.core.enums.VariantType;
import edu.yu.einstein.genplay.core.multiGenome.data.display.content.MGChromosomeContent;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class InsertionVariant extends MultiNucleotideVariant {

	/** Default serial version ID */
	private static final long serialVersionUID = -4204229667840781053L;


	/**
	 * Constructor of {@link InsertionVariant}
	 * @param chromosomeContent the {@link MGChromosomeContent}
	 * @param referencePositionIndex the index position on the reference genome
	 */
	public InsertionVariant(MGChromosomeContent chromosomeContent, int referencePositionIndex) {
		super(chromosomeContent, referencePositionIndex);
	}


	/**
	 * Constructor of {@link InsertionVariant}
	 * @param chromosomeContent the {@link MGChromosomeContent}
	 * @param referencePositionIndex the index position on the reference genome
	 * @param start start position
	 * @param stop stop position
	 */
	public InsertionVariant(MGChromosomeContent chromosomeContent, int referencePositionIndex, int start, int stop) {
		super(chromosomeContent, referencePositionIndex, start, stop);
	}


	@Override
	public VariantType getType() {
		return VariantType.INSERTION;
	}


	/**
	 * @return a description of the {@link Variant}
	 */
	@Override
	public String getDescription () {
		String description = super.getDescription();
		description += " TYPE: INSERTION;";
		return description;
	}
}
