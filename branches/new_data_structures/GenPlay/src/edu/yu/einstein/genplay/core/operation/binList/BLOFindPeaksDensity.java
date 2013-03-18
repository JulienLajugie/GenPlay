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
 * Removes the values above and under specified thresholds
 * @author Julien Lajugie
 * @version 0.1
 */
public class BLOFindPeaksDensity implements Operation<BinList[]> {
	private final BinList 	binList;				// input binlist to filter
	private double 			lowThreshold;			// saturates the values under this threshold
	private double 			highThreshold;			// saturates the values above this threshold
	private double 			density;				// minimum density of windows above and under the thresholds for a region to be selected (percentage btw 0 and 1)
	private int 			halfWidth;				// half size of the region (in number of bins) 
	private boolean			stopped = false;		// true if the operation must be stopped
	
	
	/**
	 * Creates an instance of {@link BLOFindPeaksDensity}
	 * @param binList {@link BinList} to filter
	 */
	public BLOFindPeaksDensity(BinList binList) {
		this.binList = binList;
	}
	
	
	@Override
	public BinList[] compute() throws Exception {
		if (lowThreshold >= highThreshold) {
			throw new IllegalArgumentException("The high threshold must be greater than the low one");
		}		
		// if percentage is zero, everything is selected
		if (density == 0) {	
			BinList resultList = binList.deepClone();
			BinList[] returnValue = {resultList};		
			return returnValue;
		}
		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<List<Double>>> threadList = new ArrayList<Callable<List<Double>>>();
		// we calculate the min number of bins above the threshold needed to select a region
		final int minBinCount = (int)Math.ceil(halfWidth * 2 * density);
		final ScorePrecision precision = binList.getPrecision();
		for (final List<Double> currentList: binList) {

			Callable<List<Double>> currentThread = new Callable<List<Double>>() {			
				@Override
				public List<Double> call() throws Exception {
					List<Double> resultList = null;
					if ((currentList != null) && (currentList.size() != 0)) {
						resultList = ListFactory.createList(binList.getPrecision(), currentList.size());
						for (int j = 0; j < currentList.size() && !stopped; j++) {
							if (currentList.get(j) != 0) {
								int indexStart = j - halfWidth;
								int indexStop = j + halfWidth;
								// if the start index is negative we set it to 0
								if (indexStart < 0) {
									indexStart = 0;
								}
								// if the stop index is out of range we set it to the size of the list
								if (indexStop > currentList.size() - 1) {
									indexStop = currentList.size() - 1;
								}
								// we accept a window if there is nbConsecutiveValues above or under the filter
								int binSelectedCount = 0;
								int k = indexStart;
								while ((binSelectedCount < minBinCount) && (k <= indexStop) && !stopped) {
									// depending on the filter type we accept values above or under the threshold
									if ((currentList.get(k) > lowThreshold) && (currentList.get(k) < highThreshold)) {
										binSelectedCount++;
									}
									k++;
								}
								if (binSelectedCount >= minBinCount) {
									resultList.set(j, currentList.get(j));
								} else {
									resultList.set(j, 0d);
								}
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
			BinList[] returnValue = {resultList};
			return returnValue;
		} else {
			return null;
		}
	}


	@Override
	public String getDescription() {
		return "Operation: Density Filter, minimum = " + lowThreshold + ", maximum = " + highThreshold 
		+ ", density = " + density + ", region size = " + (halfWidth * 2 * binList.getBinSize()) + "bp";
	}


	@Override
	public String getProcessingDescription() {
		return "Filtering";
	}


	@Override
	public int getStepCount() {
		return 1 + BinList.getCreationStepCount(binList.getBinSize());
	}


	/**
	 * @param density minimum density of windows above and under the thresholds for a region to be selected (percentage btw 0 and 1)
	 */
	public final void setDensity(double density) {
		this.density = density;
	}


	/**
	 * @param halfWidth half size of the region (in number of bins) 
	 */
	public final void setHalfWidth(int halfWidth) {
		this.halfWidth = halfWidth;
	}


	/**
	 * @param highThreshold filters the values above this threshold
	 */
	public final void setHighThreshold(double highThreshold) {
		this.highThreshold = highThreshold;
	}


	/**
	 * @param lowThreshold filters the values under this threshold
	 */
	public final void setLowThreshold(double lowThreshold) {
		this.lowThreshold = lowThreshold;
	}

	
	@Override
	public void stop() {
		this.stopped = true;
	}
}
