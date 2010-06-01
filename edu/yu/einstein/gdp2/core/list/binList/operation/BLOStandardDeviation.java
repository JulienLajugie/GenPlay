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

import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.util.Utils;


/**
 * Computes the standard deviation of the {@link BinList}
 * @author Julien Lajugie
 * @version 0.1
 */
public class BLOStandardDeviation implements BinListOperation<Double> {

	private final BinList 	binList;		// input BinList
	private final boolean[] chromoList;		// 1 boolean / chromosome. 
	// each boolean sets to true means that the corresponding chromosome is selected
	private Double 			average = null;	// average of the binList


	/**
	 * Computes the standard deviation of the {@link BinList}
	 * @param binList input {@link BinList}
	 * @param chromoList list of boolean. A boolean set to true means that the 
	 * chromosome with the same index is going to be used for the calculation. 
	 */
	public BLOStandardDeviation(BinList binList, boolean[] chromoList) {
		this.binList = binList;
		this.chromoList = chromoList;
	}



	@Override
	public Double compute() throws InterruptedException, ExecutionException {
		// if the standard deviation has to be calculated on all chromosome 
		// and if it has already been calculated we don't do the calculation again
		if ((Utils.allChromosomeSelected(chromoList)) && (binList.getStDev() != null)) {
			return binList.getStDev();
		}		

		// count the number of non null bins
		long count = new BLOCountNonNullBins(binList, chromoList).compute();
		if (count == 0) {
			return 0d;
		}
		// compute the average
		average = new BLOAverage(binList, chromoList, count).compute();
		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<Double>> threadList = new ArrayList<Callable<Double>>();
		for (int i = 0; i < binList.size(); i++) {
			if (((chromoList == null) || ((i < chromoList.length) && (chromoList[i]))) && (binList.get(i) != null)) {
				final List<Double> currentList = binList.get(i);

				Callable<Double> currentThread = new Callable<Double>() {	
					@Override
					public Double call() throws Exception {
						double sum = 0;
						for (int j = 0; j < currentList.size(); j++) {
							if (currentList.get(j) != 0) {
								sum += Math.pow(currentList.get(j) - average, 2);
							}
						}
						// tell the operation pool that a chromosome is done
						op.notifyDone();
						return sum;
					}
				};

				threadList.add(currentThread);
			}			
		}		

		List<Double> result = op.startPool(threadList);
		if (result == null) {
			return null;
		}
		// sum the result of each chromosome
		double total = 0;
		for (Double currentSum: result) {
			total += currentSum;
		}
		return Math.sqrt(total / (double) count);
	}


	@Override
	public String getDescription() {
		return "Operation: Standard Deviation";
	}
	
	
	@Override
	public int getStepCount() {
		return 1;
	}
}
