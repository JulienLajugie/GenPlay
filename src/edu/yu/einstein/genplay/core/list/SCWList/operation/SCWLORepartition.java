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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import edu.yu.einstein.genplay.core.chromosomeWindow.ScoredChromosomeWindow;
import edu.yu.einstein.genplay.core.list.SCWList.ScoredChromosomeWindowList;
import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.core.operationPool.OperationPool;


/**
 * @author Chirag Gorasia
 * @version 0.1
 */
public class SCWLORepartition implements Operation<double [][][]>{

	private final ScoredChromosomeWindowList[] 	scwListArray;	// input list
	private final double 						scoreBinSize;	// size of the bins of score
	private final int 							graphType;		// type of the plot (window count or bp count)
	private boolean								stopped = false;// true if the operation must be stopped

	/**
	 * Window count plot
	 */
	public static final int WINDOW_COUNT_GRAPH = 1;

	/**
	 * Base count plot
	 */
	public static final int BASE_COUNT_GRAPH = 2;


	/**
	 * Creates an instance of {@link SCWLORepartition}
	 * @param scwListArray input list
	 * @param scoreBinSize size of the bins of score
	 * @param graphType type of graph (window count or base count)
	 */
	public SCWLORepartition(ScoredChromosomeWindowList[] scwListArray, double scoreBinSize, int graphType) {
		this.scwListArray = scwListArray;
		this.scoreBinSize = scoreBinSize;
		this.graphType = graphType;
	}


	@Override
	public double[][][] compute() throws IllegalArgumentException, IOException, InterruptedException, ExecutionException {
		if(scoreBinSize <= 0) {
			throw new IllegalArgumentException("the size of the score bins must be strictly positive");
		}
		double[][][] finalResult = new double[scwListArray.length][][];
		for (int i = 0; i < scwListArray.length; i++) {
			finalResult[i] = singleSCWListResult(scwListArray[i]);
		}
		return finalResult;
	}


	/**
	 * Generates the scatter plot data for the specified list
	 * @param scwList {@link ScoredChromosomeWindowList}
	 * @return the scater plot data for the specified list
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public double[][] singleSCWListResult (final ScoredChromosomeWindowList scwList) throws InterruptedException, ExecutionException {
		// search the greatest and smallest score
		double max = Math.max(0, scwList.getMax());
		double min = Math.min(0, scwList.getMin());
		// search the score of the first bin
		final double startPoint = Math.floor(min / scoreBinSize) * scoreBinSize;
		// distance from the max to the first score
		final double distanceMinMax = max - startPoint;
		// the +2 is because of the rounding (+1) and also because we want one more value
		// because the data are arrange this way:
		// count(res[i][1] to res[i+1][1]) = res[i][1]
		// meaning that we need to have the value for i + 1
		double result[][] = new double[(int)(distanceMinMax / scoreBinSize) + 2][2];
		int i = 0;
		// we add max + scoreBinSize to have a value for i + 1 (cf previous comment)
		while (((startPoint + (i * scoreBinSize)) <= (max + scoreBinSize)) && !stopped) {
			result[i][0] = startPoint + (i * scoreBinSize);
			i++;
		}

		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<double[]>> threadList = new ArrayList<Callable<double[]>>();

		for (final List<ScoredChromosomeWindow> currentList: scwList) {
			Callable<double[]> currentThread = new Callable<double[]>() {
				@Override
				public double[] call() throws Exception {
					if (currentList == null) {
						return null;
					}
					// create an array for the counts
					double[] chromoResult = new double[(int)(distanceMinMax / scoreBinSize) + 2];
					// count the bins
					for(int j = 0; (j < currentList.size()) && !stopped; j++) {
						if (currentList.get(j).getScore() != 0) {
							if (graphType == WINDOW_COUNT_GRAPH) {
								chromoResult[(int)((currentList.get(j).getScore() - startPoint) / scoreBinSize)]++;
							} else if (graphType == BASE_COUNT_GRAPH) {
								chromoResult[(int)((currentList.get(j).getScore() - startPoint) / scoreBinSize)] += currentList.get(j).getStop() - currentList.get(j).getStart();
							} else {
								throw new IllegalArgumentException("Invalid Plot Type");
							}
						}
					}
					op.notifyDone();
					return chromoResult;
				}
			};
			threadList.add(currentThread);
		}

		List<double[]> threadResult = op.startPool(threadList);
		if (threadResult == null) {
			return null;
		}

		for (double [] currentResult: threadResult) {
			if (currentResult != null) {
				for (i = 0; i < currentResult.length; i++) {
					result[i][1] += currentResult[i];
				}
			}
		}

		return result;
	}


	@Override
	public String getDescription() {
		return "Operation: Show Repartition";
	}


	@Override
	public String getProcessingDescription() {
		return "Plotting Repartition";
	}


	@Override
	public int getStepCount() {
		return scwListArray.length;
	}


	@Override
	public void stop() {
		this.stopped = true;
	}
}
