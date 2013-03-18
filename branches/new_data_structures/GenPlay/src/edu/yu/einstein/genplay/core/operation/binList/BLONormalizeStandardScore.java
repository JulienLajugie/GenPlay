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

import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.core.operationPool.OperationPool;
import edu.yu.einstein.genplay.dataStructure.enums.ScorePrecision;
import edu.yu.einstein.genplay.dataStructure.list.arrayList.old.ListFactory;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.binList.BinList;



/**
 * Computes a Standard Score normalization on a {@link BinList}
 * @author Julien Lajugie
 * @version 0.1
 */
public class BLONormalizeStandardScore implements Operation<BinList> {

	private final BinList 				binList;	// input list 
	private final BLOAverage 			avgOp;		// average
	private final BLOStandardDeviation 	stdevOp;	// standard deviation
	private boolean						stopped = false;// true if the operation must be stopped
	

	/**
	 * Creates an instance of {@link BLONormalizeStandardScore}
	 * @param binList input list
	 */
	public BLONormalizeStandardScore(BinList binList) {
		this.binList = binList;
		avgOp = new BLOAverage(binList, null);
		stdevOp = new BLOStandardDeviation(binList, null);
	}

	@Override
	public BinList compute() throws Exception {
		// compute average
		final double avg = avgOp.compute();
		// compute standard deviation
		final double stdev = stdevOp.compute();
		// retrieve data precision
		final ScorePrecision precision = binList.getPrecision();
		// retrieve singleton operation pool
		final OperationPool op = OperationPool.getInstance();
		// creates collection of thread for the operation pool
		final Collection<Callable<List<Double>>> threadList = new ArrayList<Callable<List<Double>>>();
		for (short i = 0; i < binList.size(); i++)  {
			final List<Double> currentList = binList.get(i);

			Callable<List<Double>> currentThread = new Callable<List<Double>>() {	
				@Override
				public List<Double> call() throws Exception {
					List<Double> resultList = null;
					if ((currentList != null) && (currentList.size() != 0)) {
						resultList = ListFactory.createList(precision, currentList.size());
						for (int j = 0; j < currentList.size() && !stopped; j++) {
							if (currentList.get(j) != 0) {
								// apply the standard score formula: (x - avg) / stdev 
								double resultScore = (currentList.get(j) - avg) / stdev; 
								resultList.set(j, resultScore);
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
		List<List<Double>> result = op.startPool(threadList);
		if (result != null) {
			BinList resultList = new BinList(binList.getBinSize(), precision, result);
			return resultList;
		} else {
			return null;
		}
	}


	@Override
	public String getDescription() {
		return "Operation: Normalize, Standard Score";
	}


	@Override
	public String getProcessingDescription() {
		return "Normalizing";
	}


	@Override
	public int getStepCount() {
		// 1 for this operation, 1 f
		return 1 + avgOp.getStepCount() + stdevOp.getStepCount() + BinList.getCreationStepCount(binList.getBinSize());
	}

	
	@Override
	public void stop() {
		this.stopped = true;
	}
}
