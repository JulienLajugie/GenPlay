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
package edu.yu.einstein.genplay.core.list.geneList.operation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import edu.yu.einstein.genplay.core.gene.Gene;
import edu.yu.einstein.genplay.core.list.GenomicDataList;
import edu.yu.einstein.genplay.core.list.geneList.GeneList;
import edu.yu.einstein.genplay.core.list.geneList.GeneListFactory;
import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.core.operationPool.OperationPool;


/**
 * Indexes the score values of a {@link GenomicDataList} of genes
 * @author Julien Lajugie
 * @version 0.1
 */
public class GLOIndexScores implements Operation<GenomicDataList<Gene>> {

	private final GenomicDataList<Gene> 	geneList;		// input list of genes
	private boolean							stopped = false;// true if the operation must be stopped


	/**
	 * Indexes the score values of a {@link GenomicDataList} of genes
	 * @param geneList input list of genes
	 */
	public GLOIndexScores(GenomicDataList<Gene> geneList) {
		this.geneList = geneList;
	}


	@Override
	public GenomicDataList<Gene> compute() throws InterruptedException, ExecutionException {
		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<List<Gene>>> threadList = new ArrayList<Callable<List<Gene>>>();
		// compute the distance between the min and the max
		final double min = new GLOMin(geneList, null).compute();
		double max = new GLOMax(geneList, null).compute();
		double distanceMinMax = max - min;
		final double indexFactor = 1000d / distanceMinMax;

		for (int i = 0; i < geneList.size(); i++) {
			final List<Gene> currentList = geneList.get(i);

			Callable<List<Gene>> currentThread = new Callable<List<Gene>>() {
				@Override
				public List<Gene> call() throws Exception {
					if (currentList == null) {
						return null;
					}
					List<Gene> resultList = new ArrayList<Gene>();
					for (int i = 0; (i < currentList.size()) && !stopped; i++) {
						Gene currentGene = currentList.get(i);
						if (currentGene != null) {
							Gene copyCurrentGene = new Gene(currentGene);
							if (copyCurrentGene.getExonScores() != null){
								for(int j = 0; j < copyCurrentGene.getExonScores().length; j++) {
									double score = (copyCurrentGene.getExonScores()[j] - min) * indexFactor;
									score = Math.max(0, score);
									score = Math.min(1000, score);
									copyCurrentGene.getExonScores()[j] = score;
								}
							}
							resultList.add(copyCurrentGene);
						}
					}
					// tell the operation pool that a chromosome is done
					op.notifyDone();
					return resultList;
				}
			};

			threadList.add(currentThread);
		}
		List<List<Gene>> result = op.startPool(threadList);
		if (result == null) {
			return null;
		} else {
			String geneDBURL = null;
			if (geneList instanceof GeneList) {
				geneDBURL = ((GeneList) geneList).getGeneDBURL();
			}
			return GeneListFactory.createGeneList(result, geneDBURL);
		}
	}


	@Override
	public String getDescription() {
		return "Operation: Index Scores";
	}


	@Override
	public int getStepCount() {
		// 1 for the min, 1 for the max and 1 for the indexation
		return 3;
	}


	@Override
	public String getProcessingDescription() {
		return "Indexing Scores";
	}


	@Override
	public void stop() {
		stopped = true;
	}
}
