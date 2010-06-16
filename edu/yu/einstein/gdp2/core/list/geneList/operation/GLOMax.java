/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.list.geneList.operation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import yu.einstein.gdp2.core.Gene;
import yu.einstein.gdp2.core.list.geneList.GeneList;
import yu.einstein.gdp2.core.operation.Operation;
import yu.einstein.gdp2.core.operationPool.OperationPool;


/**
 * Searches the maximum value of the selected chromosomes of a specified {@link GeneList}
 * @author Julien Lajugie
 * @version 0.1
 */
public class GLOMax implements Operation<Double> {

	private final GeneList 	geneList;		// input GeneList
	private final boolean[] chromoList;		// 1 boolean / chromosome. 
	// each boolean sets to true means that the corresponding chromosome is selected


	/**
	 * Searches the maximum value of the selected chromosomes of a specified {@link GeneList}
	 * @param geneList input {@link GeneList}
	 * @param chromoList list of boolean. A boolean set to true means that the 
	 * chromosome with the same index is going to be used for the calculation. 
	 */
	public GLOMax(GeneList geneList, boolean[] chromoList) {
		this.geneList = geneList;
		this.chromoList = chromoList;
	}


	@Override
	public Double compute() throws InterruptedException, ExecutionException {
		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<Double>> threadList = new ArrayList<Callable<Double>>();
		for (int i = 0; i < geneList.size(); i++) {
			if (((chromoList == null) || ((i < chromoList.length) && (chromoList[i]))) && (geneList.get(i) != null)) {
				final List<Gene> currentList = geneList.get(i);

				Callable<Double> currentThread = new Callable<Double>() {	
					@Override
					public Double call() throws Exception {
						// we set the max to the smallest double value
						double max = Double.NEGATIVE_INFINITY;
						for (Gene currentGene: currentList) {
							if ((currentGene != null) && (currentGene.getExonScores() != null)) {
								for (Double currentScore: currentGene.getExonScores()) {
									if (currentScore != 0) {
										max = Math.max(max, currentScore);					
									}
								}
							}
						}
						// tell the operation pool that a chromosome is done
						op.notifyDone();
						return max;
					}
				};

				threadList.add(currentThread);
			}			
		}		

		List<Double> result = op.startPool(threadList);
		if (result == null) {
			return null;
		}
		// we search for the max of the chromosome minimums
		double max = Double.NEGATIVE_INFINITY;
		for (Double currentMax: result) {
			max = Math.max(max, currentMax);
		}
		return max;
	}


	@Override
	public String getDescription() {
		return "Operation: Maximum";
	}


	@Override
	public int getStepCount() {
		return 1;
	}


	@Override
	public String getProcessingDescription() {
		return "Searching Maximum";
	}
}
