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

import edu.yu.einstein.genplay.core.manager.project.ProjectChromosomes;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.core.operationPool.OperationPool;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.enums.GeneScoreType;
import edu.yu.einstein.genplay.dataStructure.gene.Gene;
import edu.yu.einstein.genplay.dataStructure.gene.SimpleGene;
import edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.SCWListView.generic.GenericSCWListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.geneListView.GeneListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.ListOfListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SCWList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.geneList.GeneList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.geneList.SimpleGeneList;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;
import edu.yu.einstein.genplay.util.Utils;


/**
 * Attributes a score to the exons of a GeneList from the scores of a {@link SCWList}
 * @author Julien Lajugie
 */
public class GLOScoreFromSCWList implements Operation<GeneList> {
	private final GeneList 			geneList;		// input GeneList
	private final SCWList 			scwList;		// BinList with the scores
	private final GeneScoreType 	geneScoreType;	// the score type of the genes and exons (RPKM, base coverage sum, max coverage)
	private boolean 				stopped = false;// true if the writer needs to be stopped


	/**
	 * Creates an instance of {@link GLOScoreFromSCWList}
	 * @param geneList input GeneList
	 * @param scwList {@link SCWList} with the scores
	 * @param geneScore the score type of the genes and exons (RPKM, base coverage sum, max coverage)
	 */
	public GLOScoreFromSCWList(GeneList geneList, SCWList scwList, GeneScoreType geneScore) {
		this.geneList = geneList;
		this.scwList = scwList;
		geneScoreType = geneScore;
	}


	@Override
	public GeneList compute() throws Exception {
		// in the case of RPKM we need to know the score count genome wide
		final double scoreCount;
		if (geneScoreType == GeneScoreType.RPKM) {
			scoreCount = scwList.getStatistics().getScoreSum();
		} else {
			scoreCount = 0;
		}

		ProjectChromosomes projectChromosomes = ProjectManager.getInstance().getProjectChromosomes();
		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<Void>> threadList = new ArrayList<Callable<Void>>();
		ListViewBuilder<Gene> lvbPrototype = new GeneListViewBuilder();
		final ListOfListViewBuilder<Gene> resultListBuilder = new ListOfListViewBuilder<Gene>(lvbPrototype);

		for (final Chromosome chromosome: projectChromosomes) {
			final ListView<ScoredChromosomeWindow> currentSCWList = scwList.get(chromosome);
			final ListView<Gene> currentGeneList = geneList.get(chromosome);

			Callable<Void> currentThread = new Callable<Void>() {

				@Override
				public Void call() throws Exception {
					if ((currentSCWList != null) && (currentGeneList != null)) {
						for (int j = 0; (j < currentGeneList.size()) && !stopped; j++) {
							Gene currentGene = currentGeneList.get(j);
							if ((currentGene != null) && (currentGene.getExons() != null) && (!currentGene.getExons().isEmpty()))  {
								double[] scores = new double[currentGene.getExons().size()] ; // array for the exon scores (1 score / exon)
								double score = 0; // gene score
								// set the score per exon
								for (int k = 0; (k < currentGene.getExons().size()) && !stopped; k++) {
									List<ScoredChromosomeWindow> currentExonSCW = Utils.searchChromosomeWindowInterval(currentSCWList, currentGene.getExons().get(k).getStart(), currentGene.getExons().get(k).getStop());
									if (currentExonSCW != null) {
										for (int l = 0; (l < currentExonSCW.size()) && !stopped; l++) {
											if (geneScoreType == GeneScoreType.MAXIMUM_COVERAGE) {
												scores[k] = Math.max(scores[k], currentExonSCW.get(l).getScore());
												break;
											} else { // case RPKM and BASE_COVERAGE_SUM
												double start = Math.max(currentExonSCW.get(l).getStart(), currentGene.getExons().get(k).getStart());
												double stop = Math.min(currentExonSCW.get(l).getStop(), currentGene.getExons().get(k).getStop());
												scores[k] += currentExonSCW.get(l).getScore() * (stop - start);
											}
										}
									}
								}
								// set the score for the gene
								switch (geneScoreType) {
								case BASE_COVERAGE_SUM:
									for (int i = 0; i < scores.length; i++) {
										score += scores[i];
									}
									break;
								case MAXIMUM_COVERAGE:
									if (scores.length > 0) {
										score = scores[0];
										for (int i = 1; i < scores.length; i++) {
											score = Math.max(score, scores[i]);
										}
									}
									break;
								case RPKM:
									double length = 0;
									for (int i = 0; i < scores.length; i++) {
										double exonLength = (currentGene.getExons().get(i).getSize());
										score += scores[i];
										length += exonLength;
										// compute the RPKM for the current exon
										// RPKM(Exon) = (Base_coverage_sum(Exon) * 10^9) / (Length(Exon) * Score_Count(SCWL))
										scores[i] *= Math.pow(10, 9);
										scores[i] /= exonLength * scoreCount;
									}
									// compute the RPKM for the current gene
									// RPKM(Gene) = (Base_coverage_sum(Gene) * 10^9) / (Length(Exons of genes) * Score_Count(SCWL))
									score *= Math.pow(10, 9);
									score /= length * scoreCount;
									break;
								}
								GenericSCWListViewBuilder exonLVBuilder = new GenericSCWListViewBuilder();
								for (int i = 0; i < scores.length; i++) {
									int exonStart = currentGene.getExons().get(i).getStart();
									int exonStop = currentGene.getExons().get(i).getStop();
									float exonScore = (float) scores[i];
									exonLVBuilder.addElementToBuild(exonStart, exonStop, exonScore);
								}
								Gene geneToAdd = new SimpleGene(currentGene.getName(),
										currentGene.getStrand(),
										currentGene.getStart(),
										currentGene.getStop(),
										(float) score,
										currentGene.getUTR5Bound(),
										currentGene.getUTR3Bound(),
										exonLVBuilder.getListView());
								resultListBuilder.addElementToBuild(chromosome, geneToAdd);
							}
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
		List<ListView<Gene>> data = resultListBuilder.getGenomicList();
		return new SimpleGeneList(data, geneScoreType, geneList.getGeneDBURL());
	}


	@Override
	public String getDescription() {
		return "Operation: Score Exons, Method of calculation: " + geneScoreType + ", from track: ";
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
