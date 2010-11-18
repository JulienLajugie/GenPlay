/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.SNPList.operation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

import yu.einstein.gdp2.core.SNP;
import yu.einstein.gdp2.core.SNPList.SNPList;
import yu.einstein.gdp2.core.operation.Operation;
import yu.einstein.gdp2.core.operationPool.OperationPool;


/**
 * Removes the SNPs with a ratio (first base count) on (second base count)
 * greater or smaller than a specified value
 * @author Julien Lajugie
 * @version 0.1
 */
public class SLOFilterRatio implements Operation<SNPList>{

	private final SNPList 	inputList;			// input SNP list
	private final double 	thresholdLow;		// remove SNPs with ratio smaller than this threshold
	private final double 	thresholdHigh;		// remove SNPs with ratio greater than this threshold
	private boolean			stopped = false;	// true if the operation must be stopped


	/**
	 * Creates an instance of {@link SLOFilterRatio}
	 * @param inputList input {@link SNPList} 
	 * @param thresholdLow the SNPs with a (first base count) on (second base count) ratio strictly smaller than this value are removed
	 * @param thresholdHigh the SNPs with a ratio strictly greater than this value are removed
	 */
	public SLOFilterRatio(SNPList inputList, double thresholdLow, double thresholdHigh) {
		this.inputList = inputList;
		this.thresholdHigh = thresholdHigh;
		this.thresholdLow = thresholdLow;
	}	


	@Override
	public SNPList compute() throws Exception {
		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<List<SNP>>> threadList = new ArrayList<Callable<List<SNP>>>();
		for (final List<SNP> currentList: inputList) {

			Callable<List<SNP>> currentThread = new Callable<List<SNP>>() {			
				@Override
				public List<SNP> call() throws Exception {
					List<SNP> resultList = null;
					if ((currentList != null) && (currentList.size() != 0)) {
						resultList = new ArrayList<SNP>();
						for (int i = 0; i < currentList.size() && !stopped; i++) {
							int currentFirstBaseCount = currentList.get(i).getFirstBaseCount();
							int currentSecondBaseCount = currentList.get(i).getSecondBaseCount();
							// we can't calculate the ratio if second base count = 0
							if (currentSecondBaseCount > 0) {
								double ratio = currentFirstBaseCount / (double) currentSecondBaseCount;
								if ((ratio >= thresholdLow) && 
										(ratio <= thresholdHigh)) {
									resultList.add(currentList.get(i));
								}
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
		List<List<SNP>> result = op.startPool(threadList);
		if (result != null) {
			SNPList resultList = new SNPList(result);
			return resultList;
		} else {
			return null;
		}
	}


	@Override
	public String getDescription() {
		return "Operation: Filter ratio, high threshold = " + thresholdHigh + ", low threshold = " + thresholdLow;
	}


	@Override
	public String getProcessingDescription() {
		return "Filtering";
	}


	@Override
	public int getStepCount() {
		return 1;
	}


	@Override
	public void stop() {
		stopped = true;
	}
}
