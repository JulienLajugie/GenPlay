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
package edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import edu.yu.einstein.genplay.core.IO.reader.SCWReader;
import edu.yu.einstein.genplay.core.manager.project.ProjectChromosome;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.operation.SCWList.overlap.OverlapManagement;
import edu.yu.einstein.genplay.core.operationPool.OperationPool;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.enums.ScoreCalculationMethod;
import edu.yu.einstein.genplay.dataStructure.list.arrayList.old.DoubleArrayAsDoubleList;
import edu.yu.einstein.genplay.dataStructure.list.arrayList.old.IntArrayAsIntegerList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.GenomicDataArrayList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.GenomicListView;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.SimpleScoredChromosomeWindow;
import edu.yu.einstein.genplay.exception.exceptions.InvalidChromosomeException;

/**
 * Factory class for vending mask {@link ScoredChromosomeWindowList} of {@link SimpleScoredChromosomeWindow} objects.
 * Mask SCWL are lists with a score or 0 or 1
 * @author Julien Lajugie
 */
public class SimpleSCWListFactory {


	/**
	 * Creates a {@link ScoredChromosomeWindowList} from the data retrieved by the specified {@link SCWReader}
	 * @param scwReader a {@link SCWReader}
	 * @param scm method for the calculation of overlapping windows
	 * @return a {@link ScoredChromosomeWindowList}
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	public static ScoredChromosomeWindowList createSimpleSCWArrayList(SCWReader scwReader, ScoreCalculationMethod scm) throws InterruptedException, ExecutionException {
		GenomicListView<Integer> startList = new GenomicDataArrayList<Integer>();
		GenomicListView<Integer> stopList = new GenomicDataArrayList<Integer>();
		GenomicListView<Double> scoreList = new GenomicDataArrayList<Double>();
		ProjectChromosome projectChromosome = ProjectManager.getInstance().getProjectChromosome();
		for (int i = 0; i < projectChromosome.size(); i++) {
			startList.add(new IntArrayAsIntegerList());
			stopList.add(new IntArrayAsIntegerList());
			scoreList.add(new DoubleArrayAsDoubleList());
		}

		ScoredChromosomeWindow currentSCW = null;
		// read until eof
		while ((currentSCW = scwReader.readScoredChromosomeWindow()) != null) {
			Chromosome chromosome = scwReader.getCurrentChromosome();
			startList.add(chromosome, currentSCW.getStart());
			stopList.add(chromosome, currentSCW.getStop());
			scoreList.add(chromosome, currentSCW.getScore());
		}
		return createSimpleSCWArrayList(startList, stopList, scoreList, scm);
	}


	/**
	 * Creates an instance of {@link SimpleScoredChromosomeWindow} from a list of {@link ScoredChromosomeWindow}
	 * @param data list of {@link SimpleScoredChromosomeWindow} with the data of the {@link SimpleScoredChromosomeWindow} to create
	 * @param scm method for the calculation of overlapping windows
	 * @return a {@link ScoredChromosomeWindowList} of {@link ScoredChromosomeWindow}
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	public static <T extends ScoredChromosomeWindow> ScoredChromosomeWindowList createSimpleSCWList(List<List<T>> data, ScoreCalculationMethod scm) throws InterruptedException, ExecutionException {
		GenomicListView<Integer> startList = new GenomicDataArrayList<Integer>();
		GenomicListView<Integer> stopList = new GenomicDataArrayList<Integer>();
		GenomicListView<Double> scoreList = new GenomicDataArrayList<Double>();
		ProjectChromosome projectChromosome = ProjectManager.getInstance().getProjectChromosome();
		for (int i = 0; i < projectChromosome.size(); i++) {
			startList.add(new IntArrayAsIntegerList());
			stopList.add(new IntArrayAsIntegerList());
			scoreList.add(new DoubleArrayAsDoubleList());
		}
		// read until eof
		for (int i = 0; i < data.size(); i++) {
			for (T currentSCW: data.get(i)) {
				startList.get(i).add(currentSCW.getStart());
				stopList.get(i).add(currentSCW.getStop());
				scoreList.get(i).add(currentSCW.getScore());
			}
		}
		return createSimpleSCWArrayList(startList, stopList, scoreList, scm);
	}





	/**
	 * Creates an instance of {@link SimpleScoredChromosomeWindow} from a specified {@link BinList}
	 * @param binList BinList used for the creation of the {@link SimpleScoredChromosomeWindow}
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	/*public SimpleSCWList (final BinList binList) throws InterruptedException, ExecutionException {
		super();
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

								boolean hasToUpdate = false;										// here, we want to check whether the current window is following the previous one or not.
								int prevIndex = resultList.size() - 1;								// get the last index
								if (prevIndex >= 0) {												// if it exists
									int prevStop = resultList.get(prevIndex).getStop();				// get the last inserted stop
									if (prevStop == start) {										// if the previous window stops where the current one starts
										double prevScore = resultList.get(prevIndex).getScore();	// we get the previous score
										if (currentScore == prevScore) {							// if scores are the same, both window follow each other and are the same
											hasToUpdate = true;										// an update of the previous window is enough
										}
									}
								}

								if (hasToUpdate) {
									resultList.get(prevIndex).setStop(stop);
								} else {
									resultList.add(new SimpleScoredChromosomeWindow(start, stop, currentScore));
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
				add(currentList);
			}
		}
		generateStatistics();
	}*/


