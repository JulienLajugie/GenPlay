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
package edu.yu.einstein.genplay.core.list.SCWList.operation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

import edu.yu.einstein.genplay.core.chromosomeWindow.ScoredChromosomeWindow;
import edu.yu.einstein.genplay.core.chromosomeWindow.SimpleScoredChromosomeWindow;
import edu.yu.einstein.genplay.core.enums.ScoreCalculationMethod;
import edu.yu.einstein.genplay.core.list.SCWList.ScoredChromosomeWindowList;
import edu.yu.einstein.genplay.core.list.SCWList.SimpleScoredChromosomeWindowList;
import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.core.operationPool.OperationPool;
import edu.yu.einstein.genplay.util.SCWLists;


/**
 * Defines regions as "islands" of non zero value ScoredChromosomeWindows
 * separated by more than a specified number of zero value ScoredChromosomeWindows.
 * Computes the average/sum/max on these regions.
 * Returns a new {@link ScoredChromosomeWindowList} with the defined regions having their average/max/sum as a score
 * @author Chirag Gorasia
 * @version 0.1
 */
public class SCWLOTransfrag implements Operation<ScoredChromosomeWindowList> {

	private final ScoredChromosomeWindowList 	scwList;		// input list
	private final int 						zeroSCWGap;		// minimum size of the gap separating two intervals
	private final ScoreCalculationMethod 		operation;		// operation to use to compute the score of the intervals
	private boolean						stopped = false;// true if the operation must be stopped


	/**
	 * Creates an instance of {@link SCWLOTransfrag}
	 * @param scwList input list
	 * @param zeroSCWGap minimum size of the gap separating two intervals
	 * @param operation operation to use to compute the score of the intervals
	 */
	public SCWLOTransfrag(ScoredChromosomeWindowList scwList, int zeroSCWGap, ScoreCalculationMethod operation) {
		this.scwList = scwList;
		this.zeroSCWGap = zeroSCWGap;
		this.operation = operation;
	}


	@Override
	public ScoredChromosomeWindowList compute() throws Exception {

		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<List<ScoredChromosomeWindow>>> threadList = new ArrayList<Callable<List<ScoredChromosomeWindow>>>();

		for (short i = 0; i < scwList.size(); i++) {
			final List<ScoredChromosomeWindow> currentList = scwList.get(i);

			Callable<List<ScoredChromosomeWindow>> currentThread = new Callable<List<ScoredChromosomeWindow>>() {
				@Override
				public List<ScoredChromosomeWindow> call() throws Exception {
					List<ScoredChromosomeWindow> resultList = new ArrayList<ScoredChromosomeWindow>();
					if ((currentList != null) && (currentList.size() != 0)) {
						int j = 0;
						while ((j < currentList.size()) && !stopped) {
							// skip zero values
							while ((j < currentList.size()) && (currentList.get(j) == null) && !stopped) {
								j++;
							}
							int regionStartIndex = j;
							int regionStopIndex = regionStartIndex;
							// a region stops when there is maxZeroWindowGap consecutive zero bins
							while (((j + 1) < currentList.size()) && ((currentList.get(j + 1).getStart() - currentList.get(j).getStop()) <= zeroSCWGap) && !stopped) {
								regionStopIndex = j+1;
								j++;
							}
							if (regionStopIndex >= currentList.size()) {
								regionStopIndex = currentList.size()-1;
							}
							if (regionStopIndex >= regionStartIndex) {
								double regionScore = 0;
								if (operation == ScoreCalculationMethod.AVERAGE) {
									// all the windows of the region are set with the average value on the region
									regionScore = SCWLists.average(currentList, regionStartIndex, regionStopIndex);
								} else if (operation == ScoreCalculationMethod.MAXIMUM) {
									// all the windows of the region are set with the max value on the region
									regionScore = SCWLists.maxNoZero(currentList, regionStartIndex, regionStopIndex);
								} else {
									// all the windows of the region are set with the sum value on the region
									regionScore = SCWLists.sum(currentList, regionStartIndex, regionStopIndex);
								}
								resultList.add(new SimpleScoredChromosomeWindow(currentList.get(regionStartIndex).getStart(), currentList.get(regionStopIndex).getStop(), regionScore));
							}
							j++;
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
			ScoredChromosomeWindowList resultList = new SimpleScoredChromosomeWindowList(result);
			return resultList;
		} else {
			return null;
		}
	}


	@Override
	public String getDescription() {
		return "Operation: Transfrag, Gap Size = " + zeroSCWGap + " Zero Value Successive ScoredChromosomeWindows";
	}


	@Override
	public String getProcessingDescription() {
		return "Calculating Transfrag";
	}


	@Override
	public int getStepCount() {
		return 3;
	}


	@Override
	public void stop() {
		this.stopped = true;
	}
}
