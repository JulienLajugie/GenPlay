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
package edu.yu.einstein.genplay.core.operation.binList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import edu.yu.einstein.genplay.core.manager.project.ProjectChromosomes;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.core.operationPool.OperationPool;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.binList.BinList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.binList.BinListBuilder;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;

/**
 * Computes the average of a BinList for a specified factor.
 * For example if the factor is 2 this operation will compute a new list of {@link ScoredChromosomeWindow}
 * with bins twice as big as the input list with a score value equal to the average of the scores of the bins
 * it spans.
 * @author Julien Lajugie
 */
public class BLOComputeAverageList implements Operation<List<ListView<ScoredChromosomeWindow>>> {

	private final BinList 	binList;			// input BinList
	private final int 		factor;				// average factor
	private boolean			stopped = false;	// true if the operation must be stopped

	/**
	 * Creates an instance of {@link BLOComputeAverageList}
	 * @param binList input binlist
	 * @param factor integer greater than 1. Number of bin from the input list to average
	 */
	public BLOComputeAverageList(BinList binList, int factor) {
		this.binList = binList;
		this.factor = factor;
	}


	@Override
	public List<ListView<ScoredChromosomeWindow>> compute() throws InterruptedException, ExecutionException, CloneNotSupportedException {
		// size of the bins of the average list
		final int averageListBinSize = binList.getBinSize() * factor;

		ProjectChromosomes projectChromosomes = ProjectManager.getInstance().getProjectChromosomes();
		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<Void>> threadList = new ArrayList<Callable<Void>>();
		final BinListBuilder resultListBuilder = new BinListBuilder(averageListBinSize);

		for (final Chromosome chromosome: projectChromosomes) {
			final ListView<ScoredChromosomeWindow> currentList = binList.get(chromosome);

			Callable<Void> currentThread = new Callable<Void>() {
				@Override
				public Void call() throws Exception {
					if (currentList != null) {
						// We divide each element by a constant

						for (int i = 0; (i < currentList.size()) && !stopped; i += factor) {
							float sum = 0;
							int count = 0;
							for (int j = 0; (j < factor) && ((i + j) < currentList.size()); j++) {
								if (currentList.get(i + j).getScore() != 0) {
									sum += currentList.get(i + j).getScore();
									count++;
								}
							}
							float score;
							if (count == 0) {
								score = 0;
							} else {
								score = sum / count;
							}
							resultListBuilder.addElementToBuild(chromosome, score);
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
		return resultListBuilder.getListOfListViews();
	}


	@Override
	public String getDescription() {
		return "Operation: Compute Average";
	}


	@Override
	public String getProcessingDescription() {
		return "Averaging List";
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
