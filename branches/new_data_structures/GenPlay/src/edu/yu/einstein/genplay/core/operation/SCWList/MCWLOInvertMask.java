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

import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.core.operationPool.OperationPool;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.SCWListView.mask.MaskListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SCWList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SimpleSCWList.SimpleSCWList;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;



/**
 * Reverts a mask reverting mask windows by white spaces and white spaces by mask windows.
 * @author Nicolas Fourel
 * @author Julien Lajugie
 */
public class MCWLOInvertMask implements Operation<SCWList> {

	private final SCWList 	scwList;		// input list
	private boolean			stopped = false;// true if the operation must be stopped


	/**
	 * Constructor of {@link MCWLOInvertMask}
	 * @param scwList input mask list
	 */
	public MCWLOInvertMask(SCWList scwList) {
		this.scwList = scwList;
	}


	@Override
	public SCWList compute() throws Exception {
		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<ListView<ScoredChromosomeWindow>>> threadList = new ArrayList<Callable<ListView<ScoredChromosomeWindow>>>();

		for (short i = 0; i < scwList.size(); i++) {
			final ListView<ScoredChromosomeWindow> currentList = scwList.get(i);
			final Chromosome currentChromosome = ProjectManager.getInstance().getProjectChromosomes().get(i);

			Callable<ListView<ScoredChromosomeWindow>> currentThread = new Callable<ListView<ScoredChromosomeWindow>>() {
				@Override
				public ListView<ScoredChromosomeWindow> call() throws Exception {
					MaskListViewBuilder lvBuilder = new MaskListViewBuilder();
					if ((currentList != null) && (currentList.size() != 0)) {
						int currentPosition = 0;
						for (int j = 0; (j < currentList.size()) && !stopped; j++) {
							ScoredChromosomeWindow currentWindow = currentList.get(j);
							int resultStart = currentPosition;
							int resultStop = currentWindow.getStart();
							if (resultStart < resultStop) {
								lvBuilder.addElementToBuild(resultStart, resultStop);
							}
							currentPosition = currentWindow.getStop();
						}

						// Insert the last position
						int resultStart = currentPosition;
						int resultStop = currentChromosome.getLength();
						if (resultStart < resultStop) {
							lvBuilder.addElementToBuild(resultStart, resultStop);
						}
					}
					// tell the operation pool that a chromosome is done
					op.notifyDone();
					return lvBuilder.getListView();
				}
			};

			threadList.add(currentThread);
		}
		List<ListView<ScoredChromosomeWindow>> result = op.startPool(threadList);
		if (result != null) {
			SCWList resultList = new SimpleSCWList(result);
			return resultList;
		} else {
			return null;
		}
	}


	@Override
	public String getDescription() {
		return "Operation: Invert Mask";
	}


	@Override
	public String getProcessingDescription() {
		return "Inverting Mask";
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
