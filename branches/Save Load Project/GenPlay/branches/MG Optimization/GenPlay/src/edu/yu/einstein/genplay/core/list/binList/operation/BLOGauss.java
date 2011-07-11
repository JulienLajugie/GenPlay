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
 *     Author: Julien Lajugie <julien.lajugie@einstein.yu.edu>
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
 * Applies a gaussian filter on the BinList and returns the result in a new BinList.
 * @author Julien Lajugie
 * @version 0.1
 */
public class BLOGauss implements Operation<BinList> {

	private final BinList 	binList;		// input list
	private final int 		sigma;			// sigma parameter of the gaussian
	private final boolean	fillNullValues; // true to fill the null values
	private boolean			stopped = false;// true if the operation must be stopped
	
	
	/**
	 * Creates an instance of {@link BLOGauss}
	 * Applies a gaussian filter on the BinList and returns the result in a new BinList.
	 * @param binList {@link BinList} to gauss
	 * @param sigma parameter of the gaussian filter
	 * @param fillNullValues set to true to fill the null values
	 */
	public BLOGauss(BinList binList, int sigma, boolean fillNullValues) {
		this.binList = binList;
		this.sigma = sigma;
		this.fillNullValues = fillNullValues;
	}
	
	
	@Override
	public BinList compute() throws InterruptedException, ExecutionException {
		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<List<Double>>> threadList = new ArrayList<Callable<List<Double>>>();
		final DataPrecision precision = binList.getPrecision();
		final int binSize =  binList.getBinSize();
		final int halfWidth = 2 * sigma / binSize;
		// we create an array of coefficients. The index correspond to a distance and for each distance we calculate a coefficient 
		final double[] coefTab = new double[halfWidth + 1];
		for(int i = 0; i <= halfWidth; i++) {
			coefTab[i] = Math.exp(-(Math.pow(((double) (i * binSize)), 2) / (2.0 * Math.pow((double) sigma, 2))));
		}
		// we gauss
		for(short i = 0; i < binList.size(); i++) {
			final List<Double> currentList = binList.get(i);
			Callable<List<Double>> currentThread = new Callable<List<Double>>() {			 	
				@Override
				public List<Double> call() throws Exception {
					List<Double> listToAdd = null;
					if ((currentList != null) && (currentList.size() != 0)) {
						listToAdd = ListFactory.createList(precision, currentList.size());
						for(int j = 0; j < currentList.size() && !stopped; j++) {
							if ((currentList.get(j) != 0) || (fillNullValues)) {
								// apply the array of coefficients centered on the current value to gauss
								double SumCoef = 0;
								double SumNormSignalCoef = 0;
								for (int k = -halfWidth; k <= halfWidth && !stopped; k++) {
									if((j + k >= 0) && ((j + k) < currentList.size()))  {
										int distance = Math.abs(k);
										if(currentList.get(j + k) != 0)  {
											SumCoef += coefTab[distance];
											SumNormSignalCoef += coefTab[distance] * currentList.get(j + k);
										}
									}
								}
								if(SumCoef == 0) {
									listToAdd.set(j, 0d);
								} else {
									listToAdd.set(j, SumNormSignalCoef / SumCoef);
								}
							} else {
								listToAdd.set(j, 0d);
							}
						}
					}
					op.notifyDone();
					return listToAdd;
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
		return "Operation: Gauss, Sigma = " + sigma + "bp";
	}
	
	
	@Override
	public int getStepCount() {
		return BinList.getCreationStepCount(binList.getBinSize()) + 1;
	}
	
	
	@Override
	public String getProcessingDescription() {
		return "Gaussing";
	}

	
	@Override
	public void stop() {
		this.stopped = true;
	}
}
