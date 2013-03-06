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

import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFLine;
import edu.yu.einstein.genplay.core.multiGenome.data.display.content.MGChromosomeContent;
import edu.yu.einstein.genplay.dataStructure.enums.AlleleType;
import edu.yu.einstein.genplay.dataStructure.enums.VariantType;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class SNPVariant extends SingleNucleotideVariant {

	/** Default serial version ID */
	private static final long serialVersionUID = 3674147338434086605L;


	/**
	 * Constructor of {@link SNPVariant}
	 * @param chromosomeContent the {@link MGChromosomeContent}
	 * @param referencePositionIndex the index position on the reference genome
	 */
	public SNPVariant(MGChromosomeContent chromosomeContent, int referencePositionIndex) {
		super(chromosomeContent, referencePositionIndex);
	}


	/**
	 * Constructor of {@link SNPVariant}
	 * @param chromosomeContent the {@link MGChromosomeContent}
	 * @param referencePositionIndex the index position on the reference genome
	 * @param start start position
	 */
	public SNPVariant(MGChromosomeContent chromosomeContent, int referencePositionIndex, int start) {
		super(chromosomeContent, referencePositionIndex, start);
	}


	@Override
	public VariantType getType() {
		return VariantType.SNPS;
	}


	/**
	 * @return a description of the {@link Variant}
	 */
	@Override
	public String getDescription () {
		String description = super.getDescription();
		description += " TYPE: SNP;";
		return description;
	}


	@Override
	public String getVariantSequence(String genomeName, AlleleType alleType) {
		VCFLine line = getVCFLine();
		if (line != null) {
			line.processForAnalyse();
			String chain = line.getAlternativeFromFullName(genomeName, alleType);
			if (chain == null) {
				chain = line.getREF();
			}
			return chain;
		}
		return super.getVariantSequence();
	}
}
