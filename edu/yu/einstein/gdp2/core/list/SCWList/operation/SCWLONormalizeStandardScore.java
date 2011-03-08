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
 *     Author: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *     Website: <http://genplay.einstein.yu.edu>
 *******************************************************************************/
package yu.einstein.gdp2.core.list.SCWList.operation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

import yu.einstein.gdp2.core.ScoredChromosomeWindow;
import yu.einstein.gdp2.core.list.SCWList.ScoredChromosomeWindowList;
import yu.einstein.gdp2.core.operation.Operation;
import yu.einstein.gdp2.core.operationPool.OperationPool;


/**
 * Computes a Standard Score normalization on a {@link ScoredChromosomeWindowList}
 * @author Julien Lajugie
 * @version 0.1
 */
public class SCWLONormalizeStandardScore implements Operation<ScoredChromosomeWindowList> {

	private final ScoredChromosomeWindowList 	scwList;		// input list 
	private final SCWLOAverage 					avgOp;			// average
	private final SCWLOStandardDeviation 		stdevOp;		// standard deviation
	private boolean								stopped = false;// true if the operation must be stopped


	/**
	 * Creates an instance of {@link SCWLONormalizeStandardScore}
	 * @param scwList input list
	 */
	public SCWLONormalizeStandardScore(ScoredChromosomeWindowList scwList) {
		this.scwList = scwList;
		avgOp = new SCWLOAverage(scwList, null);
		stdevOp = new SCWLOStandardDeviation(scwList, null);
	}

	@Override
	public ScoredChromosomeWindowList compute() throws Exception {
		// compute average
		final double avg = avgOp.compute();
		// compute standard deviation
		final double stdev = stdevOp.compute();
		// retrieve singleton operation pool
		final OperationPool op = OperationPool.getInstance();
		// creates collection of thread for the operation pool
		final Collection<Callable<List<ScoredChromosomeWindow>>> threadList = new ArrayList<Callable<List<ScoredChromosomeWindow>>>();
		for (short i = 0; i < scwList.size(); i++) {
			final List<ScoredChromosomeWindow> currentList = scwList.get(i);

			Callable<List<ScoredChromosomeWindow>> currentThread = new Callable<List<ScoredChromosomeWindow>>() {	
				@Override
				public List<ScoredChromosomeWindow> call() throws Exception {
					List<ScoredChromosomeWindow> resultList = null;
					if ((currentList != null) && (currentList.size() != 0)) {
						resultList = new ArrayList<ScoredChromosomeWindow>();
						for (int j = 0; j < currentList.size() && !stopped; j++) {
							ScoredChromosomeWindow currentWindow = currentList.get(j);
							ScoredChromosomeWindow resultWindow = new ScoredChromosomeWindow(currentWindow);
							if (currentWindow.getScore() != 0) {
								// apply the standard score formula: (x - avg) / stdev 
								double resultScore = (currentWindow.getScore() - avg) / stdev; 
								resultWindow.setScore(resultScore);
							}
							resultList.add(resultWindow);
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
			ScoredChromosomeWindowList resultList = new ScoredChromosomeWindowList(result);
			return resultList;
		} else {
			return null;
		}
	}


	@Override
	public String getDescription() {
		return "Operation: Normalize, Standard Score";
	}


	@Override
	public String getProcessingDescription() {
		return "Normalizing";
	}


	@Override
	public int getStepCount() {
		return 1 + avgOp.getStepCount() + stdevOp.getStepCount() + ScoredChromosomeWindowList.getCreationStepCount();
	}


	@Override
	public void stop() {
		this.stopped = true;
	}
}
