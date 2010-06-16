/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.list.binList.operation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

import yu.einstein.gdp2.core.enums.DataPrecision;
import yu.einstein.gdp2.core.list.arrayList.ListFactory;
import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.operation.Operation;
import yu.einstein.gdp2.core.operationPool.OperationPool;


/**
 * Removes the values above and under specified thresholds
 * @author Julien Lajugie
 * @version 0.1
 */
public class BLOFilterDensity implements Operation<BinList> {
	private final BinList 	binList;				// input binlist to filter
	private final double 	lowThreshold;			// saturates the values under this threshold
	private final double 	highThreshold;			// saturates the values above this threshold
	private final double 	density;				// minimum density of windows above and under the thresholds for a region to be selected (percentage btw 0 and 1)
	private final int 		regionSize;				// size of the region (in number of bins) 

	
	/**
	 * Creates an instance of {@link BLOFilterDensity}
	 * @param binList {@link BinList} to filter
	 * @param lowThreshold filters the values under this threshold
	 * @param highThreshold filters the values above this threshold
	 * @param density minimum density of windows above and under the thresholds for a region to be selected (percentage btw 0 and 1)
	 * @param regionSize size of the region (in number of bins) 
	 */
	public BLOFilterDensity(BinList binList, double lowThreshold, double highThreshold, double density, int regionSize) {
		this.binList = binList;
		this.lowThreshold = lowThreshold;
		this.highThreshold = highThreshold;
		this.density = density;
		this.regionSize = regionSize;
	}


	@Override
	public BinList compute() throws Exception {
		if (lowThreshold >= highThreshold) {
			throw new IllegalArgumentException("The high threshold must be greater than the low one");
		}		
		// if percentage is zero, everything is selected
		if (density == 0) {
			return binList.deepClone();
		}
		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<List<Double>>> threadList = new ArrayList<Callable<List<Double>>>();
		// we calculate the min number of bins above the threshold needed to select a region
		final int minBinCount = (int)Math.ceil(regionSize * density);
		final DataPrecision precision = binList.getPrecision();
		for (final List<Double> currentList: binList) {

			Callable<List<Double>> currentThread = new Callable<List<Double>>() {			
				@Override
				public List<Double> call() throws Exception {
					List<Double> resultList = null;
					if ((currentList != null) && (currentList.size() != 0)) {
						resultList = ListFactory.createList(binList.getPrecision(), currentList.size());
						for (int j = 0; j < currentList.size() - regionSize; j++) {
							int binSelectedCount = 0;
							int k = 0;
							// we accept a window if there is nbConsecutiveValues above or under the filter
							while ((binSelectedCount < minBinCount) && (k < regionSize) && (j + k < currentList.size())) {
								// depending on the filter type we accept values above or under the threshold
								if ((currentList.get(j + k) > lowThreshold) && (currentList.get(j + k) < highThreshold)) {
									binSelectedCount++;
								}
								k++;
							}
							// we calculate where the current region ends
							k = 0;
							if (binSelectedCount >= minBinCount) {
								while ((j + k < currentList.size()) && (k < regionSize)) {
									resultList.set(j + k, currentList.get(j + k));
									k++;
								}
							}
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
			BinList resultList = new BinList(binList.getBinSize(), precision, result);
			return resultList;
		} else {
			return null;
		}
	}


	@Override
	public String getDescription() {
		return "Operation: Density Filter, minimum = " + lowThreshold + ", maximum = " + highThreshold + ", density = " + density + ", region size = " + regionSize;
	}


	@Override
	public String getProcessingDescription() {
		return "Filtering";
	}


	@Override
	public int getStepCount() {
		return 1 + BinList.getCreationStepCount(binList.getBinSize());
	}
}
