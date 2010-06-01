/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.list.binList.operation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import yu.einstein.gdp2.core.enums.DataPrecision;
import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.list.binList.ListFactory;
import yu.einstein.gdp2.util.DoubleLists;


/**
 * Defines regions as "islands" of non zero value bins 
 * separated by more than a specified number of zero value bins.
 * Computes the average on these regions.
 * Returns a new {@link BinList} with the defined regions having their average as a score
 * @author Julien Lajugie
 * @version 0.1
 */
public class BLOTransfrag implements BinListOperation<BinList> {

	private final BinList 	binList;	// input binlist
	private final int 		zeroBinGap; // number of zero value bins defining a gap between two islands 


	/**
	 * Defines regions as "islands" of non zero value bins 
	 * separated by more than a specified number of zero value bins.
	 * Computes the average on these regions.
	 * Returns a new {@link BinList} with the defined regions having their average as a score
	 * @param binList input BinList
	 * @param zeroBinGap number of zero value windows defining a gap between two islands
	 */
	public BLOTransfrag(BinList binList, int zeroBinGap) {
		this.binList = binList;
		this.zeroBinGap = zeroBinGap;
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
							while ((j < currentList.size()) && (zeroWindowCount < zeroBinGap)) {
								if (currentList.get(j) == 0) {
									zeroWindowCount++;
								} else {
									zeroWindowCount = 0;
									regionStop = j;
								}
								j++;
							}
							if (regionStart != regionStop) { 
								// all the windows of the region are set with the average value on the region
								double regionScoreAvg = DoubleLists.average(currentList, regionStart, regionStop);
								for (j = regionStart; j <= regionStop; j++) {
									if (j < resultList.size()) {
										resultList.set(j, regionScoreAvg);
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
}
