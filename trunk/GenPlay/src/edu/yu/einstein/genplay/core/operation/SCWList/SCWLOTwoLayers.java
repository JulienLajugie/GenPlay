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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import edu.yu.einstein.genplay.core.manager.project.ProjectChromosomes;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.core.operationPool.OperationPool;
import edu.yu.einstein.genplay.core.pileupFlattener.GenomeWideFlattener;
import edu.yu.einstein.genplay.core.pileupFlattener.SimpleSCWPileupFlattener;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.enums.ScoreOperation;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SCWList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SimpleSCWList.SimpleSCWList;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListOfListViewsIterator;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.SimpleScoredChromosomeWindow;
import edu.yu.einstein.genplay.gui.statusBar.Stoppable;


/**
 * Realizes operation on two Layers
 * @author Nicolas Fourel
 * @author Julien Lajugie
 */
public class SCWLOTwoLayers implements Operation<SCWList>, Stoppable {

	private final SCWList 			list1;				// fist list
	private final SCWList 			list2;				// second list
	private final ScoreOperation 	scoreOperation;		// operation between the 2 layers
	private boolean					stopped = false;	// true if the operation must be stopped


	/**
	 * Adds a specified constant to the scores of each window of a {@link SimpleScoredChromosomeWindow}
	 * @param list1 1st input list
	 * @param list2 2nd input list
	 * @param scoreOperation {@link ScoreOperation}
	 */
	public SCWLOTwoLayers(SCWList list1, SCWList list2, ScoreOperation scoreOperation) {
		this.list1 = list1;
		this.list2 = list2;
		this.scoreOperation = scoreOperation;
	}


	@Override
	public SCWList compute() throws CloneNotSupportedException, InterruptedException, ExecutionException  {
		ProjectChromosomes projectChromosomes = ProjectManager.getInstance().getProjectChromosomes();
		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<Void>> threadList = new ArrayList<Callable<Void>>();
		ScoreOperation pileupOperation;
		if (scoreOperation == ScoreOperation.SUBTRACTION) {
			// pileup object doesn't work with noncommutative operations so we need to transform (A - B) into A + (-B)
			pileupOperation = ScoreOperation.ADDITION;
		} else if (scoreOperation == ScoreOperation.DIVISION) {
			// pileup object doesn't work with noncommutative operations so we need to transform (A / B) into A * (1 / B)
			pileupOperation = ScoreOperation.MULTIPLICATION;
		} else {
			pileupOperation = scoreOperation;
		}
		SimpleSCWPileupFlattener flattenerPrototype = new SimpleSCWPileupFlattener(pileupOperation, list1.getSCWListType());
		final GenomeWideFlattener gwFlattener = new GenomeWideFlattener(flattenerPrototype);

		for(final Chromosome currentChromosome : projectChromosomes) {
			Callable<Void> currentThread = new Callable<Void>() {

				@Override
				public Void call() throws Exception {
					List<ListView<ScoredChromosomeWindow>> listOfLV = new ArrayList<ListView<ScoredChromosomeWindow>>();
					listOfLV.add(list1.get(currentChromosome));
					listOfLV.add(list2.get(currentChromosome));
					ListOfListViewsIterator<ScoredChromosomeWindow> listOfLVIterator = new ListOfListViewsIterator<ScoredChromosomeWindow>(listOfLV);
					while (listOfLVIterator.hasNext() && !stopped) {
						ScoredChromosomeWindow currentWindow = listOfLVIterator.next();
						int start = currentWindow.getStart();
						int stop = currentWindow.getStop();
						float score = currentWindow.getScore();
						if ((scoreOperation == ScoreOperation.SUBTRACTION) && (listOfLVIterator.getLastNextListIndex() == 1)) {
							// A - B = A + (-B)
							score *= -1;
						} else if ((scoreOperation == ScoreOperation.DIVISION) && (listOfLVIterator.getLastNextListIndex() == 1)) {
							// A / B = A * (1 / B)
							if (score != 0) {
								score = 1 / score;
							}
						}
						gwFlattener.addWindow(currentChromosome, start, stop, score);
					}
					op.notifyDone();
					return null;
				}
			};
			threadList.add(currentThread);
		}
		op.startPool(threadList);
		return new SimpleSCWList(gwFlattener.getListOfListViews());
	}


	@Override
	public String getDescription() {
		return "Operation on two layers: " + scoreOperation;
	}


	@Override
	public String getProcessingDescription() {
		return "Computing " + scoreOperation;
	}


	@Override
	public int getStepCount() {
		return 1 + list1.getCreationStepCount();
	}


	@Override
	public void stop() {
		stopped = true;
	}
}
