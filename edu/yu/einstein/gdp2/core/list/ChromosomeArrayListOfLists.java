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
package yu.einstein.gdp2.core.list;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import yu.einstein.gdp2.core.Chromosome;
import yu.einstein.gdp2.core.manager.ChromosomeManager;
import yu.einstein.gdp2.exception.InvalidChromosomeException;


/**
 * This class represents a generic list organized by chromosome.
 * @author Julien Lajugie
 * @version 0.1
 */
public class ChromosomeArrayListOfLists<T> extends ArrayList<List<T>> implements Cloneable, Serializable, ChromosomeListOfLists<T> {

	private static final long serialVersionUID = 3989560975472825193L; 	// generated ID
	protected final ChromosomeManager 	chromosomeManager;				// ChromosomeManager
	private Chromosome[] 				savedChromosomes = null;		// Chromosomes of the chromosome manager saved before serialization


	/**
	 * Constructor
	 */
	public ChromosomeArrayListOfLists() {
		super();
		chromosomeManager = ChromosomeManager.getInstance();
	}


	/**
	 * @return the {@link ChromosomeManager}
	 */
	public ChromosomeManager getChromosomeManager() {
		return chromosomeManager;
	}


	/**
	 * Saves the chromosomes of the {@link ChromosomeManager} before serialization
	 * @param out {@link ObjectOutputStream}
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		savedChromosomes = (Chromosome[]) chromosomeManager.toArray();
		out.defaultWriteObject();
	}


	/**
	 * Checks after unserialization if the current chromosome manager is the same than
	 * the one used when the object was serialized.
	 * If not, retrieves only the corresponding chromosomes of the chromosome managers 
	 * @param in {@link ObjectInputStream}
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		// check if the current chromosome manager and the one used when the object was serialized are similar 
		boolean sameManager = true;
		for (short i = 0; i < chromosomeManager.size(); i++) {
			if ((savedChromosomes == null) || (i >= savedChromosomes.length) || (!savedChromosomes[i].equals(chromosomeManager.get(i)))) {
				sameManager = false;
			}
		}
		// if the managers are different we look for chromosomes that might have been moved inside the list
		if (sameManager == false) {
			// copy the current list and copy it in a temporary list
			ArrayList<List<T>> listTmp = new ArrayList<List<T>>(this);
			this.clear();
			for (short i = 0; i < chromosomeManager.size(); i++) {
				boolean chromosomeFound = false;
				int j = 0;
				while ((!chromosomeFound) && (j < savedChromosomes.length)) {
					if (savedChromosomes[j].equals(chromosomeManager.get(i))) {
						chromosomeFound = true;
					} else {
						j++;
					}
				}
				if (chromosomeFound) {
					this.add(listTmp.get(j));
				} else {
					this.add(null);
				}
			}			
		}
	}


	@Override
	public void add(Chromosome chromosome, T element) throws InvalidChromosomeException {
		get(chromosomeManager.getIndex(chromosome)).add(element);
	}


	@Override
	public List<T> get(Chromosome chromosome) throws InvalidChromosomeException {
		return get(chromosomeManager.getIndex(chromosome));
	}


	@Override
	public T get(int chromosomeIndex, int elementIndex) {
		return get(chromosomeIndex).get(elementIndex);
	}


	@Override
	public T get(Chromosome chromosome, int index) throws InvalidChromosomeException {
		return get(chromosomeManager.getIndex(chromosome)).get(index);
	}


	@Override
	public void set(Chromosome chromosome, int index, T element) throws InvalidChromosomeException {
		get(chromosomeManager.getIndex(chromosome)).set(index, element);
	}


	@Override
	public void set(Chromosome chromosome, List<T> list) throws InvalidChromosomeException {
		set(chromosomeManager.getIndex(chromosome), list);
	}


	@Override
	public void set(int chromosomeIndex, int elementIndex, T element) {
		get(chromosomeIndex).set(elementIndex, element);
	}


	@Override
	public int size(int index) {
		return get(index).size();
	}


	@Override
	public int size(Chromosome chromosome) throws InvalidChromosomeException {
		return get(chromosomeManager.getIndex(chromosome)).size();
	}
}
