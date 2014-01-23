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
package edu.yu.einstein.genplay.core.operation.SCWList;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;

import edu.yu.einstein.genplay.core.manager.project.ProjectChromosomes;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.core.operationPool.OperationPool;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.enums.SCWListType;
import edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.SCWListView.SCWListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.SCWListView.dense.DenseSCWListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.SCWListView.generic.GenericSCWListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.SCWListView.mask.MaskListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.GenomicListView;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SCWList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SCWListBuilder;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SimpleSCWList.SimpleSCWList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.binList.BinList;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;

/**
 * Converts a {@link GenomicListView} of objects that extends the {@link ScoredChromosomeWindow}
 * interface into a {@link SimpleSCWList}.
 * @author Julien Lajugie
 */
public class SCWLOConvertIntoSimpleSCWList implements Operation<SCWList> {

	private final GenomicListView<? extends ScoredChromosomeWindow> inputList;		// input list
	private final SCWListViewBuilder 								lvbPrototype;	// prototype of list view builder
	private boolean													stopped = false;// true if the operation must be stopped


	/**
	 * Creates an instance of {@link SCWLOConvertIntoSimpleSCWList}
	 * @param inputList list to convert
	 * @param listType type of the result SCWList. Cannot be a {@link BinList}
	 */
	public SCWLOConvertIntoSimpleSCWList(GenomicListView<? extends ScoredChromosomeWindow> inputList, SCWListType listType) {
		this.inputList = inputList;
		lvbPrototype = createPrototype(listType);
	}


	@Override
	public SCWList compute() throws Exception {
		ProjectChromosomes projectChromosomes = ProjectManager.getInstance().getProjectChromosomes();
		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<Void>> threadList = new ArrayList<Callable<Void>>();
		final SCWListBuilder resultListBuilder = new SCWListBuilder(lvbPrototype);

		for (final Chromosome chromosome: projectChromosomes) {
			final ListView<? extends ScoredChromosomeWindow> currentList = inputList.get(chromosome);
			Callable<Void> currentThread = new Callable<Void>() {
				@Override
				public Void call() throws Exception {
					if (currentList != null) {
						for (int i = 0; (i < currentList.size()) && !stopped; i++) {
							ScoredChromosomeWindow scw = currentList.get(i);
							float score = scw.getScore();
							if (score != 0) {
								int start = scw.getStart();
								int stop = scw.getStop();
								resultListBuilder.addElementToBuild(chromosome, start, stop, score);
							}
						}
					}
					// tell the operation pool that a chromosome is done
					op.notifyDone();
					return null;
				}
			};
			threadList.add(currentThread);
		}
		op.startPool(threadList);
		return resultListBuilder.getSCWList();
	}


	/**
	 * @param listType a type of {@link SCWList}
	 * @return a SCWListViewBuilder to create the result list
	 */
	private SCWListViewBuilder createPrototype(SCWListType listType) {
		switch (listType) {
		case BIN:
			throw new InvalidParameterException("This object cannot build a BinList");
		case DENSE:
			return new DenseSCWListViewBuilder();
		case GENERIC:
			return new GenericSCWListViewBuilder();
		case MASK:
			return new MaskListViewBuilder();
		default:
			break;
		}
		return null;
	}


	@Override
	public String getDescription() {
		return "Operation: Convert Into Gene List";
	}


	@Override
	public String getProcessingDescription() {
		return "Converting Layer";
	}


	@Override
	public int getStepCount() {
		return 1;
	}


	@Override
	public void stop() {
		stopped = true;
	}
}
