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
import edu.yu.einstein.genplay.dataStructure.enums.Strand;
import edu.yu.einstein.genplay.dataStructure.gene.Gene;
import edu.yu.einstein.genplay.dataStructure.gene.SimpleGene;
import edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.geneListView.GeneListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.ListOfListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SCWList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.geneList.GeneList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.geneList.SimpleGeneList;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;
import edu.yu.einstein.genplay.util.ListView.SCWListViews;

/**
 * Finds islands in a specified {@link SCWList}.
 * An island is a region with windows having the same sign and border by windows with oposite sign.
 * @author Julien Lajugie
 */
public class FindIslands implements Operation<GeneList> {

	private final SCWList	inputList;			// input list
	private boolean			stopped = false;	// true if the operation must be stopped


	/**
	 * Creates an instance of {@link FindIslands}
	 * @param inputList {@link SCWList} with the island to find
	 */
	public FindIslands(SCWList inputList) {
		this.inputList = inputList;
	}


	@Override
	public GeneList compute() throws Exception {
		ProjectChromosomes projectChromosomes = ProjectManager.getInstance().getProjectChromosomes();
		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<Void>> threadList = new ArrayList<Callable<Void>>();
		ListViewBuilder<Gene> lvbPrototype = new GeneListViewBuilder();
		final ListOfListViewBuilder<Gene> resultListBuilder = new ListOfListViewBuilder<Gene>(lvbPrototype);

		for(final Chromosome currentChromosome : projectChromosomes) {
			final ListView<ScoredChromosomeWindow> currentList = inputList.get(currentChromosome);
			Callable<Void> currentThread = new Callable<Void>() {

				@Override
				public Void call() throws Exception {
					if (currentList.isEmpty()) {
						return null;
					}
					boolean wasPositive = currentList.get(0).getScore() > 0;
					int islandStart = currentList.get(0).getStart();
					int islandStop = currentList.get(0).getStop();
					int i = 1;
					while ((i < currentList.size()) && !stopped) {
						boolean isPostive = currentList.get(i).getScore() > 0;
						if (isPostive != wasPositive) {
							SimpleGene geneToAdd = new SimpleGene(null, Strand.FIVE, islandStart, islandStop, 1,SCWListViews.createGenericSCWListView(islandStart, islandStop, 1));
							resultListBuilder.addElementToBuild(currentChromosome, geneToAdd);
							wasPositive = isPostive;
							islandStart = currentList.get(i).getStart();
						}
						islandStop = currentList.get(i).getStop();
					}
					SimpleGene geneToAdd = new SimpleGene(null, Strand.FIVE, islandStart, islandStop, 1,SCWListViews.createGenericSCWListView(islandStart, islandStop, 1));
					resultListBuilder.addElementToBuild(currentChromosome, geneToAdd);
					op.notifyDone();
					return null;
				}
			};
			threadList.add(currentThread);
		}
		op.startPool(threadList);
		List<ListView<Gene>> data = resultListBuilder.getGenomicList();
		return new SimpleGeneList(data, null, null);
	}


	@Override
	public String getDescription() {
		return "Operation: Find Islands";
	}


	@Override
	public String getProcessingDescription() {
		return "Finding Islands";
	}


	@Override
	public int getStepCount() {
		return 1;
	}


	@Override
	public void stop() {
		stopped = true;

	}
}
