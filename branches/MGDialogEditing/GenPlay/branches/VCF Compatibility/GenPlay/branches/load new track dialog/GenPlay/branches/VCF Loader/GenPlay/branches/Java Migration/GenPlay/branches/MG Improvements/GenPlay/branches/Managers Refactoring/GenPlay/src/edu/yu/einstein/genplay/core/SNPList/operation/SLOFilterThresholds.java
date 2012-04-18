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
 * Removes the SNPs where the first and second base counts are smaller than specified thresholds
 * @author Julien Lajugie
 * @version 0.1
 */
public class SLOFilterThresholds implements Operation<SNPList> {

	private final SNPList 	inputList;				// input list
	private final int 		firstBaseThreshold;		// first base count must be greater than this threshold
	private final int 		secondBaseThreshold;	// second base count must be greater than this threshold
	private boolean			stopped = false;		// true if the operation must be stopped
	
	
	/**
	 * Creates an instance of {@link SLOFilterThresholds}
	 * @param inputList input SNP list
	 * @param firstBaseThreshold first base count must be greater than this threshold
	 * @param secondBaseThreshold second base count must be greater than this threshold
	 */
	public SLOFilterThresholds(SNPList inputList, int firstBaseThreshold, int secondBaseThreshold) {
		this.inputList = inputList;
		this.firstBaseThreshold = firstBaseThreshold;
		this.secondBaseThreshold = secondBaseThreshold;
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
							if ((currentFirstBaseCount >= firstBaseThreshold) && 
									(currentSecondBaseCount >= secondBaseThreshold)) {
									resultList.add(currentList.get(i));
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
		return "Operation: Filter, First base filter = " + firstBaseThreshold + ", Second base filter = " + secondBaseThreshold;
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
		this.stopped = true;
	}
}
