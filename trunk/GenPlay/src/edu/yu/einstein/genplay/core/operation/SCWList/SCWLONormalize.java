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
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import edu.yu.einstein.genplay.core.manager.project.ProjectChromosomes;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.core.operationPool.OperationPool;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SCWList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SCWListBuilder;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;


/**
 * Normalizes a {@link SCWList} and multiplies the result by a factor
 * @author Julien Lajugie
 */
public class SCWLONormalize implements Operation<SCWList> {

	private final SCWList 	inputList;			// input ScoredChromosomeWindowList
	private final double	factor;				// the result of the normalization is multiplied by this factor
	private Double 			scoreSum;			// sum of the scores
	private boolean			stopped = false;	// true if the operation must be stopped


	/**
	 * Normalizes a {@link SCWList} and multiplies the result by a specified factor
	 * @param inputList ScoredChromosomeWindowList to normalize
	 * @param factor factor
	 */
	public SCWLONormalize(SCWList inputList, double factor) {
		this.inputList = inputList;
		this.factor = factor;
	}


	@Override
	public SCWList compute() throws InterruptedException, ExecutionException, CloneNotSupportedException {
		scoreSum = inputList.getStatistics().getScoreSum();
		// we want to multiply each window by the following coefficient
		final double coef = factor / scoreSum;

		ProjectChromosomes projectChromosomes = ProjectManager.getInstance().getProjectChromosomes();
		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<Void>> threadList = new ArrayList<Callable<Void>>();
		final SCWListBuilder resultListBuilder = new SCWListBuilder(inputList);

		for (final Chromosome chromosome: projectChromosomes) {
			final ListView<ScoredChromosomeWindow> currentList = inputList.get(chromosome);
			Callable<Void> currentThread = new Callable<Void>() {

				@Override
				public Void call() throws Exception {
					if (currentList != null) {
						for (int j = 0; (j < currentList.size()) && !stopped; j++) {
							// we multiply each window by the coefficient previously computed
							int start = currentList.get(j).getStart();
							int stop = currentList.get(j).getStop();
							float score = (float) (currentList.get(j).getScore() * coef);
							resultListBuilder.addElementToBuild(chromosome, start, stop, score);
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


	@Override
	public String getDescription() {
		return "Operation: Normalize, Factor = " + factor;
	}


	@Override
	public String getProcessingDescription() {
		return "Normalizing";
	}


	@Override
	public int getStepCount() {
		return 1 + inputList.getCreationStepCount();
	}


	@Override
	public void stop() {
		stopped = true;
	}
}
