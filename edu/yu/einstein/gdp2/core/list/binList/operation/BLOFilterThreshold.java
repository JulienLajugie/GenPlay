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
	private final double 	lowThreshold;			// saturates the values under this threshold
	private final double 	highThreshold;			// saturates the values above this threshold
	private final  int 		nbConsecutiveValues;	// windows are accepted only if there is this  
													// number of successive windows above (or under) the threshold

	/**
	 * Creates an instance of {@link BLOFilterThreshold}
	 * @param binList {@link BinList} to filter
	 * @param lowThreshold filters the values under this threshold
	 * @param highThreshold filters the values above this threshold
	 * @param nbConsecutiveValues windows are accepted only if there is this number of successive windows above (or under) the threshold
	 */
	public BLOFilterThreshold(BinList binList, double lowThreshold, double highThreshold, int nbConsecutiveValues) {
		this.binList = binList;
		this.lowThreshold = lowThreshold;
		this.highThreshold = highThreshold;
		this.nbConsecutiveValues = nbConsecutiveValues;
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
						for (int j = 0; j < currentList.size() - nbConsecutiveValues; j++) {
							boolean selected = true;
							int k = 0;
							// we accept a window if there is nbConsecutiveValues above or under the filter
							while ((selected) && (k < nbConsecutiveValues) && (j + k < currentList.size())) {
								// depending on the filter type we accept values above or under the threshold
								selected = (currentList.get(j + k) > lowThreshold) && (currentList.get(j + k) < highThreshold);						
								k++;
							}
							if (selected) {
								while ((j < currentList.size()) && (currentList.get(j) > lowThreshold) && (currentList.get(j) < highThreshold)) {
									resultList.set(j, currentList.get(j));
									j++;
								}
							} else {
								resultList.set(j, 0d);
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
		return "Operation: Threshold Filter, minimum = " + lowThreshold + ", maximum = " + highThreshold + ", number of successive values = " + nbConsecutiveValues;
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
