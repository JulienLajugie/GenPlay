/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.list.SCWList.operation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import yu.einstein.gdp2.core.ScoredChromosomeWindow;
import yu.einstein.gdp2.core.list.SCWList.ScoredChromosomeWindowList;
import yu.einstein.gdp2.core.operation.Operation;
import yu.einstein.gdp2.core.operationPool.OperationPool;


/**
 * Normalizes a {@link ScoredChromosomeWindowList} and multiplies the result by a factor
 * @author Julien Lajugie
 * @version 0.1
 */
public class SCWLONormalize implements Operation<ScoredChromosomeWindowList> {

	private final ScoredChromosomeWindowList 	inputList;		// input ScoredChromosomeWindowList
	private final double						factor;			// the result of the normalization is multiplied by this factor
	private Double 								scoreSum;		// sum of the scores
	private boolean								stopped = false;// true if the operation must be stopped
		
	
	/**
	 * Normalizes a {@link ScoredChromosomeWindowList} and multiplies the result by a specified factor
	 * @param inputList ScoredChromosomeWindowList to normalize
	 * @param factor factor
	 */
	public SCWLONormalize(ScoredChromosomeWindowList inputList, double factor) {
		this.inputList = inputList;
		this.factor = factor;
	}
	
	
	@Override
	public ScoredChromosomeWindowList compute() throws InterruptedException, ExecutionException {
		scoreSum = new SCWLOSumScore(inputList, null).compute();
		// we want to multiply each window by the following coefficient
		final double coef = factor / scoreSum;
		
		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<List<ScoredChromosomeWindow>>> threadList = new ArrayList<Callable<List<ScoredChromosomeWindow>>>();
		for (short i = 0; i < inputList.size(); i++)  {
			final List<ScoredChromosomeWindow> currentList = inputList.get(i);

			Callable<List<ScoredChromosomeWindow>> currentThread = new Callable<List<ScoredChromosomeWindow>>() {	
				@Override
				public List<ScoredChromosomeWindow> call() throws Exception {
					List<ScoredChromosomeWindow> resultList = null;
					if ((currentList != null) && (currentList.size() != 0)) {
						resultList = new ArrayList<ScoredChromosomeWindow>();
						for (int j = 0; j < currentList.size() && !stopped; j++) {
							// we multiply each window by the coefficient previously computed
							ScoredChromosomeWindow windowToAdd = new ScoredChromosomeWindow(currentList.get(j));
							windowToAdd.setScore(currentList.get(j).getScore() * coef);
							resultList.add(windowToAdd);
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
		return "Operation: Normalize, Factor = " + factor;
	}
	
	
	@Override
	public int getStepCount() {
		return ScoredChromosomeWindowList.getCreationStepCount() + 1;
	}
	
	
	@Override
	public String getProcessingDescription() {
		return "Normalizing";
	}

	
	@Override
	public void stop() {
		this.stopped = true;
	}
}
