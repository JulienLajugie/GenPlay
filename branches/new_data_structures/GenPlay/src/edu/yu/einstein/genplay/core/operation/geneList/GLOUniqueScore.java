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
import edu.yu.einstein.genplay.dataStructure.enums.ScorePrecision;
import edu.yu.einstein.genplay.dataStructure.gene.Gene;
import edu.yu.einstein.genplay.dataStructure.gene.SimpleGene;
import edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.SCWListView.generic.GenericSCWListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.geneListView.GeneListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.geneList.GeneList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.geneList.SimpleGeneList;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.SimpleScoredChromosomeWindow;


/**
 * Sets a specified value to the scores of each exons of {@link GeneList}
 * @author Julien Lajugie
 * @version 0.1
 */
public class GLOUniqueScore implements Operation<GeneList> {
	private final GeneList 			geneList;			// input GeneList
	private final float 			constant;			// constant to add
	private final ScorePrecision 	scorePrecision;		// precision of the scores of the result list
	private boolean 				stopped = false;	// true if the writer needs to be stopped


	/**
	 * Creates an instance of {@link GLOUniqueScore}
	 * @param geneList input GeneList
	 * @param constant constant to add
	 * @param scorePrecision precision of the scores of the genes of the result list
	 */
	public GLOUniqueScore(GeneList geneList, float constant, ScorePrecision scorePrecision) {
		this.geneList = geneList;
		this.constant = constant;
		this.scorePrecision = scorePrecision;
	}


	@Override
	public GeneList compute() throws Exception {
		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<ListView<Gene>>> threadList = new ArrayList<Callable<ListView<Gene>>>();
		for(int i = 0; i < geneList.size(); i++) {
			final ListView<Gene> currentGeneList = geneList.get(i);
			Callable<ListView<Gene>> currentThread = new Callable<ListView<Gene>>() {
				@Override
				public ListView<Gene> call() throws Exception {
					if (currentGeneList == null) {
						return null;
					}
					ListViewBuilder<Gene> resultLVBuilder = new GeneListViewBuilder(scorePrecision);
					for (int j = 0; (j < currentGeneList.size()) && !stopped; j++) {
						Gene currentGene = currentGeneList.get(j);
						Gene geneToAdd = createGeneCopyWithConstantScore(currentGene);
						resultLVBuilder.addElementToBuild(geneToAdd);
					}
					// tell the operation pool that a chromosome is done
					op.notifyDone();
					return resultLVBuilder.getListView();
				}
			};
			threadList.add(currentThread);
		}
		List<ListView<Gene>> result = op.startPool(threadList);
		if (result == null) {
			return null;
		} else {
			return new SimpleGeneList(result, geneList.getGeneScoreType(), geneList.getGeneDBURL());
		}
	}


	/**
	 * Creates a copy of the specified gene with a constant score and with
	 * exons having all the same constant score.
	 * @param gene a gene
	 * @return a copy of the specified gene with a constant score
	 */
	private Gene createGeneCopyWithConstantScore(Gene gene) {
		ListViewBuilder<ScoredChromosomeWindow> exonLVBuilder = new GenericSCWListViewBuilder(scorePrecision);
		if (gene.getExons() != null) {
			for (ScoredChromosomeWindow currentExon: gene.getExons()) {
				ScoredChromosomeWindow newExon = new SimpleScoredChromosomeWindow(currentExon.getStart(), currentExon.getStop(), constant);
				exonLVBuilder.addElementToBuild(newExon);
			}
		}
		return new SimpleGene(gene.getName(), gene.getStrand(), gene.getStart(), gene.getStop(), constant, gene.getUTR5Bound(), gene.getUTR3Bound(), exonLVBuilder.getListView());
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
