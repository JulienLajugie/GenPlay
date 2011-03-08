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
package yu.einstein.gdp2.core.list.binList.operation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import yu.einstein.gdp2.core.enums.DataPrecision;
import yu.einstein.gdp2.core.list.arrayList.ListFactory;
import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.operation.Operation;
import yu.einstein.gdp2.core.operationPool.OperationPool;


/**
 * Applies a Loess regression on the BinList and returns the result in a new BinList.
 * @author Julien Lajugie
 * @version 0.1
 */
public class BLOLoessRegression implements Operation<BinList> {

	private final BinList 	binList;			// input list
	private final int 		halfMovingWindow;	// half size of the moving window in bp
	private final boolean	fillNullValues; 	// true to fill the null values
	private boolean			stopped = false;	// true if the operation must be stopped


	/**
	 * Creates an instance of {@link BLOLoessRegression}
	 * Applies a Loess regression on the BinList and returns the result in a new BinList.
	 * @param binList input {@link BinList}
	 * @param halfMovingWindow half size of the moving window in bp
	 * @param fillNullValues set to true to fill the null values
	 */
	public BLOLoessRegression(BinList binList, int halfMovingWindow, boolean fillNullValues) {
		this.binList = binList;
		this.halfMovingWindow = halfMovingWindow;
		this.fillNullValues = fillNullValues;
	}


	@Override
	public BinList compute() throws InterruptedException, ExecutionException {
		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<List<Double>>> threadList = new ArrayList<Callable<List<Double>>>();
		final DataPrecision precision = binList.getPrecision();
		final int binSize =  binList.getBinSize();
		final int halfWidth = 2 * halfMovingWindow / binSize;		
		// we create an array of coefficients. The index correspond to a distance and for each distance we calculate a coefficient 
		final double[] weights = new double[halfWidth + 1];
		for(int i = 0; i <= halfWidth; i++) {
			weights[i] = Math.pow(1d - Math.pow(i / (double) halfWidth,  3d), 3d);
		}
		// we compute the Loess regression
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
								double sumWts = 0;
								double sumWtX = 0;
								double sumWtX2 = 0;
								double sumWtY = 0;
								double sumWtXY = 0;
								for (int k = -halfWidth; k <= halfWidth && !stopped; k++) {
									int movingX = j + k; // x coordinate of the current point in the moving window
									if((movingX >= 0) && (movingX < currentList.size()))  {
										int distance = Math.abs(k);
										if(currentList.get(j + k) != 0)  {
											sumWts += weights[distance];
											sumWtX += movingX * weights[distance];
											sumWtX2 += (movingX ^ 2) * weights[distance];											
											sumWtY += currentList.get(movingX) * weights[distance];
											sumWtXY += movingX * currentList.get(movingX) * weights[distance];										
										}
									}
								}
								double denom = (sumWts * sumWtX2) - Math.pow(sumWtX, 2);
								if(denom == 0) {
									listToAdd.set(j, 0d);
								} else {
									double WLRSlope = (sumWts * sumWtXY - sumWtX * sumWtY) / denom;
									double WLRIntercept = (sumWtX2 * sumWtY - sumWtX * sumWtXY) / denom;
									double yLoess = WLRSlope * j + WLRIntercept;									
									listToAdd.set(j, yLoess);
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
		return "Operation: Loess Regression, half moving window size = " + halfMovingWindow + "bp";
	}


	@Override
	public int getStepCount() {
		return BinList.getCreationStepCount(binList.getBinSize()) + 1;
	}


	@Override
	public String getProcessingDescription() {
		return "Computing Loess Regression";
	}


	@Override
	public void stop() {
		this.stopped = true;
	}
}
