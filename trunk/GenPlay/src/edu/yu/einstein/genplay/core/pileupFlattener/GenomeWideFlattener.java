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
package edu.yu.einstein.genplay.core.pileupFlattener;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import edu.yu.einstein.genplay.core.manager.project.ProjectChromosomes;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.SCWListView.bin.BinListView;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SCWList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SimpleSCWList.SimpleSCWList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.binList.BinList;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;
import edu.yu.einstein.genplay.exception.exceptions.InvalidChromosomeException;
import edu.yu.einstein.genplay.exception.exceptions.ObjectAlreadyBuiltException;

/**
 * List of {@link PileupFlattener} with one {@link PileupFlattener} per {@link Chromosome}.
 * @author Julien Lajugie
 */
public class GenomeWideFlattener {

	/** List of {@link PileupFlattener} with one flattener per chromosome */
	private final List<PileupFlattener> flatteners;

	/** We store the {@link ProjectChromosomes} to avoid wasting time retrieving it */
	private final ProjectChromosomes projectChromosomes;


	/**
	 * Creates an instance of {@link GenomeWideFlattener}
	 * @param flattenerPrototype
	 * @throws CloneNotSupportedException
	 */
	public GenomeWideFlattener(PileupFlattener flattenerPrototype) throws CloneNotSupportedException {
		projectChromosomes =  ProjectManager.getInstance().getProjectChromosomes();
		int chromosomeCount = projectChromosomes.size();
		flatteners = new ArrayList<PileupFlattener>(chromosomeCount);
		if (chromosomeCount > 0) {
			flatteners.add(0, flattenerPrototype);
			for (int i = 1; i < chromosomeCount; i++) {
				flatteners.add(i, flattenerPrototype.clone());
			}
		}
	}


	/**
	 * Adds a Scored window to the list of {@link ScoredChromosomeWindow}
	 * @param chromosome {@link Chromosome} of the element to add
	 * @param start start position of the window to add
	 * @param stop stop position of the window to add
	 * @param score score of the window to add
	 * @throws InvalidChromosomeException
	 * @throws ObjectAlreadyBuiltException
	 */
	public void addWindow(Chromosome chromosome, int start, int stop, float score) throws ObjectAlreadyBuiltException {
		int chromosomeIndex = projectChromosomes.getIndex(chromosome);
		flatteners.get(chromosomeIndex).addWindow(start, stop, score);
	}


	/**
	 * Adds a Scored window to the list of {@link ScoredChromosomeWindow}
	 * @param chromosome {@link Chromosome} of the element to add
	 * @param currentWindow window to add
	 * @throws InvalidChromosomeException
	 * @throws ObjectAlreadyBuiltException
	 */
	public void addWindow(Chromosome chromosome, ScoredChromosomeWindow currentWindow) {
		int chromosomeIndex = projectChromosomes.getIndex(chromosome);
		flatteners.get(chromosomeIndex).addWindow(currentWindow);
	}


	/**
	 * Creates an instance of {@link SCWList} that can either be an instance of {@link BinList} or of {@link SimpleSCWList} depending
	 * on the specified {@link ListView} objects
	 * @return An instance of {@link ScoredChromosomeWindow}
	 * @throws ExecutionException
	 * @throws InterruptedException
	 * @throws InvalidParameterException
	 * @throws CloneNotSupportedException
	 * @throws Exception
	 */
	private final SCWList createSCWList(List<ListView<ScoredChromosomeWindow>> data) throws InvalidParameterException, InterruptedException, ExecutionException, CloneNotSupportedException  {
		if ((data == null) || data.isEmpty()) {
			return null;
		}
		if (data.get(0) instanceof BinListView) {
			return new BinList(data);
		} else {
			return new SimpleSCWList(data);
		}
	}


	/**
	 * @return the list of {@link ListView} elements
	 */
	public List<ListView<ScoredChromosomeWindow>> getListOfListViews() {
		List<ListView<ScoredChromosomeWindow>> genomicList = new ArrayList<ListView<ScoredChromosomeWindow>>();
		for (PileupFlattener currentFlattener: flatteners) {
			genomicList.add(currentFlattener.getListView());
		}
		return genomicList;
	}


	/**
	 * @return A SCWList resulting from the flattening process.<br>
	 * The type of the SCWList will be the same as the one from the list specified during this object construction.
	 * @throws ExecutionException
	 * @throws InterruptedException
	 * @throws InvalidParameterException
	 * @throws CloneNotSupportedException
	 */
	public SCWList getSCWList() throws InvalidParameterException, InterruptedException, ExecutionException, CloneNotSupportedException  {
		SCWList list = createSCWList(getListOfListViews());
		return list;
	}
}
