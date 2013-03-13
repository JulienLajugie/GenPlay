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
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.core.operationPool.OperationPool;
import edu.yu.einstein.genplay.dataStructure.enums.DataPrecision;
import edu.yu.einstein.genplay.dataStructure.enums.ScoreCalculationMethod;
import edu.yu.einstein.genplay.dataStructure.genomeList.binList.BinList;
import edu.yu.einstein.genplay.dataStructure.list.arrayList.ListFactory;
import edu.yu.einstein.genplay.exception.exceptions.BinListDifferentWindowSizeException;
import edu.yu.einstein.genplay.util.DoubleLists;



/**
 * Computes the average, the max or the sum of the {@link BinList} on intervals defined by another BinList
 * @author Julien Lajugie
 * @version 0.1
 */
public class BLOIntervalsScoring implements Operation<BinList> {

	private final BinList 					intervalList;				// BinList defining the intervals 
	private final BinList 					valueList;					// BinList defining the values for the calculation
	private final int 						percentageAcceptedValues;	// the calculation is calculated only on the x% greatest values of each interval 
	private final ScoreCalculationMethod 	method;						// method of calculation
	private final DataPrecision 			precision;					// precision of the result BinList
	private boolean							stopped = false;			// true if the operation must be stopped
	

	/**
	 * Creates an instance of {@link BLOIntervalsScoring}
	 * Computes the average, the max or the sum of the {@link BinList} on intervals defined by another BinList
	 * @param intervalList BinList defining the intervals
	 * @param valueList BinList defining the values for the calculation
	 * @param percentageAcceptedValues the calculation is calculated only on the x% greatest values of each interval 
	 * @param method method of calculation
	 * @param precision precision of the result BinList
	 */
	public BLOIntervalsScoring(BinList intervalList, BinList valueList, int percentageAcceptedValues, ScoreCalculationMethod method, DataPrecision precision) {
		this.intervalList = intervalList;
		this.valueList = valueList;
		this.percentageAcceptedValues = percentageAcceptedValues;
		this.method = method;
		this.precision = precision;		
	}


	@Override
	public BinList compute() throws InterruptedException, ExecutionException, BinListDifferentWindowSizeException {
		// check if the binList defining the intervals and the binList with the values have the same bin size
		if (intervalList.getBinSize() != valueList.getBinSize()) {
			throw new BinListDifferentWindowSizeException();
		}

		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<List<Double>>> threadList = new ArrayList<Callable<List<Double>>>();


		for (short i = 0; i < intervalList.size(); i++)  {
			final List<Double> currentIntervals = intervalList.get(i);
			final List<Double> currentValues = valueList.get(i);

			Callable<List<Double>> currentThread = new Callable<List<Double>>() {	
				@Override
				public List<Double> call() throws Exception {
					List<Double> resultList = null;
					if ((currentIntervals != null) && (currentValues != null)) {
						resultList = ListFactory.createList(precision, currentIntervals.size());
						int j = 0;
						while ((j < currentIntervals.size()) && (j < currentValues.size()) && !stopped) {
							while ((j < currentIntervals.size()) && (j < currentValues.size()) && (currentIntervals.get(j) == 0) && !stopped) {
								resultList.set(j, 0d);
								j++;
							}
							int k = j;
							List<Double> values = new ArrayList<Double>();
							while ((j < currentIntervals.size()) && (j < currentValues.size()) && (currentIntervals.get(j) != 0) && !stopped) {
								if (currentValues.get(j) != 0) {
									values.add(currentValues.get(j));					
								}
								j++;
							}
							if (values.size() > 0) {
								Collections.sort(values);
								int indexStart = values.size() - (int)(values.size() * (double)percentageAcceptedValues / 100d);
								double result = 0;
								switch (method) {
								case AVERAGE:
									result = DoubleLists.average(values, indexStart, values.size() - 1);
									break;
								case MAXIMUM:
									List<Double> listTmp = values.subList(indexStart, values.size() - 1);
									if ((listTmp != null) && (listTmp.size() > 0)) {
										result = Collections.max(listTmp);
									}
									break;							
								case SUM:
									result = DoubleLists.sum(values, indexStart, values.size() - 1);
									break;
								default:
									throw new IllegalArgumentException("Invalid score calculation method");
								}

								for (; k <= j; k++) {
									if (k < resultList.size()) {
										resultList.set(k, result);
									}
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
			BinList resultList = new BinList(valueList.getBinSize(), precision, result);
			return resultList;
		} else {
			return null;
		}
	}


	@Override
	public String getDescription() {
		return "Operation: Calculation on Projection, Accepted Values = " + percentageAcceptedValues + "%, Method = " + method + ", precision = " + precision;
	}
	
	
	@Override
	public int getStepCount() {
		return BinList.getCreationStepCount(intervalList.getBinSize()) + 1;
	}


	@Override
	public String getProcessingDescription() {
		return "Computing Calculation on Projection";
	}

	
	@Override
	public void stop() {
		this.stopped = true;
	}
}
