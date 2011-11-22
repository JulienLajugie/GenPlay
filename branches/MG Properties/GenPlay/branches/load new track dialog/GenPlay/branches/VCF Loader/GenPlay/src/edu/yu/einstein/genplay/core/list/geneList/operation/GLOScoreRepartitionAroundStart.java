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

import edu.yu.einstein.genplay.core.Gene;
import edu.yu.einstein.genplay.core.chromosome.Chromosome;
import edu.yu.einstein.genplay.core.enums.ScoreCalculationMethod;
import edu.yu.einstein.genplay.core.enums.Strand;
import edu.yu.einstein.genplay.core.list.binList.BinList;
import edu.yu.einstein.genplay.core.list.geneList.GeneList;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.core.operationPool.OperationPool;


/**
 * Shows the repartition of the scores around the start position of each gene.
 * @author Julien Lajugie
 */
public class GLOScoreRepartitionAroundStart implements Operation<double[][]> {

	private boolean							stopped = false;		// true if the operation must be stopped
	private final GeneList					geneList;				// input gene list
	private final BinList 					binList;				// input bin list
	private final boolean[] 				selectedChromosomes;	// selected chromosomes
	private final int 						binSize;				// size of the bins of scores
	private final int 						binCount;				// number of bins each side of the zero position
	private final ScoreCalculationMethod 	scoreCalculationMethod; // method for the calculation of the bin of scores


	/**
	 * Creates an instance of {@link GLOScoreRepartitionAroundStart}. 
	 * Shows the repartition of the scores around the start position of each gene.
	 * @param geneList input gene list
	 * @param binList list containing the scores
	 * @param selectedChromosomes chromosome on which we show the repartition
	 * @param binSize size of the bins of score
	 * @param binCount count of bins each side of the promoter
	 * @param scoreCalculationMethod {@link ScoreCalculationMethod} to compute the score of the bins
	 */
	public GLOScoreRepartitionAroundStart(GeneList geneList, BinList binList, boolean[] selectedChromosomes,
			int binSize, int binCount, ScoreCalculationMethod scoreCalculationMethod) {
		this.geneList = geneList;
		this.binList = binList;
		this.selectedChromosomes = selectedChromosomes;
		this.binSize = binSize;
		this.binCount = binCount;
		this.scoreCalculationMethod = scoreCalculationMethod;
	}


