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
package edu.yu.einstein.genplay.core.operation.geneList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.core.operationPool.OperationPool;
import edu.yu.einstein.genplay.dataStructure.gene.Gene;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.geneList.GeneList;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;


/**
 * Searches the maximum value of the selected chromosomes of a specified {@link GeneList}
 * @author Julien Lajugie
 */
public class GLOMax implements Operation<Float> {

	private final GeneList 	geneList;		// input GeneList
	private final boolean[] chromoList;		// 1 boolean / chromosome.
	// each boolean sets to true means that the corresponding chromosome is selected
	private boolean			stopped = false;// true if the operation must be stopped


	/**
	 * Searches the maximum value of the selected chromosomes of a specified {@link GeneList}
	 * @param geneList input {@link GeneList}
	 * @param chromoList list of boolean. A boolean set to true means that the
	 * chromosome with the same index is going to be used for the calculation.
	 */
	public GLOMax(GeneList geneList, boolean[] chromoList) {
		this.geneList = geneList;
		this.chromoList = chromoList;
	}


	@Override
	public Float compute() throws InterruptedException, ExecutionException {
		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<Float>> threadList = new ArrayList<Callable<Float>>();
		for (int i = 0; i < geneList.size(); i++) {
			if (((chromoList == null) || ((i < chromoList.length) && (chromoList[i]))) && (geneList.get(i) != null)) {
				final ListView<Gene> currentList = geneList.get(i);

				Callable<Float> currentThread = new Callable<Float>() {
					@Override
					public Float call() throws Exception {
						// we set the max to the smallest float value
						float max = Float.NEGATIVE_INFINITY;
						for (int i = 0; (i < currentList.size()) && !stopped; i++) {
							Gene currentGene = currentList.get(i);
							if ((currentGene != null) && (currentGene.getExons() != null)) {
								for (ScoredChromosomeWindow currentExon: currentGene.getExons()) {
									Float currentScore = currentExon.getScore();
									if ((currentScore != 0) && (currentScore != Float.NaN)) {
										max = Math.max(max, currentScore);
									}
								}
							}
						}
						// tell the operation pool that a chromosome is done
						op.notifyDone();
						return max;
					}
				};

				threadList.add(currentThread);
			}
		}

		List<Float> result = op.startPool(threadList);
		if (result == null) {
			return null;
		}
		// we search for the max of the chromosome minimums
		float max = Float.NEGATIVE_INFINITY;
		for (Float currentMax: result) {
			max = Math.max(max, currentMax);
		}
		return max;
	}


	@Override
	public String getDescription() {
		return "Operation: Maximum";
	}


	@Override
	public String getProcessingDescription() {
		return "Searching Maximum";
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
