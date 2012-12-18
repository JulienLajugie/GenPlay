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
package edu.yu.einstein.genplay.core.multiGenome.data.display.content;

import java.util.ArrayList;
import java.util.List;

import edu.yu.einstein.genplay.core.chromosome.Chromosome;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.data.display.array.MGVariantArray;
import edu.yu.einstein.genplay.core.multiGenome.data.display.variant.DeletionVariant;
import edu.yu.einstein.genplay.core.multiGenome.data.display.variant.InsertionVariant;
import edu.yu.einstein.genplay.core.multiGenome.data.display.variant.SNPVariant;
import edu.yu.einstein.genplay.core.multiGenome.data.display.variant.Variant;
import edu.yu.einstein.genplay.core.multiGenome.utils.FormattedMultiGenomeName;
import edu.yu.einstein.genplay.core.multiGenome.utils.ShiftCompute;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class MGChromosomeVariants {

	private List<MGVariantArray> variants;


	/**
	 * Generates the variants based on {@link MGChromosomeContent} information.
	 * @param chromosomeContent the {@link MGChromosomeContent}
	 */
	public void generateVariants (MGChromosomeContent chromosomeContent) {
		int alternativeNumber = chromosomeContent.getMaxAlternativeNumber();
		int lineNumber = chromosomeContent.getSize();
		initializeLists(alternativeNumber, lineNumber);
		compute(chromosomeContent);
	}


	/**
	 * Initializes the variant lists
	 * @param alternativeNumber the maximum number of alternatives in a line
	 * @param lineNumber the number of lines
	 */
	private void initializeLists (int alternativeNumber, int lineNumber) {
		variants = new ArrayList<MGVariantArray>();
		for (int i = 0; i < alternativeNumber; i++) {
			variants.add(new MGVariantArray(lineNumber));
		}
	}


	/**
	 * Insert information into the variant list as {@link Variant}
	 * @param chromosomeContent the {@link MGChromosomeContent}
	 */
	private void compute (MGChromosomeContent chromosomeContent) {
		int size = chromosomeContent.getSize();
		int alternativeNumber = variants.size();
		String referenceGenomeName = ProjectManager.getInstance().getAssembly().getDisplayName();
		String metaGenomeName = FormattedMultiGenomeName.META_GENOME_NAME;
		Chromosome chromosome = chromosomeContent.getChromosome();
		for (int i = 0; i < size; i++) {

			for (int j = 0; j < alternativeNumber; j++) {
				Variant variant = null;
				int referencePosition = chromosomeContent.getPositions().get(i);
				int length = chromosomeContent.getAlternatives().get(j).get(i);
				int start = ShiftCompute.getPosition(referenceGenomeName, null, referencePosition, chromosome, metaGenomeName);
				if (length > 0) {
					//int stop = ShiftCompute.getPosition(referenceGenomeName, null, referencePosition + 1, chromosome, metaGenomeName) - 1;
					start++;
					int stop = start + length;
					variant = new InsertionVariant(chromosomeContent, i, start, stop);
				} else if (length < 0) {
					//int stop = ShiftCompute.getPosition(referenceGenomeName, null, referencePosition - length, chromosome, metaGenomeName);
					start++;
					int stop = start + (length * -1);
					variant = new DeletionVariant(chromosomeContent, i, start, stop);
				} else {
					variant = new SNPVariant(chromosomeContent, i, start);
				}
				variants.get(j).set(i, variant);
			}
		}
	}


	/**
	 * @param alternativeIndex
	 * @param positionIndex
	 * @return the variant for the given alternative & position indexes
	 */
	public Variant getVariant (int alternativeIndex, int positionIndex) {
		return variants.get(alternativeIndex).get(positionIndex);
	}


	/**
	 * Shows information
	 */
	public void show () {
		String info = "";
		int alternativeNumber = variants.size();
		if (alternativeNumber > 0) {
			int size = variants.get(0).size();
			for (int i = 0; i < size; i++) {
				for (int j = 0; j < alternativeNumber; j++) {
					Variant current = variants.get(j).get(i);
					info += current.getDescription() + "\n";
				}
			}
		} else {
			info = "Variants list is empty.";
		}
		System.out.println(info);
	}

}
