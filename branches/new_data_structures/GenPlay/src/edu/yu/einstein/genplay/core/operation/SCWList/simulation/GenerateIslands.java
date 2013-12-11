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
package edu.yu.einstein.genplay.core.operation.SCWList.simulation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

import edu.yu.einstein.genplay.core.manager.project.ProjectChromosomes;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.core.operationPool.OperationPool;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.enums.SCWListType;
import edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.SCWListView.mask.MaskListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SCWList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SimpleSCWList.SimpleSCWList;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;

/**
 * Generates the island used during the simulation
 * @author Julien Lajugie
 */
public class GenerateIslands implements Operation<SCWList> {

	private final int 	stepSize;		// size between two islands start positions
	private final int 	islandSize;		// size of the islands
	private boolean		stopped = false;// true if the operation must be stopped


	/**
	 * Creates an instance of {@link GenerateIslands}
	 * @param stepSize size between two islands start positions
	 * @param islandSize size of the islands
	 */
	public GenerateIslands(int stepSize, int islandSize) {
		this.stepSize = stepSize;
		this.islandSize = islandSize;
	}


	@Override
	public SCWList compute() throws Exception {
		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<ListView<ScoredChromosomeWindow>>> threadList = new ArrayList<Callable<ListView<ScoredChromosomeWindow>>>();
		ProjectChromosomes projectChromosomes = ProjectManager.getInstance().getProjectChromosomes();

		for (final Chromosome currentChr: projectChromosomes) {
			Callable<ListView<ScoredChromosomeWindow>> currentThread = new Callable<ListView<ScoredChromosomeWindow>>() {
				@Override
				public ListView<ScoredChromosomeWindow> call() throws Exception {
					MaskListViewBuilder lvBuilder = new MaskListViewBuilder();
					int islandStart = 1;
					int islandStop = islandStart + islandSize;
					while ((islandStop < currentChr.getLength()) && !stopped) {
						lvBuilder.addElementToBuild(islandStart, islandStop);
						islandStart += stepSize;
						islandStop = islandStart + islandSize;
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
		return "Operation: Generate Islands";
	}


	@Override
	public String getProcessingDescription() {
		return "Generating Islands";
	}


	@Override
	public int getStepCount() {
		return 1 + SimpleSCWList.getCreationStepCount(SCWListType.MASK);
	}


	@Override
	public void stop() {
		stopped = true;
	}
}
