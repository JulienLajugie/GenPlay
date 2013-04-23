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
import edu.yu.einstein.genplay.dataStructure.enums.LogBase;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SCWList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SCWListBuilder;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SimpleSCWList.SimpleSCWList;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.SimpleScoredChromosomeWindow;


/**
 * Applies the function f(x)=log(x) to each score x of the {@link SCWList}
 * @author Julien Lajugie
 */
public class SCWLOLog implements Operation<SCWList> {

	private final SCWList 	scwList;			// input list
	private final LogBase	logBase;			// base of the log
	private boolean			stopped = false;	// true if the operation must be stopped


	/**
	 * Creates an instance of {@link SCWLOLog}
	 * @param scwList input list
	 * @param logBase base of the log
	 */
	public SCWLOLog(SCWList scwList, LogBase logBase) {
		this.scwList = scwList;
		this.logBase = logBase;
	}


	@Override
	public SCWList compute() throws Exception {
		ProjectChromosome projectChromosome = ProjectManager.getInstance().getProjectChromosome();
		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<Void>> threadList = new ArrayList<Callable<Void>>();
		final SCWListBuilder resultListBuilder = new SCWListBuilder(scwList);

		for (final Chromosome chromosome: projectChromosome) {
			final ListView<ScoredChromosomeWindow> currentList = scwList.get(chromosome);
			Callable<Void> currentThread = new Callable<Void>() {

				@Override
				public Void call() throws Exception {
					if (currentList != null) {
						// We log each element
						for (int j = 0; (j < currentList.size()) && !stopped; j++) {
							int start = currentList.get(j).getStart();
							int stop = currentList.get(j).getStop();
							float score = currentList.get(j).getScore();
							// log is define on R+*
							if (score > 0) {
								if (logBase == LogBase.BASE_E) {
									// the Math.log function return the natural log (no needs to change the base)
									score = (float) Math.log(score);
								} else {
									// change of base: logb(x) = logk(x) / logk(b)
									score = (float) (Math.log(score) / Math.log(logBase.getValue()));
								}
							} else if (score < 0) {
								// can't apply a log function on a negative or null numbers
								throw new ArithmeticException("Logarithm of a negative value not allowed");
							}
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
		return "Operation: Log, Base = " + logBase;
	}


	@Override
	public String getProcessingDescription() {
		return "Logging";
	}


	@Override
	public int getStepCount() {
		return 1 + SimpleSCWList.getCreationStepCount(scwList.getSCWListType());
	}


	@Override
	public void stop() {
		stopped = true;
	}
}
