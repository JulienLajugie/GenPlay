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
import edu.yu.einstein.genplay.dataStructure.enums.ScorePrecision;
import edu.yu.einstein.genplay.dataStructure.enums.LogBase;
import edu.yu.einstein.genplay.dataStructure.list.arrayList.old.ListFactory;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.binList.BinList;
import edu.yu.einstein.genplay.util.Utils;



/**
 * Applies the function f(x)=log(x) to each score x of the {@link BinList}
 * @author Julien Lajugie
 * @version 0.1
 */
public class BLOLog implements Operation<BinList> {

	private final BinList 	binList;		// input binlist
	private final LogBase	logBase;		// base of the log
	private boolean			stopped = false;// true if the operation must be stopped
	
	
	/**
	 * Applies the function f(x)=log(x) to each score x of the {@link BinList}
	 * @param binList input {@link BinList}
	 * @param logBase base of the logarithm
	 */
	public BLOLog(BinList binList, LogBase logBase) {
		this.binList = binList;
		this.logBase = logBase;
	}
	
	
	@Override
	public BinList compute() throws InterruptedException, ExecutionException {
		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<List<Double>>> threadList = new ArrayList<Callable<List<Double>>>();
		final ScorePrecision precision = binList.getPrecision();

		for (short i = 0; i < binList.size(); i++) {
			final List<Double> currentList = binList.get(i);
			
			Callable<List<Double>> currentThread = new Callable<List<Double>>() {	
				@Override
				public List<Double> call() throws Exception {
					List<Double> resultList = null;
					if ((currentList != null) && (currentList.size() != 0)) {
						resultList = ListFactory.createList(precision, currentList.size());
						// We add a constant to each element
						for (int j = 0; j < currentList.size() && !stopped; j++) {
							// log is define on R+*
							if (currentList.get(j) > 0) {
								double resultValue = Utils.log(logBase, currentList.get(j));
								resultList.set(j, resultValue);
							} else if (currentList.get(j) == 0) {
								resultList.set(j, 0d);
							} else {
								// can't apply a log function on a negative or null numbers
								throw new ArithmeticException("Logarithm of a negative value not allowed");
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
		return "Operation: Log, Base = " + logBase;
	}
	
	
	@Override
	public int getStepCount() {
		return BinList.getCreationStepCount(binList.getBinSize()) + 1;
	}
	
	
	@Override
	public String getProcessingDescription() {
		return "Logging";
	}

	
	@Override
	public void stop() {
		this.stopped = true;
	}
}
