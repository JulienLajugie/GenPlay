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
import edu.yu.einstein.genplay.dataStructure.genomeList.GenomicDataList;
import edu.yu.einstein.genplay.dataStructure.genomeList.SCWList.ScoredChromosomeWindowList;
import edu.yu.einstein.genplay.dataStructure.genomeList.SCWList.SimpleSCWList;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;
import edu.yu.einstein.genplay.util.Utils;



/**
 * Computes the average value of the scores of the {@link SimpleSCWList}
 * @author Julien Lajugie
 * @version 0.1
 */
public class SCWLOAverage implements Operation<Double> {

	private final boolean[] chromoList; // list of the selected chromosomes
	private final ScoredChromosomeWindowList scwList; // input list
	private Long length = null; // sum of the lengths of non null windows
	private boolean				stopped = false;// true if the operation must be stopped

	/**
	 * Computes the average of the list defined as:
	 * sum(score * length) / sum(length)
	 * @param scwList ChromosomeListOfLists of ScoredChromosomeWindow
	 * @param chromoList set to true each chromosome of this list that you want to use in the calculation
	 * Perform the operation on every chromosome if null
	 */
	public SCWLOAverage(ScoredChromosomeWindowList scwList, boolean[] chromoList) {
		this.chromoList = chromoList;
		this.scwList = scwList;
	}


	/**
	 * Computes the average of the list defined as:
	 * sum(score * length) / sum(length)
	 * @param scwList input {@link GenomicDataList}
	 * @param chromoList list of boolean. A boolean set to true means that the
	 * chromosome with the same index is going to be used for the calculation.
	 * @param length sum of the lengths of non null windows
	 */
	public SCWLOAverage(ScoredChromosomeWindowList scwList, boolean[] chromoList, long length) {
		this.chromoList = chromoList;
		this.scwList = scwList;
		this.length = length;
	}


	@Override
	public Double compute() throws Exception {
		// if the average has to be calculated on all chromosome
		// and if it has already been calculated we don't do the calculation again
		if (Utils.allChromosomeSelected(chromoList)) {
			return scwList.getAverage();
		}

		// count the sum of the lengths of the non-null windows if wasn't specified in the constructor
		if (length == null) {
			length = new SCWLOCountNonNullLength(scwList, chromoList).compute();
		}
		// if there is no none-null value we return 0
		if (length == 0) {
			return 0d;
		}

		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<Double>> threadList = new ArrayList<Callable<Double>>();

		for (int i = 0; i < scwList.size(); i++) {
			final List<ScoredChromosomeWindow> currentList = scwList.getView(i);
			final int currentIndex = i;

			Callable<Double> currentThread = new Callable<Double>() {
				@Override
				public Double call() throws Exception {
					double sumScoreByLength = 0;
					if (((chromoList == null) || ((currentIndex < chromoList.length) && (chromoList[currentIndex]))) && (scwList.getView(currentIndex) != null)) {
						for (int j = 0; (j < currentList.size()) && !stopped; j++) {
							ScoredChromosomeWindow currentWindow = currentList.get(j);
							if (currentWindow.getScore() != 0) {
								sumScoreByLength += currentWindow.getScore() * currentWindow.getSize();
							}
						}
					}
					// tell the operation pool that a chromosome is done
					op.notifyDone();
					return sumScoreByLength;
				}
			};

			threadList.add(currentThread);
		}

		List<Double> result = op.startPool(threadList);
		if (result == null) {
			return null;
		}

		// sum the result of each chromosome
		double total = 0;
		for (Double currentResult: result) {
			total += currentResult;
		}
		return total / (double) length;
	}


	@Override
	public String getDescription() {
		return "Operation: Average";
	}


	@Override
	public String getProcessingDescription() {
		return "Computing Average";
	}


	@Override
	public int getStepCount() {
		return 1;
	}


	@Override
	public void stop() {
		stopped = true;
	}
}
