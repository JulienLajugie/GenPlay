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
import yu.einstein.gdp2.core.enums.LogBase;
import yu.einstein.gdp2.core.list.arrayList.ListFactory;
import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.operation.Operation;
import yu.einstein.gdp2.core.operationPool.OperationPool;


/**
 * Applies the function f(x)=log(x) to each score x of the {@link BinList}
 * @author Julien Lajugie
 * @version 0.1
 */
public class BLOLog implements Operation<BinList> {

	private final BinList 	binList;		// input binlist
	private final LogBase	logBase;		// base of the log
	private boolean			stopped = false;// true if the operation must be stopped
	
	
	/**
	 * Applies the function f(x)=log(x) to each score x of the {@link BinList}
	 * @param binList input {@link BinList}
	 * @param logBase base of the logarithm
	 */
	public BLOLog(BinList binList, LogBase logBase) {
		this.binList = binList;
		this.logBase = logBase;
	}
	
	
	@Override
	public BinList compute() throws InterruptedException, ExecutionException {
		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<List<Double>>> threadList = new ArrayList<Callable<List<Double>>>();
		final DataPrecision precision = binList.getPrecision();

		for (short i = 0; i < binList.size(); i++) {
			final List<Double> currentList = binList.get(i);
			
			Callable<List<Double>> currentThread = new Callable<List<Double>>() {	
				@Override
				public List<Double> call() throws Exception {
					List<Double> resultList = null;
					if ((currentList != null) && (currentList.size() != 0)) {
						resultList = ListFactory.createList(precision, currentList.size());
						// We add a constant to each element
						for (int j = 0; j < currentList.size() && !stopped; j++) {
							// log is define on R+*
							if (currentList.get(j) > 0) {
								double resultValue;
								if (logBase == LogBase.BASE_E) {
									// the Math.log function return the natural log (no needs to change the base)
									resultValue = Math.log(currentList.get(j));
								} else {
									// change of base: logb(x) = logk(x) / logk(b)
									resultValue = Math.log(currentList.get(j)) / Math.log(logBase.getValue());									
								}
								resultList.set(j, resultValue);
							} else if (currentList.get(j) == 0) {
								resultList.set(j, 0d);
							} else {
								// can't apply a log function on a negative or null numbers
								throw new ArithmeticException("Logarithm of a negative value not allowed");
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
		return "Operation: Log, Base = " + logBase;
	}
	
	
	@Override
	public int getStepCount() {
		return BinList.getCreationStepCount(binList.getBinSize()) + 1;
	}
	
	
	@Override
	public String getProcessingDescription() {
		return "Logging";
	}

	
	@Override
	public void stop() {
		this.stopped = true;
	}
}
