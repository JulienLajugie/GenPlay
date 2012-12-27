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
package edu.yu.einstein.genplay.core.multiGenome.VCF.VCFScanner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.yu.einstein.genplay.core.enums.VariantType;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFLine;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFile.VCFFile;
import edu.yu.einstein.genplay.core.multiGenome.filter.MGFilter;
import edu.yu.einstein.genplay.core.multiGenome.utils.VCFLineUtility;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public abstract class VCFScanner {

	private final VCFScannerReceiver receiver;
	protected final VCFFile vcfFile;		// The VCF file to scan.

	private List<String> genomes;
	private List<VariantType> variations;
	private List<MGFilter> filters;


	/**
	 * Constructor of {@link VCFScanner}
	 * @param receiver
	 * @param vcfFile
	 * @throws IOException
	 */
	public VCFScanner (VCFScannerReceiver receiver, VCFFile vcfFile) throws IOException {
		this.receiver = receiver;
		this.vcfFile = vcfFile;
		genomes = null;
		variations = null;
		filters = null;
	}


	/**
	 * Starts scanning the file.
	 * @throws IOException
	 */
	public void compute () throws IOException {
		// Get the first line of data
		VCFLine line = getFirstLine();

		// Scan the file line by line
		while (!line.isLastLine()) {
			// Initialize the line for treatments
			line.processForAnalyse();

			// Filters
			boolean pass = true;
			List<Integer> alternativeIndexes = getScopeDefinedVariationIndexes(line);		// Get alternative indexes from all required genomes
			if (!genomesValidation(alternativeIndexes)) {									// Genome validation filter
				pass = false;
			}
			if (!variationsValidation(line, alternativeIndexes)) {							// Variation validation filter
				pass = false;
			}

			// Send the line for process if meets requirements
			if (pass) {
				receiver.processLine(line);
			}

			// Move to the next line
			line = getNextLine();
		}

		// Closes the file streams
		endScan();
	}


	/**
	 * @return the first line to process
	 */
	protected abstract VCFLine getFirstLine ();


	/**
	 * @return the next line to process
	 */
	protected abstract VCFLine getNextLine ();


	/**
	 * Performs last operations such as closing the file streams.
	 */
	protected abstract void endScan ();


	/**
	 * Retrieves indexes of the alternatives from the scope of the project.
	 * The scope is defined by the genomes to load, defined by the user.
	 * @param line the {@link VCFLine}
	 * @return the list of indexes (include no call (-2) and references (-1))
	 */
	private List<Integer> getScopeDefinedVariationIndexes (VCFLine line) {
		// Get genome indexes
		List<Integer> genomeIndexes = new ArrayList<Integer>();
		if (genomes != null) {
			for (String genome: genomes) {
				genomeIndexes.add(vcfFile.getHeader().getIndexFromFullGenomeName(genome));
			}
		} else {
			int genomeNumber = vcfFile.getHeader().getGenomeNames().size();
			int index = 9;
			for (int i = 0; i < genomeNumber; i++) {
				genomeIndexes.add(index + i);
			}
		}

		// Get alternatives indexes define by selected genomes
		List<Integer> altIndexes = new ArrayList<Integer>();
		for (int index: genomeIndexes) {
			String genotype = line.getFormatField(index, 0).toString();
			genotype = genotype.replace('|', '/');
			String[] currentAltIndexes = genotype.split("/");
			for (String currentAlt: currentAltIndexes) {
				int current = VCFLineUtility.getAlleleIndex(currentAlt);
				if ((current != -1) && !altIndexes.contains(current)) {
					altIndexes.add(current);
				}
			}
		}

		// return result
		return altIndexes;
	}


	/**
	 * @param alternativeIndexes list of alternatives indexes
	 * @return true if at least one variation is defined (no call is seen as a {@link VariantType}), false otherwise
	 */
	private boolean genomesValidation (List<Integer> alternativeIndexes) {
		boolean defineSomething = false;

		int index = 0;
		while ((index < alternativeIndexes.size()) && !defineSomething) {
			if (alternativeIndexes.get(index) != -1) {
				defineSomething = true;
			}
			index++;
		}

		return defineSomething;
	}


	/**
	 * 
	 * @param line					the {@link VCFLine}
	 * @param alternativeIndexes	the list of alternatives indexes
	 * @return						true if at least one of the mandatory variation is defined, false otherwise
	 */
	private boolean variationsValidation (VCFLine line, List<Integer> alternativeIndexes) {
		if (variations == null) {
			return true;
		}

		List<VariantType> foundVariations = new ArrayList<VariantType>();
		VariantType[] definedVariations = line.getAlternativesTypes();
		for (int index: alternativeIndexes) {
			VariantType currentType = null;
			if (index == -2) {
				currentType = VariantType.NO_CALL;
			} else if (index > -1) {
				currentType = definedVariations[index];
			}

			if ((currentType != null) && !foundVariations.contains(currentType)) {
				foundVariations.add(currentType);
			}
		}


		for (VariantType currentType: foundVariations) {
			if (variations.contains(currentType)) {
				return true;
			}
		}

		return false;
	}


	/**
	 * @return the genomes
	 */
	public List<String> getGenomes() {
		return genomes;
	}


	/**
	 * @param genomes the genomes to set
	 */
	public void setGenomes(List<String> genomes) {
		this.genomes = genomes;
	}


	/**
	 * @return the variations
	 */
	public List<VariantType> getVariations() {
		return variations;
	}


	/**
	 * @param variations the variations to set
	 */
	public void setVariations(List<VariantType> variations) {
		this.variations = variations;
	}


	/**
	 * @return the filters
	 */
	public List<MGFilter> getFilters() {
		return filters;
	}


	/**
	 * @param filters the filters to set
	 */
	public void setFilters(List<MGFilter> filters) {
		this.filters = filters;
	}

}
