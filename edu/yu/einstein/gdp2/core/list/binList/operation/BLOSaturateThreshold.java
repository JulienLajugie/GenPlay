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
 * Saturates the values above and under specified thresholds
 * @author Julien Lajugie
 * @version 0.1
 */
public class BLOSaturateThreshold implements Operation<BinList> {
	private final BinList 	binList;		// input binlist to saturate
	private final double 	minSaturated;	// saturates the values under this threshold
	private final double 	maxSaturated;	// saturates the values above this threshold


	/**
	 * Creates an instance of {@link BLOSaturateThreshold}
	 * @param binList {@link BinList} to saturate
	 * @param minSaturated saturates the values under this threshold
	 * @param maxSaturated saturates the values above this threshold
	 */
	public BLOSaturateThreshold(BinList binList, double minSaturated, double maxSaturated) {
		this.binList = binList;
		this.minSaturated = minSaturated;
		this.maxSaturated = maxSaturated;
	}



	@Override
	public BinList compute() throws Exception {
		if (minSaturated >= maxSaturated) {
			throw new IllegalArgumentException("The maximum must be greater than the minimum");
		}

		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<List<Double>>> threadList = new ArrayList<Callable<List<Double>>>();
		final DataPrecision precision = binList.getPrecision();

		for (final List<Double> currentList: binList) {

			Callable<List<Double>> currentThread = new Callable<List<Double>>() {			
				@Override
				public List<Double> call() throws Exception {
					List<Double> resultList = null;
					if ((currentList != null) && (currentList.size() != 0)) {
						resultList = ListFactory.createList(binList.getPrecision(), currentList.size());
						for (int i = 0; i < currentList.size(); i++) {
							double currentScore = currentList.get(i);
							if (currentScore != 0) {
								if (currentScore > maxSaturated) {
									resultList.set(i, maxSaturated);
								} else if (currentScore < minSaturated) {
									resultList.set(i, minSaturated);
								} else {
									resultList.set(i, currentScore);
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
		return "Operation: Threshold Saturate, minimum = " + minSaturated + ", maximum = " + maxSaturated;
	}



	@Override
	public String getProcessingDescription() {
		return "Saturating";
	}



	@Override
	public int getStepCount() {
		return 1 + BinList.getCreationStepCount(binList.getBinSize());
	}
}