	/**
	 * Creates an instance of {@link SimpleSCWList}
	 * @param startList list of start positions
	 * @param stopList list of stop position
	 * @param scoreList list of score
	 * @param scm method for the calculation of overlapping windows
	 * @return a new {@link ScoredChromosomeWindowList} of {@link SimpleScoredChromosomeWindow}
	 * @throws InvalidChromosomeException
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	public static ScoredChromosomeWindowList createSimpleSCWArrayList(final GenomicListView<Integer> startList,
			final GenomicListView<Integer> stopList,
			final GenomicListView<Double> scoreList,
			final ScoreCalculationMethod scm) throws InvalidChromosomeException, InterruptedException, ExecutionException {

		// retrieve chromosome manager
		ProjectChromosome projectChromosome = ProjectManager.getInstance().getProjectChromosome();
		// retrieve the instance of the OperationPool
		final OperationPool op = OperationPool.getInstance();
		// list for the threads
		final Collection<Callable<List<ScoredChromosomeWindow>>> threadList = new ArrayList<Callable<List<ScoredChromosomeWindow>>>();

		final boolean runOverLapEngine;
		final OverlapManagement overlapManagement = new OverlapManagement(startList, stopList, scoreList);
		if (scm != null) {
			overlapManagement.setScoreCalculationMethod(scm);
			runOverLapEngine = true;
		} else {
			runOverLapEngine = false;
		}
		for(final Chromosome currentChromosome : projectChromosome) {
			Callable<List<ScoredChromosomeWindow>> currentThread = new Callable<List<ScoredChromosomeWindow>>() {
				@Override
				public List<ScoredChromosomeWindow> call() throws Exception {
					if (startList.get(currentChromosome) == null) {
						return null;
					}
					List<ScoredChromosomeWindow> resultList = new ArrayList<ScoredChromosomeWindow>();
					if (runOverLapEngine) {
						overlapManagement.run(currentChromosome);
					}
					List<ScoredChromosomeWindow> list = overlapManagement.getList(currentChromosome);
					for(int j = 0; j < list.size(); j++) {
						double score = list.get(j).getScore();
						if (score != 0) {
							resultList.add(new SimpleScoredChromosomeWindow(	list.get(j).getStart(),
									list.get(j).getStop(),
									score));
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
				//add(currentList);
			}
		}
		// generate the statistics
		//generateStatistics();
		// TODO return real list
		return null;
	}
}
