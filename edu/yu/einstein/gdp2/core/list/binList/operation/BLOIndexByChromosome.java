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

import yu.einstein.gdp2.core.enums.DataPrecision;
import yu.einstein.gdp2.core.list.arrayList.ListFactory;
import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.operation.Operation;
import yu.einstein.gdp2.core.operationPool.OperationPool;
import yu.einstein.gdp2.util.DoubleLists;


/**
 * Indexes the scores of a {@link BinList} based on 
 * the greatest and the smallest value of each chromosome
 * @author Julien Lajugie
 * @version 0.1
 */
public class BLOIndexByChromosome implements Operation<BinList> {

	private final BinList 	binList;		// binlist to index
	private final double 	newMin;			// new min after index
	private final double 	newMax;			// new max after index
	private boolean			stopped = false;// true if the operation must be stopped
	

	/**
	 * Creates an instance of {@link BLOIndexByChromosome}
	 * Indexes the scores between the specified minimum and maximum 
	 * based on the greatest and the smallest value of each chromosome.
	 * @param binList {@link BinList} to index
	 * @param newMin minimum value after index
	 * @param newMax maximum value after index
	 */
	public BLOIndexByChromosome(BinList binList, double newMin, double newMax) {
		this.binList = binList;
		this.newMin = newMin;
		this.newMax = newMax;
	}


	@Override
	public BinList compute() throws InterruptedException, ExecutionException {
		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<List<Double>>> threadList = new ArrayList<Callable<List<Double>>>();
		final DataPrecision precision = binList.getPrecision();
		final int binSize = binList.getBinSize(); 
		// We calculate the difference between the greatest and the smallest scores
		final double newDistance = newMax - newMin;
		for (short i = 0; i < binList.size(); i++) {
			final List<Double> currentList = binList.get(i);	

			Callable<List<Double>> currentThread = new Callable<List<Double>>() {	
				@Override
				public List<Double> call() throws Exception {
					List<Double> resultList = null;
					if ((currentList != null) && (currentList.size() != 0)) {
						resultList = ListFactory.createList(precision, currentList.size());
						// search the min and max for the current chromosome before index 
						double oldMin = DoubleLists.minNoZero(currentList);						
						double oldMax = DoubleLists.maxNoZero(currentList);
						// we calculate the difference between the highest and the lowest value
						double oldDistance = oldMax - oldMin;
						if (oldDistance != 0) {
							// We index the intensities 
							for (int j = 0; j < currentList.size() && !stopped; j++) {
								if(currentList.get(j) == 0) {
									resultList.set(j, 0d);
								} else { 
									resultList.set(j, newDistance * (currentList.get(j) - oldMin) / oldDistance + newMin);
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
		List<List<Double>> result = op.startPool(threadList);
		if (result != null) {
			BinList resultList = new BinList(binSize, precision, result);
			return resultList;
		} else {
			return null;
		}
	}



	@Override
	public String getDescription() {
		return "Operation: Index per Chromsome Between " +  newMin + " and " + newMax;
	}
	
	
	@Override
	public int getStepCount() {
		return BinList.getCreationStepCount(binList.getBinSize()) + 1;
	}
	
	
	@Override
	public String getProcessingDescription() {
		return "Indexing";
	}

	
	@Override
	public void stop() {
		this.stopped = true;
	}
}
