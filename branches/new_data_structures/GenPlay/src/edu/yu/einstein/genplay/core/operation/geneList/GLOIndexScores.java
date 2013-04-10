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
import java.util.concurrent.ExecutionException;

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
 * Indexes the score values of a {@link GeneList}
 * @author Julien Lajugie
 * @version 0.1
 */
public class GLOIndexScores implements Operation<GeneList> {

	private final GeneList 			geneList;			// input GeneList
	private boolean					stopped = false;	// true if the operation must be stopped
	private final ScorePrecision 	scorePrecision;		// precision of the scores of the result list


	/**
	 * Indexes the score values of a {@link GeneList}
	 * @param geneList input {@link GeneList}
	 * @param scorePrecision precision of the scores of the genes of the result list
	 */
	public GLOIndexScores(GeneList geneList, ScorePrecision scorePrecision) {
		this.geneList = geneList;
		this.scorePrecision = scorePrecision;
	}


	@Override
	public GeneList compute() throws InterruptedException, ExecutionException {
		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<ListView<Gene>>> threadList = new ArrayList<Callable<ListView<Gene>>>();
		// compute the distance between the min and the max
		final float min = new GLOMin(geneList, null).compute();
		float max = new GLOMax(geneList, null).compute();
		float distanceMinMax = max - min;
		final float indexFactor = 1000f / distanceMinMax;

		for (int i = 0; i < geneList.size(); i++) {
			final ListView<Gene> currentList = geneList.get(i);

			Callable<ListView<Gene>> currentThread = new Callable<ListView<Gene>>() {
				@Override
				public ListView<Gene> call() throws Exception {
					if (currentList == null) {
						return null;
					}
					ListViewBuilder<Gene> resultLVBuilder = new GeneListViewBuilder(scorePrecision);
					for (int i = 0; (i < currentList.size()) && !stopped; i++) {
						Gene currentGene = currentList.get(i);
						if (currentGene != null) {
							Gene copyCurrentGene;
							copyCurrentGene = createIndexedGene(currentGene, min, indexFactor);
							resultLVBuilder.addElementToBuild(copyCurrentGene);
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
		if (result == null) {
			return null;
		} else {
			return new SimpleGeneList(result, geneList.getGeneScoreType(), geneList.getGeneDBURL());
		}
	}


	/**
	 * Creates a new gene having the same attribute as the specified gene, except for the gene score
	 * and the exon scores that are indexed
	 * @param gene a {@link Gene}
	 * @param min minimum value of the {@link GeneList} (needed for the indexing)
	 * @param indexFactor factor of the indexing
	 * @return
	 */
	private Gene createIndexedGene(Gene gene, float min, float indexFactor) {
		float geneScore = Float.NaN;
		if (gene.getScore() != Float.NaN) {
			geneScore = (gene.getScore() - min) * indexFactor;
			geneScore = Math.max(0, geneScore);
			geneScore = Math.min(1000, geneScore);
		}
		ListViewBuilder<ScoredChromosomeWindow> exonLVBuilder = new GenericSCWListViewBuilder(scorePrecision);
		if (gene.getExons() != null) {
			for (ScoredChromosomeWindow exon: gene.getExons()) {
				float exonScore = Float.NaN;
				if (exon.getScore() != Float.NaN) {
					exonScore = (exon.getScore() - min) * indexFactor;
					exonScore = Math.max(0, exonScore);
					exonScore = Math.min(1000, exonScore);
				}
				ScoredChromosomeWindow exonToAdd = new SimpleScoredChromosomeWindow(exon.getStart(), exon.getStop(), exonScore);
				exonLVBuilder.addElementToBuild(exonToAdd);
			}
		}
		Gene newGene = new SimpleGene(gene.getName(), gene.getStrand(), gene.getStart(), gene.getStop(), geneScore, gene.getUTR5Bound(), gene.getUTR3Bound(), exonLVBuilder.getListView());
		return newGene;
	}


	@Override
	public String getDescription() {
		return "Operation: Index Scores";
	}


	@Override
	public String getProcessingDescription() {
		return "Indexing Scores";
	}


	@Override
	public int getStepCount() {
		// 1 for the min, 1 for the max and 1 for the indexing
		return 3;
	}


	@Override
	public void stop() {
		stopped = true;
	}
}
