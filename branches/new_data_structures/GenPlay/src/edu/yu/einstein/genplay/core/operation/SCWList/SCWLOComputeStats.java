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
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import edu.yu.einstein.genplay.core.manager.project.ProjectChromosomes;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.core.operationPool.OperationPool;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SCWList;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;

/**
 * Computes statistics (eg: min, max, avg, stdev) on a specified {@link SCWList}
 * @author Julien Lajugie
 */
public class SCWLOComputeStats implements Operation<Void> {

	/** Smallest value of the list */
	private Float minimum = null;

	/** Greatest value of the list */
	private Float maximum = null;

	/** Average of the list */
	private Double average = null;

	/** Standard deviation of the list */
	private Double standardDeviation = null;

	/** Sum of Float scores of all windows */
	private Double scoreSum = null;

	/** Count of none-null bins in the BinList */
	private Long nonNullLength = null;

	/**  input list */
	private final SCWList inputList;

	/** True if the operation must be stopped */
	private boolean stopped = false;


	/**
	 * Creates an instance of {@link SCWLOComputeStats}
	 * @param inputList input list to analyze
	 */
	public SCWLOComputeStats(SCWList inputList) {
		this.inputList = inputList;
	}


	@Override
	public Void compute() throws InterruptedException, ExecutionException {
		// retrieve the project manager
		ProjectChromosomes projectChromosomes = ProjectManager.getInstance().getProjectChromosomes();

		// retrieve the instance of the OperationPool singleton
		final OperationPool op = OperationPool.getInstance();
		// list for the threads
		final Collection<Callable<Void>> threadList = new ArrayList<Callable<Void>>();

		// set the default value
		minimum = Float.POSITIVE_INFINITY;
		maximum = Float.NEGATIVE_INFINITY;
		average = 0d;
		standardDeviation = 0d;
		scoreSum = 0d;
		nonNullLength = 0l;

		// create arrays so each statics variable can be calculated for each chromosome
		final float[] mins = new float[projectChromosomes.size()];
		final float[] maxs = new float[projectChromosomes.size()];
		final double[] stDevs = new double[projectChromosomes.size()];
		final double[] scoreSums = new double[projectChromosomes.size()];
		final long[] nonNullLengths = new long[projectChromosomes.size()];

		// computes min / max / total score / non null bin count for each chromosome
		for(short i = 0; i < inputList.size(); i++)  {
			final ListView<ScoredChromosomeWindow> currentList = inputList.get(i);
			final short currentIndex = i;

			Callable<Void> currentThread = new Callable<Void>() {
				@Override
				public Void call() throws Exception {
					mins[currentIndex] = Float.POSITIVE_INFINITY;
					maxs[currentIndex] = Float.NEGATIVE_INFINITY;
					if (currentList != null) {
						for (int j = 0; (j < currentList.size()) && !stopped; j++) {
							ScoredChromosomeWindow currentWindow = currentList.get(j);
							if (currentWindow.getScore() != 0) {
								mins[currentIndex] = Math.min(mins[currentIndex], currentWindow.getScore());
								maxs[currentIndex] = Math.max(maxs[currentIndex], currentWindow.getScore());
								scoreSums[currentIndex] += currentWindow.getScore() * currentWindow.getSize();
								nonNullLengths[currentIndex] += currentWindow.getSize();
							}
						}
					}
					// notify that the current chromosome is done
					op.notifyDone();
					return null;
				}
			};

			threadList.add(currentThread);
		}
		// start the pool of thread
		op.startPool(threadList);

		// compute the genome wide result from the chromosomes results
		for (int i = 0; i < projectChromosomes.size(); i++) {
			minimum = Math.min(minimum, mins[i]);
			maximum = Math.max(maximum, maxs[i]);
			scoreSum += scoreSums[i];
			nonNullLength += nonNullLengths[i];
		}

		if (nonNullLength != 0) {
			// compute the average
			average = scoreSum / (double) nonNullLength;
			threadList.clear();

			// compute the standard deviation for each chromosome
			for(short i = 0; i < inputList.size(); i++)  {
				final ListView<ScoredChromosomeWindow> currentList = inputList.get(i);
				final short currentIndex = i;

				Callable<Void> currentThread = new Callable<Void>() {
					@Override
					public Void call() throws Exception {
						if (currentList != null) {
							for (int j = 0; (j < currentList.size()) && !stopped; j++) {
								ScoredChromosomeWindow currentWindow = currentList.get(j);
								if (currentWindow.getScore() != 0) {
									stDevs[currentIndex] += Math.pow(currentWindow.getScore() - average, 2) * currentWindow.getSize();
								}
							}
						}
						// notify that the current chromosome is done
						op.notifyDone();
						return null;
					}
				};

				threadList.add(currentThread);
			}
			// start the pool of thread
			op.startPool(threadList);

			// compute the genome wide standard deviation
			for (int i = 0; i < projectChromosomes.size(); i++) {
				standardDeviation += stDevs[i];
			}
			standardDeviation = Math.sqrt(standardDeviation / (double) nonNullLength);
		}
		return null;
	}


	/**
	 * @return the average
	 */
	public Double getAverage() {
		return average;
	}


	@Override
	public String getDescription() {
		return "Operation: Compute Statistics";
	}


	/**
	 * @return the inputList
	 */
	public SCWList getInputList() {
		return inputList;
	}


	/**
	 * @return the maximum
	 */
	public Float getMaximum() {
		return maximum;
	}


	/**
	 * @return the minimum
	 */
	public Float getMinimum() {
		return minimum;
	}


	/**
	 * @return the nonNullLength
	 */
	public Long getNonNullLength() {
		return nonNullLength;
	}


	@Override
	public String getProcessingDescription() {
		return "Computing Statistics";
	}


	/**
	 * @return the scoreSum
	 */
	public Double getScoreSum() {
		return scoreSum;
	}


	/**
	 * @return the standardDeviation
	 */
	public Double getStandardDeviation() {
		return standardDeviation;
	}


	@Override
	public int getStepCount() {
		return 2;
	}


	@Override
	public void stop() {
		stopped = true;
	}
}
