/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.list.SCWList.operation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

import yu.einstein.gdp2.core.ScoredChromosomeWindow;
import yu.einstein.gdp2.core.list.SCWList.ScoredChromosomeWindowList;
import yu.einstein.gdp2.core.operation.Operation;
import yu.einstein.gdp2.core.operationPool.OperationPool;


/**
 * Removes the values between two specified thresholds
 * @author Julien Lajugie
 * @version 0.1
 */
public class SCWLOFilterBandStop implements Operation<ScoredChromosomeWindowList> {

	private final ScoredChromosomeWindowList 	inputList;		// input SCW list
	private final double 						lowThreshold;	// low bound 
	private final double 						highThreshold;	// high bound
	private boolean								stopped = false;// true if the operation must be stopped
	
	
	/**
	 * Creates an instance of {@link SCWLOFilterBandStop}
	 * @param inputList input {@link ScoredChromosomeWindowList}
	 * @param lowThreshold low threshold
	 * @param highThreshold high threshold
	 */
	public SCWLOFilterBandStop(ScoredChromosomeWindowList inputList, double lowThreshold, double highThreshold) {
		this.inputList = inputList;
		this.lowThreshold = lowThreshold;
		this.highThreshold = highThreshold;
	}
	
	
	@Override
	public ScoredChromosomeWindowList compute() throws Exception {
		if (lowThreshold >= highThreshold) {
			throw new IllegalArgumentException("The high threshold must be greater than the low one");
		}	

		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<List<ScoredChromosomeWindow>>> threadList = new ArrayList<Callable<List<ScoredChromosomeWindow>>>();
		for (final List<ScoredChromosomeWindow> currentList: inputList) {

			Callable<List<ScoredChromosomeWindow>> currentThread = new Callable<List<ScoredChromosomeWindow>>() {			
				@Override
				public List<ScoredChromosomeWindow> call() throws Exception {
					List<ScoredChromosomeWindow> resultList = null;
					if ((currentList != null) && (currentList.size() != 0)) {
						resultList = new ArrayList<ScoredChromosomeWindow>();
						for (int j = 0; j < currentList.size() && !stopped; j++) {
							double currentValue = currentList.get(j).getScore(); 
							if ((currentValue < lowThreshold) || (currentValue > highThreshold)) {
								ScoredChromosomeWindow windowToAdd = new ScoredChromosomeWindow(currentList.get(j));
								resultList.add(windowToAdd);
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
		List<List<ScoredChromosomeWindow>> result = op.startPool(threadList);
		if (result != null) {
			ScoredChromosomeWindowList resultList = new ScoredChromosomeWindowList(result);
			return resultList;
		} else {
			return null;
		}
	}


	@Override
	public String getDescription() {
		return "Operation: Band-Stop Filter, Low Threshold = " + lowThreshold + ", High Threshold = " + highThreshold;
	}


	@Override
	public String getProcessingDescription() {
		return "Filtering";
	}


	@Override
	public int getStepCount() {
		return 1 + ScoredChromosomeWindowList.getCreationStepCount();
	}

	
	@Override
	public void stop() {
		this.stopped = true;
	}
}
