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
import edu.yu.einstein.genplay.dataStructure.enums.ScoreCalculationTwoLayersMethod;
import edu.yu.einstein.genplay.dataStructure.list.arrayList.ListFactory;
import edu.yu.einstein.genplay.dataStructure.list.binList.BinList;
import edu.yu.einstein.genplay.dataStructure.list.genomicDataList.ImmutableGenomicDataList;
import edu.yu.einstein.genplay.exception.exceptions.BinListDifferentWindowSizeException;



/**
 * Adds the scores of the bins of two specified BinLists
 * @author Julien Lajugie
 * @version 0.1
 */
public class BLOTwoLayers implements Operation<ImmutableGenomicDataList<?>> {

	private final BinList 					binList1;		// first binlist to add
	private final BinList 					binList2; 		// second binlist to add
	private final DataPrecision 			precision;		// precision of the result list
	private final ScoreCalculationTwoLayersMethod 	scm;			// method of calculation for the score
	private boolean							stopped = false;// true if the operation must be stopped


	/**
	 * Adds the scores of the bins of the two specified BinLists
	 * @param binList1
	 * @param binList2
	 * @param precision precision of the result {@link BinList}
	 * @param scm {@link ScoreCalculationTwoLayersMethod} method used to compute the scores
	 */
	public BLOTwoLayers(BinList binList1, BinList binList2, DataPrecision precision, ScoreCalculationTwoLayersMethod scm) {
		this.binList1 = binList1;
		this.binList2 = binList2;
		this.precision = precision;
		this.scm = scm;
	}


	@Override
	public BinList compute() throws InterruptedException, ExecutionException, BinListDifferentWindowSizeException {
		// make sure that the two binlists have the same size of bins
		if (binList1.getBinSize() != binList2.getBinSize()) {
			throw new BinListDifferentWindowSizeException();
		}

		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<List<Double>>> threadList = new ArrayList<Callable<List<Double>>>();
		for(short i = 0; i < binList1.size(); i++)  {
			final List<Double> currentList1 = binList1.get(i);
			final List<Double> currentList2 = binList2.get(i);

			final boolean firstLayerIsEmpty;
			final boolean secondLayerIsEmpty;
			if ((currentList1 != null) && (currentList1.size() != 0)) {
				firstLayerIsEmpty = false;
			} else {
				firstLayerIsEmpty = true;
			}
			if ((currentList2 != null) && (currentList2.size() != 0)) {
				secondLayerIsEmpty = false;
			} else {
				secondLayerIsEmpty = true;
			}

			Callable<List<Double>> currentThread = new Callable<List<Double>>() {
				@Override
				public List<Double> call() throws Exception {
					List<Double> resultList = null;

					if (!firstLayerIsEmpty && !secondLayerIsEmpty) {
						resultList = ListFactory.createList(precision, currentList1.size());
						for (int j = 0; (j < currentList1.size()) && !stopped; j++) {
							if (j < currentList2.size()) {
								// we add the bins of the two binlists
								resultList.set(j, getScore(currentList1.get(j), currentList2.get(j)));
							} else {
								resultList.set(j, 0d);
							}
						}
					} else {
						List<Double> currentList = null;
						if (!firstLayerIsEmpty & secondLayerIsEmpty) {
							currentList = currentList1;
						} else if (firstLayerIsEmpty & !secondLayerIsEmpty) {
							currentList = currentList2;
						}

						if (currentList != null) {
							resultList = ListFactory.createList(precision, currentList.size());
							if ((scm == ScoreCalculationTwoLayersMethod.ADDITION) || (scm == ScoreCalculationTwoLayersMethod.MAXIMUM)) {
								for (int j = 0; (j < currentList.size()) && !stopped; j++) {
									resultList.set(j, currentList.get(j));
								}
							} else if (scm == ScoreCalculationTwoLayersMethod.SUBTRACTION) {
								int factor = 1;
								if (firstLayerIsEmpty) {
									factor = -1;
								}
								for (int j = 0; (j < currentList.size()) && !stopped; j++) {
									resultList.set(j, factor*currentList.get(j));
								}
							} else if ((scm == ScoreCalculationTwoLayersMethod.MULTIPLICATION) || (scm == ScoreCalculationTwoLayersMethod.DIVISION) || (scm == ScoreCalculationTwoLayersMethod.MINIMUM)) {
								for (int j = 0; (j < currentList.size()) && !stopped; j++) {
									resultList.set(j, 0d);
								}
							} else if (scm == ScoreCalculationTwoLayersMethod.AVERAGE) {
								for (int j = 0; (j < currentList.size()) && !stopped; j++) {
									resultList.set(j, currentList.get(j) / 2);
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
			BinList resultList = new BinList(binList1.getBinSize(), precision, result);
			return resultList;
		} else {
			return null;
		}
	}


	@Override
	public String getDescription() {
		return "Operation on two layers: " + scm.toString();
	}


	@Override
	public int getStepCount() {
		return BinList.getCreationStepCount(binList1.getBinSize()) + 1;
	}


	@Override
	public String getProcessingDescription() {
		return "Two Layers Operation";
	}


	/**
	 * getScore method
	 * This method manages the calculation of the score according to the score calculation method.
	 * 
	 * @return	the score
	 */
	private double getScore (double a, double b) {
		switch (scm) {
		case ADDITION:
			return sum(a, b);
		case SUBTRACTION:
			return subtraction(a, b);
		case MULTIPLICATION:
			return multiplication(a, b);
		case DIVISION:
			return division(a, b);
		case AVERAGE:
			return average(a, b);
		case MAXIMUM:
			return maximum(a, b);
		case MINIMUM:
			return minimum(a, b);
		default:
			return -1.0;
		}
	}


	///////////////////////////	Calculation methods

	private double sum(double a, double b) {
		return (a + b);
	}


	private double subtraction(double a, double b) {
		return (a - b);
	}


	private double multiplication(double a, double b) {
		return (a * b);
	}


	private double division(double a, double b) {
		if ((a != 0.0) && (b != 0.0)) {
			return a / b;
		} else {
			return 0.0;
		}
	}


	private double average(double a, double b) {
		return sum(a, b) / 2;
	}


	private double maximum(double a, double b) {
		return Math.max(a, b);
	}


	private double minimum(double a, double b) {
		return Math.min(a, b);
	}


	@Override
	public void stop() {
		stopped = true;
	}
}
