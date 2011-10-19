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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import edu.yu.einstein.genplay.core.chromosome.Chromosome;
import edu.yu.einstein.genplay.exception.InvalidChromosomeException;


/**
 * The ChromosomeManager class provides tools to load and access and list of {@link Chromosome}.
 * This class follows the design pattern <i>Singleton</i> 
 * @author Julien Lajugie
 * @author Nicolas Fourel
 * @version 0.1
 */
public final class ProjectChromosome implements Serializable, Iterable<Chromosome> {

	private static final long serialVersionUID = 8781043776370540275L;	// generated ID
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 0;			// saved format version
	private		 	Map<String, Integer> 		chromosomeHash;			// Hashtable indexed by chromosome name
	//private			Map<String, Chromosome> 	chromosomeList;			// List of chromosome
	private			List<Chromosome> 	chromosomeList;			// List of chromosome
	private			Chromosome					currentChromosome;		// Current chromosome in the genome window (uses for multi genome project)
	private			long						genomeLength;			// Total length of the genome


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeObject(chromosomeHash);
		out.writeObject(chromosomeList);
		out.writeObject(currentChromosome);
		out.writeLong(genomeLength);
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
		chromosomeHash = (Map<String, Integer>) in.readObject();
		chromosomeList = (List<Chromosome>) in.readObject();
		currentChromosome = (Chromosome) in.readObject();
		genomeLength = (long) in.readLong();
	}


	/**
	 * Constructor of {@link ProjectChromosome}.
	 */
	protected ProjectChromosome() {
		//super();
	}


	/**
	 * @param chromosomeList the chromosomeList to set
	 */
	public void setChromosomeList(List<Chromosome> chromosomeList) {
		this.chromosomeList = chromosomeList;
		createChromosomeHash();
		genomeLengthCalculation();
	}


	/**
	 * Creates the chromosome hash.
	 * Replaces the existing one.
	 */
	private void createChromosomeHash () {
		// Creates the chromosome names list
		List<String> chromosomeNames = new ArrayList<String>();
		for (Chromosome chromosome: chromosomeList) {
			chromosomeNames.add(chromosome.getName().toLowerCase());
		}

		// Creates the chromosome/index mapping
		chromosomeHash = new HashMap<String, Integer>();
		int cpt = 0;
		for (String s: chromosomeNames) {
			chromosomeHash.put(s, cpt);
			cpt++;
		}
	}


	/**
	 * Compute the size of the genome.
	 */
	private void genomeLengthCalculation () {
		genomeLength = 0;
		for (Chromosome chromosome: chromosomeList) {
			genomeLength += chromosome.getLength();
		}
	}


	/**
	 * @return the length of the genome in bp
	 */
	public long getGenomeLength() {
		return genomeLength;
	}


	/**
	 * @param index index of a {@link Chromosome}
	 * @return the first chromosome with the specified index
	 * @throws InvalidChromosomeException
	 */
	public Chromosome get(int index) {
		if (index < chromosomeList.size()) {
			return chromosomeList.get(index);
		}
		
		// throw an exception if nothing found
		throw new InvalidChromosomeException();
	}


	/**
	 * @param chromosomeName name of a {@link Chromosome}
	 * @return the first chromosome having the specified name
	 * @throws InvalidChromosomeException
	 */
	public Chromosome get(String chromosomeName) throws InvalidChromosomeException {
		// we put the chromosome name in lower case to avoid problems related to case sensitivity
		chromosomeName = chromosomeName.toLowerCase();
		
		// we get the index of associated to the chromosome name
		Integer index = chromosomeHash.get(chromosomeName);
		
		// if the index (thus the chromosome) exists, we can return the chromosome
		if (index != null) {
			return chromosomeList.get(index);
		}
		
		// throw an exception if nothing found
		throw new InvalidChromosomeException();
	}


	/**
	 * @param chromosome a {@link Chromosome}
	 * @return the index of the specified chromosome
	 * @throws InvalidChromosomeException
	 */
	public short getIndex(Chromosome chromosome) throws InvalidChromosomeException {
		return getIndex(chromosome.getName());
	}


	/**
	 * @param chromosomeName name of a chromosome.
	 * @return the index of the first chromosome having the specified name
	 * @throws InvalidChromosomeException
	 */
	public short getIndex(String chromosomeName) throws InvalidChromosomeException {
		// we put the chromosome name in lower case to avoid problems related to case sensitivity
		chromosomeName = chromosomeName.toLowerCase();
		
		// we get the index of associated to the chromosome name 
		short index = (short)chromosomeHash.get(chromosomeName).intValue();
		
		// throw an exception if nothing found
		if (index == -1) {
			throw new InvalidChromosomeException();
		} else {
			return index;	// the index is returned
		}
	}


	/**
	 * @return the size of the list of chromosome (ie: the number of chromosomes)
	 */
	public int size() {
		return chromosomeList.size();
	}


	/**
	 * @return an array containing all the chromosomes of the manager
	 */
	public Chromosome[] toArray() {
		// Initializes the chromosome array
		Chromosome[] returnArray = new Chromosome[chromosomeList.size()];
		
		// Fills the chromosome array
		returnArray = chromosomeList.toArray(returnArray);
		
		// Fills the chromosome array
		/*int cpt = 0;
		for (Chromosome chromosome: chromosomeList) {
			returnArray[cpt] = chromosome;
			cpt++;
		}*/
		
		// Returns the array
		return returnArray;
	}


	/**
	 * @return the chromosomeList
	 */
	public List<Chromosome> getChromosomeList() {
		return chromosomeList;
	}


	/**
	 * @return the currentChromosome
	 */
	public Chromosome getCurrentChromosome() {
		if (currentChromosome == null) {
			return get(0);
		}
		return currentChromosome;
	}


	/**
	 * @param currentChromosome the currentChromosome to set
	 */
	public void setCurrentChromosome(Chromosome currentChromosome) {
		this.currentChromosome = currentChromosome;
	}


	@Override
	/**
	 * Constructor for chromosome manager iterator.
	 */
	public Iterator<Chromosome> iterator() {
		return new ChromosomeManagerIterator();
	}


	/**
	 * Iterator for chromosome manager.
	 * @author Julien Lajugie
	 * @author Nicolas Fourel
	 */
	private class ChromosomeManagerIterator implements Iterator<Chromosome> {

		private int currentIndex = 0;

		@Override
		public boolean hasNext() {
			if (currentIndex < chromosomeHash.size()) {
				return true;
			} else {
				return false;
			}
		}


		@Override
		public Chromosome next() throws NoSuchElementException {
			for (Chromosome chromosome: chromosomeList){
				// we put the chromosome name in lower case to avoid problems related to case sensitivity
				if (chromosomeHash.get(chromosome.getName().toLowerCase()) == currentIndex) {
					currentIndex++;
					return chromosome;
				}
			}
			throw new NoSuchElementException(); 
		}


		@Override
		public void remove() throws UnsupportedOperationException {
			throw new UnsupportedOperationException();				
		}		
	}
}
