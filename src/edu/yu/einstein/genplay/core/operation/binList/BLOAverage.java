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
package edu.yu.einstein.genplay.core.operation.binList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.core.operationPool.OperationPool;
import edu.yu.einstein.genplay.dataStructure.list.binList.BinList;
import edu.yu.einstein.genplay.util.Utils;



/**
 * Computes the average value of the scores of the {@link BinList}
 * @author Julien Lajugie
 * @version 0.1
 */
public class BLOAverage implements Operation<Double> {

	private final BinList 	binList;		// input BinList
	private final boolean[] chromoList;		// 1 boolean / chromosome. 
	// each boolean sets to true means that the corresponding chromosome is selected
	private Long 			count = null;	// count of non null bins
	private boolean			stopped = false;// true if the operation must be stopped
	
	
	/**
	 * Computes the average value of the scores of the {@link BinList}
	 * @param binList input {@link BinList}
	 * @param chromoList list of boolean. A boolean set to true means that the 
	 * chromosome with the same index is going to be used for the calculation. 
	 */
	public BLOAverage(BinList binList, boolean[] chromoList) {
		this.binList = binList;
		this.chromoList = chromoList;
	}


	/**
	 * Computes the average value of the scores of the {@link BinList}
	 * @param binList input {@link BinList}
	 * @param chromoList list of boolean. A boolean set to true means that the 
	 * chromosome with the same index is going to be used for the calculation.
	 * @param count count of non null bins 
	 */
	public BLOAverage(BinList binList, boolean[] chromoList, long count) {
		this.binList = binList;
		this.chromoList = chromoList;
		this.count = count;
	}
	

	@Override
	public Double compute() throws InterruptedException, ExecutionException {
		// if the average has to be calculated on all chromosome 
		// and if it has already been calculated we don't do the calculation again
		if ((Utils.allChromosomeSelected(chromoList)) && (binList.getAverage() != null)) {
			return binList.getAverage();
		}		

		// count the number of non null bins if wasn't specified in the constructor  
		if (count == null) {
			count = new BLOCountNonNullBins(binList, chromoList).compute();
		}
		// if there is no none-null value we return 0
		if (count == 0) {
			return 0d;
		}

		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<Double>> threadList = new ArrayList<Callable<Double>>();
		for (int i = 0; i < binList.size(); i++) {
			final List<Double> currentList = binList.get(i);
			final int currentIndex = i;
			Callable<Double> currentThread = new Callable<Double>() {
				@Override
				public Double call() throws Exception {
					double sum = 0;
					if (((chromoList == null) || ((currentIndex < chromoList.length) && (chromoList[currentIndex]))) && (binList.get(currentIndex) != null)) {
						for (int j = 0; j < currentList.size() && !stopped; j++) {
							if (currentList.get(j) != 0) {
								sum += currentList.get(j);
							}
						}
					}
					// tell the operation pool that a chromosome is done
					op.notifyDone();
					return sum;
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
		for (Double currentSum: result) {
			total += currentSum;
		}
		return total / (double) count;
	}


	@Override
	public String getDescription() {
		return "Operation: Average";
	}
	
	
	@Override
	public int getStepCount() {
		return 1;
	}


	@Override
	public String getProcessingDescription() {
		return "Computing Average";
	}

	
	@Override
	public void stop() {
		this.stopped = true;
	}
}
