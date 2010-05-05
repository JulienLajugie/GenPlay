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
import yu.einstein.gdp2.exception.BinListDifferentWindowSizeException;


/**
 * Computes the correlation coefficient between two {@link BinList}.
 * @author Julien Lajugie
 * @version 0.1
 */
public class BLOCorrelation implements BinListOperation<Double> {

	private final BinList 	binList1;		// input BinList
	private final BinList	binList2;		// input BinList
	private final boolean[] chromoList;		// 1 boolean / chromosome. 
	// each boolean sets to true means that the corresponding chromosome is selected
	private int 			counter = 0;	// counter for the none-null value 
	private double 			mean1 = 0;		// average of binList1
	private double 			mean2 = 0;		// average of binList2
	private double 			stdev1 = 0;		// standard deviation of binList1
	private double 			stdev2 = 0;		// standard deviation of binList2
	private double 			correlation = 0;// correlation coefficient

	
	/**
	 * Computes the correlation coefficient between two {@link BinList}.
	 * Only the chromosomes set to <i>true</i> in chromoList will be used in the calculation. 
	 * @param binList input {@link BinList}
	 * @param chromoList list of boolean. A boolean set to true means that the 
	 * chromosome with the same index is going to be used for the calculation. 
	 */
	public BLOCorrelation(BinList binList1, BinList binList2, boolean[] chromoList) {
		this.binList1 = binList1;
		this.binList2 = binList2;
		this.chromoList = chromoList;
	}


	/**
	 * Increments the counter of none-null value. Thread safe.
	 */
	private synchronized void incrementCounter() {
		counter++;
	}

	
	/**
	 * Adds a value to mean1 in a thread safe method
	 * @param valueToAdd value to add
	 */
	private synchronized void addToMean1(double valueToAdd) {
		mean1 += valueToAdd;
	}


	/**
	 * Adds a value to mean2 in a thread safe method
	 * @param valueToAdd value to add
	 */
	private synchronized void addToMean2(double valueToAdd) {
		mean2 += valueToAdd;
	}


	/**
	 * Adds a value to stdev1 in a thread safe method
	 * @param valueToAdd value to add
	 */
	private synchronized void addToStdev1(double valueToAdd) {
		stdev1 += valueToAdd;
	}


	/**
	 * Adds a value to stdev2 in a thread safe method
	 * @param valueToAdd value to add
	 */
	private synchronized void addToStdev2(double valueToAdd) {
		stdev2 += valueToAdd;
	}	


	/**
	 * Adds a value to correlation in a thread safe method
	 * @param valueToAdd value to add
	 */
	private synchronized void addToCorrelation(double valueToAdd) {
		correlation += valueToAdd;
	}


	@Override
	public Double compute() throws InterruptedException, ExecutionException, BinListDifferentWindowSizeException {
		try {
			computeMeans();
		} catch (InterruptedException e) {
			// it the computation of the averages had been interrupted we return null
			return null;
		}
		// if there is no none-null value we return 0
		if (counter == 0) { 
			return 0d;
		}		

		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<Void>> threadList = new ArrayList<Callable<Void>>();
		for (int i = 0; i < binList1.size(); i++) {
			if (((chromoList == null) || ((i < chromoList.length) && (chromoList[i]))) && (binList1.get(i) != null) && (binList2.get(i) != null)) {
				final List<Double> currentList1 = binList1.get(i);
				final List<Double> currentList2 = binList2.get(i);

				Callable<Void> currentThread = new Callable<Void>() {	
					@Override
					public Void call() throws Exception {
						int j = 0;
						while ((j < currentList1.size()) && (j < currentList2.size())) {
							if ((currentList1.get(j) != 0) && (currentList2.get(j) != 0)) {
								addToStdev1(Math.pow(currentList1.get(j) - mean1, 2));
								addToStdev2(Math.pow(currentList2.get(j) - mean2, 2));
								addToCorrelation(currentList1.get(j) * currentList2.get(j));
							}
							j++;
						}						
						// tell the operation pool that a chromosome is done
						op.notifyDone();
						return null;
					}
				};

				threadList.add(currentThread);
			}			
		}		

		if (op.startPool(threadList) == null) {
			return null;
		}
		stdev1 = Math.sqrt(stdev1 / counter);
		stdev2 = Math.sqrt(stdev2 / counter);
		// We compute the correlation 
		correlation = (correlation - (counter * mean1 * mean2)) / ((counter - 1) * stdev1 * stdev2);
		return correlation;
	}


	/**
	 * Computes the means of the two BinLists. 
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws BinListDifferentWindowSizeException
	 */
	private void computeMeans() throws InterruptedException, ExecutionException, BinListDifferentWindowSizeException {
		if (binList1.getBinSize() != binList2.getBinSize()) {
			throw new BinListDifferentWindowSizeException();
		}

		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<Void>> threadList = new ArrayList<Callable<Void>>();
		for (int i = 0; i < binList1.size(); i++) {
			if (((chromoList == null) || ((i < chromoList.length) && (chromoList[i]))) && (binList1.get(i) != null) && (binList2.get(i) != null)) {
				final List<Double> currentList1 = binList1.get(i);
				final List<Double> currentList2 = binList2.get(i);

				Callable<Void> currentThread = new Callable<Void>() {	
					@Override
					public synchronized Void call() throws Exception {
						int j = 0;
						// compute the average only when the two scores are not null
						while ((j < currentList1.size()) && (j < currentList2.size())) {
							if ((currentList1.get(j) != 0) && (currentList2.get(j) != 0)) {
								synchronized (BLOCorrelation.this) {
									addToMean1(currentList1.get(j));
									addToMean2(currentList2.get(j));
									incrementCounter();
								}
							}
							j++;
						}


						// tell the operation pool that a chromosome is done
						op.notifyDone();
						return null;
					}
				};

				threadList.add(currentThread);
			}			
		}		

		if (op.startPool(threadList) == null) {
			throw new InterruptedException();
		}
		// if there is no none-null value we return 0
		if (counter != 0) {
			mean1 /= counter;
			mean2 /= counter;
		}
	}


	@Override
	public String getDescription() {
		return "Operation: Correlation Coefficient";
	}
}