	@Override
	public double[][] compute() throws Exception {
		final int totalBinCount = binCount * 2 + 1;
		double result[][] = new double[totalBinCount][2];
		for (int i = -binCount; i <= binCount; i++) {
			result[i + binCount][0] = i * binSize;
		}
		// if the method to compute the score is max
		// we need to initialize all the score to negative infinity
		if (scoreCalculationMethod == ScoreCalculationMethod.MAXIMUM) {
			for (int i = 0; i < totalBinCount; i++) {
				result[i][1] = Double.NEGATIVE_INFINITY;
			}
		}

		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<double[]>> threadList = new ArrayList<Callable<double[]>>();

		for (int i = 0; i < geneList.size() && !stopped; i++) {
			final Chromosome currentChromo = ProjectManager.getInstance().getProjectChromosome().get(i); 
			if (((selectedChromosomes == null) || ((i < selectedChromosomes.length) && (selectedChromosomes[i]))) && (geneList.get(i) != null) && (binList.get(i) != null)) {
				final List<Gene> currentGeneList = geneList.get(i);
				Callable<double[]> currentThread = new Callable<double[]>() {	
					@Override
					public double[] call() throws Exception {
						double[] chromoResult = new double[totalBinCount];
						// if the method to compute the score is max
						// we need to initialize all the score to negative infinity
						if (scoreCalculationMethod == ScoreCalculationMethod.MAXIMUM) {
							for (int j = 0; j < totalBinCount; j++) {
								chromoResult[j] = Double.NEGATIVE_INFINITY;
							}
						}
						int[] count = new int[totalBinCount];
						for (Gene currentGene: currentGeneList) {
							int startPos;
							if (currentGene.getStrand().equals(Strand.FIVE)) {
								startPos = currentGene.getStart() - (binCount * binSize);
							} else {
								startPos = currentGene.getStop() + (binCount * binSize);
							}
							for (int j = 0; j < totalBinCount; j++) {
								double currentScore;
								if (currentGene.getStrand().equals(Strand.FIVE)) {
									currentScore = retrieveScore(currentChromo, startPos, startPos + binSize, binList);
									startPos += binSize;
								} else {
									currentScore = retrieveScore(currentChromo, startPos - binSize, startPos, binList);
									startPos -= binSize;
								}
								switch (scoreCalculationMethod) {
								case AVERAGE:
									if (currentScore != 0) {
										chromoResult[j] += currentScore;
										count[j]++;
									}
									break;
								case MAXIMUM:
									chromoResult[j] = Math.max(chromoResult[j], currentScore);
									break;
								case SUM:
									chromoResult[j] += currentScore;
									break;
								}
							}
						}
						// compute the average if it's the method for the score calculation
						if (scoreCalculationMethod == ScoreCalculationMethod.AVERAGE) {
							for (int j = 0; j < totalBinCount; j++) {
								if (count[j] != 0) {
									chromoResult[j] /= count[j];
								} else {
									chromoResult[j] = 0;
								}
							}
						}
						op.notifyDone();
						return chromoResult;
					}
				};
				threadList.add(currentThread);
			} 
		}

		List<double[]> threadResult = op.startPool(threadList);
		if (threadResult == null) {
			return null;		
		}
		int[] count = new int[totalBinCount];
		for (double [] currentResult: threadResult) {
			if (currentResult != null) {
				for (int i = 0; i < currentResult.length; i++) {
					switch (scoreCalculationMethod) {
					case AVERAGE:
						if (currentResult[i] != 0) {
							result[i][1] += currentResult[i];
							count[i]++;
						}
						break;
					case MAXIMUM:
						result[i][1] = Math.max(result[i][1], currentResult[i]);
						break;
					case SUM:
						result[i][1] += currentResult[i];
						break;
					}
				}
			}
		}
		// compute the average if it's the method for the score calculation
		if (scoreCalculationMethod == ScoreCalculationMethod.AVERAGE) {
			for (int i = 0; i < totalBinCount; i++) {
				if (count[i] != 0) {
					result[i][1] /= count[i];
				} else {
					result[i][1] = 0;
				}
			}
		}
		return result;

	}


	/**
	 * Retrieves the average score of the bins between a start and a stop position on a specified chromosome
	 * @param currentChromo chromosome position
	 * @param start start position
	 * @param stop stop position
	 * @param binList list with the scores
	 * @return the average score of the bins between a start and a stop position on a specified chromosome
	 */
	protected double retrieveScore(Chromosome currentChromo, int start, int stop, BinList binList) {
		//TODO handle the first and last windows when only a small part of the start and stop 
		//are in the window
		
		int startIndex = (int) Math.floor(start / (double) binList.getBinSize());
		int stopIndex = (int) Math.ceil(stop / (double) binList.getBinSize());
		int count = 0;
		double totalScore = 0;
		startIndex = Math.max(0, startIndex);
		stopIndex = Math.min(binList.size(currentChromo) - 1, stopIndex);
		for (int i = startIndex; i <= stopIndex; i++) {
			double score = binList.get(currentChromo, i);
			if (score != 0) {
				count++;
				totalScore += score;
			}
		}
		if (count == 0) {
			 return 0;
		} else {
			return totalScore / count;
		}
	}


	@Override
	public String getDescription() {
		return "Operation: Show Repartition Around Gene Start";
	}


	@Override
	public String getProcessingDescription() {
		return "Computing Score Repartition";
	}


	@Override
	public int getStepCount() {
		return 1;
	}


	@Override
	public void stop() {
		this.stopped = true;		
	}
}
