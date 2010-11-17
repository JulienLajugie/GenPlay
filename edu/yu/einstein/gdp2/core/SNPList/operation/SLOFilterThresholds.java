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
 * Removes the SNPs where the first and second base counts are smaller than specified thresholds
 * @author Julien Lajugie
 * @version 0.1
 */
public class SLOFilterThresholds implements Operation<SNPList> {

	private final SNPList 	inputList;				// input list
	private final int 		firstBaseThreshold;		// first base count must be greater than this threshold
	private final int 		secondBaseThreshold;	// second base count must be greater than this threshold
	private boolean			stopped = false;		// true if the operation must be stopped
	
	
	/**
	 * Creates an instance of {@link SLOFilterThresholds}
	 * @param inputList input SNP list
	 * @param firstBaseThreshold first base count must be greater than this threshold
	 * @param secondBaseThreshold second base count must be greater than this threshold
	 */
	public SLOFilterThresholds(SNPList inputList, int firstBaseThreshold, int secondBaseThreshold) {
		this.inputList = inputList;
		this.firstBaseThreshold = firstBaseThreshold;
		this.secondBaseThreshold = secondBaseThreshold;
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
							if ((currentFirstBaseCount >= firstBaseThreshold) && 
									(currentSecondBaseCount >= secondBaseThreshold)) {
									resultList.add(currentList.get(i));
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
		return "Operation: Filter, First base filter = " + firstBaseThreshold + ", Second base filter = " + secondBaseThreshold;
	}
	

	@Override
	public String getProcessingDescription() {
		return "Filtering";
	}

	
	@Override
	public int getStepCount() {
		return 2;
	}

	
	@Override
	public void stop() {
		this.stopped = true;
	}
}
