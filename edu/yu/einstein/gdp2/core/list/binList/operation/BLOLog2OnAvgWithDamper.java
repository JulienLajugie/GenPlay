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
 * Applies the function f(x)=log((x + damper) / (avg + damper)) to each score x of the {@link BinList}
 * @author Julien Lajugie
 * @version 0.1
 */
public class BLOLog2OnAvgWithDamper implements BinListOperation<BinList> {

	private final BinList 	binList;	// input binlist
	private final double	damper;		// damper
	
	/**
	 * Applies the function f(x)=log((x + damper) / (avg + damper)) to each score x of the {@link BinList}
	 * @param binList input {@link BinList}
	 */
	public BLOLog2OnAvgWithDamper(BinList binList, double damper) {
		this.binList = binList;
		this.damper = damper;
	}
	
	
	@Override
	public BinList compute() throws InterruptedException, ExecutionException, ArithmeticException {
		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<List<Double>>> threadList = new ArrayList<Callable<List<Double>>>();
		final DataPrecision precision = binList.getPrecision();

		// compute log2(average)
		final double mean = binList.getAverage();
		final double logMean;
		// log is defined on R+*
		if (mean + damper > 0) {
			// change of base: logb(x) = logk(x) / logk(b)
			logMean = Math.log(mean + damper) / Math.log(2);
		} else {
			throw new ArithmeticException("Logarithm of a negative value not allowed");
		}		
		
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
							// make sure that the list 
							if (currentList.get(j) > 0) {
							// change of base: logb(x) = logk(x) / logk(b)
								resultList.set(j, Math.log(currentList.get(j) + damper) / Math.log(2) - logMean);
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
			BinList resultList = new BinList(binList.getChromosomeManager(), binList.getBinSize(), precision, result);
			return resultList;
		} else {
			return null;
		}
	}

	
	@Override
	public String getDescription() {
		return "Operation: Log2 on Average, with damper - f(x) = log((x + " + damper + ") / (average + " + damper + "))";
	}
}
