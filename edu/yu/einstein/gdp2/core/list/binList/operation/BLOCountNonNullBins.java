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
import yu.einstein.gdp2.core.operationPool.OperationPool;
import yu.einstein.gdp2.util.Utils;


/**
 * Counts the non-null (different from 0) bins of the {@link BinList}
 * @author Julien Lajugie
 * @version 0.1
 */
public class BLOCountNonNullBins implements BinListOperation<Long> {

	private final BinList 	binList;		// input BinList
	private final boolean[] chromoList;		// 1 boolean / chromosome. 
	// each boolean sets to true means that the corresponding chromosome is selected
	
	
	/**
	 * Counts the non-null (different from 0) bins of the {@link BinList}
	 * @param binList input {@link BinList}
	 * @param chromoList list of boolean. A boolean set to true means that the 
	 * chromosome with the same index is going to be used for the calculation. 
	 */
	public BLOCountNonNullBins(BinList binList, boolean[] chromoList) {
		this.binList = binList;
		this.chromoList = chromoList;
	}
	
	
	
	@Override
	public Long compute() throws InterruptedException, ExecutionException {
		// if the count has to be calculated on all chromosome 
		// and if it has already been calculated we don't do the calculation again
		if ((Utils.allChromosomeSelected(chromoList)) && (binList.getBinCount() != null)) {
			return binList.getBinCount();
		}		

		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<Long>> threadList = new ArrayList<Callable<Long>>();
		for (int i = 0; i < binList.size(); i++) {
			if (((chromoList == null) || ((i < chromoList.length) && (chromoList[i]))) && (binList.get(i) != null)) {
				final List<Double> currentList = binList.get(i);
				
				Callable<Long> currentThread = new Callable<Long>() {	
					@Override
					public Long call() throws Exception {
						long count = 0;
						for (int j = 0; j < currentList.size(); j++) {
							if (currentList.get(j) != 0) {
								count++;
							}
						}
						// tell the operation pool that a chromosome is done
						op.notifyDone();
						return count;
					}
				};
			
				threadList.add(currentThread);
			}			
		}		
		
		List<Long> result = op.startPool(threadList);
		if (result == null) {
			return null;
		}
		// sum the result of each chromosome
		long total = 0;
		for (Long currentCounter: result) {
			total += currentCounter;
		}
		return total;
	}

	
	@Override
	public String getDescription() {
		return "Operation: Count Non-Null Bins";
	}
	
	
	@Override
	public int getStepCount() {
		return 1;
	}
	
	
	@Override
	public String getProcessingDescription() {
		return "Counting Non Null Windows";
	}
}
