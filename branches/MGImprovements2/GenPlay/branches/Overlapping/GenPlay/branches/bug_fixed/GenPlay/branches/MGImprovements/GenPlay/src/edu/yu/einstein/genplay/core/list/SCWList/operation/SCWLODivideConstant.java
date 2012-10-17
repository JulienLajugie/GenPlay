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
package edu.yu.einstein.genplay.core.list.SCWList.operation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

import edu.yu.einstein.genplay.core.chromosomeWindow.ScoredChromosomeWindow;
import edu.yu.einstein.genplay.core.chromosomeWindow.SimpleScoredChromosomeWindow;
import edu.yu.einstein.genplay.core.list.SCWList.ScoredChromosomeWindowList;
import edu.yu.einstein.genplay.core.list.SCWList.SimpleScoredChromosomeWindowList;
import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.core.operationPool.OperationPool;



/**
 * Divides the scores of each window of a {@link SimpleScoredChromosomeWindow} by a constant
 * @author Julien Lajugie
 * @version 0.1
 */
public class SCWLODivideConstant implements Operation<ScoredChromosomeWindowList> {

	private final ScoredChromosomeWindowList 	scwList;	// input list
	private final double 						constant;	// constant of the division
	private boolean				stopped = false;// true if the operation must be stopped


	/**
	 * Divides the scores of each window of a {@link SimpleScoredChromosomeWindow} by a constant
	 * @param scwList input list
	 * @param constant constant to add
	 */
	public SCWLODivideConstant(ScoredChromosomeWindowList scwList, double constant) {
		this.scwList = scwList;
		this.constant = constant;
	}


	@Override
	public ScoredChromosomeWindowList compute() throws Exception {
		if (constant == 1) {
			return scwList.deepClone();
		} else if (constant == 0) {
			throw new ArithmeticException("Division By Zero");
		}

		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<List<ScoredChromosomeWindow>>> threadList = new ArrayList<Callable<List<ScoredChromosomeWindow>>>();

		for (short i = 0; i < scwList.size(); i++) {
			final List<ScoredChromosomeWindow> currentList = scwList.get(i);

			Callable<List<ScoredChromosomeWindow>> currentThread = new Callable<List<ScoredChromosomeWindow>>() {
				@Override
				public List<ScoredChromosomeWindow> call() throws Exception {
					List<ScoredChromosomeWindow> resultList = new ArrayList<ScoredChromosomeWindow>();
					if ((currentList != null) && (currentList.size() != 0)) {
						// We divide each element by a constant
						for (int j = 0; (j < currentList.size()) && !stopped; j++) {
							ScoredChromosomeWindow currentWindow = currentList.get(j);
							ScoredChromosomeWindow resultWindow = new SimpleScoredChromosomeWindow(currentWindow);
							resultWindow.setScore(currentWindow.getScore() / constant);
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
			ScoredChromosomeWindowList resultList = new SimpleScoredChromosomeWindowList(result);
			return resultList;
		} else {
			return null;
		}
	}


	@Override
	public String getDescription() {
		return "Operation: Divide by Constant, Constant = " + constant;
	}


	@Override
	public String getProcessingDescription() {
		return "Dividing by Constant";
	}


	@Override
	public int getStepCount() {
		return 1 + SimpleScoredChromosomeWindowList.getCreationStepCount();
	}


	@Override
	public void stop() {
		this.stopped = true;
	}
}
