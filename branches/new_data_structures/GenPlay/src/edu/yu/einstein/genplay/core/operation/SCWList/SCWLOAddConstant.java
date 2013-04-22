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

import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.core.operationPool.OperationPool;
import edu.yu.einstein.genplay.dataStructure.enums.SCWListType;
import edu.yu.einstein.genplay.dataStructure.enums.ScorePrecision;
import edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.SCWListView.generic.GenericSCWListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SCWList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SimpleSCWList;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.SimpleScoredChromosomeWindow;


/**
 * Adds a specified constant to the scores of each window of a {@link SimpleScoredChromosomeWindow}
 * @author Julien Lajugie
 */
public class SCWLOAddConstant implements Operation<SCWList> {

	private final SCWList 	scwList;		// input list
	private final float 	constant;		// constant to add
	private boolean			stopped = false;// true if the operation must be stopped


	/**
	 * Adds a specified constant to the scores of each window of a {@link SimpleScoredChromosomeWindow}
	 * @param scwList input list
	 * @param constant constant to add
	 */
	public SCWLOAddConstant(SCWList scwList, float constant) {
		this.scwList = scwList;
		this.constant = constant;
	}


	@Override
	public SCWList compute() throws Exception {
		if (constant == 0) {
			return scwList;
		}

		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<ListView<ScoredChromosomeWindow>>> threadList = new ArrayList<Callable<ListView<ScoredChromosomeWindow>>>();
		final ScorePrecision precision = scwList.getScorePrecision();
		for (short i = 0; i < scwList.size(); i++) {
			final ListView<ScoredChromosomeWindow> currentList = scwList.get(i);

			Callable<ListView<ScoredChromosomeWindow>> currentThread = new Callable<ListView<ScoredChromosomeWindow>>() {
				@Override
				public ListView<ScoredChromosomeWindow> call() throws Exception {
					GenericSCWListViewBuilder resultListBuilder = new GenericSCWListViewBuilder(precision);
					if ((currentList != null) && (currentList.size() != 0)) {
						// We add a constant to each element
						for (int j = 0; (j < currentList.size()) && !stopped; j++) {
							int start = currentList.get(j).getStart();
							int stop = currentList.get(j).getStop();
							float score = currentList.get(j).getScore() + constant;
							resultListBuilder.addElementToBuild(start, stop, score);
						}
					}
					// tell the operation pool that a chromosome is done
					op.notifyDone();
					return resultListBuilder.getListView();
				}
			};

			threadList.add(currentThread);
		}
		List<ListView<ScoredChromosomeWindow>> result = op.startPool(threadList);
		if (result != null) {
			SCWList resultList = new SimpleSCWList(result, SCWListType.GENERIC, precision);
			return resultList;
		} else {
			return null;
		}
	}


	@Override
	public String getDescription() {
		return "Operation: Add Constant, Constant = " + constant;
	}


	@Override
	public String getProcessingDescription() {
		return "Adding Constant";
	}


	@Override
	public int getStepCount() {
		return 1 + SimpleSCWList.getCreationStepCount(SCWListType.GENERIC);
	}


	@Override
	public void stop() {
		stopped = true;
	}
}
