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
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.SimpleScoredChromosomeWindow;


/**
 * Removes the values above and under specified thresholds
 * @author Julien Lajugie
 */
public class SCWLOFilterThreshold implements Operation<SCWList> {
	private final SCWList 	inputList; 		// input SCW list
	private final float 	lowThreshold;	// filters the values under this threshold
	private final float 	highThreshold;	// filters the values above this threshold
	private final boolean	isSaturation;	// true if we saturate, false if we remove the filtered values
	private boolean			stopped = false;// true if the operation must be stopped


	/**
	 * Creates an instance of {@link SCWLOFilterThreshold}
	 * @param inputList {@link SCWList} to filter
	 * @param lowThreshold filters the values under this threshold
	 * @param highThreshold filters the values above this threshold
	 * @param isSaturation true to saturate, false to remove the filtered values
	 */
	public SCWLOFilterThreshold(SCWList inputList, float lowThreshold, float highThreshold, boolean isSaturation) {
		this.inputList = inputList;
		this.lowThreshold = lowThreshold;
		this.highThreshold = highThreshold;
		this.isSaturation = isSaturation;
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
						for (int i = 0; (i < currentList.size()) && !stopped; i++) {
							double currentScore = currentList.get(i).getScore();
							if (currentScore != 0) {
								if (currentScore > highThreshold) {
									// if the score is greater than the high threshold
									if (isSaturation) {
										// set the value to high threshold (saturation)
										int start = currentList.get(i).getStart();
										int stop = currentList.get(i).getStop();
										resultListBuilder.addElementToBuild(chromosome, new SimpleScoredChromosomeWindow(start, stop, highThreshold));
									}
								} else if (currentScore < lowThreshold) {
									// if the score is smaller than the low threshold
									if (isSaturation) {
										// set the value to low threshold (saturation)
										int start = currentList.get(i).getStart();
										int stop = currentList.get(i).getStop();
										resultListBuilder.addElementToBuild(chromosome, new SimpleScoredChromosomeWindow(start, stop, lowThreshold));
									}
								} else {
									// if the score is between the two threshold
									resultListBuilder.addElementToBuild(chromosome, currentList.get(i));
								}
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
		String optionStr;
		if (isSaturation) {
			optionStr = ", option = saturation";
		} else {
			optionStr = ", option = remove";
		}
		return "Operation: Threshold Filter, minimum = " + lowThreshold + ", maximum = " + highThreshold + optionStr;
	}


	@Override
	public String getProcessingDescription() {
		return "Filtering";
	}


	@Override
	public int getStepCount() {
		return 1 + inputList.getCreationStepCount();
	}


	@Override
	public void stop() {
		stopped = true;
	}
}
