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
package edu.yu.einstein.genplay.core.operation.geneList;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.core.operationPool.OperationPool;
import edu.yu.einstein.genplay.dataStructure.gene.Gene;
import edu.yu.einstein.genplay.dataStructure.gene.SimpleGene;
import edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.geneListView.GeneListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.geneList.GeneList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.geneList.SimpleGeneList;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListViewBuilder;


/**
 * Removes the genes with an overall RPKM between two specified thresholds
 * @author Julien Lajugie
 */
public class GLOFilterBandStop implements Operation<GeneList> {

	private final GeneList 			geneList;			// input GeneList
	private final float 			lowThreshold;		// low bound
	private final float 			highThreshold;		// high bound
	private boolean					stopped = false;	// true if the operation must be stopped


	/**
	 * Creates an instance of {@link GLOFilterBandStop}
	 * @param geneList input {@link GeneList}
	 * @param lowThreshold low threshold
	 * @param highThreshold high threshold
	 */
	public GLOFilterBandStop(GeneList geneList, float lowThreshold, float highThreshold) {
		this.geneList = geneList;
		this.lowThreshold = lowThreshold;
		this.highThreshold = highThreshold;
	}


	@Override
	public GeneList compute() throws Exception {
		if (lowThreshold >= highThreshold) {
			throw new IllegalArgumentException("The high threshold must be greater than the low one");
		}

		final OperationPool op = OperationPool.getInstance();
		final List<Callable<ListView<Gene>>> threadList = new ArrayList<Callable<ListView<Gene>>>();
		for (final ListView<Gene> currentList: geneList) {

			Callable<ListView<Gene>> currentThread = new Callable<ListView<Gene>>() {
				@Override
				public ListView<Gene> call() throws Exception {
					ListViewBuilder<Gene> resultLVBuilder = new GeneListViewBuilder();
					if ((currentList != null) && (currentList.size() != 0)) {
						for (int j = 0; (j < currentList.size()) && !stopped; j++) {
							Float currentValue = currentList.get(j).getScore();
							if ((currentValue == null) || ((currentValue < lowThreshold) && (currentValue > highThreshold))) {
								Gene geneToAdd = new SimpleGene(currentList.get(j));
								resultLVBuilder.addElementToBuild(geneToAdd);
							}
						}
					}
					// tell the operation pool that a chromosome is done
					op.notifyDone();
					return resultLVBuilder.getListView();
				}
			};

			threadList.add(currentThread);
		}
		List<ListView<Gene>> result = op.startPool(threadList);
		if (result != null) {
			GeneList resultList = new SimpleGeneList(result, geneList.getGeneScoreType(), geneList.getGeneDBURL());
			return resultList;
		} else {
			return null;
		}
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
		return 1;
	}


	@Override
	public void stop() {
		stopped = true;
	}
}
