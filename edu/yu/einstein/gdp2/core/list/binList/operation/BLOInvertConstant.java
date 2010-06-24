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
 * Inverses the specified {@link BinList}. Applies the function f(x) = a / x, where a is a specified double
 * @author Julien Lajugie
 * @version 0.1
 */
public class BLOInvertConstant implements Operation<BinList> {

	private final BinList 	binList;		// input BinList 
	private final double 	constant;		// coefficient a in f(x) = a / x
	
	
	/**
	 * Creates an instance of {@link BLOInvertConstant}
	 * @param binList input {@link BinList}
	 * @param constant constant a in f(x) = a / x
	 */
	public BLOInvertConstant(BinList binList, double constant) {
		this.binList = binList;
		this.constant = constant;
	}

	
	@Override
	public BinList compute() throws Exception {
		if (constant == 0) {
			return null;
		}
		
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
						for (int j = 0; j < currentList.size(); j++) {
							if(currentList.get(j) == 0) {
								resultList.set(j, 0d);
							} else {
								resultList.set(j, constant / currentList.get(j));
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
		return "Operation: Invert, constant = " + constant;
	}

	
	@Override
	public String getProcessingDescription() {
		return "Inverting";
	}

	
	@Override
	public int getStepCount() {
		return 1 + BinList.getCreationStepCount(binList.getBinSize());
	}
}