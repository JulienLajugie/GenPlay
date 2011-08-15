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
import edu.yu.einstein.genplay.core.enums.VariantType;
import edu.yu.einstein.genplay.core.manager.ChromosomeManager;
import edu.yu.einstein.genplay.core.manager.multiGenomeManager.MultiGenomeManager;




/**
 * This class manages the genome information in a multi genome project.
 * The genome information is all variation information for a specific genome.
 * Those information are mainly the list of {@link MGChromosome} object.
 * 
 * This class can be considered as a "sub-class" of {@link MGMultiGenome}.
 * 
 * @author Nicolas Fourel
 * @version 0.1
 */
public class MGGenome {

	private		final String					genomeName;				// The full genome information
	private 	Map<Chromosome, MGChromosome> 	genomeInformation;		// Chromosomes information of the genome


	/**
	 * Constructor of {@link MGGenome}
	 * @param genomeName the name of the genome
	 */
	protected MGGenome (String genomeName) {
		this.genomeName = genomeName;
		genomeInformation = new HashMap<Chromosome, MGChromosome>();
		for (Chromosome chromosome: ChromosomeManager.getInstance().getCurrentMultiGenomeChromosomeList().values()) {
			genomeInformation.put(chromosome, new MGChromosome(this, chromosome));
		}
	}


	/**
	 * Adds a variant according to a chromosome.
	 * @param chromosome 	the chromosome
	 * @param variant 		the variant
	 */
	protected void addVariant (Chromosome chromosome, Variant variant) {
		getChromosomeInformation(chromosome).addVariant(variant);
	}
	
	
	/**
	 * Adds a variant according to a chromosome.
	 * @param chromosome 	the chromosome
	 * @param position 		the position on the reference genome
	 * @param variant 		the variant
	 */
	protected void addBlank (Chromosome chromosome, int position, Variant variant) {
		getChromosomeInformation(chromosome).addBlank(position, variant);
	}


	/**
	 * @param chromosome 	the related chromosome
	 * @return				valid chromosome containing position information
	 */
	protected MGChromosome getChromosomeInformation (Chromosome chromosome) {
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
	protected Map<Chromosome, MGChromosome> getGenomeInformation() {
		return genomeInformation;
	}


	/**
	 * @return the genomeFullName
	 */
	public String getGenomeName() {
		return genomeName;
	}

	
	/**
	 * Shows chromosome information.
	 */
	protected void showData () {
		for (Chromosome chromosome: genomeInformation.keySet()) {
			System.out.println("= chromosome name: " + chromosome.getName());
			getChromosomeInformation(chromosome).showData();
		}
	}
}