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
import yu.einstein.gdp2.core.operation.Operation;
import yu.einstein.gdp2.core.operationPool.OperationPool;


/**
 * Normalizes a {@link BinList} and multiplies the result by a factor
 * @author Julien Lajugie
 * @version 0.1
 */
public class BLONormalize implements Operation<BinList> {

	private final BinList 	binList;		// input BinList
	private final double	factor;			// the result of the normalization is multiplied by this factor
	private Double 			scoreSum;		// sum of the scores
	 
	
	
	/**
	 * Normalizes a {@link BinList} and multiplies the result by a specified factor
	 * @param binList BinList to normalize
	 * @param factor factor
	 */
	public BLONormalize(BinList binList, double factor) {
		this.binList = binList;
		this.factor = factor;
	}
	
	
	@Override
	public BinList compute() throws InterruptedException, ExecutionException {
		scoreSum = new BLOSumScore(binList, null).compute();
		// we want to multiply each bin by the following coefficient
		final double coef = factor / scoreSum;
		final DataPrecision precision = binList.getPrecision();
		
		final OperationPool op = OperationPool.getInstance();
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
							// we multiply each bin by the coefficient previously computed
							resultList.set(j, currentList.get(j) * coef);
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
		return "Operation: Normalize, Factor = " + factor;
	}
	
	
	@Override
	public int getStepCount() {
		return BinList.getCreationStepCount(binList.getBinSize()) + 1;
	}
	
	
	@Override
	public String getProcessingDescription() {
		return "Normalizing";
	}
}
