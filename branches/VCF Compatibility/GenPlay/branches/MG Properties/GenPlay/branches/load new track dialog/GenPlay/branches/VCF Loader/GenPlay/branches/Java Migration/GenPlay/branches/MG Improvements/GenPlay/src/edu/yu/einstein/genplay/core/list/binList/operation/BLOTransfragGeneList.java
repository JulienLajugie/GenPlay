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
package edu.yu.einstein.genplay.core.list.binList.operation;

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
import edu.yu.einstein.genplay.core.manager.project.ProjectChromosome;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.core.operationPool.OperationPool;
import edu.yu.einstein.genplay.util.DoubleLists;


/**
 * Defines regions as "islands" of non zero value bins 
 * separated by more than a specified number of zero value bins.
 * Computes the average on these regions.
 * Returns a new {@link GeneList} with the defined regions having their average/max/sum as a score
 * @author Chirag Gorasia
 * @version 0.1
 */
public class BLOTransfragGeneList implements Operation<GeneList> {

	private final BinList 					binList;		// input binlist
	private final int 						zeroBinGap; 	// number of zero value bins defining a gap between two islands
	private final ScoreCalculationMethod 	operation;		//sum / average / max
	private final ProjectChromosome projectChromosome; // Instance of the Chromosome Manager
	private boolean							stopped = false;// true if the operation must be stopped
	

	/**
	 * Defines regions as "islands" of non zero value bins 
	 * separated by more than a specified number of zero value bins.
	 * Computes the average on these regions.
	 * Returns a new {@link GeneList} with the defined regions having their average/max/sum as a score
	 * @param binList input BinList
	 * @param zeroBinGap number of zero value windows defining a gap between two islands
	 * @param operation operation to use to compute the score of the intervals
	 */
	public BLOTransfragGeneList(BinList binList, int zeroBinGap, ScoreCalculationMethod operation) {
		this.binList = binList;
		this.zeroBinGap = zeroBinGap;
		this.operation = operation;
		projectChromosome = ProjectManager.getInstance().getProjectChromosome();
	}

	
	@Override
	public GeneList compute() throws Exception {
		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<List<Gene>>> threadList = new ArrayList<Callable<List<Gene>>>();

		for (short i = 0; i < binList.size(); i++) {
			final List<Double> currentList = binList.get(i);			
			final String chromosomeName = projectChromosome.get(i).getName();
			final int chromosomeLength = projectChromosome.get(i).getLength();
			Callable<List<Gene>> currentThread = new Callable<List<Gene>>() {	
				@Override
				public List<Gene> call() throws Exception {
					
					List<Gene> resultGeneList = new ArrayList<Gene>();
					Gene newGene;
					if ((currentList != null) && (currentList.size() != 0)) {
						int j = 0;	
						int geneCounter = 1;
						while (j < currentList.size() && !stopped) {
							// skip zero values
							while ((j < currentList.size()) && (currentList.get(j) == 0) && !stopped) {
								j++;
							}
							int regionStart = j;
							int regionStop = regionStart;
							int zeroWindowCount = 0;
							int[] exonStart = new int[1];
							int[] exonStop = new int[1];
							double[] exonScore = new double[1];
							
							// a region stops when there is maxZeroWindowGap consecutive zero bins
							while ((j < currentList.size()) && (zeroWindowCount <= zeroBinGap) && !stopped) {
								if (currentList.get(j) == 0) {
									zeroWindowCount++;
								} else {
									zeroWindowCount = 0;
									regionStop = j;
								}
								j++;
							}
							if (regionStop == currentList.size()) {
								regionStop--;
							}
							if (regionStop >= regionStart) {
								double regionScore = 0;
								if (operation == ScoreCalculationMethod.AVERAGE) {
									// all the windows of the region are set with the average value on the region
									regionScore = DoubleLists.average(currentList, regionStart, regionStop);
								} else if (operation == ScoreCalculationMethod.SUM) {
									// all the windows of the region are set with the sum value on the region
									regionScore = DoubleLists.sum(currentList, regionStart, regionStop);
								} else {
									// all the windows of the region are set with the max value on the region
									regionScore = DoubleLists.maxNoZero(currentList, regionStart, regionStop);
								}
								regionStart *= binList.getBinSize();
								regionStop++;
								regionStop *= binList.getBinSize();
								exonStart[0] = regionStart;
								exonStop[0] = regionStop;
								exonScore[0] = regionScore;
								newGene = new Gene(chromosomeName + "." + Integer.toString(geneCounter++), new Chromosome(chromosomeName, chromosomeLength), Strand.get('+'), regionStart, regionStop, exonStart, exonStop, exonScore);
								resultGeneList.add(newGene);
							}
						}
					}
					// tell the operation pool that a chromosome is done
					op.notifyDone();
					return resultGeneList;
				}
			};
			threadList.add(currentThread);
		}
		List<List<Gene>> result = op.startPool(threadList);
		if (result != null) {
			GeneList resultList = new GeneList(result);
			return resultList;
		} else {
			return null;
		}
	}

	
	@Override
	public String getDescription() {
		return "Operation: Transfrag, Gap Size = " + zeroBinGap + " Zero Value Successive Bins";
	}
	

	@Override
	public String getProcessingDescription() {
		return "Computing Transfrag";
	}

	
	@Override
	public int getStepCount() {
		return BinList.getCreationStepCount(binList.getBinSize()) + 1;
	}
	
	
	@Override
	public void stop() {
		this.stopped = true;
	}
}
