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
 * Computes the average value of the scores of the {@link BinList}
 * @author Julien Lajugie
 * @version 0.1
 */
public class BLOAverage implements BinListOperation<Double> {

	private final BinList 	binList;		// input BinList
	private final boolean[] chromoList;		// 1 boolean / chromosome. 
	// each boolean sets to true means that the corresponding chromosome is selected
	private Long 			count = null;	// count of non null bins

	/**
	 * Computes the average value of the scores of the {@link BinList}
	 * @param binList input {@link BinList}
	 * @param chromoList list of boolean. A boolean set to true means that the 
	 * chromosome with the same index is going to be used for the calculation. 
	 */
	public BLOAverage(BinList binList, boolean[] chromoList) {
		this.binList = binList;
		this.chromoList = chromoList;
	}


	/**
	 * Computes the average value of the scores of the {@link BinList}
	 * @param binList input {@link BinList}
	 * @param chromoList list of boolean. A boolean set to true means that the 
	 * chromosome with the same index is going to be used for the calculation.
	 * @param count count of non null bins 
	 */
	public BLOAverage(BinList binList, boolean[] chromoList, long count) {
		this.binList = binList;
		this.chromoList = chromoList;
		this.count = count;
	}
	

	@Override
	public Double compute() throws InterruptedException, ExecutionException {
		// if the average has to be calculated on all chromosome 
		// and if it has already been calculated we don't do the calculation again
		if ((Utils.allChromosomeSelected(chromoList)) && (binList.getAverage() != null)) {
			return binList.getAverage();
		}		

		// count the number of non null bins if wasn't specified in the constructor  
		if (count == null) {
			count = new BLOCountNonNullBins(binList, chromoList).compute();
		}
		// if there is no none-null value we return 0
		if (count == 0) {
			return 0d;
		}

		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<Double>> threadList = new ArrayList<Callable<Double>>();
		for (int i = 0; i < binList.size(); i++) {
			final List<Double> currentList = binList.get(i);
			final int currentIndex = i;
			Callable<Double> currentThread = new Callable<Double>() {
				@Override
				public Double call() throws Exception {
					double sum = 0;
					if (((chromoList == null) || ((currentIndex < chromoList.length) && (chromoList[currentIndex]))) && (binList.get(currentIndex) != null)) {
						for (int j = 0; j < currentList.size(); j++) {
							if (currentList.get(j) != 0) {
								sum += currentList.get(j);
							}
						}
					}
					// tell the operation pool that a chromosome is done
					op.notifyDone();
					return sum;
				}
			};

			threadList.add(currentThread);
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
		return total / (double) count;

	}


	@Override
	public String getDescription() {
		return "Operation: Average";
	}
}
