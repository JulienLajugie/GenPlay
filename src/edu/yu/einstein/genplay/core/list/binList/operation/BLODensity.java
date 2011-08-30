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
package edu.yu.einstein.genplay.core.list.binList.operation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import edu.yu.einstein.genplay.core.enums.DataPrecision;
import edu.yu.einstein.genplay.core.list.arrayList.ListFactory;
import edu.yu.einstein.genplay.core.list.binList.BinList;
import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.core.operationPool.OperationPool;



/**
 * Computes the density of bin with values on a region of halfWidth * 2 + 1 bins
 * @author Julien Lajugie
 * @version 0.1
 */
public class BLODensity implements Operation<BinList> {

	private final BinList 	binList;		// input BinList
	private final int		halfWidth; 		// half size of the region (in number of bin)
	private boolean			stopped = false;// true if the operation must be stopped
	

	/**
	 * Computes the density of bin with values on a region of halfWidth * 2 + 1 bins
	 * @param binList input {@link BinList}
	 * @param halfWidth half size of the region (in number of bin)
	 */
	public BLODensity(BinList binList, int halfWidth) {
		this.binList = binList;
		this.halfWidth = halfWidth;
	}


	@Override
	public BinList compute() throws InterruptedException, ExecutionException {
		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<List<Double>>> threadList = new ArrayList<Callable<List<Double>>>();
		// the result is returned in 32 bits because the result is btw 0 and 1
		final DataPrecision defaultPrecision = DataPrecision.PRECISION_32BIT;
		final int binCount = 2 * halfWidth + 1;

		for (short i = 0; i < binList.size(); i++) {
			final List<Double> currentList = binList.get(i);
			
			Callable<List<Double>> currentThread = new Callable<List<Double>>() {	
				@Override
				public List<Double> call() throws Exception {
					List<Double> resultList = null;
					if ((currentList != null) && (currentList.size() != 0)) {
						resultList = ListFactory.createList(defaultPrecision, currentList.size());
						// We compute the density for each bin
						for (int j = 0; j < currentList.size() && !stopped; j++) {
							int noneZeroBinCount = 0;
							for (int k = -halfWidth; k <= halfWidth && !stopped; k++) {
								if((j + k >= 0) && ((j + k) < currentList.size()))  {
									if (currentList.get(j + k) != 0) {
										noneZeroBinCount++;
									}
								}
							}
							resultList.set(j, noneZeroBinCount / (double)binCount);
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
			BinList resultList = new BinList(binList.getBinSize(), defaultPrecision, result);
			return resultList;
		} else {
			return null;
		}
	}
	

	@Override
	public String getDescription() {
		return "Operation: Density, Region Size = " + (halfWidth * 2 + 1) + " Bins";
	}
	
	
	@Override
	public int getStepCount() {
		return BinList.getCreationStepCount(binList.getBinSize()) + 1;
	}
	
	
	@Override
	public String getProcessingDescription() {
		return "Computing Density";
	}

	
	@Override
	public void stop() {
		this.stopped = true;
	}
}
