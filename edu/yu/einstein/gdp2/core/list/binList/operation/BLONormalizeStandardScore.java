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
 * Computes a Standard Score normalization on a {@link BinList}
 * @author Julien Lajugie
 * @version 0.1
 */
public class BLONormalizeStandardScore implements Operation<BinList> {

	private final BinList 				binList;	// input list 
	private final BLOAverage 			avgOp;		// average
	private final BLOStandardDeviation 	stdevOp;	// standard deviation


	/**
	 * Creates an instance of {@link BLONormalizeStandardScore}
	 * @param binList input list
	 */
	public BLONormalizeStandardScore(BinList binList) {
		this.binList = binList;
		avgOp = new BLOAverage(binList, null);
		stdevOp = new BLOStandardDeviation(binList, null);
	}

	@Override
	public BinList compute() throws Exception {
		// compute average
		final double avg = avgOp.compute();
		// compute standard deviation
		final double stdev = stdevOp.compute();
		// retrieve data precision
		final DataPrecision precision = binList.getPrecision();
		// retrieve singleton operation pool
		final OperationPool op = OperationPool.getInstance();
		// creates collection of thread for the operation pool
		final Collection<Callable<List<Double>>> threadList = new ArrayList<Callable<List<Double>>>();
		for (short i = 0; i < binList.size(); i++)  {
			final List<Double> currentList = binList.get(i);

			Callable<List<Double>> currentThread = new Callable<List<Double>>() {	
				@Override
				public List<Double> call() throws Exception {
					List<Double> resultList = null;
					if ((currentList != null) && (currentList.size() != 0)) {
						resultList = ListFactory.createList(precision, currentList.size());
						for (int j = 0; j < currentList.size(); j++) {
							if (currentList.get(j) != 0) {
								// apply the standard score formula: (x - avg) / stdev 
								double resultScore = (currentList.get(j) - avg) / stdev; 
								resultList.set(j, resultScore);
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
		return "Operation: Normalize, Standard Score";
	}


	@Override
	public String getProcessingDescription() {
		return "Normalizing";
	}


	@Override
	public int getStepCount() {
		// 1 for this operation, 1 f
		return 1 + avgOp.getStepCount() + stdevOp.getStepCount() + BinList.getCreationStepCount(binList.getBinSize());
	}
}
