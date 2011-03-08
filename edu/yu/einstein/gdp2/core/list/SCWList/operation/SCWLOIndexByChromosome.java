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
 *     Author: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *     Website: <http://genplay.einstein.yu.edu>
 *******************************************************************************/
package yu.einstein.gdp2.core.list.SCWList.operation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import yu.einstein.gdp2.core.ScoredChromosomeWindow;
import yu.einstein.gdp2.core.list.SCWList.ScoredChromosomeWindowList;
import yu.einstein.gdp2.core.operation.Operation;
import yu.einstein.gdp2.core.operationPool.OperationPool;


/**
 * Indexes the scores of a {@link ScoredChromosomeWindowList} based on 
 * the greatest and the smallest value of each chromosome
 * @author Julien Lajugie
 * @version 0.1
 */
public class SCWLOIndexByChromosome implements Operation<ScoredChromosomeWindowList> {

	private final ScoredChromosomeWindowList	scwList;	// list to index
	private final double 						newMin;		// new min after index
	private final double 						newMax;		// new max after index
	private boolean				stopped = false;// true if the operation must be stopped


	/**
	 * Creates an instance of {@link SCWLOIndexByChromosome}
	 * Indexes the scores between the specified minimum and maximum 
	 * based on the greatest and the smallest value of each chromosome.
	 * @param scwList {@link ScoredChromosomeWindowList} to index
	 * @param newMin minimum value after index
	 * @param newMax maximum value after index
	 */
	public SCWLOIndexByChromosome(ScoredChromosomeWindowList scwList, double newMin, double newMax) {
		this.scwList = scwList;
		this.newMin = newMin;
		this.newMax = newMax;
	}


	@Override
	public ScoredChromosomeWindowList compute() throws InterruptedException, ExecutionException {
		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<List<ScoredChromosomeWindow>>> threadList = new ArrayList<Callable<List<ScoredChromosomeWindow>>>();

		// We calculate the difference between the greatest and the smallest scores
		final double newDistance = newMax - newMin;
		for (short i = 0; i < scwList.size(); i++) {
			final List<ScoredChromosomeWindow> currentList = scwList.get(i);

			Callable<List<ScoredChromosomeWindow>> currentThread = new Callable<List<ScoredChromosomeWindow>>() {
				@Override
				public List<ScoredChromosomeWindow> call() throws Exception {
					List<ScoredChromosomeWindow> resultList = null;
					if ((currentList != null) && (currentList.size() != 0)) {
						resultList = new ArrayList<ScoredChromosomeWindow>();
						// search the min and max for the current chromosome before index 
						double oldMin = minNoZero(currentList);						
						double oldMax = maxNoZero(currentList);
						// we calculate the difference between the highest and the lowest value
						double oldDistance = oldMax - oldMin;
						if (oldDistance != 0) {
							// We index the intensities 
							for (int j = 0; j < currentList.size() && !stopped; j++) {
								ScoredChromosomeWindow currentWindow = currentList.get(j);
								ScoredChromosomeWindow resultWindow = new ScoredChromosomeWindow(currentWindow);
								if (currentWindow.getScore() != 0) {
									resultWindow.setScore(newDistance * (currentWindow.getScore() - oldMin) / oldDistance + newMin);
								}
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
			ScoredChromosomeWindowList resultList = new ScoredChromosomeWindowList(result);
			return resultList;
		} else {
			return null;
		}		
	}


	@Override
	public String getDescription() {
		return "Operation: Index per Chromsome Between " +  newMin + " and " + newMax;
	}


	@Override
	public int getStepCount() {
		return 1 + ScoredChromosomeWindowList.getCreationStepCount();
	}


	@Override
	public String getProcessingDescription() {
		return "Indexing";
	}
	
	
	/**
	 * Returns the maximum of the list in parameter. Doesn't take the 0 value elements into account.
	 * @param list
	 * @return the non-zero maximum of the specified list
	 */
	public static double maxNoZero(List<ScoredChromosomeWindow> list) {
		double max = Double.NEGATIVE_INFINITY;
		for (ScoredChromosomeWindow currentWindow : list) {
			if (currentWindow.getScore() != 0) {
				max = Math.max(max, currentWindow.getScore());
			}
		}
		return max;
	}
	
	
	/**
	 * Returns the minimum of the list in parameter. Doesn't take the 0 value elements into account.
	 * @param list
	 * @return the non-zero minimum of the specified list
	 */
	public static double minNoZero(List<ScoredChromosomeWindow> list) {
		double min = Double.POSITIVE_INFINITY;
		for (ScoredChromosomeWindow currentWindow : list) {
			if (currentWindow.getScore() != 0) {
				min = Math.min(min, currentWindow.getScore());
			}
		}
		return min;
	}

	
	@Override
	public void stop() {
		this.stopped = true;
	}
}
