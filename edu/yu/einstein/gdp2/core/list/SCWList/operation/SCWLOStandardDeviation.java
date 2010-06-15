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
 * Computes the average value of the scores of the {@link ScoredChromosomeWindowList}
 * @author Julien Lajugie
 * @version 0.1
 */
public class SCWLOStandardDeviation implements Operation<Double> {

	private final boolean[] chromoList; // list of the selected chromosomes
	private final ScoredChromosomeWindowList scwList; // input list


	/**
	 * Computes the standard deviation of the list
	 * @param scwList ChromosomeListOfLists of ScoredChromosomeWindow
	 * @param chromoList set to true each chromosome of this list that you want to use in the calculation
	 * Perform the operation on every chromosome if null
	 */
	public SCWLOStandardDeviation(ScoredChromosomeWindowList scwList, boolean[] chromoList) {
		this.chromoList = chromoList;
		this.scwList = scwList;
	}


	@Override
	public Double compute() throws Exception {
		// if the operation has to be calculated on all chromosome 
		// and if it has already been calculated we don't do the calculation again
		if ((Utils.allChromosomeSelected(chromoList)) && (scwList.getStDev() != null)) {
			return scwList.getStDev();
		}	
		
		// computes the sum of the length of the non-null windows
		Long length = new SCWLOCountNonNullLength(scwList, chromoList).compute();
		if (length == 0) {
			return 0d;
		}
		// compute the mean
		final double mean = new SCWLOAverage(scwList, chromoList, length).compute();
		
		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<Double>> threadList = new ArrayList<Callable<Double>>();

		for (int i = 0; i < scwList.size(); i++) {
			final List<ScoredChromosomeWindow> currentList = scwList.get(i);
			final int currentIndex = i;

			Callable<Double> currentThread = new Callable<Double>() {	
				@Override
				public Double call() throws Exception {
					double stDev = 0;
					if (((chromoList == null) || ((currentIndex < chromoList.length) && (chromoList[currentIndex]))) && (scwList.get(currentIndex) != null)) {
						for(ScoredChromosomeWindow currentWindow : currentList) {	
							if (currentWindow.getScore() != 0) {
								stDev += Math.pow(currentWindow.getScore() - mean, 2) * currentWindow.getSize();
							}
						}
					}
					// tell the operation pool that a chromosome is done
					op.notifyDone();
					return stDev;
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
		for (Double currentResult: result) {
			total += currentResult;
		}
		return Math.sqrt(total / (double) length);
	}


	@Override
	public String getDescription() {
		return "Operation: Standard Deviation";
	}


	@Override
	public String getProcessingDescription() {
		return "Computing Standard Deviation";
	}


	@Override
	public int getStepCount() {
		// 1 for the stardard deviation and 1 for the average
		return 1 + 1;
	}
}
