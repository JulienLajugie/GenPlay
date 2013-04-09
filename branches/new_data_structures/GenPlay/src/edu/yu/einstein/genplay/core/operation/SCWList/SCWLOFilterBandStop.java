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

import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.core.operationPool.OperationPool;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SCWList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SimpleSCWList;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.SimpleScoredChromosomeWindow;



/**
 * Removes the values between two specified thresholds
 * @author Julien Lajugie
 * @version 0.1
 */
public class SCWLOFilterBandStop implements Operation<SCWList> {

	private final SCWList 	inputList;		// input SCW list
	private final double 						lowThreshold;	// low bound
	private final double 						highThreshold;	// high bound
	private boolean								stopped = false;// true if the operation must be stopped


	/**
	 * Creates an instance of {@link SCWLOFilterBandStop}
	 * @param inputList input {@link SCWList}
	 * @param lowThreshold low threshold
	 * @param highThreshold high threshold
	 */
	public SCWLOFilterBandStop(SCWList inputList, double lowThreshold, double highThreshold) {
		this.inputList = inputList;
		this.lowThreshold = lowThreshold;
		this.highThreshold = highThreshold;
	}


	@Override
	public SCWList compute() throws Exception {
		if (lowThreshold >= highThreshold) {
			throw new IllegalArgumentException("The high threshold must be greater than the low one");
		}

		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<List<ScoredChromosomeWindow>>> threadList = new ArrayList<Callable<List<ScoredChromosomeWindow>>>();
		for (final List<ScoredChromosomeWindow> currentList: inputList) {

			Callable<List<ScoredChromosomeWindow>> currentThread = new Callable<List<ScoredChromosomeWindow>>() {
				@Override
				public List<ScoredChromosomeWindow> call() throws Exception {
					List<ScoredChromosomeWindow> resultList = new ArrayList<ScoredChromosomeWindow>();
					if ((currentList != null) && (currentList.size() != 0)) {
						for (int j = 0; (j < currentList.size()) && !stopped; j++) {
							double currentValue = currentList.get(j).getScore();
							if ((currentValue < lowThreshold) || (currentValue > highThreshold)) {
								ScoredChromosomeWindow windowToAdd = new SimpleScoredChromosomeWindow(currentList.get(j));
								resultList.add(windowToAdd);
							}
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
			SCWList resultList = new SimpleSCWList(result);
			return resultList;
		} else {
			return null;
		}
	}


	@Override
	public String getDescription() {
		return "Operation: Band-Stop Filter, Low Threshold = " + lowThreshold + ", High Threshold = " + highThreshold;
	}


	@Override
	public String getProcessingDescription() {
		return "Filtering";
	}


	@Override
	public int getStepCount() {
		return 1 + SimpleSCWList.getCreationStepCount();
	}


	@Override
	public void stop() {
		this.stopped = true;
	}
}
