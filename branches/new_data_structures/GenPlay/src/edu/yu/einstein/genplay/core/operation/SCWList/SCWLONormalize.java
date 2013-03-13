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
package edu.yu.einstein.genplay.core.operation.SCWList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.core.operationPool.OperationPool;
import edu.yu.einstein.genplay.dataStructure.genomeList.SCWList.ScoredChromosomeWindowList;
import edu.yu.einstein.genplay.dataStructure.genomeList.SCWList.SimpleSCWList;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.SimpleScoredChromosomeWindow;



/**
 * Normalizes a {@link ScoredChromosomeWindowList} and multiplies the result by a factor
 * @author Julien Lajugie
 * @version 0.1
 */
public class SCWLONormalize implements Operation<ScoredChromosomeWindowList> {

	private final ScoredChromosomeWindowList 	inputList;		// input ScoredChromosomeWindowList
	private final double						factor;			// the result of the normalization is multiplied by this factor
	private Double 								scoreSum;		// sum of the scores
	private boolean								stopped = false;// true if the operation must be stopped


	/**
	 * Normalizes a {@link ScoredChromosomeWindowList} and multiplies the result by a specified factor
	 * @param inputList ScoredChromosomeWindowList to normalize
	 * @param factor factor
	 */
	public SCWLONormalize(ScoredChromosomeWindowList inputList, double factor) {
		this.inputList = inputList;
		this.factor = factor;
	}


	@Override
	public ScoredChromosomeWindowList compute() throws InterruptedException, ExecutionException {
		scoreSum = new SCWLOSumScore(inputList, null).compute();
		// we want to multiply each window by the following coefficient
		final double coef = factor / scoreSum;

		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<List<ScoredChromosomeWindow>>> threadList = new ArrayList<Callable<List<ScoredChromosomeWindow>>>();
		for (short i = 0; i < inputList.size(); i++)  {
			final List<ScoredChromosomeWindow> currentList = inputList.getView(i);

			Callable<List<ScoredChromosomeWindow>> currentThread = new Callable<List<ScoredChromosomeWindow>>() {
				@Override
				public List<ScoredChromosomeWindow> call() throws Exception {
					List<ScoredChromosomeWindow> resultList = new ArrayList<ScoredChromosomeWindow>();
					if ((currentList != null) && (currentList.size() != 0)) {
						for (int j = 0; (j < currentList.size()) && !stopped; j++) {
							// we multiply each window by the coefficient previously computed
							ScoredChromosomeWindow windowToAdd = new SimpleScoredChromosomeWindow(currentList.get(j));
							windowToAdd.setScore(currentList.get(j).getScore() * coef);
							resultList.add(windowToAdd);
						}
					}
					// tell the operation pool that a chromosome is done
					op.notifyDone();
					return resultList;
				}
			};

			threadList.add(currentThread);
		}
		List<List<ScoredChromosomeWindow>> result = op.startPool(threadList);
		if (result != null) {
			ScoredChromosomeWindowList resultList = new SimpleSCWList(result);
			return resultList;
		} else {
			return null;
		}
	}


	@Override
	public String getDescription() {
		return "Operation: Normalize, Factor = " + factor;
	}


	@Override
	public int getStepCount() {
		return SimpleSCWList.getCreationStepCount() + 1;
	}


	@Override
	public String getProcessingDescription() {
		return "Normalizing";
	}


	@Override
	public void stop() {
		stopped = true;
	}
}
