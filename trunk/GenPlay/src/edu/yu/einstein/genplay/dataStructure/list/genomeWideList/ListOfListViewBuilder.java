/*******************************************************************************
 * GenPlay, Einstein Genome Analyzer
 * Copyright (C) 2009, 2014 Albert Einstein College of Medicine
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * Authors: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *          Nicolas Fourel <nicolas.fourel@einstein.yu.edu>
 *          Eric Bouhassira <eric.bouhassira@einstein.yu.edu>
 * 
 * Website: <http://genplay.einstein.yu.edu>
 ******************************************************************************/
package edu.yu.einstein.genplay.dataStructure.list.genomeWideList;

import java.util.ArrayList;
import java.util.List;

import edu.yu.einstein.genplay.core.manager.project.ProjectChromosomes;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListViewBuilder;
import edu.yu.einstein.genplay.exception.exceptions.InvalidChromosomeException;
import edu.yu.einstein.genplay.exception.exceptions.ObjectAlreadyBuiltException;

/**
 * Creates a list of {@link ListView} objects.
 * The list has one {@link ListView} per chromosome of the project genome.
 * @param <T> type of the data stored in the {@link ListView} objects (eg: genes, SCW)
 * @author Julien Lajugie
 */
public class ListOfListViewBuilder<T> {

	/** List of the builders that will create the {@link ListView} objects */
	private List<ListViewBuilder<T>> builders;

	/** We store the {@link ProjectChromosomes} to avoid wasting time retrieving it */
	private final ProjectChromosomes projectChromosomes;


	/**
	 * Creates a new instance of {@link ListOfListViewBuilder}.
	 * @param listViewBuilderPrototype prototype of {@link ListViewBuilder} to use to creates the {@link ListView} objects
	 * @throws CloneNotSupportedException
	 */
	public <U extends ListViewBuilder<T>> ListOfListViewBuilder(U listViewBuilderPrototype) throws CloneNotSupportedException {
		projectChromosomes =  ProjectManager.getInstance().getProjectChromosomes();
		int chromosomeCount = projectChromosomes.size();
		builders = new ArrayList<ListViewBuilder<T>>(chromosomeCount);
		if (chromosomeCount > 0) {
			builders.add(0, listViewBuilderPrototype);
			for (int i = 1; i < chromosomeCount; i++) {
				builders.add(i, listViewBuilderPrototype.clone());
			}
		}
	}


	/**
	 * Adds an element to the list of {@link ListView} to be built
	 * @param chromosome {@link Chromosome} of the element to add
	 * @param element element to add
	 * @throws InvalidChromosomeException
	 * @throws ObjectAlreadyBuiltException
	 */
	public void addElementToBuild(Chromosome chromosome, T element) throws InvalidChromosomeException, ObjectAlreadyBuiltException {
		if (builders == null) {
			throw new ObjectAlreadyBuiltException();
		}
		int chromosomeIndex = projectChromosomes.getIndex(chromosome);
		builders.get(chromosomeIndex).addElementToBuild(element);
	}


	/**
	 * @param chromosome a {@link Chromosome}
	 * @return the builders for the {@link ListView} of the specified {@link Chromosome}
	 */
	public ListViewBuilder<T> getBuilder(Chromosome chromosome) {
		if (builders == null) {
			throw new ObjectAlreadyBuiltException();
		}
		int chromosomeIndex = projectChromosomes.getIndex(chromosome);
		return builders.get(chromosomeIndex);
	}


	/**
	 * @return the list of {@link ListView} elements
	 */
	public List<ListView<T>> getGenomicList() {
		List<ListView<T>> genomicList = new ArrayList<ListView<T>>();
		for (ListViewBuilder<T> currentBuilder: builders) {
			genomicList.add(currentBuilder.getListView());
		}
		builders = null;
		return genomicList;
	}
}
