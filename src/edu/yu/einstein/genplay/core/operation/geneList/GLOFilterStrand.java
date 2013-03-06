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
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.core.operationPool.OperationPool;
import edu.yu.einstein.genplay.dataStructure.enums.Strand;
import edu.yu.einstein.genplay.dataStructure.gene.Gene;
import edu.yu.einstein.genplay.dataStructure.list.geneList.GeneList;
import edu.yu.einstein.genplay.dataStructure.list.geneList.GeneListFactory;


/**
 * Creates a new GeneList containing only the genes on the selected strand
 * @author Julien Lajugie
 * @version 0.1
 */
public class GLOFilterStrand implements Operation<GeneList> {
	private final GeneList 	geneList;			// input list
	private final Strand	strandToKeep;		// strand with the genes we want to keep
	private boolean			stopped = false;	// true if the operation must be stopped


	/**
	 * Creates an instance of {@link GLOFilterStrand}
	 * @param geneList input list
	 * @param strandToKeep strand with the genes we want to keep
	 */
	public GLOFilterStrand(GeneList geneList, Strand strandToKeep) {
		this.geneList = geneList;
		this.strandToKeep = strandToKeep;
	}


	@Override
	public GeneList compute() throws Exception {
		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<List<Gene>>> threadList = new ArrayList<Callable<List<Gene>>>();

		for(short i = 0; i < geneList.size(); i++) {
			final List<Gene> currentList = geneList.get(i);
			Callable<List<Gene>> currentThread = new Callable<List<Gene>>() {
				@Override
				public List<Gene> call() throws Exception {
					if (currentList == null) {
						return null;
					}
					List<Gene> resultList = new ArrayList<Gene>();
					for (int j = 0; (j < currentList.size()) && !stopped; j++) {
						Gene currentGene = currentList.get(j);
						if (currentGene.getStrand().equals(strandToKeep)) {
							resultList.add(currentGene);
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
			return GeneListFactory.createGeneArrayList(result, geneList.getGeneDBURL(), geneList.getGeneScoreType());
		}
	}


	@Override
	public String getDescription() {
		return "Operation: Filter Strand, only genes on \""+ strandToKeep + "\" strand kept";
	}


	@Override
	public String getProcessingDescription() {
		return "Filtering Genes";
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
