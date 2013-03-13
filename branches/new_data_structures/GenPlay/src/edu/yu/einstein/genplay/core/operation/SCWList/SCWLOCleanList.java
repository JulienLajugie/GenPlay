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
import edu.yu.einstein.genplay.dataStructure.list.SCWList.ScoredChromosomeWindowArrayList;
import edu.yu.einstein.genplay.dataStructure.list.SCWList.ScoredChromosomeWindowList;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;


/**
 * Cleans a {@link ScoredChromosomeWindow}.
 * Sometimes, generating a scored chromosome window involve complex controls before inserting windows.
 * These controls can also be difficult to re-code every time.
 * This method generate a new {@link ScoredChromosomeWindow} that does not contain:
 * - duplicates
 * - windows with length of 0bp
 * 
 * It also merges overlapped windows.
 * 
 * @author Nicolas Fourel
 * @version 0.1
 */
public class SCWLOCleanList implements Operation<ScoredChromosomeWindowList> {

	private final ScoredChromosomeWindowList 	scwList;		// input list
	private boolean								stopped = false;// true if the operation must be stopped


	/**
	 * Cleans a {@link ScoredChromosomeWindow}.
	 * @param scwList input list
	 */
	public SCWLOCleanList(ScoredChromosomeWindowList scwList) {
		this.scwList = scwList;
	}


	@Override
	public ScoredChromosomeWindowList compute() throws Exception {
		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<List<ScoredChromosomeWindow>>> threadList = new ArrayList<Callable<List<ScoredChromosomeWindow>>>();

		for (short i = 0; i < scwList.size(); i++) {
			final List<ScoredChromosomeWindow> currentList = scwList.getView(i);

			Callable<List<ScoredChromosomeWindow>> currentThread = new Callable<List<ScoredChromosomeWindow>>() {
				@Override
				public List<ScoredChromosomeWindow> call() throws Exception {
					List<ScoredChromosomeWindow> resultList = new ArrayList<ScoredChromosomeWindow>();
					if ((currentList != null) && (currentList.size() != 0)) {
						int firstIndex = 0;
						while (isIndexValid(currentList, firstIndex) && !stopped) {
							ScoredChromosomeWindow currentWindow = currentList.get(firstIndex);
							int lastIndex = getLastInvolvedIndex(currentList, firstIndex);
							ScoredChromosomeWindow resultWindow = currentWindow.getClass().newInstance();
							if (firstIndex == lastIndex) {
								resultWindow.setStart(currentWindow.getStart());
								resultWindow.setStop(currentWindow.getStop());
								resultWindow.setScore(currentWindow.getScore());
							} else {
								resultWindow = getMergedWindow(currentList, firstIndex, lastIndex);
							}
							firstIndex = lastIndex + 1;
							if (resultWindow.getSize() > 0) {
								resultList.add(resultWindow);
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
			ScoredChromosomeWindowList resultList = scwList.getClass().newInstance();
			resultList.addAll(result);
			return resultList;
		} else {
			return null;
		}
	}


	/**
	 * @param list the {@link ScoredChromosomeWindowList} for the current chromosome
	 * @param indexStart index of the first window of the cluster to merge
	 * @param indexStop index of the last window of the cluster to merge
	 * @return a single window that is the merging of the specified ones
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	private ScoredChromosomeWindow getMergedWindow(List<ScoredChromosomeWindow> list, int indexStart, int indexStop) throws InstantiationException, IllegalAccessException {
		int start = list.get(indexStart).getStart();
		int stop = list.get(indexStart).getStop();
		int score = 0;
		// compute stop position
		for (int i = indexStart + 1; i <= indexStop; i++) {
			stop = Math.max(stop, list.get(i).getStop());
		}
		// compute score
		for (int i = indexStart; i <= indexStop; i++) {
			score += list.get(i).getScore();
		}
		ScoredChromosomeWindow resultWindow = list.get(indexStart).getClass().newInstance();
		resultWindow.setStart(start);
		resultWindow.setStop(stop);
		resultWindow.setScore(score);
		return resultWindow;
	}


	/**
	 * Goes to the next index involved in an overlap
	 * @param list		the list of {@link ScoredChromosomeWindow}
	 * @param index		the index to start the search
	 * @return			the last index involved in the potential current overlap
	 */
	private int getLastInvolvedIndex (List<ScoredChromosomeWindow> list, int index) {
		int lastIndex = index + 1;
		int currentStopPosition = list.get(index).getStop();
		while (((lastIndex < (list.size())) && (currentStopPosition >= list.get(lastIndex).getStart()))) {
			currentStopPosition = Math.max(currentStopPosition, list.get(lastIndex).getStop());
			lastIndex++;
		}
		return lastIndex - 1;
	}


	/**
	 * Check if an index is valid
	 * @param list	the list of {@link ScoredChromosomeWindow}
	 * @param index	the index to look for
	 * @return	true if the index is valid, false otherwise
	 */
	private boolean isIndexValid (List<ScoredChromosomeWindow> list, int index) {
		return index < list.size();
	}


	@Override
	public String getDescription() {
		return "Operation: Clean list";
	}


	@Override
	public String getProcessingDescription() {
		return "Cleaning list";
	}


	@Override
	public int getStepCount() {
		return ScoredChromosomeWindowArrayList.getCreationStepCount(scwList.getSCWListType());
	}


	@Override
	public void stop() {
		stopped = true;
	}
}
