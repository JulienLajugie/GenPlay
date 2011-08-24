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
package edu.yu.einstein.genplay.core.manager.multiGenomeManager;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import edu.yu.einstein.genplay.core.Chromosome;
import edu.yu.einstein.genplay.core.manager.ChromosomeManager;


/**
 * This class symbolizes the meta genome in a multi genome project.
 * A meta genome represents a virtual genome containing every information of all VCF files loaded.
 * @author Nicolas Fourel
 * @version 0.1
 */
public class MetaGenomeManager implements Serializable {

	private static final long serialVersionUID = 8473172631163790164L; 	// generated ID
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 0;			// saved format version
	private static 	MetaGenomeManager 			instance;			// The instance of the class
	private 		Map<Chromosome, Integer> 	chromosomeLength;	// The chromosome length list
	private			Map<String, Chromosome> 	chromosomeList;		// The chromosome list for multi genome project
	private 		long 						genomomeLength = 0;	// Genome length


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeObject(instance);
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
		instance = (MetaGenomeManager) in.readObject();
		chromosomeLength = (Map<Chromosome, Integer>) in.readObject();
		chromosomeList = (Map<String, Chromosome>) in.readObject();
		genomomeLength = in.readLong();
	}
	
	
	/**
	 * Constructor of {@link MetaGenomeManager}
	 */
	private MetaGenomeManager () {}


	/**
	 * @return the instance of the singleton {@link MetaGenomeManager}.
	 */
	public static MetaGenomeManager getInstance () {
		if (instance == null) {
			instance = new MetaGenomeManager();
		}
		return instance;
	}


	/**
	 * Initializes the chromosome length.
	 * The minimum length for a meta genome chromosome is the length from the reference chromosome.
	 */
	public void initializeChromosomeLength () {
		chromosomeLength = new HashMap<Chromosome, Integer>();
		for (Chromosome chromosome: ChromosomeManager.getInstance().getChromosomeList().values()) {
			chromosomeLength.put(chromosome, chromosome.getLength());
		}

	}


	/**
	 * Compute the size of the genome
	 */
	public synchronized void computeGenomeSize() {
		genomomeLength = 0;
		for (Integer length: chromosomeLength.values()) {
			genomomeLength += length;
		}
	}


	/**
	 * @param chromosome the chromosome
	 * @return the chromosome length
	 */
	public Integer getChromosomeLength (Chromosome chromosome) {
		return chromosomeLength.get(chromosome);
	}


	/**
	 * Updates the chromosome length.
	 * Only insertion can increase a chromosome length.
	 * @param chromosome the chromosome
	 * @param length	the length to add
	 */
	public void updateChromosomeLength (Chromosome chromosome, int length) {
		chromosomeLength.put(chromosome, chromosomeLength.get(chromosome) + length);
	}


	/**
	 * Initializes the chromosome list
	 */
	public void initChromosomeList () {
		chromosomeList = new HashMap<String, Chromosome>();
		for (Chromosome chromosome: ChromosomeManager.getInstance().getChromosomeList().values()) {
			chromosomeList.put(chromosome.getName(), new Chromosome(chromosome.getName(), chromosome.getLength()));
		}
	}


	/**
	 * Updates the chromosome list.
	 * It consists on set new chromosome lengths.
	 */
	public void updateChromosomeList () {
		for (Chromosome chromosome: chromosomeLength.keySet()) {
			chromosomeList.get(chromosome.getName()).setLength(chromosomeLength.get(chromosome));
		}
	}


	/**
	 * @return the chromosome list
	 */
	public Map<String, Chromosome> getChromosomeList () {
		return chromosomeList;
	}


	/**
	 * @return the length of the genome in bp
	 */
	public long getGenomeLength() {
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
		for (String name: chromosomeList.keySet()) {
			System.out.println(chromosomeList.get(name).getName() + ", " + chromosomeList.get(name).getLength());
		}
	}
}