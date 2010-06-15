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
 * Searches the minimum value of the selected chromosomes of a specified {@link ScoredChromosomeWindowList}
 * @author Julien Lajugie
 * @version 0.1
 */
public class SCWLOMin implements Operation<Double> {
	private final boolean[] chromoList;	// list of the selected chromosomes
	private final ScoredChromosomeWindowList scwList; // input list
	
	
	/**
	 * Searches the minimum value of the selected chromosomes of a specified {@link ScoredChromosomeWindowList}
	 * @param scwList input list
	 * @param chromoList list of boolean. A boolean set to true means that the 
	 * chromosome with the same index is going to be used for the calculation. 
	 */
	public SCWLOMin(ScoredChromosomeWindowList scwList, boolean[] chromoList) {
		this.scwList = scwList;
		this.chromoList = chromoList;
	}
	
	
	@Override
	public Double compute() throws Exception {
		// if the operation has to be calculated on all chromosome 
		// and if it has already been calculated we don't do the calculation again
		if ((Utils.allChromosomeSelected(chromoList)) && (scwList.getMin() != null)) {
			return scwList.getMin();
		}	
		
		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<Double>> threadList = new ArrayList<Callable<Double>>();
		for (int i = 0; i < scwList.size(); i++) {
			if (((chromoList == null) || ((i < chromoList.length) && (chromoList[i]))) && (scwList.get(i) != null)) {
				final List<ScoredChromosomeWindow> currentList = scwList.get(i);
				
				Callable<Double> currentThread = new Callable<Double>() {	
					@Override
					public Double call() throws Exception {
						// we set the min to the smallest double value
						double min = Double.POSITIVE_INFINITY;
						for (int j = 0; j < currentList.size(); j++) {
							if (currentList.get(j).getScore() != 0) {
								min = Math.min(min, currentList.get(j).getScore());					
							}
						}
						// tell the operation pool that a chromosome is done
						op.notifyDone();
						return min;
					}
				};
			
				threadList.add(currentThread);
			}			
		}
		List<Double> result = op.startPool(threadList);
		if (result == null) {
			return null;
		}
		// we search for the min of the chromosome minimums
		double min = Double.POSITIVE_INFINITY;
		for (Double currentMax: result) {
			min = Math.min(min, currentMax);
		}
		return min;
	}

	
	@Override
	public String getDescription() {
		return "Operation: Minimum";
	}

	
	@Override
	public String getProcessingDescription() {
		return "Searching Minimum";
	}

	
	@Override
	public int getStepCount() {
		return 1;
	}
}
