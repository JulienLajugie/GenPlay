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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import edu.yu.einstein.genplay.core.chromosome.Chromosome;
import edu.yu.einstein.genplay.core.manager.project.ProjectChromosome;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.exception.exceptions.InvalidChromosomeException;



/**
 * This class represents a generic list organized by {@link Chromosome}
 * @param <T> type of the objects stored in the list
 * @author Julien Lajugie
 * @version 0.1
 */
public class ChromosomeArrayListOfLists<T> implements List<List<T>>, Cloneable, Serializable, ChromosomeListOfLists<T> {

	private static final long serialVersionUID = 3989560975472825193L; 	// generated ID
	private static final int SAVED_FORMAT_VERSION_NUMBER = 0;			// saved format version
	private ProjectChromosome projectChromosome; 						// Instance of the Chromosome Manager
	private List<List<T>> dataList;


	/**
	 * Constructor of {@link ChromosomeArrayListOfLists}.
	 */
	public ChromosomeArrayListOfLists() {
		super();
		this.projectChromosome = ProjectManager.getInstance().getProjectChromosome();
		dataList = new ArrayList<List<T>>();
	}


	/**
	 * Saves the format version number during serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeObject(projectChromosome);
		out.writeObject(dataList);
	}


	/**
	 * Unserializes the save format version number
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.readInt();
		projectChromosome = (ProjectChromosome) in.readObject();
		dataList = (List<List<T>>) in.readObject();
	}


	// Implentation of ChromosomeListOfLists<T>
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


	// Implementation of List<List<T>>
	@Override
	public boolean add(List<T> e) {
		return dataList.add(e);
	}


	@Override
	public void add(int index, List<T> element) {
		dataList.add(index, element);
	}


	@Override
	public boolean addAll(Collection<? extends List<T>> c) {
		return dataList.addAll(c);
	}


	@Override
	public boolean addAll(int index, Collection<? extends List<T>> c) {
		return dataList.addAll(index, c);
	}


	@Override
	public void clear() {
		dataList.clear();
	}


	@Override
	public boolean contains(Object o) {
		return dataList.contains(o);
	}


	@Override
	public boolean containsAll(Collection<?> c) {
		return dataList.containsAll(c);
	}


	@Override
	public List<T> get(int index) {
		return dataList.get(index);
	}


	@Override
	public int indexOf(Object o) {
		return dataList.indexOf(o);
	}


	@Override
	public boolean isEmpty() {
		return dataList.isEmpty();
	}


	@Override
	public Iterator<List<T>> iterator() {
		return dataList.iterator();
	}


	@Override
	public int lastIndexOf(Object o) {
		return dataList.lastIndexOf(o);
	}


	@Override
	public ListIterator<List<T>> listIterator() {
		return dataList.listIterator();
	}
	@Override
	public ListIterator<List<T>> listIterator(int index) {
		return dataList.listIterator(index);
	}


	@Override
	public boolean remove(Object o) {
		return dataList.remove(o);
	}


	@Override
	public List<T> remove(int index) {
		return dataList.remove(index);
	}


	@Override
	public boolean removeAll(Collection<?> c) {
		return dataList.removeAll(c);
	}


	@Override
	public boolean retainAll(Collection<?> c) {
		return retainAll(c);
	}


	@Override
	public List<T> set(int index, List<T> element) {
		return dataList.set(index, element);
	}


	@Override
	public int size() {
		return dataList.size();
	}


	@Override
	public List<List<T>> subList(int fromIndex, int toIndex) {
		return dataList.subList(fromIndex, toIndex);
	}


	@Override
	public Object[] toArray() {
		return dataList.toArray();
	}


	@Override
	public <U> U[] toArray(U[] a) {
		return dataList.toArray(a);
	}
}