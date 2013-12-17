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
import edu.yu.einstein.genplay.core.list.SCWList.ScoredChromosomeWindowList;
import edu.yu.einstein.genplay.core.list.SCWList.SimpleScoredChromosomeWindowList;
import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.core.operationPool.OperationPool;



/**
 * Filters {@link ScoredChromosomeWindow} according to the length they are separated.
 * @author Julien Lajugie
 * @author Nicolas Fourel
 * @version 0.1
 */
public class SCWLOFilterWidth implements Operation<ScoredChromosomeWindowList> {

	private final ScoredChromosomeWindowList 	scwList;	// input list
	private final int 							width;		// minimum width
	private boolean				stopped = false;// true if the operation must be stopped


	/**
	 * Adds a specified constant to the scores of each window of a {@link SimpleScoredChromosomeWindow}
	 * @param scwList input list
	 * @param width constant to add
	 */
	public SCWLOFilterWidth(ScoredChromosomeWindowList scwList, int width) {
		this.scwList = scwList;
		this.width = width;
	}


	@Override
	public ScoredChromosomeWindowList compute() throws Exception {
		if (width < 1) {
			return scwList.deepClone();
		}

		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<List<ScoredChromosomeWindow>>> threadList = new ArrayList<Callable<List<ScoredChromosomeWindow>>>();

		for (short i = 0; i < scwList.size(); i++) {
			final List<ScoredChromosomeWindow> currentList = scwList.get(i);

			Callable<List<ScoredChromosomeWindow>> currentThread = new Callable<List<ScoredChromosomeWindow>>() {
				@Override
				public List<ScoredChromosomeWindow> call() throws Exception {
					List<ScoredChromosomeWindow> resultList = new ArrayList<ScoredChromosomeWindow>();
					if ((currentList != null) && (currentList.size() != 0)) {
						ScoredChromosomeWindow currentWindow = currentList.get(0);
						ScoredChromosomeWindow previousWindow = currentWindow;
						List<ScoredChromosomeWindow> tmpList = new ArrayList<ScoredChromosomeWindow>();
						tmpList.add(currentWindow);
						for (int j = 1; (j < currentList.size()) && !stopped; j++) {
							currentWindow = currentList.get(j);
							int diff = currentWindow.getStart() - previousWindow.getStop();
							if (diff > width) {
								resultList = insertWindowsFromListToList(resultList, tmpList);
								tmpList = new ArrayList<ScoredChromosomeWindow>();
							}
							tmpList.add(currentWindow);
							previousWindow = currentWindow;
						}
						resultList = insertWindowsFromListToList(resultList, tmpList);
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


	/**
	 * Gather the windows of a list to one window and insert it to a main list.
	 * @param mainList	the main list
	 * @param tmpList	the windows to group
	 * @return			the main list
	 */
	private List<ScoredChromosomeWindow> insertWindowsFromListToList (List<ScoredChromosomeWindow> mainList, List<ScoredChromosomeWindow> tmpList) {
		ScoredChromosomeWindow newWindow = null;
		if (tmpList.size() == 1) {
			newWindow = tmpList.get(0);
		} else {
			int start = tmpList.get(0).getStart();
			int stop = tmpList.get(tmpList.size() - 1).getStop();
			double score = getScore(tmpList);
			newWindow = new SimpleScoredChromosomeWindow(start, stop, score);
		}
		mainList.add(newWindow);
		return mainList;
	}


	private double getScore (List<ScoredChromosomeWindow> list) {
		return 1.0;
	}



	@Override
	public String getDescription() {
		return "Operation: Filter too close windows, width = " + width;
	}


	@Override
	public String getProcessingDescription() {
		return "Filtering";
	}


	@Override
	public int getStepCount() {
		return 1 + SimpleScoredChromosomeWindowList.getCreationStepCount();
	}


	@Override
	public void stop() {
		this.stopped = true;
	}
}
