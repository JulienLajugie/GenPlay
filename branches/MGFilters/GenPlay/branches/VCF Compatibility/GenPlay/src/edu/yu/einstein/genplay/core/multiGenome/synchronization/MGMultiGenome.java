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
package edu.yu.einstein.genplay.core.multiGenome.synchronization;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class MGMultiGenome {

	private MGReference 	referenceGenome;		// instance of the reference genome
	private List<MGGenome> 	genomesInformation;		// list of the genomes information
	
	
	/**
	 * Constructor of {@link MGMultiGenome}
	 * @param genomeNames full name of the genomes
	 */
	public MGMultiGenome (List<String> genomeNames) {
		this.referenceGenome = new MGReference();
		genomesInformation = new ArrayList<MGGenome>();
		for (String genomeName: genomeNames) {
			genomesInformation.add(new MGGenome(genomeName));
		}
	}
	
	
	/**
	 * @param genomeName genome full name
	 * @return	the genome information object
	 */
	public MGGenome getGenomeInformation (String genomeName) {
		return genomesInformation.get(getGenomeIndex(genomeName));
	}
	
	
	/**
	 * @return	the list of genome information
	 */
	public List<MGGenome> getGenomeInformation () {
		return genomesInformation;
	}
	
	
	/**
	 * Gets the index 
	 * @param genomeName
	 * @return the index of the genome name
	 */
	public int getGenomeIndex (String genomeName) {
		int index = 0;
		for (MGGenome genome: genomesInformation) {
			if (genome.getName().indexOf(genomeName) != -1) {
				return index;
			}
			index++;
		}
		return -1;
	}
	
	
	/**
	 * Sorts the alleles of every genomes according to the position of the variation 
	 */
	public void sort() {
		referenceGenome.sort();
		for (MGGenome genome: genomesInformation) {
			genome.sort();
		}
	}
	
	
	/**
	 * Compacts the lists of position of the alleles of every genomes and chromosomes
	 */
	public void compactLists() {
		referenceGenome.compact();
		for (MGGenome genome: genomesInformation) {
			genome.compact();
		}
	}
	
	
	/**
	 * @return the referenceGenome
	 */
	public MGReference getReferenceGenome() {
		return referenceGenome;
	}
	
	
	/**
	 * Show the information of the {@link MGMultiGenome}
	 */
	public void show () {
		referenceGenome.show();
		for (MGGenome genome: genomesInformation) {
			genome.show();
		}
	}
	
}
