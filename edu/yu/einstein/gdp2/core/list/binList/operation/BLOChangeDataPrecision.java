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


/**
 * Creates a new BinList with a new data precision
 * @author Julien Lajugie
 * @version 0.1
 */
public class BLOChangeDataPrecision implements BinListOperation<BinList> {

	private final BinList 		binList;	// input BinList
	private final DataPrecision precision; 	// new data precision


	/**
	 * Creates a new BinList with a new data precision
	 * @param binList input BinList
	 * @param precision new precision
	 */
	public BLOChangeDataPrecision(BinList binList, DataPrecision precision) {
		this.binList = binList;
		this.precision = precision; 
	}


	@Override
	public BinList compute() throws InterruptedException, ExecutionException {
		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<List<Double>>> threadList = new ArrayList<Callable<List<Double>>>();
		for(short i = 0; i < binList.size(); i++)  {
			final List<Double> currentList = binList.get(i);


			Callable<List<Double>> currentThread = new Callable<List<Double>>() {	
				@Override
				public List<Double> call() throws Exception {
					List<Double> resultList = null;
					if ((currentList != null) && (currentList.size() != 0)) {
						resultList = ListFactory.createList(precision, currentList.size());
						for (int j = 0; j < currentList.size(); j++) {
							resultList.set(j, currentList.get(j));
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
			BinList resultList = new BinList(binList.getChromosomeManager(), binList.getBinSize(), precision, result);
			return resultList;
		} else {
			return null;
		}
	}
	

	@Override
	public String getDescription() {
		return "Precision Changes to " + precision;
	}
}
