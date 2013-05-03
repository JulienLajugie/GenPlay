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
package edu.yu.einstein.genplay.dataStructure.list.genomeWideList.repeatFamilyList;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import edu.yu.einstein.genplay.core.manager.project.ProjectChromosome;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.repeatListView.RepeatFamilyListView;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.dataStructure.list.listView.subListView.SubListView;
import edu.yu.einstein.genplay.exception.exceptions.InvalidChromosomeException;


/**
 * Simple implementation of the {@link RepeatFamilyList} interface.
 * @author Julien Lajugie
 */
public class SimpleRepeatFamilyList implements Serializable, RepeatFamilyList, Iterator<ListView<RepeatFamilyListView>> {

	/** Generated serial ID */
	private static final long serialVersionUID = 5575142659472215610L;

	/** Version number of the class */
	private static final transient int CLASS_VERSION_NUMBER = 0;

	/** {@link GenomicDataArrayList} containing the Genes */
	private final List<ListView<RepeatFamilyListView>> data;

	/** Array containing the names of all the families present in the list in alphabetical order  */
	private final String[] familyNames;

	/** Current index of the iterator */
	private transient int iteratorIndex = 0;


	/**
	 * Creates an instance of {@link SimpleRepeatFamilyList}
	 * @param data {@link ListView} of {@link RepeatFamilyListView} organized by chromosome
	 */
	public SimpleRepeatFamilyList(List<ListView<RepeatFamilyListView>> data) {
		super();
		ProjectChromosome projectChromosome = ProjectManager.getInstance().getProjectChromosome();
		this.data = new ArrayList<ListView<RepeatFamilyListView>>(projectChromosome.size());
		for (int i = 0; i < data.size(); i++){
			data.add(data.get(i));
		}
		familyNames = retrieveFamilyNames();
	}


	@Override
	public ListView<RepeatFamilyListView> get(Chromosome chromosome) throws InvalidChromosomeException {
		ProjectChromosome projectChromosome = ProjectManager.getInstance().getProjectChromosome();
		int chromosomeIndex = projectChromosome.getIndex(chromosome);
		return get(chromosomeIndex);
	}


	@Override
	public RepeatFamilyListView get(Chromosome chromosome, int index) throws InvalidChromosomeException {
		ProjectChromosome projectChromosome = ProjectManager.getInstance().getProjectChromosome();
		int chromosomeIndex = projectChromosome.getIndex(chromosome);
		return get(chromosomeIndex, index);
	}

	@Override
	public ListView<RepeatFamilyListView> get(int chromosomeIndex) {
		return data.get(chromosomeIndex);
	}


	@Override
	public RepeatFamilyListView get(int chromosomeIndex, int elementIndex) {
		return data.get(chromosomeIndex).get(elementIndex);
	}


	@Override
	public String[] getFamilyNames() {
		return familyNames;
	}


	@Override
	public boolean hasNext() {
		return iteratorIndex < size();
	}


	@Override
	public boolean isEmpty() {
		return data.isEmpty();
	}


	@Override
	public Iterator<ListView<RepeatFamilyListView>> iterator() {
		return this;
	}


	@Override
	public ListView<RepeatFamilyListView> next() {
		int currentIndex = iteratorIndex;
		iteratorIndex++;
		return get(currentIndex);
	}


	/**
	 * Method used for deserialization
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.readInt();
		// read final fields
		in.defaultReadObject();
	}


	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}


	/**
	 * @return an array containing the names of all the families present in the list in alphabetical order
	 */
	private String[] retrieveFamilyNames() {
		Set<String> familyName = new HashSet<String>();
		for (ListView<RepeatFamilyListView> chromoList: data) {
			for (RepeatFamilyListView currentFamily: chromoList) {
				familyName.add(currentFamily.getName());
			}
		}
		String[] familyNames = familyName.toArray(new String[0]);
		Arrays.sort(familyNames);
		return familyNames;
	}


	@Override
	public int size() {
		return data.size();
	}


	@Override
	public int size(Chromosome chromosome) throws InvalidChromosomeException {
		ProjectChromosome projectChromosome = ProjectManager.getInstance().getProjectChromosome();
		int index = projectChromosome.getIndex(chromosome);
		return size(index);
	}


	@Override
	public int size(int chromosomeIndex) {
		return data.get(chromosomeIndex).size();
	}


	@Override
	public ListView<ListView<RepeatFamilyListView>> subList(int fromIndex, int toIndex) {
		return new SubListView<ListView<RepeatFamilyListView>>(this, fromIndex, toIndex);
	}


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(CLASS_VERSION_NUMBER);
		out.defaultWriteObject();
	}
}
