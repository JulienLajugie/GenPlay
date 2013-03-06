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
import edu.yu.einstein.genplay.dataStructure.enums.DataPrecision;
import edu.yu.einstein.genplay.dataStructure.list.arrayList.ListFactory;
import edu.yu.einstein.genplay.dataStructure.list.binList.BinList;



/**
 * Indexes the scores of a {@link BinList} based on 
 * the greatest and the smallest value of the whole genome
 * @author Julien Lajugie
 * @version 0.1
 */
public class BLOIndex implements Operation<BinList> {

	private final BinList 	binList;		// binlist to index
	private final double 	newMin;			// new min after index
	private final double 	newMax;			// new max after index
	private boolean			stopped = false;// true if the operation must be stopped
	

	/**
	 * Creates an instance of {@link BLOIndex}
	 * Indexes the scores between the specified minimum and maximum 
	 * based on the greatest and the smallest value of the whole genome.
	 * @param binList {@link BinList} to index
	 * @param newMin minimum value after index
	 * @param newMax maximum value after index
	 */
	public BLOIndex(BinList binList, double newMin, double newMax) {
		this.binList = binList;
		this.newMin = newMin;
		this.newMax = newMax;
	}


	@Override
	public BinList compute() throws InterruptedException, ExecutionException {
		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<List<Double>>> threadList = new ArrayList<Callable<List<Double>>>();
		final DataPrecision precision = binList.getPrecision();
		final int binSize = binList.getBinSize(); 
		final double oldMin = binList.getMin();
		final double oldMax = binList.getMax();
		// We calculate the difference between the highest and the lowest value
		final double oldDistance = oldMax - oldMin;
		if (oldDistance != 0) {
			final double newDistance = newMax - newMin;
			for (short i = 0; i < binList.size(); i++) {
				final List<Double> currentList = binList.get(i);	
	
				Callable<List<Double>> currentThread = new Callable<List<Double>>() {	
					@Override
					public List<Double> call() throws Exception {
						List<Double> resultList = null;
						if ((currentList != null) && (currentList.size() != 0)) {
							resultList = ListFactory.createList(precision, currentList.size());
							// We index the intensities
							for (int j = 0; j < currentList.size() && !stopped; j++) {
								if (currentList.get(j) == 0) {
									resultList.set(j, 0d);
								} else { 
									resultList.set(j, newDistance * (currentList.get(j) - oldMin) / oldDistance + newMin);
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
				BinList resultList = new BinList(binSize, precision, result);
				return resultList;
			} else {
				return null;
			}
		} else {
			return null;
		}
	}


	@Override
	public String getDescription() {
		return "Operation: Index Between " +  newMin + " and " + newMax;
	}
	
	
	@Override
	public int getStepCount() {
		return BinList.getCreationStepCount(binList.getBinSize()) + 1;
	}
	
	
	@Override
	public String getProcessingDescription() {
		return "Indexing";
	}

	
	@Override
	public void stop() {
		this.stopped = true;
	}
}
