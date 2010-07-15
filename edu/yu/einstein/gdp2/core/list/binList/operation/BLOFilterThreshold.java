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
public class BLOFilterThreshold implements Operation<BinList> {
	private final BinList 	binList;				// input binlist to filter
	private final double 	lowThreshold;			// filters the values under this threshold
	private final double 	highThreshold;			// filters the values above this threshold
	private final boolean	isSaturation;			// true if we saturate, false if we remove the filtered values 

	
	/**
	 * Creates an instance of {@link BLOFilterThreshold}
	 * @param binList {@link BinList} to filter
	 * @param lowThreshold filters the values under this threshold
	 * @param highThreshold filters the values above this threshold
	 * @param isSaturation true to saturate, false to remove the filtered values
	 */
	public BLOFilterThreshold(BinList binList, double lowThreshold, double highThreshold, boolean isSaturation) {
		this.binList = binList;
		this.lowThreshold = lowThreshold;
		this.highThreshold = highThreshold;
		this.isSaturation = isSaturation;
	}


	@Override
	public BinList compute() throws Exception {
		if (lowThreshold >= highThreshold) {
			throw new IllegalArgumentException("The high threshold must be greater than the low one");
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
								if (currentScore > highThreshold) {
									// if the score is greater than the high threshold
									if (isSaturation) {
										// set the value to high threshold (saturation)
										resultList.set(i, highThreshold);
									} else {
										// set the value to 0
										resultList.set(i, 0d);
									}
								} else if (currentScore < lowThreshold) {
									// if the score is smaller than the low threshold
									if (isSaturation) {
										// set the value to low threshold (saturation)
										resultList.set(i, lowThreshold);
									} else {
										// set the value to 0
										resultList.set(i, 0d);
									}
								} else {
									// if the score is between the two threshold
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
		return "Operation: Threshold Filter, minimum = " + lowThreshold + ", maximum = " + highThreshold;
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
