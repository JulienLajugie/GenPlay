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

import edu.yu.einstein.genplay.core.gene.Gene;
import edu.yu.einstein.genplay.core.list.geneList.GeneList;
import edu.yu.einstein.genplay.core.list.geneList.GeneListFactory;
import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.core.operationPool.OperationPool;


/**
 * Sets a specified value to the scores of each exons of {@link GeneList}
 * @author Julien Lajugie
 * @version 0.1
 */
public class GLOUniqueScore implements Operation<GeneList> {
	private final GeneList 						geneList;	// input GeneList
	private final double 	constant;		// constant to add
	private boolean stopped = false;	// true if the writer needs to be stopped


	/**
	 * Creates an instance of {@link GLOUniqueScore}
	 * @param geneList input GeneList
	 * @param constant constant to add
	 */
	public GLOUniqueScore(GeneList geneList, double constant) {
		this.geneList = geneList;
		this.constant = constant;
	}


	@Override
	public GeneList compute() throws Exception {
		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<List<Gene>>> threadList = new ArrayList<Callable<List<Gene>>>();
		for(int i = 0; i < geneList.size(); i++) {
			final List<Gene> currentGeneList = geneList.get(i);
			Callable<List<Gene>> currentThread = new Callable<List<Gene>>() {
				@Override
				public List<Gene> call() throws Exception {
					if (currentGeneList == null) {
						return null;
					}
					List<Gene> resultList = new ArrayList<Gene>();
					for (int j = 0; (j < currentGeneList.size()) && !stopped; j++) {
						Gene currentGene = currentGeneList.get(j);
						if ((currentGene != null) && (currentGene.getExonStarts() != null) && (currentGene.getExonStarts().length != 0))  {
							double[] scores = new double[currentGene.getExonStarts().length] ;
							for (int k = 0; (k < currentGene.getExonStarts().length) && !stopped; k++) {
								scores[k] = constant;
							}
							Gene geneToAdd = new Gene(currentGeneList.get(j));
							geneToAdd.setExonScores(scores);
							resultList.add(geneToAdd);
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
			return GeneListFactory.createGeneList(result, geneList.getGeneDBURL());
		}
	}


	@Override
	public String getDescription() {
		return "Operation: Set score for all exons, score = " + constant;
	}


	@Override
	public String getProcessingDescription() {
		return "Scoring the Genes";
	}


	@Override
	public int getStepCount() {
		return 2;
	}


	@Override
	public void stop() {
		stopped = true;
	}
}
