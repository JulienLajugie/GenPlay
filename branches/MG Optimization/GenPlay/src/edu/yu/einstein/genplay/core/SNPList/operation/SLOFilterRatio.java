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
package edu.yu.einstein.genplay.core.SNPList.operation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

import edu.yu.einstein.genplay.core.SNP;
import edu.yu.einstein.genplay.core.SNPList.SNPList;
import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.core.operationPool.OperationPool;



/**
 * Removes the SNPs with a ratio (first base count) on (second base count)
 * greater or smaller than a specified value
 * @author Julien Lajugie
 * @version 0.1
 */
public class SLOFilterRatio implements Operation<SNPList>{

	private final SNPList 	inputList;			// input SNP list
	private final double 	thresholdLow;		// remove SNPs with ratio smaller than this threshold
	private final double 	thresholdHigh;		// remove SNPs with ratio greater than this threshold
	private boolean			stopped = false;	// true if the operation must be stopped


	/**
	 * Creates an instance of {@link SLOFilterRatio}
	 * @param inputList input {@link SNPList} 
	 * @param thresholdLow the SNPs with a (first base count) on (second base count) ratio strictly smaller than this value are removed
	 * @param thresholdHigh the SNPs with a ratio strictly greater than this value are removed
	 */
	public SLOFilterRatio(SNPList inputList, double thresholdLow, double thresholdHigh) {
		this.inputList = inputList;
		this.thresholdHigh = thresholdHigh;
		this.thresholdLow = thresholdLow;
	}	


	@Override
	public SNPList compute() throws Exception {
		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<List<SNP>>> threadList = new ArrayList<Callable<List<SNP>>>();
		for (final List<SNP> currentList: inputList) {

			Callable<List<SNP>> currentThread = new Callable<List<SNP>>() {			
				@Override
				public List<SNP> call() throws Exception {
					List<SNP> resultList = null;
					if ((currentList != null) && (currentList.size() != 0)) {
						resultList = new ArrayList<SNP>();
						for (int i = 0; i < currentList.size() && !stopped; i++) {
							int currentFirstBaseCount = currentList.get(i).getFirstBaseCount();
							int currentSecondBaseCount = currentList.get(i).getSecondBaseCount();
							// we can't calculate the ratio if second base count = 0
							if (currentSecondBaseCount > 0) {
								double ratio = currentFirstBaseCount / (double) currentSecondBaseCount;
								if ((ratio >= thresholdLow) && 
										(ratio <= thresholdHigh)) {
									resultList.add(currentList.get(i));
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
		List<List<SNP>> result = op.startPool(threadList);
		if (result != null) {
			SNPList resultList = new SNPList(result);
			return resultList;
		} else {
			return null;
		}
	}


	@Override
	public String getDescription() {
		return "Operation: Filter ratio, high threshold = " + thresholdHigh + ", low threshold = " + thresholdLow;
	}


	@Override
	public String getProcessingDescription() {
		return "Filtering";
	}


	@Override
	public int getStepCount() {
		return 1;
	}


	@Override
	public void stop() {
		stopped = true;
	}
}
