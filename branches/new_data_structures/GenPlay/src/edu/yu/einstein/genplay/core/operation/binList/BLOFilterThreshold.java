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
import edu.yu.einstein.genplay.dataStructure.enums.DataPrecision;
import edu.yu.einstein.genplay.dataStructure.list.arrayList.ListFactory;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.binList.BinList;



/**
 * Removes the values above and under specified thresholds
 * @author Julien Lajugie
 * @version 0.1
 */
public class BLOFilterThreshold implements Operation<BinList> {
	private final BinList 	binList;				// input binlist to filter
	private final double 	lowThreshold;			// filters the values under this threshold
	private final double 	highThreshold;			// filters the values above this threshold
	private final boolean	isSaturation;			// true if we saturate, false if we remove the filtered values 
	private boolean			stopped = false;		// true if the operation must be stopped
	
	
	/**
	 * Creates an instance of {@link BLOFilterThreshold}
	 * @param binList {@link BinList} to filter
	 * @param lowThreshold filters the values under this threshold
	 * @param highThreshold filters the values above this threshold
	 * @param isSaturation true to saturate, false to remove the filtered values
	 */
	public BLOFilterThreshold(BinList binList, double lowThreshold, double highThreshold, boolean isSaturation) {
		this.binList = binList;
		this.lowThreshold = lowThreshold;
		this.highThreshold = highThreshold;
		this.isSaturation = isSaturation;
	}


	@Override
	public BinList compute() throws Exception {
		if (lowThreshold >= highThreshold) {
			throw new IllegalArgumentException("The high threshold must be greater than the low one");
		}

		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<List<Double>>> threadList = new ArrayList<Callable<List<Double>>>();
		final DataPrecision precision = binList.getPrecision();
		for (final List<Double> currentList: binList) {

			Callable<List<Double>> currentThread = new Callable<List<Double>>() {			
				@Override
				public List<Double> call() throws Exception {
					List<Double> resultList = null;
					if ((currentList != null) && (currentList.size() != 0)) {
						resultList = ListFactory.createList(binList.getPrecision(), currentList.size());
						for (int i = 0; i < currentList.size() && !stopped; i++) {
							double currentScore = currentList.get(i);
							if (currentScore != 0) {
								if (currentScore > highThreshold) {
									// if the score is greater than the high threshold
									if (isSaturation) {
										// set the value to high threshold (saturation)
										resultList.set(i, highThreshold);
									} else {
										// set the value to 0
										resultList.set(i, 0d);
									}
								} else if (currentScore < lowThreshold) {
									// if the score is smaller than the low threshold
									if (isSaturation) {
										// set the value to low threshold (saturation)
										resultList.set(i, lowThreshold);
									} else {
										// set the value to 0
										resultList.set(i, 0d);
									}
								} else {
									// if the score is between the two threshold
									resultList.set(i, currentScore);
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
			return resultList;
		} else {
			return null;
		}
	}


	@Override
	public String getDescription() {
		String optionStr;
		if (isSaturation) {
			optionStr = ", option = saturation";
		} else {
			optionStr = ", option = remove";
		}
		return "Operation: Threshold Filter, minimum = " + lowThreshold + ", maximum = " + highThreshold + optionStr;
	}


	@Override
	public String getProcessingDescription() {
		return "Filtering";
	}


	@Override
	public int getStepCount() {
		return 1 + BinList.getCreationStepCount(binList.getBinSize());
	}

	
	@Override
	public void stop() {
		this.stopped = true;
	}
}
