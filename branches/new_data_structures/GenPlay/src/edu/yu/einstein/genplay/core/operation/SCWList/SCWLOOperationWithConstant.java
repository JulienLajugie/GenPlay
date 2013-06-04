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
import java.util.concurrent.Callable;

import edu.yu.einstein.genplay.core.manager.project.ProjectChromosomes;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.core.operationPool.OperationPool;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.enums.OperationWithConstant;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SCWList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SCWListBuilder;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.SimpleScoredChromosomeWindow;


/**
 * Adds a specified constant to the scores of each window of a {@link SimpleScoredChromosomeWindow}
 * @author Julien Lajugie
 */
public class SCWLOOperationWithConstant implements Operation<SCWList> {

	private final SCWList 				scwList;			// input list
	private final OperationWithConstant operation;			// operation type
	private final float 				constant;			// constant to add
	private final boolean 				applyToNullWindows; // apply to windows with a score of 0
	private boolean						stopped = false;	// true if the operation must be stopped


	/**
	 * Adds a specified constant to the scores of each window of a {@link SimpleScoredChromosomeWindow}
	 * @param scwList input list
	 * @param operation {@link OperationWithConstant}
	 * @param constant constant to add
	 * @param applyToNullWindows set to true to apply the operation to the windows with a score of 0
	 */
	public SCWLOOperationWithConstant(SCWList scwList, OperationWithConstant operation, float constant, boolean applyToNullWindows) {
		this.scwList = scwList;
		this.operation = operation;
		this.constant = constant;
		this.applyToNullWindows = applyToNullWindows;
	}


	@Override
	public SCWList compute() throws Exception {
		if (constant == 0) {
			return scwList;
		}

		ProjectChromosomes projectChromosomes = ProjectManager.getInstance().getProjectChromosomes();
		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<Void>> threadList = new ArrayList<Callable<Void>>();
		final SCWListBuilder resultListBuilder = new SCWListBuilder(scwList);

		for (final Chromosome chromosome: projectChromosomes) {
			final ListView<ScoredChromosomeWindow> currentList = scwList.get(chromosome);
			Callable<Void> currentThread = new Callable<Void>() {

				@Override
				public Void call() throws Exception {
					if (currentList != null) {
						int lastStop = 1;
						// we add a constant to each element
						for (int j = 0; (j < currentList.size()) && !stopped; j++) {
							int start = currentList.get(j).getStart();
							int stop = currentList.get(j).getStop();
							if ((start != lastStop) && applyToNullWindows) {
								resultListBuilder.addElementToBuild(chromosome, lastStop, start, computeScore(0));
							}
							float currentScore = currentList.get(j).getScore();
							if (applyToNullWindows || (currentScore != 0)) {
								resultListBuilder.addElementToBuild(chromosome, start, stop, computeScore(currentScore));
							}
							lastStop = stop;
						}
					}
					// tell the operation pool that a chromosome is done
					op.notifyDone();
					return null;
				}
			};

			threadList.add(currentThread);
		}
		op.startPool(threadList);
		return resultListBuilder.getSCWList();
	}


	/**
	 * @param score current score
	 * @return the score after applying the operation with constant
	 */
	private float computeScore(float score) {
		switch (operation) {
		case ADDITION:
			return score + constant;
		case INVERTION:
			if (score == 0) {
				return 0;
			}
			return constant / score;
		case MULTIPLICATION:
			return score * constant;
		case DIVISION:
			if (constant == 0) {
				return 0;
			}
			return score / constant;
		case SUBTRACTION:
			return score - constant;
		case UNIQUE_SCORE:
			return constant;
		default:
			return 0;
		}
	}


	@Override
	public String getDescription() {
		return "Operation: Operation With Constant, Operation = " + operation + ", Constant = " + constant;
	}


	@Override
	public String getProcessingDescription() {
		return "Computing  " + operation;
	}


	@Override
	public int getStepCount() {
		return 1 + scwList.getCreationStepCount();
	}


	@Override
	public void stop() {
		stopped = true;
	}
}
