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
package edu.yu.einstein.genplay.core.multiGenome.data.display.content;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.data.display.array.MGVariantArray;
import edu.yu.einstein.genplay.core.multiGenome.data.display.variant.DeletionVariant;
import edu.yu.einstein.genplay.core.multiGenome.data.display.variant.InsertionVariant;
import edu.yu.einstein.genplay.core.multiGenome.data.display.variant.SNPVariant;
import edu.yu.einstein.genplay.core.multiGenome.data.display.variant.Variant;
import edu.yu.einstein.genplay.core.multiGenome.utils.FormattedMultiGenomeName;
import edu.yu.einstein.genplay.core.multiGenome.utils.ShiftCompute;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;

/**
 * The {@link MGChromosomeVariants} contains the lists of {@link Variant}.
 * More than one {@link Variant} can be defined within a same line, there are as much list as the maximum number of defined {@link Variant} in line within a {@link Chromosome}.
 * The worst example is: in one chromosome, all lines define only one {@link Variant} except one that defines three {@link Variant}, there will be three lists (1 full, 2 with only one {@link Variant}).
 * The memory usage estimation for millions of null elements in a list represents few MBs. Which is negligible.
 * 
 * @author Nicolas Fourel
 * @version 0.1
 */
public class MGChromosomeVariants implements Serializable {

	/** Default serial version ID */
	private static final long serialVersionUID = -203508611757257381L;
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 0;		// saved format version
	private List<MGVariantArray> variants;			// The lists of variants.


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeObject(variants);
	}


	/**
	 * Method used for unserialization
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.readInt();
		variants = (List<MGVariantArray>) in.readObject();
	}


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
		int size = chromosomeContent.getSize();															// Get the total number of lines.
		int alternativeNumber = variants.size();														// Get the maximum number of alternatives per line.
		String referenceGenomeName = ProjectManager.getInstance().getAssembly().getDisplayName();		// Get the name of the reference genome.
		String metaGenomeName = FormattedMultiGenomeName.META_GENOME_NAME;								// Get the name of the meta genome.
		Chromosome chromosome = chromosomeContent.getChromosome();										// Get the chromosome the variants will refer to.
		for (int i = 0; i < size; i++) {																// Scan over the lines

			for (int j = 0; j < alternativeNumber; j++) {												// Scan over the different alternatives of a line
				Variant variant = null;
				int referencePosition = chromosomeContent.getPositions().get(i);						// Get the current reference genome position.
				int length = chromosomeContent.getAlternatives().get(j).get(i);							// Get the length of the current alternative.
				int start = ShiftCompute.getPosition(referenceGenomeName, null, referencePosition, chromosome, metaGenomeName);	// Calculate the start position of the variant.
				if (length > 0) {																		// In case of an insertion:
					start++;																			// The actual variation starts one nucleotide further
					int stop = start + length;															// Calculate the stop position: only start + length
					variant = new InsertionVariant(chromosomeContent, i, start, stop);					// Create the variant.
				} else if (length < 0) {																// In case of a deletion
					start++;																			// The actual variation starts one nucleotide further
					int stop = start + (length * -1);													// Calculate the stop position: only start + length (the length is here negative since it's a deletion)
					variant = new DeletionVariant(chromosomeContent, i, start, stop);					// Create the variant.
				} else {																				// A 0 nulceotide length can only be a SNP.
					variant = new SNPVariant(chromosomeContent, i, start);								// Create the variant.
				}
				variants.get(j).set(i, variant);														// Add the variant to the lists.
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
	 * @param positionIndex
	 * @return the variants for the given position index
	 */
	public List<Variant> getVariants (int positionIndex) {
		List<Variant> result = new ArrayList<Variant>();
		for (MGVariantArray array: variants) {
			result.add(array.get(positionIndex));
		}
		return result;
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
