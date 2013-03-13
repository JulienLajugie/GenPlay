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
package edu.yu.einstein.genplay.core.operation.SCWList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.core.operationPool.OperationPool;
import edu.yu.einstein.genplay.dataStructure.enums.LogBase;
import edu.yu.einstein.genplay.dataStructure.genomeList.SCWList.ScoredChromosomeWindowList;
import edu.yu.einstein.genplay.dataStructure.genomeList.SCWList.SimpleSCWList;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.SimpleScoredChromosomeWindow;



/**
 * Applies the function f(x)=log((x + damper) / (avg + damper)) to each score x of the {@link ScoredChromosomeWindowList}
 * @author Julien Lajugie
 * @version 0.1
 */
public class SCWLOLogOnAvgWithDamper implements Operation<ScoredChromosomeWindowList> {

	private final ScoredChromosomeWindowList 	scwList;	// input binlist
	private final LogBase						logBase;	// base of the log
	private final double						damper;		// damper
	private boolean				stopped = false;// true if the operation must be stopped


	/**
	 * Applies the function f(x)=log((x + damper) / (avg + damper)) to each score x of the {@link ScoredChromosomeWindowList}
	 * @param scwList input {@link ScoredChromosomeWindowList}
	 * @param logBase base of the logarithm
	 * @param damper a double value
	 */
	public SCWLOLogOnAvgWithDamper(ScoredChromosomeWindowList scwList, LogBase logBase, double damper) {
		this.scwList = scwList;
		this.logBase = logBase;
		this.damper = damper;
	}


	@Override
	public ScoredChromosomeWindowList compute() throws InterruptedException, ExecutionException, ArithmeticException {
		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<List<ScoredChromosomeWindow>>> threadList = new ArrayList<Callable<List<ScoredChromosomeWindow>>>();

		// compute log(average)
		final double mean = scwList.getAverage();
		final double logMean;
		// log is defined on R+*
		if ((mean + damper) > 0) {
			if (logBase == LogBase.BASE_E) {
				// the Math.log function return the natural log (no needs to change the base)
				logMean = Math.log(mean + damper);
			} else {
				// change of base: logb(x) = logk(x) / logk(b)
				logMean = Math.log(mean + damper) / Math.log(logBase.getValue());
			}
		} else {
			throw new ArithmeticException("Logarithm of a negative value not allowed");
		}

		for (short i = 0; i < scwList.size(); i++) {
			final List<ScoredChromosomeWindow> currentList = scwList.getView(i);

			Callable<List<ScoredChromosomeWindow>> currentThread = new Callable<List<ScoredChromosomeWindow>>() {
				@Override
				public List<ScoredChromosomeWindow> call() throws Exception {
					List<ScoredChromosomeWindow> resultList = new ArrayList<ScoredChromosomeWindow>();
					if ((currentList != null) && (currentList.size() != 0)) {
						// We log each element
						for (int j = 0; (j < currentList.size()) && !stopped; j++) {
							ScoredChromosomeWindow currentWindow = currentList.get(j);
							ScoredChromosomeWindow resultWindow = new SimpleScoredChromosomeWindow(currentWindow);
							// log is define on R+*
							if (currentWindow.getScore() > 0) {
								double resultValue;
								if (logBase == LogBase.BASE_E) {
									// the Math.log function return the natural log (no needs to change the base)
									resultValue = Math.log(currentWindow.getScore() + damper) - logMean;
								} else {
									// change of base: logb(x) = logk(x) / logk(b)
									resultValue = (Math.log(currentWindow.getScore() + damper) / Math.log(logBase.getValue())) - logMean;
								}
								resultWindow.setScore(resultValue);
							} else if (currentWindow.getScore() == 0) {
								resultWindow.setScore(0d);
							} else {
								// can't apply a log function on a negative or null numbers
								throw new ArithmeticException("Logarithm of a negative value not allowed");
							}
							resultList.add(resultWindow);
						}
					}
					// tell the operation pool that a chromosome is done
					op.notifyDone();
					return resultList;
				}
			};

			threadList.add(currentThread);
		}
		List<List<ScoredChromosomeWindow>> result = op.startPool(threadList);
		if (result != null) {
			ScoredChromosomeWindowList resultList = new SimpleSCWList(result);
			return resultList;
		} else {
			return null;
		}
	}


	@Override
	public String getDescription() {
		return "Operation: " +  logBase + " on Average, with damper - f(x) = log((x + " + damper + ") / (average + " + damper + "))";
	}


	@Override
	public int getStepCount() {
		return 1 + SimpleSCWList.getCreationStepCount();
	}


	@Override
	public String getProcessingDescription() {
		return "Logging";
	}


	@Override
	public void stop() {
		stopped = true;
	}
}
