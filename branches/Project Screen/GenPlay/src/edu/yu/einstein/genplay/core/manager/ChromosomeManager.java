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
package edu.yu.einstein.genplay.core.manager;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import edu.yu.einstein.genplay.core.Chromosome;
import edu.yu.einstein.genplay.core.manager.multiGenomeManager.MetaGenomeManager;
import edu.yu.einstein.genplay.exception.InvalidChromosomeException;
import edu.yu.einstein.genplay.gui.dialog.projectScreen.newProject.ChromosomeComparator;


/**
 * The ChromosomeManager class provides tools to load and access and list of {@link Chromosome}.
 * This class follows the design pattern <i>Singleton</i> 
 * @author Julien Lajugie
 * @author Nicolas Fourel
 * @version 0.1
 */
public final class ChromosomeManager implements Serializable, Iterable<Chromosome> {

	private static final long serialVersionUID = 8781043776370540275L;	// generated ID
	private static 	ChromosomeManager 			instance = null;		// unique instance of the singleton
	private		 	Map<String, Integer> 		chromosomeHash;			// Hashtable indexed by chromosome name
	private			Map<String, Chromosome> 	chromosomeList;
	
	
	
	
	/**
	 * @return an instance of a {@link ChromosomeManager}. 
	 * Makes sure that there is only one unique instance as specified in the singleton pattern
	 */
	public static ChromosomeManager getInstance() {
		if (instance == null) {
			synchronized(ChromosomeManager.class) {
				if (instance == null) {
					instance = new ChromosomeManager();
				}
			}
		}
		return instance;
	}
	

	/**
	 * Private constructor of the singleton. Creates an instance of a {@link ChromosomeManager}.
	 */
	private ChromosomeManager() {
		super();
		chromosomeHash = new HashMap<String, Integer>();
		setChromosomeList();
	}
	
	
	public void setChromosomeList () {
		if (ProjectManager.getInstance().isMultiGenomeProject()) {
			chromosomeList = MetaGenomeManager.getInstance().getChromosomeList();
			//MetaGenomeManager.getInstance().showData();
			if (chromosomeList == null) {
				chromosomeList = ProjectManager.getInstance().getAssembly().getChromosomeList();
			}
		} else {
			chromosomeList = ProjectManager.getInstance().getAssembly().getChromosomeList();
		}
		List<String> chromosomeNames = new ArrayList<String>(chromosomeList.keySet());
		Collections.sort(chromosomeNames, new ChromosomeComparator());
		chromosomeHash = new HashMap<String, Integer>();
		int cpt = 0;
		for (String s: chromosomeNames) {
			chromosomeHash.put(s, cpt);
			cpt++;
		}
	}
	
	
	/**
	 * @return the length of the genome in bp
	 */
	public long getGenomeLength() {
		if (ProjectManager.getInstance().isMultiGenomeProject()) {
			return MetaGenomeManager.getInstance().getGenomeLength();
		} else {
			return ProjectManager.getInstance().getAssembly().getGenomeLength();
		}
	}
	
	
	/**
	 * @param index index of a {@link Chromosome}
	 * @return the first chromosome with the specified index
	 * @throws InvalidChromosomeException
	 */
	public Chromosome get(int index) {
		for (Chromosome chromosome: chromosomeList.values()){
			if (chromosomeHash.get(chromosome.getName()) == index) {
				return chromosome;
			}
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
		Integer result = chromosomeHash.get(chromosomeName);
		if (result != null) {
			return chromosomeList.get(chromosomeName);
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
		short index = (short)chromosomeHash.get(chromosome.getName()).intValue();
		if (index == -1) {
			// if nothing has been found
			throw new InvalidChromosomeException();
		} else {
			return index;
		}
	}


	/**
	 * @param chromosomeName name of a chromosome.
	 * @return the index of the first chromosome having the specified name
	 * @throws InvalidChromosomeException
	 */
	public short getIndex(String chromosomeName) throws InvalidChromosomeException {
		short index = (short)chromosomeHash.get(chromosomeName).intValue();
		if (index == -1) {
			// if nothing has been found
			throw new InvalidChromosomeException();
		} else {
			return index;
		}
	}


	@Override
	public Iterator<Chromosome> iterator() {
		return new ChromosomeManagerIterator();
	}


	/**
	 * Methods used for the serialization of the singleton object.
	 * The readResolve method is called when ObjectInputStream has 
	 * read an object from the stream and is preparing to return it 
	 * to the caller.
	 * See javadocs for more information
	 * @return the unique instance of the singleton
	 * @throws ObjectStreamException
	 */
	private Object readResolve() throws ObjectStreamException {
		return getInstance();
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
		Chromosome[] returnArray = new Chromosome[chromosomeList.size()];
		List<String> chromosomeNames = new ArrayList<String>(chromosomeList.keySet());
		Collections.sort(chromosomeNames, new ChromosomeComparator());
		int cpt = 0;
		for (String s: chromosomeNames) {
			returnArray[cpt] = chromosomeList.get(s);
			cpt++;
		}
		return returnArray;
	}


	/**
	 * @return the chromosomeList
	 */
	public Map<String, Chromosome> getChromosomeList() {
		return chromosomeList;
	}

	
	
	private class ChromosomeManagerIterator implements Iterator<Chromosome> {

		private int currentIndex = 0;
		
		
		@Override
		public boolean hasNext() {
			if (currentIndex < chromosomeHash.size() - 1) {
				return true;
			} else {
				return false;
			}
		}

		@Override
		public Chromosome next() throws NoSuchElementException {
			for (Chromosome chromosome: chromosomeList.values()){
				if (chromosomeHash.get(chromosome.getName()) == currentIndex) {
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
