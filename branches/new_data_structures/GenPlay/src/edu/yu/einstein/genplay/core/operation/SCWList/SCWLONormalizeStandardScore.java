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
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SCWList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SCWListBuilder;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.SimpleScoredChromosomeWindow;


/**
 * Computes a Standard Score normalization on a {@link SCWList}
 * @author Julien Lajugie
 */
public class SCWLONormalizeStandardScore implements Operation<SCWList> {

	private final SCWList 					scwList;			// input list
	private final SCWLOAverage 				avgOp;				// average
	private final SCWLOStandardDeviation 	stdevOp;			// standard deviation
	private boolean							stopped = false;	// true if the operation must be stopped


	/**
	 * Creates an instance of {@link SCWLONormalizeStandardScore}
	 * @param scwList input list
	 */
	public SCWLONormalizeStandardScore(SCWList scwList) {
		this.scwList = scwList;
		avgOp = new SCWLOAverage(scwList, null);
		stdevOp = new SCWLOStandardDeviation(scwList, null);
	}


	@Override
	public SCWList compute() throws Exception {
		// compute average
		final double avg = avgOp.compute();
		// compute standard deviation
		final double stdev = stdevOp.compute();

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
						for (int j = 0; (j < currentList.size()) && !stopped; j++) {
							int start = currentList.get(j).getStart();
							int stop = currentList.get(j).getStop();
							// apply the standard score formula: (x - avg) / stdev
							float score = (float) ((currentList.get(j).getScore() - avg) / stdev);
							resultListBuilder.addElementToBuild(chromosome, new SimpleScoredChromosomeWindow(start, stop, score));
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


	@Override
	public String getDescription() {
		return "Operation: Normalize, Standard Score";
	}


	@Override
	public String getProcessingDescription() {
		return "Normalizing";
	}


	@Override
	public int getStepCount() {
		return 1 + avgOp.getStepCount() + stdevOp.getStepCount() + scwList.getCreationStepCount();
	}


	@Override
	public void stop() {
		stopped = true;
	}
}
