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
 * Cleans a {@link SimpleScoredChromosomeWindow}.
 * Sometimes, generating a scored chromosome window involve complex controls before inserting windows.
 * These controls can also be difficult to re-code every time.
 * This method generate a new {@link SimpleScoredChromosomeWindow} that does not contain:
 * - duplicates
 * - windows with length of 0bp
 * 
 * It also merges overlapped windows.
 * 
 * @author Nicolas Fourel
 * @version 0.1
 */
public class SCWLOCleanList implements Operation<ScoredChromosomeWindowList> {

	private final ScoredChromosomeWindowList 	scwList;	// input list
	private boolean				stopped = false;// true if the operation must be stopped


	/**
	 * Cleans a {@link SimpleScoredChromosomeWindow}.
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
			final List<ScoredChromosomeWindow> currentList = scwList.get(i);

			Callable<List<ScoredChromosomeWindow>> currentThread = new Callable<List<ScoredChromosomeWindow>>() {
				@Override
				public List<ScoredChromosomeWindow> call() throws Exception {
					List<ScoredChromosomeWindow> resultList = new ArrayList<ScoredChromosomeWindow>();
					if ((currentList != null) && (currentList.size() != 0)) {
						int index = 0;
						while (isIndexValid(currentList, index) && !stopped) {
							ScoredChromosomeWindow currentWindow = currentList.get(index);
							int nextIndex = getNextInvolvedIndex(currentList, index);
							ScoredChromosomeWindow resultWindow = null;
							if (index == nextIndex) {
								resultWindow = new SimpleScoredChromosomeWindow(currentWindow.getStart(), currentWindow.getStop(), currentWindow.getScore());
							} else {
								resultWindow = getMergedWindow(currentList, index, nextIndex);
							}
							if (resultWindow.getSize() > 0) {
								resultList.add(resultWindow);
							}
							index = nextIndex + 1;
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


	/**
	 * Goes to the next index involved in an overlap
	 * @param list		the list of {@link ScoredChromosomeWindow}
	 * @param index		the index to start the search
	 * @return			the last index involved in th potential current overlap
	 */
	private int getNextInvolvedIndex (List<ScoredChromosomeWindow> list, int index) {
		int nextIndex = index + 1;
		if (isIndexValid(list, nextIndex)) {
			if (overlap(list.get(index), list.get(nextIndex))) {
				return getNextInvolvedIndex(list, nextIndex);
			}
		}
		return index;
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


	/**
	 * Looks if two windows are overlapping
	 * @param window01	the first window
	 * @param window02	the second window
	 * @return	true if the second window overlaps the first window
	 */
	private boolean overlap (ScoredChromosomeWindow window01, ScoredChromosomeWindow window02) {
		boolean overlap = false;
		if (window01.getStop() > window02.getStart()) {
			overlap = true;
		}
		return overlap;
	}


	/**
	 * Merges windows of a list from an index to anoteher one
	 * @param list			the list of {@link ScoredChromosomeWindow}
	 * @param firstIndex	the first index to include
	 * @param lastIndex		the last index to include
	 * @return				the merged {@link ScoredChromosomeWindow}
	 */
	private ScoredChromosomeWindow getMergedWindow (List<ScoredChromosomeWindow> list, int firstIndex, int lastIndex) {
		int start = list.get(firstIndex).getStart();
		int stop = list.get(firstIndex).getStop();
		double sumScore = 0;
		for (int i = firstIndex + 1; i <= lastIndex; i++) {
			ScoredChromosomeWindow currentWindow = list.get(i);
			int currentStart =  currentWindow.getStart();
			int currentStop = currentWindow.getStop();
			if (currentStart < start) {
				start = currentStart;
			}
			if (currentStop > stop) {
				currentStop = stop;
			}
			sumScore += currentWindow.getScore();
		}
		double score = sumScore;
		return new SimpleScoredChromosomeWindow(start, stop, score);
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
		return 1 + SimpleScoredChromosomeWindowList.getCreationStepCount();
	}


	@Override
	public void stop() {
		this.stopped = true;
	}
}
