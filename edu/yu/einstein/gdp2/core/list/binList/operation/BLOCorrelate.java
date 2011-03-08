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

import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.operation.Operation;
import yu.einstein.gdp2.core.operationPool.OperationPool;
import yu.einstein.gdp2.exception.BinListDifferentWindowSizeException;


/**
 * Computes the correlation coefficients between two {@link BinList} for every chromosome as well as genome wide
 * @author Julien Lajugie
 * @version 0.1
 */
public class BLOCorrelate implements Operation<Double[]> {

	private final BinList 	binList1;		// input BinList
	private final BinList	binList2;		// input BinList
	private int[] 			counters;		// counters for the none-null value 
	private int 			counter = 0;	// counter for the none-null value
	private double[]		means1;			// chromosome averages of binList1
	private double			mean1 = 0;		// average of binList1
	private double[]		means2;			// chromosome averages of binList2
	private double			mean2 = 0;		// average of binList2
	private double[]		stdevs1;		// chromosome standard deviations of binList1 based on the chromosome average
	private double			stdev1 = 0;		// standard deviation of binList1
	private double[]		stddevtotals1;	// chromosome standard deviations of binList1 based on the total average
	private double[]		stdevs2;		// chromosome standard deviations of binList2 based on the chromosome average
	private double			stdev2 = 0;		// standard deviation of binList2
	private double[]		stddevtotals2;	// chromosome standard deviations of binList2 based on the total average
	private double[]		correlations;	// chromosome correlation coefficients
	private double 			correlation = 0;// correlation coefficient
	private boolean			stopped = false;// true if the operation must be stopped
	

	/**
	 * Computes the correlation coefficients between two {@link BinList} for every chromosome as well as genome wide
	 * @param binList1 1st input {@link BinList}
	 * @param binList2 2nd input {@link BinList}
	 */
	public BLOCorrelate(BinList binList1, BinList binList2) {
		this.binList1 = binList1;
		this.binList2 = binList2;
		counters = new int[binList1.size()];
		means1 = new double[binList1.size()];
		means2 = new double[binList2.size()];
		stdevs1 = new double[binList1.size()];
		stdevs2 = new double[binList2.size()];
		stddevtotals1 = new double[binList1.size()];
		stddevtotals2 = new double[binList2.size()];
		correlations = new double[binList1.size()];
	}


	@Override
	public Double[] compute() throws InterruptedException, ExecutionException, BinListDifferentWindowSizeException {
		try {
			computeMeans();
		} catch (InterruptedException e) {
			// it the computation of the averages had been interrupted we return null
			return null;
		}
		// if there is no none-null value we return null
		if (counter == 0) { 
			return null;
		}		

		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<Void>> threadList = new ArrayList<Callable<Void>>();
		for (int i = 0; i < binList1.size(); i++) {
			final List<Double> currentList1 = binList1.get(i);
			final List<Double> currentList2 = binList2.get(i);
			final int currentIndex = i;
			Callable<Void> currentThread = new Callable<Void>() {	
				@Override
				public Void call() throws Exception {
					if ((currentList1 != null) && (currentList2 != null)) {
						int j = 0;
						while ((j < currentList1.size()) && (j < currentList2.size())) {
							if ((currentList1.get(j) != 0) && (currentList2.get(j) != 0)) {
								stdevs1[currentIndex] += Math.pow(currentList1.get(j) - means1[currentIndex], 2);
								stdevs2[currentIndex] += Math.pow(currentList2.get(j) - means2[currentIndex], 2);
								stddevtotals1[currentIndex] += Math.pow(currentList1.get(j) - mean1, 2);
								stddevtotals2[currentIndex] += Math.pow(currentList2.get(j) - mean2, 2);
								correlations[currentIndex] += currentList1.get(j) * currentList2.get(j);
							}
							j++;
						}	
					}
					// tell the operation pool that a chromosome is done
					op.notifyDone();
					return null;
				}
			};

			threadList.add(currentThread);
		}		
		if (op.startPool(threadList) == null) {
			return null;
		}
		// we sum the chromosome results to have a genome wide result
		for (int i = 0; i < correlations.length; i++) {
			correlation += correlations[i];
			stdev1 += stddevtotals1[i];
			stdev2 += stddevtotals2[i];
			if (counters[i] != 0) {
				stdevs1[i] = Math.sqrt(stdevs1[i] / counters[i]);
				stdevs2[i] = Math.sqrt(stdevs2[i] / counters[i]);
			}
		}
		// compute the standard deviation
		stdev1 = Math.sqrt(stdev1 / counter);
		stdev2 = Math.sqrt(stdev2 / counter);
		// we compute the correlation 
		Double[] result = new Double[correlations.length + 1];
		for (int i = 0; i < correlations.length; i++) {
			if (counters[i] != 0) {
				result[i] = (correlations[i] - (counters[i] * means1[i] * means2[i])) / ((counters[i] - 1) * stdevs1[i] * stdevs2[i]);
			} else {
				result[i] = null;
			}
		}
		correlation = (correlation - (counter * mean1 * mean2)) / ((counter - 1) * stdev1 * stdev2);
		result[result.length - 1] = correlation;		
		return result;
	}


	/**
	 * Computes the means of the two BinLists for every chromosome as well as genome wide
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws BinListDifferentWindowSizeException
	 */
	private void computeMeans() throws InterruptedException, ExecutionException, BinListDifferentWindowSizeException {
		if (binList1.getBinSize() != binList2.getBinSize()) {
			throw new BinListDifferentWindowSizeException();
		}

		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<Void>> threadList = new ArrayList<Callable<Void>>();
		for (int i = 0; i < binList1.size(); i++) {
			final List<Double> currentList1 = binList1.get(i);
			final List<Double> currentList2 = binList2.get(i);
			final int currentIndex = i;
			Callable<Void> currentThread = new Callable<Void>() {	
				@Override
				public synchronized Void call() throws Exception {
					if ((currentList1 != null) && (currentList2 != null)) {
						int j = 0;
						// compute the average only when the two scores are not null
						while ((j < currentList1.size()) && (j < currentList2.size()) && !stopped) {
							if ((currentList1.get(j) != 0) && (currentList2.get(j) != 0)) {
								means1[currentIndex] += currentList1.get(j);
								means2[currentIndex] += currentList2.get(j);
								counters[currentIndex]++;							
							}							
							j++;
						}
					}

					// tell the operation pool that a chromosome is done
					op.notifyDone();
					return null;
				}
			};

			threadList.add(currentThread);
		}		

		if (op.startPool(threadList) == null) {
			throw new InterruptedException();
		}
		// we sum the chromosome results to have a genome wide result
		for (int i = 0; i < counters.length && !stopped; i++) {
			counter += counters[i];
			mean1 += means1[i];
			mean2 += means2[i];
			if (counters[i] != 0) {
				means1[i] /= counters[i]; 
				means2[i] /= counters[i];
			}
		}
		// if there is no none-null value we return 0
		if (counter != 0) {
			mean1 /= counter;
			mean2 /= counter;
		}
	}


	@Override
	public String getDescription() {
		return "Operation: Correlation Coefficient";
	}


	@Override
	public int getStepCount() {
		return 2;
	}


	@Override
	public String getProcessingDescription() {
		return "Computing Correlation";
	}

	
	@Override
	public void stop() {
		this.stopped = true;
	}
}
