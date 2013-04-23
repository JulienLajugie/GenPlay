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

import edu.yu.einstein.genplay.core.manager.project.ProjectChromosome;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.core.operationPool.OperationPool;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SCWList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SCWListBuilder;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SimpleSCWList.SimpleSCWList;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;


/**
 * Removes the values between two specified thresholds
 * @author Julien Lajugie
 */
public class SCWLOFilterBandStop implements Operation<SCWList> {

	private final SCWList 	inputList;		// input SCW list
	private final float 	lowThreshold;	// low bound
	private final float 	highThreshold;	// high bound
	private boolean			stopped = false;// true if the operation must be stopped


	/**
	 * Creates an instance of {@link SCWLOFilterBandStop}
	 * @param inputList input {@link SCWList}
	 * @param lowThreshold low threshold
	 * @param highThreshold high threshold
	 */
	public SCWLOFilterBandStop(SCWList inputList, float lowThreshold, float highThreshold) {
		this.inputList = inputList;
		this.lowThreshold = lowThreshold;
		this.highThreshold = highThreshold;
	}


	@Override
	public SCWList compute() throws Exception {
		if (lowThreshold >= highThreshold) {
			throw new IllegalArgumentException("The high threshold must be greater than the low one");
		}

		ProjectChromosome projectChromosome = ProjectManager.getInstance().getProjectChromosome();
		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<Void>> threadList = new ArrayList<Callable<Void>>();
		final SCWListBuilder resultListBuilder = new SCWListBuilder(inputList);

		for (final Chromosome chromosome: projectChromosome) {
			final ListView<ScoredChromosomeWindow> currentList = inputList.get(chromosome);
			Callable<Void> currentThread = new Callable<Void>() {

				@Override
				public Void call() throws Exception {
					if ((currentList != null) && (currentList.size() != 0)) {
						for (int j = 0; (j < currentList.size()) && !stopped; j++) {
							float currentValue = currentList.get(j).getScore();
							if ((currentValue < lowThreshold) || (currentValue > highThreshold)) {
								resultListBuilder.addElementToBuild(chromosome, currentList.get(j));
							}
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
		return "Operation: Band-Stop Filter, Low Threshold = " + lowThreshold + ", High Threshold = " + highThreshold;
	}


	@Override
	public String getProcessingDescription() {
		return "Filtering";
	}


	@Override
	public int getStepCount() {
		return 1 + SimpleSCWList.getCreationStepCount(inputList.getSCWListType());
	}


	@Override
	public void stop() {
		stopped = true;
	}
}
