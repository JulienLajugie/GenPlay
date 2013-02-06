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
package edu.yu.einstein.genplay.core.list.chromosomeWindowList;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import edu.yu.einstein.genplay.core.chromosome.Chromosome;
import edu.yu.einstein.genplay.core.chromosomeWindow.SimpleChromosomeWindow;
import edu.yu.einstein.genplay.core.list.ChromosomeListOfLists;
import edu.yu.einstein.genplay.core.list.DisplayableListOfLists;
import edu.yu.einstein.genplay.core.manager.project.ProjectChromosome;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.operationPool.OperationPool;
import edu.yu.einstein.genplay.exception.ExceptionManager;
import edu.yu.einstein.genplay.exception.exceptions.InvalidChromosomeException;


/**
 * A list of {@link SimpleChromosomeWindow} with tool to rescale it
 * @author Julien Lajugie
 * @version 0.1
 */
public class ChromosomeWindowList extends DisplayableListOfLists<SimpleChromosomeWindow, List<SimpleChromosomeWindow>> implements Serializable {

	private static final long serialVersionUID = -2180037918131928807L; // generated ID
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 0;			// saved format version


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
	}


	/**
	 * Method used for unserialization
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.readInt();
	}


	/**
	 * Creates an instance of {@link ChromosomeWindowList}
	 * @param startList list of start positions
	 * @param stopList list of stop positions
	 * @throws InvalidChromosomeException
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	public ChromosomeWindowList(final ChromosomeListOfLists<Integer> startList,
			final ChromosomeListOfLists<Integer> stopList) throws InvalidChromosomeException, InterruptedException, ExecutionException {
		super();
		// retrieve the instance of the OperationPool
		final OperationPool op = OperationPool.getInstance();
		// list for the threads
		final Collection<Callable<List<SimpleChromosomeWindow>>> threadList = new ArrayList<Callable<List<SimpleChromosomeWindow>>>();
		ProjectChromosome projectChromosome = ProjectManager.getInstance().getProjectChromosome();
		for(final Chromosome currentChromosome : projectChromosome) {
			Callable<List<SimpleChromosomeWindow>> currentThread = new Callable<List<SimpleChromosomeWindow>>() {
				@Override
				public List<SimpleChromosomeWindow> call() throws Exception {
					if (startList.get(currentChromosome) == null) {
						return null;
					}
					List<SimpleChromosomeWindow> resultList = new ArrayList<SimpleChromosomeWindow>();
					for(int j = 0; j < startList.size(currentChromosome); j++) {
						int start = startList.get(currentChromosome).get(j);
						int stop = stopList.get(currentChromosome).get(j);
						resultList.add(new SimpleChromosomeWindow(start, stop));
					}
					// tell the operation pool that a chromosome is done
					op.notifyDone();
					return resultList;
				}
			};

			threadList.add(currentThread);
		}
		List<List<SimpleChromosomeWindow>> result = null;
		// starts the pool
		result = op.startPool(threadList);
		// add the chromosome results
		if (result != null) {
			for (List<SimpleChromosomeWindow> currentList: result) {
				add(currentList);
			}
		}
		// sort the list
		for (List<SimpleChromosomeWindow> currentChrWindowList : this) {
			Collections.sort(currentChrWindowList);
		}
	}


	/**
	 * Merges two windows together if the gap between this two windows is not visible
	 */
	@Override
	protected void fitToScreen() {
		List<SimpleChromosomeWindow> currentChromosomeList;
		try {
			currentChromosomeList = get(fittedChromosome);
		} catch (InvalidChromosomeException e) {
			ExceptionManager.getInstance().handleException(e);
			fittedDataList = null;
			return;
		}

		if (fittedXRatio > 1) {
			fittedDataList = currentChromosomeList;
		} else {
			fittedDataList = new ArrayList<SimpleChromosomeWindow>();
			if (currentChromosomeList.size() > 1) {
				fittedDataList.add(new SimpleChromosomeWindow(currentChromosomeList.get(0)));
				int i = 1;
				int j = 0;
				while (i < currentChromosomeList.size()) {
					double distance = (currentChromosomeList.get(i).getStart() - fittedDataList.get(j).getStop()) * fittedXRatio;
					// we merge two intervals together if there is a gap smaller than 1 pixel
					while ((distance < 1) && ((i + 1) < currentChromosomeList.size())) {
						// the new stop position is the max of the current stop and the stop of the new merged interval
						int newStop = Math.max(fittedDataList.get(j).getStop(), currentChromosomeList.get(i).getStop());
						fittedDataList.get(j).setStop(newStop);
						i++;
						distance = (currentChromosomeList.get(i).getStart() - fittedDataList.get(j).getStop()) * fittedXRatio;
					}
					fittedDataList.add(new SimpleChromosomeWindow(currentChromosomeList.get(i)));
					i++;
					j++;
				}
			}
		}
	}


	@Override
	protected List<SimpleChromosomeWindow> getFittedData(int start, int stop) {
		if ((fittedDataList == null) || (fittedDataList.size() == 0)) {
			return null;
		}

		ArrayList<SimpleChromosomeWindow> resultList = new ArrayList<SimpleChromosomeWindow>();

		int indexStart = findStart(fittedDataList, start, 0, fittedDataList.size() - 1);
		int indexStop = findStop(fittedDataList, stop, 0, fittedDataList.size() - 1);
		if (indexStart > 0) {
			if (fittedDataList.get(indexStart - 1).getStop() >= start) {
				SimpleChromosomeWindow currentWindow = fittedDataList.get(indexStart - 1);
				SimpleChromosomeWindow newLastWindow = new SimpleChromosomeWindow(start, currentWindow.getStop());
				resultList.add(newLastWindow);
			}
		}
		for (int i = indexStart; i <= indexStop; i++) {
			resultList.add(fittedDataList.get(i));
		}
		if ((indexStop + 1) < fittedDataList.size()) {
			if (fittedDataList.get(indexStop + 1).getStart() <= stop) {
				SimpleChromosomeWindow currentWindow = fittedDataList.get(indexStop + 1);
				SimpleChromosomeWindow newLastWindow = new SimpleChromosomeWindow(currentWindow.getStart(), stop);
				resultList.add(newLastWindow);
			}
		}
		return resultList;
	}


	/**
	 * Recursive function. Returns the index where the start value of the window is found
	 * or the index right after if the exact value is not find.
	 * @param list
	 * @param value
	 * @param indexStart
	 * @param indexStop
	 * @return the index where the start value of the window is found or the index right after if the exact value is not find
	 */
	private int findStart(List<SimpleChromosomeWindow> list, int value, int indexStart, int indexStop) {
		int middle = (indexStop - indexStart) / 2;
		if (indexStart == indexStop) {
			return indexStart;
		} else if (value == list.get(indexStart + middle).getStart()) {
			return indexStart + middle;
		} else if (value > list.get(indexStart + middle).getStart()) {
			return findStart(list, value, indexStart + middle + 1, indexStop);
		} else {
			return findStart(list, value, indexStart, indexStart + middle);
		}
	}


	/**
	 * Recursive function. Returns the index where the stop value of the window is found
	 * or the index right before if the exact value is not find.
	 * @param list
	 * @param value
	 * @param indexStart
	 * @param indexStop
	 * @return the index where the stop value of the window is found or the index right before if the exact value is not find
	 */
	private int findStop(List<SimpleChromosomeWindow> list, int value, int indexStart, int indexStop) {
		int middle = (indexStop - indexStart) / 2;
		if (indexStart == indexStop) {
			return indexStart;
		} else if (value == list.get(indexStart + middle).getStop()) {
			return indexStart + middle;
		} else if (value > list.get(indexStart + middle).getStop()) {
			return findStop(list, value, indexStart + middle + 1, indexStop);
		} else {
			return findStop(list, value, indexStart, indexStart + middle);
		}
	}
}
