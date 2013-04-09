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
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SCWList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SimpleSCWList;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;


/**
 * Merges overlapping windows of a {@link SCWList}. The score of the merged window will be the score
 * of the first window of the overlapping cluster of windows
 * @author Julien Lajugie
 */
public class SCWLOMergeWindows implements Operation<SCWList> {

	private final SCWList 	scwList;		// input list
	private boolean								stopped = false;// true if the operation must be stopped


	/**
	 * Creates an instance of {@link SCWLOMergeWindows}
	 * @param scwList {@link SCWList} with windows to merge
	 */
	public SCWLOMergeWindows(SCWList scwList) {
		this.scwList = scwList;
	}


	@Override
	public void stop() {
		stopped = true;

	}


	@Override
	public String getDescription() {
		return "Operation: Merge Windows";
	}


	@Override
	public String getProcessingDescription() {
		return "Merging Windows";
	}


	@Override
	public SCWList compute() throws InterruptedException, ExecutionException {
		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<List<ScoredChromosomeWindow>>> threadList = new ArrayList<Callable<List<ScoredChromosomeWindow>>>();

		for (short i = 0; i < scwList.size(); i++) {
			final List<ScoredChromosomeWindow> currentList = scwList.getView(i);

			Callable<List<ScoredChromosomeWindow>> currentThread = new Callable<List<ScoredChromosomeWindow>>() {
				@Override
				public List<ScoredChromosomeWindow> call() throws Exception {
					List<ScoredChromosomeWindow> resultList = new ArrayList<ScoredChromosomeWindow>();
					if ((currentList != null) && (currentList.size() != 0)) {
						ScoredChromosomeWindow windowToAdd = currentList.get(0).deepClone();
						for (int j = 1; (j < currentList.size()) && !stopped; j++) {
							ScoredChromosomeWindow currentWindow = currentList.get(j);
							if ((currentWindow.getStart() <= windowToAdd.getStop()) &&
									(currentWindow.getStop() > windowToAdd.getStop())) {
								windowToAdd.setStop(currentWindow.getStop());
							} else {
								resultList.add(windowToAdd);
								windowToAdd = currentWindow;
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
	public int getStepCount() {
		return 1;
	}
}
