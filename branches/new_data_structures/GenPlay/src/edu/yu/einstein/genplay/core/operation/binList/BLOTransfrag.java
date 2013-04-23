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
import edu.yu.einstein.genplay.dataStructure.enums.ScorePrecision;
import edu.yu.einstein.genplay.dataStructure.enums.ScoreOperation;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.binList.BinList;
import edu.yu.einstein.genplay.dataStructure.list.primitiveList.old.ListFactory;
import edu.yu.einstein.genplay.util.FloatLists;



/**
 * Defines regions as "islands" of non zero value bins 
 * separated by more than a specified number of zero value bins.
 * Computes the average on these regions.
 * Returns a new {@link BinList} with the defined regions having their average/max/sum as a score
 * @author Julien Lajugie
 * @author Chirag Gorasia
 * @version 0.1
 */
public class BLOTransfrag implements Operation<BinList> {

	private final BinList 					binList;	// input binlist
	private final int 						zeroBinGap; // number of zero value bins defining a gap between two islands
	private final ScoreOperation	operation;	// max / sum / average 
	private boolean							stopped = false;// true if the operation must be stopped
	

	/**
	 * Defines regions as "islands" of non zero value bins 
	 * separated by more than a specified number of zero value bins.
	 * Computes the average on these regions.
	 * Returns a new {@link BinList} with the defined regions having their average/max/sum as a score
	 * @param binList input BinList
	 * @param zeroBinGap number of zero value windows defining a gap between two islands
	 * @param operation operation to use to compute the score of the intervals
	 */
	public BLOTransfrag(BinList binList, int zeroBinGap, ScoreOperation operation) {
		this.binList = binList;
		this.zeroBinGap = zeroBinGap;
		this.operation = operation;		
	}


	@Override
	public BinList compute() throws InterruptedException, ExecutionException {
		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<List<Double>>> threadList = new ArrayList<Callable<List<Double>>>();

		final int binSize = binList.getBinSize();
		final ScorePrecision precision = binList.getPrecision();

		for (short i = 0; i < binList.size(); i++) {
			final List<Double> currentList = binList.get(i);	

			Callable<List<Double>> currentThread = new Callable<List<Double>>() {	
				@Override
				public List<Double> call() throws Exception {
					List<Double> resultList = null;
					if ((currentList != null) && (currentList.size() != 0)) {
						resultList = ListFactory.createList(precision, currentList.size());
						int j = 0;				
						while (j < currentList.size() && !stopped) {
							// skip zero values
							while ((j < currentList.size()) && (currentList.get(j) == 0) && !stopped) {
								j++;
							}
							int regionStart = j;
							int regionStop = regionStart;
							int zeroWindowCount = 0;
							// a region stops when there is maxZeroWindowGap consecutive zero bins
							while ((j < currentList.size()) && (zeroWindowCount <= zeroBinGap) && !stopped) {
								if (currentList.get(j) == 0) {
									zeroWindowCount++;
								} else {
									zeroWindowCount = 0;
									regionStop = j;
								}
								j++;
							}
							if (regionStop == currentList.size()) {
								regionStop--;
							}
							if (regionStop >= regionStart) { 
								double regionScore = 0;
								if (operation == ScoreOperation.AVERAGE) {
									// all the windows of the region are set with the average value on the region
									regionScore = FloatLists.average(currentList, regionStart, regionStop);
								} else if (operation == ScoreOperation.SUM) {
									// all the windows of the region are set with the sum value on the region
									regionScore = FloatLists.sum(currentList, regionStart, regionStop);
								} else {
									// all the windows of the region are set with the max value on the region
									regionScore = FloatLists.maxNoZero(currentList, regionStart, regionStop);
								}
								for (j = regionStart; j <= regionStop; j++) {
									if (j < resultList.size()) {
										resultList.set(j, regionScore);
									}
								}
							}
							j++;
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
			BinList resultList = new BinList(binSize, precision, result);
			return resultList;
		} else {
			return null;
		}
	}


	@Override
	public String getDescription() {
		return "Operation: Transfrag, Gap Size = " + zeroBinGap + " Zero Value Successive Bins";
	}
	
	
	@Override
	public int getStepCount() {
		return BinList.getCreationStepCount(binList.getBinSize()) + 1;
	}
	
	
	@Override
	public String getProcessingDescription() {
		return "Computing Transfrag";
	}
	

	@Override
	public void stop() {
		this.stopped = true;
	}
}
