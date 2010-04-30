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
import yu.einstein.gdp2.exception.BinListDifferentWindowSizeException;


/**
 * Subtracts the scores of the bins of two specified BinLists
 * @author Julien Lajugie
 * @version 0.1
 */
public class BLOSubtract implements BinListOperation<BinList> {

	private final BinList 		binList1;	// first binlist  
	private final BinList 		binList2; 	// second binlist, result = binlist1 - binlist2
	private final DataPrecision precision;	// precision of the result list


	/**
	 * Subtracts the scores of the bins of the two specified BinLists: binList1 - binList2
	 * @param binList1
	 * @param binList2
	 * @param precision precision of the result {@link BinList} 
	 */
	public BLOSubtract(BinList binList1, BinList binList2, DataPrecision precision) {
		this.binList1 = binList1;
		this.binList2 = binList2;
		this.precision = precision;
	}


	@Override
	public BinList compute() throws InterruptedException, ExecutionException, BinListDifferentWindowSizeException {
		// make sure that the two binlists have the same size of bins
		if (binList1.getBinSize() != binList2.getBinSize()) {
			throw new BinListDifferentWindowSizeException();
		}

		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<List<Double>>> threadList = new ArrayList<Callable<List<Double>>>();
		for (short i = 0; i < binList1.size(); i++)  {
			final List<Double> currentList1 = binList1.get(i);
			final List<Double> currentList2 = binList2.get(i);

			Callable<List<Double>> currentThread = new Callable<List<Double>>() {	
				@Override
				public List<Double> call() throws Exception {
					if ((currentList1 == null) || (currentList1.size() == 0) || (currentList2 == null) || (currentList2.size() == 0)) {
						return null;
					} else {
						List<Double> resultList = ListFactory.createList(precision, currentList1.size());
						for(int j = 0; j < currentList1.size(); j++) {
							if (j < currentList2.size()) {
								// we subtract the bins of the two binlists
								resultList.set(j, currentList1.get(j) - currentList2.get(j));
							} else {
								resultList.set(j, 0d);
							}
						}
						// tell the operation pool that a chromosome is done
						op.notifyDone();
						return resultList;
					}
				}
			};

			threadList.add(currentThread);
		}
		List<List<Double>> result = op.startPool(threadList);
		if (result != null) {
			BinList resultList = new BinList(binList1.getChromosomeManager(), binList1.getBinSize(), precision, result);
			return resultList;
		} else {
			return null;
		}
	}
	

	@Override
	public String getDescription() {
		return "Operation: Subtract";
	}
}
