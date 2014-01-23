/*******************************************************************************
 * GenPlay, Einstein Genome Analyzer
 * Copyright (C) 2009, 2014 Albert Einstein College of Medicine
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * Authors: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *          Nicolas Fourel <nicolas.fourel@einstein.yu.edu>
 *          Eric Bouhassira <eric.bouhassira@einstein.yu.edu>
 * 
 * Website: <http://genplay.einstein.yu.edu>
 ******************************************************************************/
package edu.yu.einstein.genplay.core.operation.SCWList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;

import edu.yu.einstein.genplay.core.manager.project.ProjectChromosomes;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.core.operationPool.OperationPool;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.enums.ScoreOperation;
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
 * Defines regions as "islands" of non zero value bins
 * separated by more than a specified number of zero value bins.
 * Computes the average on these regions.
 * Returns a new {@link GeneList} with the defined regions having their average/max/sum as a score
 * @author Chirag Gorasia
 */
public class SCWLOTransfragGeneList implements Operation<GeneList> {

	private final SCWList 			scwList;			// input list
	private final int 				zeroSCWGap;			// minimum size of the gap separating two intervals
	private final ScoreOperation 	operation;			// operation to use to compute the score of the intervals
	private boolean					stopped = false;	// true if the operation must be stopped



	/**
	 * Creates an instance of {@link SCWLOTransfrag}
	 * @param scwList input list
	 * @param zeroSCWGap minimum size of the gap separating two intervals
	 * @param operation operation to use to compute the score of the intervals
	 */
	public SCWLOTransfragGeneList(SCWList scwList, int zeroSCWGap, ScoreOperation operation) {
		this.scwList = scwList;
		this.zeroSCWGap = zeroSCWGap;
		this.operation = operation;
	}


	@Override
	public GeneList compute() throws Exception {
		ProjectChromosomes projectChromosomes = ProjectManager.getInstance().getProjectChromosomes();
		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<Void>> threadList = new ArrayList<Callable<Void>>();
		ListViewBuilder<Gene> builderPrototype = new GeneListViewBuilder();
		final ListOfListViewBuilder<Gene> resultListBuilder = new ListOfListViewBuilder<Gene>(builderPrototype);

		for (final Chromosome chromosome: projectChromosomes) {
			final ListView<ScoredChromosomeWindow> currentList = scwList.get(chromosome);
			Callable<Void> currentThread = new Callable<Void>() {

				@Override
				public Void call() throws Exception {
					Gene newGene;

					if ((currentList != null) && (currentList.size() != 0)) {
						int geneCounter = 1;
						int j = 0;
						while ((j < currentList.size()) && !stopped) {
							// skip zero values
							while ((j < currentList.size()) && (currentList.get(j).getScore() == 0) &&!stopped) {
								j++;
							}
							int regionStartIndex = j;
							int regionStopIndex = regionStartIndex;
							ListView<ScoredChromosomeWindow> exons;
							int gapSize = 0;
							// a region stops when there is maxZeroWindowGap consecutive zero bins
							while (((j + 1) < currentList.size()) && (gapSize <= zeroSCWGap) && !stopped) {
								regionStopIndex = j;
								if (currentList.get(j + 1).getScore() == 0) {
									gapSize += currentList.get(j + 1).getSize();
								} else if (currentList.get(j + 1).getStart() != currentList.get(j).getStop()) {
									gapSize += currentList.get(j + 1).getStart() - currentList.get(j).getStop();
								} else {
									gapSize = 0;
								}
								j++;
							}
							j = regionStopIndex;
							if (regionStopIndex >= currentList.size()) {
								regionStopIndex = currentList.size()-1;
							}
							if (regionStopIndex >= regionStartIndex) {
								float regionScore = 0;
								if (operation == ScoreOperation.AVERAGE) {
									// all the windows of the region are set with the average value on the region
									regionScore = (float) SCWListViews.average(currentList, regionStartIndex, regionStopIndex);
								} else if (operation == ScoreOperation.MAXIMUM) {
									// all the windows of the region are set with the max value on the region
									regionScore = SCWListViews.maxNoZero(currentList, regionStartIndex, regionStopIndex);
								} else {
									// all the windows of the region are set with the sum value on the region
									regionScore = (float) SCWListViews.sum(currentList, regionStartIndex, regionStopIndex);
								}
								int regionStart = currentList.get(regionStartIndex).getStart();
								int regionStop = currentList.get(regionStopIndex).getStop();
								exons = SCWListViews.createGenericSCWListView(regionStart, regionStop, regionScore);
								newGene = new SimpleGene(chromosome.getName() + "." + Integer.toString(geneCounter++), Strand.get('+'), regionStart, regionStop, regionScore, exons);
								resultListBuilder.addElementToBuild(chromosome, newGene);
							}
							j++;
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
		return new SimpleGeneList(resultListBuilder.getGenomicList(), null, null);
	}


	@Override
	public String getDescription() {
		return "Operation: Transfrag, Gap Size = " + zeroSCWGap + " Zero Value Successive Bins";
	}


	@Override
	public String getProcessingDescription() {
		return "Computing Transfrag";
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
