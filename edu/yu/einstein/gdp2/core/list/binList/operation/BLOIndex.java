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
import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.list.binList.ListFactory;


/**
 * Indexes the scores of a {@link BinList} based on 
 * the greatest and the smallest value of the whole genome
 * @author Julien Lajugie
 * @version 0.1
 */
public class BLOIndex implements BinListOperation<BinList> {

	private final BinList 	binList;	// binlist to index
	private final double 	newMin;		// new min after index
	private final double 	newMax;		// new max after index


	/**
	 * Creates an instance of {@link BLOIndex}
	 * Indexes the scores between the specified minimum and maximum 
	 * based on the greatest and the smallest value of the whole genome.
	 * @param binList {@link BinList} to index
	 * @param newMin minimum value after index
	 * @param newMax maximum value after index
	 */
	public BLOIndex(BinList binList, double newMin, double newMax) {
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
		final double oldMin = binList.getMin();
		final double oldMax = binList.getMax();
		// We calculate the difference between the highest and the lowest value
		final double oldDistance = oldMax - oldMin;
		if (oldDistance != 0) {
			final double newDistance = newMax - newMin;
			for (short i = 0; i < binList.size(); i++) {
				final List<Double> currentList = binList.get(i);	
	
				Callable<List<Double>> currentThread = new Callable<List<Double>>() {	
					@Override
					public List<Double> call() throws Exception {
						List<Double> resultList = null;
						if ((currentList != null) && (currentList.size() != 0)) {
							resultList = ListFactory.createList(precision, currentList.size());
							// We index the intensities
							for (int j = 0; j < currentList.size(); j++) {
								if (currentList.get(j) == 0) {
									resultList.set(j, 0d);
								} else { 
									resultList.set(j, newDistance * (currentList.get(j) - oldMin) / oldDistance + newMin);
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
		} else {
			return null;
		}
	}


	@Override
	public String getDescription() {
		return "Operation: Index Between " +  newMin + " and " + newMax;
	}
	
	
	@Override
	public int getStepCount() {
		return BinList.getCreationStepCount(binList.getBinSize()) + 1;
	}
}
