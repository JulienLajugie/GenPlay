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
import yu.einstein.gdp2.util.Utils;


/**
 * Computes the sum of the lengths (in bp) of the non-null (different from 0) windows
 * @author Julien Lajugie
 * @version 0.1
 */
public class SCWLOCountNonNullLength implements Operation<Long>{
	
	private final ScoredChromosomeWindowList scwList;	// input list
	private final boolean[] chromoList;		// 1 boolean / chromosome. 
	// each boolean sets to true means that the corresponding chromosome is selected
	private boolean				stopped = false;// true if the operation must be stopped

	
	/**
	 * Computes the sum of the lengths (in bp) of the non-null (different from 0) windows
	 * @param scwList input list
	 * @param chromoList list of boolean. A boolean set to true means that the 
	 * chromosome with the same index is going to be used for the calculation. 
	 */
	public SCWLOCountNonNullLength(ScoredChromosomeWindowList scwList, boolean[] chromoList) {
		this.scwList = scwList;
		this.chromoList = chromoList;
	}
	
	
	@Override
	public Long compute() throws Exception {
		// if the operation has to be calculated on all chromosome 
		// and if it has already been calculated we don't do the calculation again
		if ((Utils.allChromosomeSelected(chromoList)) && (scwList.getNonNullLength() != null)) {
			return scwList.getNonNullLength();
		}	
		
		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<Long>> threadList = new ArrayList<Callable<Long>>();
		for (int i = 0; i < scwList.size(); i++) {
			if (((chromoList == null) || ((i < chromoList.length) && (chromoList[i]))) && (scwList.get(i) != null)) {
				final List<ScoredChromosomeWindow> currentList = scwList.get(i);
				
				Callable<Long> currentThread = new Callable<Long>() {	
					@Override
					public Long call() throws Exception {
						long length = 0;
						for (int j = 0; j < currentList.size() && !stopped; j++) {
							ScoredChromosomeWindow currentWindow = currentList.get(j);
							if (currentWindow.getScore() != 0) {
								length += currentWindow.getSize();
							}
						}
						// tell the operation pool that a chromosome is done
						op.notifyDone();
						return length;
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
		for (Long currentLength: result) {
			total += currentLength;
		}
		return total;
	}

	@Override
	public String getDescription() {
		return "Operation: Compute Length";
	}

	
	@Override
	public String getProcessingDescription() {
		return "Computing Length";
	}

	
	@Override
	public int getStepCount() {
		return 1;
	}

	
	@Override
	public void stop() {
		this.stopped = true;
	}
}
