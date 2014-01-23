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


/**
 * Counts the number of exons on the specified chromosomes
 * @author Julien Lajugie
 * @author Nicolas Fourel
 * @version 0.1
 */
public class GLOCountExons implements Operation<Long> {

	private final GeneList 	geneList;		// input GeneList
	private final boolean[] chromoList;		// 1 boolean / chromosome.
	// each boolean sets to true means that the corresponding chromosome is selected
	private boolean			stopped = false;// true if the operation must be stopped


	/**
	 * Creates an instance of {@link GLOCountExons}.
	 * Counts the exons on the selected chromosomes of the {@link GeneList}.
	 * @param geneList input {@link GeneList}
	 * @param chromoList list of boolean. A boolean set to true means that the
	 * chromosome with the same index is going to be used for the calculation.
	 */
	public GLOCountExons(GeneList geneList, boolean[] chromoList) {
		this.geneList = geneList;
		this.chromoList = chromoList;
	}



	@Override
	public Long compute() throws InterruptedException, ExecutionException {
		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<Long>> threadList = new ArrayList<Callable<Long>>();
		for (int i = 0; i < geneList.size(); i++) {
			if (((chromoList == null) || ((i < chromoList.length) && (chromoList[i]))) && (geneList.get(i) != null)) {
				final ListView<Gene> currentList = geneList.get(i);
				Callable<Long> currentThread = new Callable<Long>() {
					@Override
					public Long call() throws Exception {
						if (currentList == null) {
							return null;
						}
						Long result = 0l;
						for (int j = 0; (j < currentList.size()) && !stopped; j++) {
							Gene currentGene = currentList.get(j);
							if ((currentGene != null) && (currentGene.getExons() != null) && (currentGene.getExons().size() > 0)) {
								result += currentGene.getExons().size();
							}
						}
						// tell the operation pool that a chromosome is done
						op.notifyDone();
						return result;
					}
				};
				threadList.add(currentThread);
			}
		}
		List<Long> result = op.startPool(threadList);
		if (result == null) {
			return null;
		} else {
			long exonCount = 0;
			for (Long chromoCount: result) {
				exonCount += chromoCount;
			}
			return exonCount;
		}
	}


	@Override
	public String getDescription() {
		return "Operation: Count Exons";
	}


	@Override
	public String getProcessingDescription() {
		return "Counting Exons";
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
