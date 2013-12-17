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
package edu.yu.einstein.genplay.core.manager.project;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.yu.einstein.genplay.core.chromosome.Chromosome;


/**
 * This class symbolizes the meta genome in a multi genome project.
 * A meta genome represents a virtual genome containing every information of all VCF files loaded.
 * @author Nicolas Fourel
 * @version 0.1
 */
public class MetaGenomeSynchroniser implements Serializable {

	private static final long serialVersionUID = 8473172631163790164L; 	// generated ID
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 0;			// saved format version
	private 		Map<String, Integer> 		chromosomeLength;		// The chromosome length list
	private			List<Chromosome> 			chromosomeList;			// The chromosome list for multi genome project
	private 		long 						genomomeLength = 0;		// Genome length


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeObject(chromosomeLength);
		out.writeObject(chromosomeList);
		out.writeLong(genomomeLength);
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
		chromosomeLength = (Map<String, Integer>) in.readObject();
		chromosomeList = (List<Chromosome>) in.readObject();
		genomomeLength = in.readLong();
	}
	
	
	/**
	 * Constructor of {@link MetaGenomeSynchroniser}
	 */
	protected MetaGenomeSynchroniser (List<Chromosome> chromosomeList) {
		initializeMetaGenomeSynchronizer(chromosomeList);
	}

	
	/**
	 * Initializes basic meta genome synchronizer parameters.
	 * The list of chromosome and the list of their length to native value.
	 * @param chromosomeListTmp
	 */
	private void initializeMetaGenomeSynchronizer (List<Chromosome> chromosomeListTmp) {
		chromosomeList = new ArrayList<Chromosome>();
		chromosomeLength = new HashMap<String, Integer>();
		for (Chromosome chromosome: chromosomeListTmp) {
			chromosomeList.add(new Chromosome(chromosome.getName(), chromosome.getLength()));
			chromosomeLength.put(chromosome, chromosome.getLength());
		}
	}


	/**
	 * Compute the size of the genome
	 */
	protected synchronized void computeGenomeSize() {
		genomomeLength = 0;
		for (Integer length: chromosomeLength.values()) {
			genomomeLength += length;
		}
	}


	/**
	 * Updates the chromosome length.
	 * Only insertion can increase a chromosome length.
	 * @param chromosome the chromosome
	 * @param length	the length to add
	 */
	protected void updateChromosomeLength (Chromosome chromosome, int length) {
		chromosomeLength.put(chromosome, chromosomeLength.get(chromosome) + length);
	}


	/**
	 * Refreshes chromosome references re-creating list with right chromosomes with right lengths.
	 */
	protected void refreshChromosomeReferences () {
		
		// Initializes temporary lists
		List<Chromosome> chromosomeListTmp = new ArrayList<Chromosome>();
		Map<Chromosome, Integer> chromosomeLengthTmp = new HashMap<Chromosome, Integer>();
		
		// For every chromosome of the project
		for (Chromosome chromosome: chromosomeList) {
			
			// Creates a new chromosome with the right length
			Chromosome newChromosome = new Chromosome(chromosome.getName(), chromosomeLength.get(chromosome));
			
			// Adds it to the temporary lists
			chromosomeListTmp.add(newChromosome);
			chromosomeLengthTmp.put(newChromosome, chromosomeLength.get(chromosome));
		}
		
		// Replaces previous list using the new ones
		chromosomeList = chromosomeListTmp;
		chromosomeLength = chromosomeLengthTmp;
	}


	/**
	 * @return the chromosome list
	 */
	protected List<Chromosome> getChromosomeList () {
		return chromosomeList;
	}


	/**
	 * @return the length of the genome in bp
	 */
	protected long getGenomeLength() {
		return genomomeLength;
	}


	/**
	 * Shows meta genome mananger information.
	 */
	public void showData () {
		System.out.println("========== chromosomeLength");
		for (Chromosome chromosome: chromosomeLength.keySet()) {
			System.out.println(chromosome.getName() + ", " + chromosome.getLength() + ", " + chromosomeLength.get(chromosome));
		}
		System.out.println("========== chromosomeList");
		for (Chromosome chromosome: chromosomeList) {
			System.out.println(chromosome.getName() + ", " + chromosome.getLength());
		}
	}
}
