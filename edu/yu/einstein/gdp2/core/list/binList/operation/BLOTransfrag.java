/**
 * @author Julien Lajugie
 * @author Chirag Gorasia
 * @version 0.1
 */
package yu.einstein.gdp2.core.list.binList.operation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import yu.einstein.gdp2.core.enums.DataPrecision;
import yu.einstein.gdp2.core.enums.ScoreCalculationMethod;
import yu.einstein.gdp2.core.list.arrayList.ListFactory;
import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.operation.Operation;
import yu.einstein.gdp2.core.operationPool.OperationPool;
import yu.einstein.gdp2.util.DoubleLists;


/**
 * Defines regions as "islands" of non zero value bins 
 * separated by more than a specified number of zero value bins.
 * Computes the average on these regions.
 * Returns a new {@link BinList} with the defined regions having their average/max/sum as a score
 * @author Julien Lajugie
 * @author Chirag Gorasia
 * @version 0.1
 */
public class BLOTransfrag implements Operation<BinList> {

	private final BinList 	binList;	// input binlist
	private final int 		zeroBinGap; // number of zero value bins defining a gap between two islands
	private final ScoreCalculationMethod	operation;	// max / sum / average 


	/**
	 * Defines regions as "islands" of non zero value bins 
	 * separated by more than a specified number of zero value bins.
	 * Computes the average on these regions.
	 * Returns a new {@link BinList} with the defined regions having their average/max/sum as a score
	 * @param binList input BinList
	 * @param zeroBinGap number of zero value windows defining a gap between two islands
	 */
	public BLOTransfrag(BinList binList, int zeroBinGap, ScoreCalculationMethod operation) {
		this.binList = binList;
		this.zeroBinGap = zeroBinGap;
		this.operation = operation;		
	}


	@Override
	public BinList compute() throws InterruptedException, ExecutionException {
		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<List<Double>>> threadList = new ArrayList<Callable<List<Double>>>();

		final int binSize = binList.getBinSize();
		final DataPrecision precision = binList.getPrecision();

		for (short i = 0; i < binList.size(); i++) {
			final List<Double> currentList = binList.get(i);	

			Callable<List<Double>> currentThread = new Callable<List<Double>>() {	
				@Override
				public List<Double> call() throws Exception {
					List<Double> resultList = null;
					if ((currentList != null) && (currentList.size() != 0)) {
						resultList = ListFactory.createList(precision, currentList.size());
						int j = 0;				
						while (j < currentList.size()) {
							// skip zero values
							while ((j < currentList.size()) && (currentList.get(j) == 0)) {
								j++;
							}
							int regionStart = j;
							int regionStop = regionStart;
							int zeroWindowCount = 0;
							// a region stops when there is maxZeroWindowGap consecutive zero bins
							while ((j < currentList.size()) && (zeroWindowCount <= zeroBinGap)) {
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
									// all the windows of the region are set with the max value on the region
									regionScore = DoubleLists.maxNoZero(currentList);
								} else {
									// all the windows of the region are set with the sum value on the region
									regionScore = DoubleLists.sum(currentList, regionStart, regionStop);
								}
								for (j = regionStart; j <= regionStop; j++) {
									if (j < resultList.size()) {
										resultList.set(j, regionScore);
									}
								}
							}
							j++;
						}
					}
					// tell the operation pool that a chromosome is done
					op.notifyDone();
					return resultList;
				}
			};

			threadList.add(currentThread);
		}
		List<List<Double>> result = op.startPool(threadList);
		if (result != null) {
			BinList resultList = new BinList(binSize, precision, result);
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
	public int getStepCount() {
		return BinList.getCreationStepCount(binList.getBinSize()) + 1;
	}
	
	
	@Override
	public String getProcessingDescription() {
		return "Computing Transfrag";
	}
}
