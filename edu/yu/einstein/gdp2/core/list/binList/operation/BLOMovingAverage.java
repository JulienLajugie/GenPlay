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
import yu.einstein.gdp2.core.list.arrayList.ListFactory;
import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.operation.Operation;
import yu.einstein.gdp2.core.operationPool.OperationPool;


/**
 * Computes a moving average on the BinList and returns the result in a new BinList.
 * @author Julien Lajugie
 * @version 0.1
 */
public class BLOMovingAverage implements Operation<BinList> {

	private final BinList 	binList;		// input list
	private final int		halfWidth;		// half of the size of the average window
	private final boolean	fillNullValues; // true to fill the null values
	private boolean			stopped = false;// true if the operation must be stopped
	
	
	/**
	 * Creates an instance of {@link BLOMovingAverage}
	 * Computes a moving average on the BinList and returns the result in a new BinList.
	 * @param binList {@link BinList} input binList
	 * @param halfWidth half size in bases
	 * @param fillNullValues set to true to fill the null values
	 */
	public BLOMovingAverage(BinList binList, int halfWidth, boolean fillNullValues) {
		this.binList = binList;
		this.halfWidth = halfWidth;
		this.fillNullValues = fillNullValues;
	}
	
	
	@Override
	public BinList compute() throws InterruptedException, ExecutionException {
		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<List<Double>>> threadList = new ArrayList<Callable<List<Double>>>();
		final DataPrecision precision = binList.getPrecision();
		final int binSize =  binList.getBinSize();
		final int halfWidthBin = 2 * halfWidth / binSize;
		// we apply the moving average
		for(short i = 0; i < binList.size(); i++) {
			final List<Double> currentList = binList.get(i);
			Callable<List<Double>> currentThread = new Callable<List<Double>>() {			 	
				@Override
				public List<Double> call() throws Exception {
					List<Double> listToAdd = null;
					if ((currentList != null) && (currentList.size() != 0)) {
						listToAdd = ListFactory.createList(precision, currentList.size());
						for(int j = 0; j < currentList.size() && !stopped; j++) {
							if ((currentList.get(j) != 0) || (fillNullValues)) {
								double count = 0;
								double SumNormSignalCoef = 0;
								for (int k = -halfWidthBin; k <= halfWidthBin && !stopped; k++) {
									if((j + k >= 0) && ((j + k) < currentList.size()))  {
										if(currentList.get(j + k) != 0)  {
											SumNormSignalCoef += currentList.get(j + k);
											count++;
										}
									}
								}
								if(count == 0) {
									listToAdd.set(j, 0d);
								} else {
									listToAdd.set(j, SumNormSignalCoef / count);
								}
							} else {
								listToAdd.set(j, 0d);
							}
						}
					}
					op.notifyDone();
					return listToAdd;
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
		return "Operation: Moving Average, Half Width = " + halfWidth + "bp";
	}
	
	
	@Override
	public int getStepCount() {
		return BinList.getCreationStepCount(binList.getBinSize()) + 1;
	}
	
	
	@Override
	public String getProcessingDescription() {
		return "Computing Moving Average";
	}

	
	@Override
	public void stop() {
		this.stopped = true;
	}
}
