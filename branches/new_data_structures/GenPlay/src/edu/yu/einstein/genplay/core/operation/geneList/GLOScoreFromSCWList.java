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
import edu.yu.einstein.genplay.core.operation.SCWList.SCWLOSumScore;
import edu.yu.einstein.genplay.core.operationPool.OperationPool;
import edu.yu.einstein.genplay.dataStructure.enums.GeneScoreType;
import edu.yu.einstein.genplay.dataStructure.gene.Gene;
import edu.yu.einstein.genplay.dataStructure.gene.SimpleGene;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.ScoredChromosomeWindowList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.geneList.GeneList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.geneList.SimpleGeneList;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;
import edu.yu.einstein.genplay.util.Utils;


/**
 * Attributes a score to the exons of a GeneList from the scores of a {@link ScoredChromosomeWindowList}
 * @author Julien Lajugie
 * @version 0.1
 */
public class GLOScoreFromSCWList implements Operation<GeneList> {
	private final GeneList 						geneList;		// input GeneList
	private final ScoredChromosomeWindowList 	scwList;		// BinList with the scores
	private final GeneScoreType 				geneScoreType;	// the score type of the genes and exons (RPKM, base coverage sum, max coverage)
	private boolean 							stopped = false;// true if the writer needs to be stopped


	/**
	 * Creates an instance of {@link GLOScoreFromSCWList}
	 * @param geneList input GeneList
	 * @param scwList {@link ScoredChromosomeWindowList} with the scores
	 * @param geneScore the score type of the genes and exons (RPKM, base coverage sum, max coverage)
	 */
	public GLOScoreFromSCWList(GeneList geneList, ScoredChromosomeWindowList scwList, GeneScoreType geneScore) {
		this.geneList = geneList;
		this.scwList = scwList;
		geneScoreType = geneScore;
	}


	@Override
	public GeneList compute() throws Exception {
		// in the case of RPKM we need to know the score count genome wide
		final double scoreCount;
		if (geneScoreType == GeneScoreType.RPKM) {
			scoreCount = new SCWLOSumScore(scwList, null).compute();
		} else {
			scoreCount = 0;
		}

		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<List<Gene>>> threadList = new ArrayList<Callable<List<Gene>>>();
		for(int i = 0; i < geneList.size(); i++) {
			final List<ScoredChromosomeWindow> currentSCWList = scwList.getView(i);
			final List<Gene> currentGeneList = geneList.getView(i);
			Callable<List<Gene>> currentThread = new Callable<List<Gene>>() {
				@Override
				public List<Gene> call() throws Exception {
					if ((currentGeneList == null) || (currentSCWList == null)) {
						return null;
					}
					List<Gene> resultList = new ArrayList<Gene>();
					for (int j = 0; (j < currentGeneList.size()) && !stopped; j++) {
						Gene currentGene = currentGeneList.get(j);
						if ((currentGene != null) && (currentGene.getExonStarts() != null) && (currentGene.getExonStarts().length != 0))  {
							double[] scores = new double[currentGene.getExonStarts().length] ; // array for the exon scores (1 score / exon)
							double score = 0; // gene score
							// set the score per exon
							for (int k = 0; (k < currentGene.getExonStarts().length) && !stopped; k++) {
								List<ScoredChromosomeWindow> currentExonSCW = Utils.searchChromosomeWindowInterval(currentSCWList, currentGene.getExonStarts()[k], currentGene.getExonStops()[k]);
								if (currentExonSCW != null) {
									for (int l = 0; (l < currentExonSCW.size()) && !stopped; l++) {
										if (geneScoreType == GeneScoreType.MAXIMUM_COVERAGE) {
											scores[k] = Math.max(scores[k], currentExonSCW.get(l).getScore());
											break;
										} else { // case RPKM and BASE_COVERAGE_SUM
											double start = Math.max(currentExonSCW.get(l).getStart(), currentGene.getExonStarts()[k]);
											double stop = Math.min(currentExonSCW.get(l).getStop(), currentGene.getExonStops()[k]);
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
									double exonLength = (currentGene.getExonStops()[i] - currentGene.getExonStarts()[i]);
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
							Gene geneToAdd = new SimpleGene(currentGeneList.get(j));
							geneToAdd.setScore(score);
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
			return new SimpleGeneList(result, geneScoreType, geneList.getGeneDBURL());
		}
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
		int stepCount = 2;
		if (geneScoreType == GeneScoreType.RPKM) {
			stepCount += new SCWLOSumScore(scwList, null).getStepCount();
		}
		return stepCount;
	}


	@Override
	public void stop() {
		stopped = true;
	}
}
