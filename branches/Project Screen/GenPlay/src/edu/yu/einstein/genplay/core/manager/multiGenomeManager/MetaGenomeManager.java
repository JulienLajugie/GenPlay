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

import java.util.HashMap;
import java.util.Map;
import edu.yu.einstein.genplay.core.Chromosome;
import edu.yu.einstein.genplay.core.manager.ChromosomeManager;


/**
 * This class symbolizes the meta genome in a multi genome project.
 * A meta genome represents a virtual genome containing every information of all VCF files loaded.
 * @author Nicolas Fourel
 */
public class MetaGenomeManager {

	private static 	MetaGenomeManager 			instance;			// The instance of the class
	private 		Map<Chromosome, Integer> 	chromosomeLength;	// The chromosome length list
	private			Map<String, Chromosome> 	chromosomeList;
	private 		long 						genomomeLength = 0;

	/**
	 * Constructor of {@link MetaGenomeManager}
	 */
	private MetaGenomeManager () {
		//initializeChromosomeLength();
	}
	
	
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
	
	
	public void initChromosomeList () {
		chromosomeList = new HashMap<String, Chromosome>();
		for (Chromosome chromosome: ChromosomeManager.getInstance().getChromosomeList().values()) {
			chromosomeList.put(chromosome.getName(), new Chromosome(chromosome.getName(), chromosome.getLength()));
		}
	}
	
	
	public void updateChromosomeList () {
		for (Chromosome chromosome: chromosomeLength.keySet()) {
			chromosomeList.get(chromosome.getName()).setLength(chromosomeLength.get(chromosome));
		}
	}
	
	
	public Map<String, Chromosome> getChromosomeList () {
		return chromosomeList;
	}
	
	
	/**
	 * @return the length of the genome in bp
	 */
	public long getGenomeLength() {
		return genomomeLength;
	}
	
	
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