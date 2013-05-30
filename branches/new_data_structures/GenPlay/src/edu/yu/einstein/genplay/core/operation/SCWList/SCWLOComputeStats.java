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
	private float minimum = Float.POSITIVE_INFINITY;

	/** Smallest values of each chromo */
	private final float[] minimums;

	/** Greatest value of the list */
	private float maximum = Float.NEGATIVE_INFINITY;

	/** Greatest value of each chromo */
	private final float[] maximums;

	/** Average of the list */
	private double average = 0d;

	/** Averages of each chromo */
	private final double[] averages;

	/** Standard deviation of the list */
	private double standardDeviation = 0d;

	/** Standard deviation of each chromo */
	private final double[] standardDeviations;

	/** Number of windows with a score different from 0 */
	private long windowCount = 0l;

	/** Number of windows with a score different from 0 for each chromo*/
	private final long[] windowCounts;

	/** Sum of the length of the windows with a score different from 0 */
	private long windowLength = 0l;

	/** Sum of the length of the windows with a score different from 0 for each chromosome */
	private final long[] windowLengths;

	/** Sum of Float scores of all windows */
	private double scoreSum = 0d;

	/** Sum of Float scores of all windows for each chromosome */
	private final double[] scoreSums;

	/**  input list */
	private final SCWList inputList;

	/** True if the operation must be stopped */
	private boolean stopped = false;


	/**
	 * Creates an instance of {@link SCWLOComputeStats}
	 * @param inputList input list to analyze
	 */
	public SCWLOComputeStats(SCWList inputList) {
		// retrieve the project manager
		ProjectChromosomes projectChromosomes = ProjectManager.getInstance().getProjectChromosomes();
		this.inputList = inputList;
		minimums = new float[projectChromosomes.size()];
		maximums = new float[projectChromosomes.size()];
		averages = new double[projectChromosomes.size()];
		standardDeviations = new double[projectChromosomes.size()];
		windowCounts = new long[projectChromosomes.size()];
		windowLengths = new long[projectChromosomes.size()];
		scoreSums = new double[projectChromosomes.size()];
	}


	@Override
	public Void compute() throws InterruptedException, ExecutionException {
		// retrieve the project manager
		ProjectChromosomes projectChromosomes = ProjectManager.getInstance().getProjectChromosomes();

		// retrieve the instance of the OperationPool singleton
		final OperationPool op = OperationPool.getInstance();
		// list for the threads
		final Collection<Callable<Void>> threadList = new ArrayList<Callable<Void>>();

		// computes min / max / total score / non null bin count for each chromosome
		for(short i = 0; i < inputList.size(); i++)  {
			final ListView<ScoredChromosomeWindow> currentList = inputList.get(i);
			final short currentIndex = i;

			Callable<Void> currentThread = new Callable<Void>() {
				@Override
				public Void call() throws Exception {
					minimums[currentIndex] = Float.POSITIVE_INFINITY;
					maximums[currentIndex] = Float.NEGATIVE_INFINITY;
					if (currentList != null) {
						for (int j = 0; (j < currentList.size()) && !stopped; j++) {
							ScoredChromosomeWindow currentWindow = currentList.get(j);
							if (currentWindow.getScore() != 0) {
								minimums[currentIndex] = Math.min(minimums[currentIndex], currentWindow.getScore());
								maximums[currentIndex] = Math.max(maximums[currentIndex], currentWindow.getScore());
								scoreSums[currentIndex] += currentWindow.getScore() * currentWindow.getSize();
								windowCounts[currentIndex]++;
								windowLengths[currentIndex] += currentWindow.getSize();
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
			minimum = Math.min(minimum, minimums[i]);
			if (Float.isInfinite(minimums[i])) {
				minimums[i] = 0;
			}
			maximum = Math.max(maximum, maximums[i]);
			if (Float.isInfinite(maximums[i])) {
				maximums[i] = 0;
			}
			if (windowLengths[i] != 0) {
				averages[i] = scoreSums[i] / windowLengths[i];
			}
			scoreSum += scoreSums[i];
			windowCount += windowCounts[i];
			windowLength += windowLengths[i];
		}
		if (Float.isInfinite(minimum)) {
			minimum = 0;
		}
		if (Float.isInfinite(maximum)) {
			maximum = 0;
		}

		if (windowLength != 0) {
			// compute the average
			average = scoreSum / windowLength;
			threadList.clear();
			// standard deviation genome wide need to be computed separetly because it uses the average GW
			final double[] gwStandardDeviations = new double[projectChromosomes.size()];
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
									gwStandardDeviations[currentIndex] += Math.pow(currentWindow.getScore() - average, 2) * currentWindow.getSize();
									standardDeviations[currentIndex] += Math.pow(currentWindow.getScore() - averages[currentIndex], 2) * currentWindow.getSize();
								}
							}
							if (windowLengths[currentIndex] != 0) {
								standardDeviations[currentIndex] = Math.sqrt(standardDeviations[currentIndex] / windowLengths[currentIndex]);
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
				standardDeviation += gwStandardDeviations[i];
			}
			standardDeviation = Math.sqrt(standardDeviation / windowLength);
		}
		return null;
	}


	/**
	 * @return the average
	 */
	public double getAverage() {
		return average;
	}


	/**
	 * @return the averages
	 */
	public double[] getAverages() {
		return averages;
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
	public float getMaximum() {
		return maximum;
	}


	/**
	 * @return the maximums
	 */
	public float[] getMaximums() {
		return maximums;
	}


	/**
	 * @return the minimum
	 */
	public float getMinimum() {
		return minimum;
	}


	/**
	 * @return the minimums
	 */
	public float[] getMinimums() {
		return minimums;
	}


	@Override
	public String getProcessingDescription() {
		return "Computing Statistics";
	}


	/**
	 * @return the nonNullLength
	 */
	public double getScoreSum() {
		return scoreSum;
	}


	/**
	 * @return the scoreSums
	 */
	public double[] getScoreSums() {
		return scoreSums;
	}


	/**
	 * @return the standardDeviation
	 */
	public double getStandardDeviation() {
		return standardDeviation;
	}


	/**
	 * @return the standardDeviations
	 */
	public double[] getStandardDeviations() {
		return standardDeviations;
	}


	@Override
	public int getStepCount() {
		return 2;
	}


	/**
	 * @return the window count
	 */
	public long getWindowCount() {
		return windowCount;
	}


	/**
	 * @return the window counts
	 */
	public long[] getWindowCounts() {
		return windowCounts;
	}


	/**
	 * @return the window length
	 */
	public long getWindowLength() {
		return windowLength;
	}


	/**
	 * @return the window lengths
	 */
	public long[] getWindowLengths() {
		return windowLengths;
	}


	@Override
	public void stop() {
		stopped = true;
	}
}
