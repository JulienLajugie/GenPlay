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
import yu.einstein.gdp2.core.list.ChromosomeListOfLists;
import yu.einstein.gdp2.core.list.SCWList.ScoredChromosomeWindowList;
import yu.einstein.gdp2.core.operation.Operation;
import yu.einstein.gdp2.core.operationPool.OperationPool;
import yu.einstein.gdp2.util.Utils;


/**
 * Computes the average value of the scores of the {@link ScoredChromosomeWindowList}
 * @author Julien Lajugie
 * @version 0.1
 */
public class SCWLOAverage implements Operation<Double> {

	private final boolean[] chromoList; // list of the selected chromosomes
	private final ScoredChromosomeWindowList scwList; // input list
	private Long length = null; // sum of the lengths of non null windows

	/**
	 * Computes the average of the list defined as: 
	 * sum(score * length) / sum(length)
	 * @param scwList ChromosomeListOfLists of ScoredChromosomeWindow
	 * @param chromoList set to true each chromosome of this list that you want to use in the calculation
	 * Perform the operation on every chromosome if null
	 */
	public SCWLOAverage(ScoredChromosomeWindowList scwList, boolean[] chromoList) {
		this.chromoList = chromoList;
		this.scwList = scwList;
	}

	
	/**
	 * Computes the average of the list defined as: 
	 * sum(score * length) / sum(length)
	 * @param scwList input {@link ChromosomeListOfLists}
	 * @param chromoList list of boolean. A boolean set to true means that the 
	 * chromosome with the same index is going to be used for the calculation.
	 * @param length sum of the lengths of non null windows 
	 */
	public SCWLOAverage(ScoredChromosomeWindowList scwList, boolean[] chromoList, long length) {
		this.chromoList = chromoList;
		this.scwList = scwList;
		this.length = length;
	}
	
	
	@Override
	public Double compute() throws Exception {
		// if the average has to be calculated on all chromosome 
		// and if it has already been calculated we don't do the calculation again
		if ((Utils.allChromosomeSelected(chromoList)) && (scwList.getAverage() != null)) {
			return scwList.getAverage();
		}	
		
		// count the sum of the lengths of the non-null windows if wasn't specified in the constructor 
		if (length == null) {
			length = new SCWLOCountNonNullLength(scwList, chromoList).compute();
		}
		// if there is no none-null value we return 0
		if (length == 0) {
			return 0d;
		}		
		
		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<Double>> threadList = new ArrayList<Callable<Double>>();

		for (int i = 0; i < scwList.size(); i++) {
			final List<ScoredChromosomeWindow> currentList = scwList.get(i);
			final int currentIndex = i;

			Callable<Double> currentThread = new Callable<Double>() {	
				@Override
				public Double call() throws Exception {
					double sumScoreByLength = 0;
					if (((chromoList == null) || ((currentIndex < chromoList.length) && (chromoList[currentIndex]))) && (scwList.get(currentIndex) != null)) {
						for(ScoredChromosomeWindow currentWindow : currentList) {	
							if (currentWindow.getScore() != 0) {
								sumScoreByLength += currentWindow.getScore() * currentWindow.getSize();
							}
						}
					}
					// tell the operation pool that a chromosome is done
					op.notifyDone();
					return sumScoreByLength;
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
		return total / (double) length;
	}


	@Override
	public String getDescription() {
		return "Operation: Average";
	}


	@Override
	public String getProcessingDescription() {
		return "Computing Average";
	}


	@Override
	public int getStepCount() {
		return 1;
	}
}
