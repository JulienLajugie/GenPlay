/**
 * @author Chirag Gorasia
 * @version 0.1
 */
package yu.einstein.gdp2.core.list.SCWList.operation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

import yu.einstein.gdp2.core.Chromosome;
import yu.einstein.gdp2.core.Gene;
import yu.einstein.gdp2.core.ScoredChromosomeWindow;
import yu.einstein.gdp2.core.enums.ScoreCalculationMethod;
import yu.einstein.gdp2.core.list.SCWList.ScoredChromosomeWindowList;
import yu.einstein.gdp2.core.list.geneList.GeneList;
import yu.einstein.gdp2.core.manager.ChromosomeManager;
import yu.einstein.gdp2.core.operation.Operation;
import yu.einstein.gdp2.core.operationPool.OperationPool;
import yu.einstein.gdp2.util.SCWLists;

/**
 * Defines regions as "islands" of non zero value bins 
 * separated by more than a specified number of zero value bins.
 * Computes the average on these regions.
 * Returns a new {@link GeneList} with the defined regions having their average/max/sum as a score
 * @author Chirag Gorasia
 * @version 0.1
 */
public class SCWLOTransfragGeneList implements Operation<GeneList> {

	private ScoredChromosomeWindowList 	scwList;		// input list
	private int 						zeroSCWGap;		// minimum size of the gap separating two intervals
	private ScoreCalculationMethod 		operation;		// operation to use to compute the score of the intervals
	private boolean						stopped = false;// true if the operation must be stopped


	/**
	 * Creates an instance of {@link SCWLOTransfrag}
	 * @param scwList input list
	 * @param zeroBinGap minimum size of the gap separating two intervals
	 * @param operation operation to use to compute the score of the intervals
	 */
	public SCWLOTransfragGeneList(ScoredChromosomeWindowList scwList, int zeroSCWGap, ScoreCalculationMethod operation) {
		this.scwList = scwList;
		this.zeroSCWGap = zeroSCWGap;
		this.operation = operation;
	}


	@Override
	public GeneList compute() throws Exception {
		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<List<Gene>>> threadList = new ArrayList<Callable<List<Gene>>>();

		for (short i = 0; i < scwList.size(); i++) {
			final List<ScoredChromosomeWindow> currentList = scwList.get(i);	
			final String chromosomeName = ChromosomeManager.getInstance().get(i).getName();
			final int chromosomeLength = ChromosomeManager.getInstance().get(i).getLength();
			Callable<List<Gene>> currentThread = new Callable<List<Gene>>() {	
				@Override
				public List<Gene> call() throws Exception {
					List<Gene> resultGeneList = new ArrayList<Gene>();
					Gene newGene;

					if ((currentList != null) && (currentList.size() != 0)) {
						int geneCounter = 1;
						int j = 0;				
						while (j < currentList.size() && !stopped) {
							// skip zero values
							while ((j < currentList.size()) && (currentList.get(j) == null) &&!stopped) {
								j++;
							}
							int regionStartIndex = j;
							int regionStopIndex = regionStartIndex;
							int[] exonStart = new int[1];
							int[] exonStop = new int[1];
							double[] exonScore = new double[1];
							// a region stops when there is maxZeroWindowGap consecutive zero bins
							while ((j + 1 < currentList.size()) && (currentList.get(j + 1).getStart() - currentList.get(j).getStop() <= zeroSCWGap) && !stopped) {
								regionStopIndex = j+1;
								j++;
							}
							if (regionStopIndex >= currentList.size()) {
								regionStopIndex = currentList.size()-1;
							}
							if (regionStopIndex >= regionStartIndex) { 
								double regionScore = 0;
								if (operation == ScoreCalculationMethod.AVERAGE) {
									// all the windows of the region are set with the average value on the region
									regionScore = SCWLists.average(currentList, regionStartIndex, regionStopIndex);
								} else if (operation == ScoreCalculationMethod.MAXIMUM) {
									// all the windows of the region are set with the max value on the region
									regionScore = SCWLists.maxNoZero(currentList, regionStartIndex, regionStopIndex);
								} else {
									// all the windows of the region are set with the sum value on the region
									regionScore = SCWLists.sum(currentList, regionStartIndex, regionStopIndex);
								}
								int regionStart = currentList.get(regionStartIndex).getStart();
								int regionStop = currentList.get(regionStopIndex).getStop();
								exonStart[0] = regionStart;
								exonStop[0] = regionStop;
								exonScore[0] = regionScore;
								newGene = new Gene(chromosomeName + "." + Integer.toString(geneCounter++), new Chromosome(chromosomeName, chromosomeLength), "+", regionStart, regionStop, exonStart, exonStop, exonScore);
								resultGeneList.add(newGene);
							}
							j++;
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
		return "Operation: Transfrag, Gap Size = " + zeroSCWGap + " Zero Value Successive Bins";
	}


	@Override
	public String getProcessingDescription() {
		return "Computing Transfrag";
	}


	@Override
	public int getStepCount() {
		return ScoredChromosomeWindowList.getCreationStepCount() + 1;
	}	

	
	@Override
	public void stop() {
		this.stopped = true;
	}	
}