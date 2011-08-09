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
package edu.yu.einstein.genplay.core.multiGenome.engine;

import java.util.HashMap;
import java.util.Map;
import edu.yu.einstein.genplay.core.Chromosome;
import edu.yu.einstein.genplay.core.enums.VCFType;
import edu.yu.einstein.genplay.core.enums.VariantType;
import edu.yu.einstein.genplay.core.manager.ChromosomeManager;
import edu.yu.einstein.genplay.core.manager.multiGenomeManager.MultiGenomeManager;


/**
 * This class manages the genome information.
 * Those information are the chromosome and its relative information.
 * @author Nicolas Fourel
 * @version 0.1
 */
public class MGGenomeInformation {

	private		final String								genomeFullName;				// The full genome information
	private 	Map<Chromosome, MGChromosomeInformation> 	genomeInformation;			// Chromosomes information
	protected 	Chromosome									fittedChromosome = null;	// Chromosome with the adapted data
	protected 	Double										fittedXRatio = null;		// xRatio of the adapted data (ie ratio between the number of pixel and the number of base to display )
	//private		int											smallestFittedDataIndex;	// The smaller index of the returned fitted data list
	//private		int											highestFittedDataIndex;		// The highest index of the returned fitted data list


	/**
	 * Constructor of {@link MGGenomeInformation}
	 */
	protected MGGenomeInformation (String genomeFullName) {
		this.genomeFullName = genomeFullName;
		genomeInformation = new HashMap<Chromosome, MGChromosomeInformation>();
		for (Chromosome chromosome: ChromosomeManager.getInstance().getCurrentMultiGenomeChromosomeList().values()) {
			genomeInformation.put(chromosome, new MGChromosomeInformation(chromosome, this));
		}
	}


	/**
	 * Adds a position information according to a chromosome.
	 * @param chromosome	the related chromosome
	 * @param position		the position
	 * @param positionInformation 
	 * @param vcfType 
	 * @param type			the information type
	 * @param info map containing genome variation information 
	 */
	protected void addInformation (Chromosome chromosome, Integer position, String fullGenomeName, Map<String, Object> VCFLine, MGPositionInformation positionInformation, VCFType vcfType) {
		getChromosomeInformation(chromosome).addVariant(position, fullGenomeName, VCFLine, positionInformation, vcfType);
	}


	/**
	 * @param chromosome 	the related chromosome
	 * @return				valid chromosome containing position information
	 */
	protected MGChromosomeInformation getChromosomeInformation (Chromosome chromosome) {
		if (genomeInformation.get(chromosome) == null &&
				MultiGenomeManager.CHROMOSOME_LOADING_OPTION == MultiGenomeManager.SEQUENTIAL) {
			System.err.println("A null pointer exception can appear because of the CHROMOSOME_LOADING_OPTION set to SEQUENTIAL");
		}
		return genomeInformation.get(chromosome);
	}


	/**
	 * @param chromosome	the chromosome
	 * @param position		the position
	 * @return				the type of a specified position according to the chromosome
	 */
	protected VariantType getType (Chromosome chromosome, Integer position) {
		return getChromosomeInformation(chromosome).getType(position);
	}


	/**
	 * @return the genomeInformation
	 */
	protected Map<Chromosome, MGChromosomeInformation> getGenomeInformation() {
		return genomeInformation;
	}


	/**
	 * Shows chromosomes information.
	 */
	protected void showData () {
		for (Chromosome chromosome: genomeInformation.keySet()) {
			System.out.println("= chromosome name: " + chromosome.getName());
			getChromosomeInformation(chromosome).showData();
		}
	}


	/**
	 * @return the genomeFullName
	 */
	public String getGenomeFullName() {
		return genomeFullName;
	}


	/**
	 * @param chromosome the chromosome
	 * @param position position of the variant on the reference genome
	 * @return the associated position information
	 */
	public MGPositionInformation getPositionInformation (Chromosome chromosome, int position) {
		if (genomeInformation.get(chromosome) != null) {
			return genomeInformation.get(chromosome).getPositionInformation(position);
		}
		return null;
	}

}