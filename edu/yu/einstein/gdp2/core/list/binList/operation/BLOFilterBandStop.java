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
 * Removes the values between two specified thresholds
 * @author Julien Lajugie
 * @version 0.1
 */
public class BLOFilterBandStop implements Operation<BinList> {

	private final BinList 	binList;		// input BinList
	private final double 	lowThreshold;	// low bound 
	private final double 	highThreshold;	// high bound

	
	/**
	 * Creates an instance of {@link BLOFilterBandStop}
	 * @param binList input {@link BinList}
	 * @param lowThreshold low threshold
	 * @param highThreshold high threshold
	 */
	public BLOFilterBandStop(BinList binList, double lowThreshold, double highThreshold) {
		this.binList = binList;
		this.lowThreshold = lowThreshold;
		this.highThreshold = highThreshold;
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
						for (int j = 0; j < currentList.size(); j++) {
							double currentValue = currentList.get(j); 
							if ((currentValue >= lowThreshold) && (currentValue <= highThreshold)) {
								resultList.set(j, 0d);
							} else {
								resultList.set(j, currentValue);								
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
		return "Operation: Band-Stop Filter, Low Threshold = " + lowThreshold + ", High Threshold = " + highThreshold;
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
