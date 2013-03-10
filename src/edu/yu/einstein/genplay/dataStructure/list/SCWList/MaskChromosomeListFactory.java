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
package edu.yu.einstein.genplay.dataStructure.list.SCWList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import edu.yu.einstein.genplay.core.IO.reader.SCWReader;
import edu.yu.einstein.genplay.core.manager.project.ProjectChromosome;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.operation.SCWList.SCWLOMergeWindows;
import edu.yu.einstein.genplay.core.operationPool.OperationPool;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.enums.SCWListType;
import edu.yu.einstein.genplay.dataStructure.list.GenomicDataList;
import edu.yu.einstein.genplay.dataStructure.list.binList.BinList;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.MaskChromosomeWindow;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;
import edu.yu.einstein.genplay.exception.exceptions.InvalidChromosomeException;


/**
 * Factory class for vending mask {@link ScoredChromosomeWindowList} objects.
 * Mask SCWL are lists with a score or 0 or 1
 * @author Julien Lajugie
 */
public class MaskChromosomeListFactory {

	/**
	 * Creates a {@link ScoredChromosomeWindowList} from the data retrieved by the specified {@link SCWReader}
	 * @param scwReader a {@link SCWReader}
	 * @return a {@link ScoredChromosomeWindowList}
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	public static ScoredChromosomeWindowList createMaskSCWArrayList(SCWReader scwReader) throws InterruptedException, ExecutionException {
		ScoredChromosomeWindowList scwList = new ScoredChromosomeWindowArrayList(SCWListType.MASK);
		ScoredChromosomeWindow currentSCW = null;
		// read until eof
		while ((currentSCW = scwReader.readScoredChromosomeWindow()) != null) {
			// convert the window into mask window
			MaskChromosomeWindow currentMCW = new MaskChromosomeWindow(currentSCW);
			scwList.add(scwReader.getCurrentChromosome(), currentMCW);
		}
		scwList.sort();
		scwList = new SCWLOMergeWindows(scwList).compute();
		scwList.computeStatistics();
		return scwList;
	}


	/**
	 * Creates a mask chromosome window list from the input list.  If the input list elements are not {@link MaskChromosomeWindow}
	 * these elements are converted.
	 * @param data input list
	 * @return a {@link ScoredChromosomeWindowList} of {@link MaskChromosomeWindow}
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public static ScoredChromosomeWindowList createMaskSCWArrayList(List<? extends List<ScoredChromosomeWindow>> data) throws InterruptedException, ExecutionException {
		boolean isMaskInputList = true;
		for (int i = 0; (i < data.size()) && isMaskInputList; i++) {
			isMaskInputList = ((data.get(i) == null) || data.get(i).isEmpty() || (data.get(i).get(0) == null) || (data.get(i).get(0) instanceof MaskChromosomeWindow));
		}
		if (isMaskInputList) {
			ScoredChromosomeWindowList scwList = new ScoredChromosomeWindowArrayList(SCWListType.MASK);
			// if the data list elements are mask chromosome window we just add them 
			for (int i = 0; (i < data.size()) && (i < scwList.size()); i++) {
				scwList.set(i, data.get(i));
			}
			scwList.sort();
			scwList = new SCWLOMergeWindows(scwList).compute();
			scwList.computeStatistics();
			return scwList;
		} else {
			// if the data list elements are not mask chromosome window we need to convert them 
			return convertIntoMaskSCWArrayList(data);
		}
	}


	/**
	 * Converts a list of list of {@link ScoredChromosomeWindow} into a {@link ScoredChromosomeWindowList} of {@link MaskChromosomeWindow}
	 * @param data a list of list of {@link ScoredChromosomeWindow}
	 * @return a {@link ScoredChromosomeWindowList} of {@link MaskChromosomeWindow}
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	private static ScoredChromosomeWindowList convertIntoMaskSCWArrayList(List<? extends List<ScoredChromosomeWindow>> data) throws InterruptedException, ExecutionException {
		ScoredChromosomeWindowList scwList = new ScoredChromosomeWindowArrayList(SCWListType.MASK);
		// retrieve the instance of the OperationPool
		final OperationPool op = OperationPool.getInstance();
		// list for the threads
		final Collection<Callable<List<ScoredChromosomeWindow>>> threadList = new ArrayList<Callable<List<ScoredChromosomeWindow>>>();
		for (final List<ScoredChromosomeWindow> currentList: data) {
			Callable<List<ScoredChromosomeWindow>> currentThread = new Callable<List<ScoredChromosomeWindow>>() {
				@Override
				public List<ScoredChromosomeWindow> call() throws Exception {
					List<ScoredChromosomeWindow> resultList = new ArrayList<ScoredChromosomeWindow>();
					for (ScoredChromosomeWindow currentWindow: currentList) {
						resultList.add(new MaskChromosomeWindow(currentWindow));
					}
					// tell the operation pool that a chromosome is done
					op.notifyDone();
					return resultList;
				}
			};
			threadList.add(currentThread);
		}
		List<List<ScoredChromosomeWindow>> result = null;
		// starts the pool
		result = op.startPool(threadList);
		// add the chromosome results
		if (result != null) {
			for (List<ScoredChromosomeWindow> currentList: result) {
				scwList.add(currentList);
			}
		}
		scwList.sort();
		scwList = new SCWLOMergeWindows(scwList).compute();
		scwList.computeStatistics();
		return scwList;
	}


	/**
	 * Creates an instance of {@link MaskWindowList}
	 * @param startList list of start positions
	 * @param stopList list of stop position
	 * @return a {@link ScoredChromosomeWindowList} of {@link MaskChromosomeWindow}
	 * @throws InvalidChromosomeException
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	public static ScoredChromosomeWindowList convertIntoMaskSCWArrayList(final GenomicDataList<Integer> startList,
			final GenomicDataList<Integer> stopList) throws InvalidChromosomeException, InterruptedException, ExecutionException {
		// TODO remove this method when the new file loading system is on
		ScoredChromosomeWindowList scwList = new ScoredChromosomeWindowArrayList(SCWListType.MASK);
		// retrieve the project chromosome
		ProjectChromosome projectChromosome = ProjectManager.getInstance().getProjectChromosome();
		// retrieve the instance of the OperationPool
		final OperationPool op = OperationPool.getInstance();
		// list for the threads
		final Collection<Callable<List<ScoredChromosomeWindow>>> threadList = new ArrayList<Callable<List<ScoredChromosomeWindow>>>();

		for(final Chromosome currentChromosome : projectChromosome) {
			Callable<List<ScoredChromosomeWindow>> currentThread = new Callable<List<ScoredChromosomeWindow>>() {
				@Override
				public List<ScoredChromosomeWindow> call() throws Exception {
					if (startList.get(currentChromosome) == null) {
						return null;
					}
					List<ScoredChromosomeWindow> resultList = new ArrayList<ScoredChromosomeWindow>();
					for (int i = 0; i < startList.size(currentChromosome); i++) {
						resultList.add(new MaskChromosomeWindow(startList.get(currentChromosome).get(i), stopList.get(currentChromosome).get(i)));
					}
					// tell the operation pool that a chromosome is done
					op.notifyDone();
					return resultList;
				}
			};

			threadList.add(currentThread);
		}
		List<List<ScoredChromosomeWindow>> result = null;
		// starts the pool
		result = op.startPool(threadList);
		// add the chromosome results
		if (result != null) {
			for (List<ScoredChromosomeWindow> currentList: result) {
				scwList.add(currentList);
			}
		}
		// generate the statistics
		scwList.sort();
		scwList = new SCWLOMergeWindows(scwList).compute();
		scwList.computeStatistics();
		return scwList;
	}

	
	/**
	 * Creates an instance of mask SCWList from a specified {@link BinList}
	 * @param binList BinList used for the creation of the mask list
	 * @return a new {@link ScoredChromosomeWindowList}
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	public static ScoredChromosomeWindowList createMaskSCWArrayList (final BinList binList) throws InterruptedException, ExecutionException {
		ScoredChromosomeWindowList scwList = new ScoredChromosomeWindowArrayList(SCWListType.MASK);
		// retrieve the project chromosome
		ProjectChromosome projectChromosome = ProjectManager.getInstance().getProjectChromosome();
		// retrieve the instance of the OperationPool
		final OperationPool op = OperationPool.getInstance();
		// list for the threads
		final Collection<Callable<List<ScoredChromosomeWindow>>> threadList = new ArrayList<Callable<List<ScoredChromosomeWindow>>>();
		final int windowData = binList.getBinSize();
		for(final Chromosome currentChromosome : projectChromosome) {
			Callable<List<ScoredChromosomeWindow>> currentThread = new Callable<List<ScoredChromosomeWindow>>() {
				@Override
				public List<ScoredChromosomeWindow> call() throws Exception {
					List<ScoredChromosomeWindow> resultList = new ArrayList<ScoredChromosomeWindow>();
					if (binList.get(currentChromosome) != null) {
						for (int i = 0; i < binList.get(currentChromosome).size(); i++) {
							double currentScore = binList.get(currentChromosome, i);
							if (currentScore != 0.0) {
								int start = i * windowData;
								int stop = start + windowData;

								boolean hasToUpdate = false;								// here, we want to check whether the current window is following the previous one or not.
								int prevIndex = resultList.size() - 1;						// get the last index
								if (prevIndex >= 0) {										// if it exists
									int prevStop = resultList.get(prevIndex).getStop();		// get the last inserted stop
									if (prevStop == start) {								// if the previous window stops where the current one starts, both window follow each other and are the same
										hasToUpdate = true;									// an update of the previous window is enough
									}
								}

								if (hasToUpdate) {
									resultList.get(prevIndex).setStop(stop);
								} else {
									resultList.add(new MaskChromosomeWindow(start, stop));
								}
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
		List<List<ScoredChromosomeWindow>> result = null;
		// starts the pool
		result = op.startPool(threadList);
		// add the chromosome results
		if (result != null) {
			for (List<ScoredChromosomeWindow> currentList: result) {
				scwList.add(currentList);
			}
		}
		scwList.computeStatistics();
		return scwList;
	}
}
