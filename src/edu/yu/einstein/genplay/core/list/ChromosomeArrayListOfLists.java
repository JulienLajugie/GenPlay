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
package edu.yu.einstein.genplay.core.list;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import edu.yu.einstein.genplay.core.chromosome.Chromosome;
import edu.yu.einstein.genplay.core.manager.project.ProjectChromosome;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.exception.InvalidChromosomeException;



/**
 * This class represents a generic list organized by {@link Chromosome}
 * @param <T> type of the objects stored in the list
 * @author Julien Lajugie
 * @version 0.1
 */
public class ChromosomeArrayListOfLists<T> extends ArrayList<List<T>> implements Cloneable, Serializable, ChromosomeListOfLists<T> {

	private static final long serialVersionUID = 3989560975472825193L; 	// generated ID
	private static final int SAVED_FORMAT_VERSION_NUMBER = 0;			// saved format version
	private ProjectChromosome projectChromosome; 						// Instance of the Chromosome Manager
	

	/**
	 * Constructor of {@link ChromosomeArrayListOfLists}.
	 */
	public ChromosomeArrayListOfLists() {
		super();
		this.projectChromosome = ProjectManager.getInstance().getProjectChromosome();
	}


	/**
	 * Saves the format version number during serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeObject(projectChromosome);
	}
	
	
	/**
	 * Unserializes the save format version number
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.readInt();
		projectChromosome = (ProjectChromosome) in.readObject();
	}
	

	@Override
	public void add(Chromosome chromosome, T element) throws InvalidChromosomeException {
		get(projectChromosome.getIndex(chromosome)).add(element);
	}


	@Override
	public List<T> get(Chromosome chromosome) throws InvalidChromosomeException {
		return get(projectChromosome.getIndex(chromosome));
	}


	@Override
	public T get(int chromosomeIndex, int elementIndex) {
		return get(chromosomeIndex).get(elementIndex);
	}


	@Override
	public T get(Chromosome chromosome, int index) throws InvalidChromosomeException {
		return get(projectChromosome.getIndex(chromosome)).get(index);
	}


	@Override
	public void set(Chromosome chromosome, int index, T element) throws InvalidChromosomeException {
		get(projectChromosome.getIndex(chromosome)).set(index, element);
	}


	@Override
	public void set(Chromosome chromosome, List<T> list) throws InvalidChromosomeException {
		set(projectChromosome.getIndex(chromosome), list);
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
		return get(projectChromosome.getIndex(chromosome)).size();
	}
}
