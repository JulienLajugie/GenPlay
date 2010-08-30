/**
 * @author Chirag Gorasia
 * @version 0.1
 */
package yu.einstein.gdp2.core.list.binList.operation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

import yu.einstein.gdp2.core.Chromosome;
import yu.einstein.gdp2.core.Gene;
import yu.einstein.gdp2.core.enums.ScoreCalculationMethod;
import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.list.geneList.GeneList;
import yu.einstein.gdp2.core.manager.ChromosomeManager;
import yu.einstein.gdp2.core.operation.Operation;
import yu.einstein.gdp2.core.operationPool.OperationPool;
import yu.einstein.gdp2.util.DoubleLists;

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
	private boolean							stopped = false;// true if the operation must be stopped
	

	/**
	 * Defines regions as "islands" of non zero value bins 
	 * separated by more than a specified number of zero value bins.
	 * Computes the average on these regions.
	 * Returns a new {@link GeneList} with the defined regions having their average/max/sum as a score
	 * @param geneList input GeneList
	 * @param zeroBinGap number of zero value windows defining a gap between two islands
	 * @param operation operation to use to compute the score of the intervals
	 */
	public BLOTransfragGeneList(BinList binList, int zeroBinGap, ScoreCalculationMethod operation) {
		this.binList = binList;
		this.zeroBinGap = zeroBinGap;
		this.operation = operation;
	}

	
	@Override
	public GeneList compute() throws Exception {
		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<List<Gene>>> threadList = new ArrayList<Callable<List<Gene>>>();

		for (short i = 0; i < binList.size(); i++) {
			final List<Double> currentList = binList.get(i);			
			final String chromosomeName = ChromosomeManager.getInstance().get(i).getName();
			final int chromosomeLength = ChromosomeManager.getInstance().get(i).getLength();
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
								newGene = new Gene(chromosomeName + "." + Integer.toString(geneCounter++), new Chromosome(chromosomeName, chromosomeLength), "+", regionStart, regionStop, exonStart, exonStop, exonScore);
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
