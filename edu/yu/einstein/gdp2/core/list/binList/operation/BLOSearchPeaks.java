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

import yu.einstein.gdp2.core.list.arrayList.ListFactory;
import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.operation.Operation;
import yu.einstein.gdp2.core.operationPool.OperationPool;
import yu.einstein.gdp2.util.DoubleLists;


/**
 * Searches the peaks of a {@link BinList}
 * @author Julien Lajugie
 * @version 0.1
 */
public class BLOSearchPeaks implements Operation<BinList> {

	private final BinList 	binList;		// input BinList 
	private final int 		halfWidth;		// half size of the moving stdev (in bins)
	private final double 	nbSDAccepted;	/* 	threshold: we accept a bin if the local stdev centered 
											 	on this point is at least this parameter time higher than 
												the chromosome wide stdev 	*/
	
	
	/**
	 * Creates an instance of BLOSearchPeaks
	 * Searches the peaks of a specified {@link BinList}. A bin is considered as a peak when the 
	 * local stdev centered on this bin is higher than a certain threshold.
	 * The threshold is specified in chromosome wide stdev folds.
	 * @param binList input {@link BinList}
	 * @param halfWidth half size of the moving stdev (in bins)
	 * @param nbSDAccepted threshold: we accept a bin if the local stdev centered on this point is at 
	 * least this parameter time higher than the chromosome wide stdev
	 */
	public BLOSearchPeaks(BinList binList, int halfWidth, double nbSDAccepted) {
		this.binList = binList;
		this.halfWidth = halfWidth;
		this.nbSDAccepted = nbSDAccepted;
	}


	@Override
	public BinList compute() throws InterruptedException, ExecutionException {
		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<List<Double>>> threadList = new ArrayList<Callable<List<Double>>>();
		final int binSize = binList.getBinSize();

		for(short i = 0; i < binList.size(); i++) {
			final List<Double> currentList = binList.get(i);	

			Callable<List<Double>> currentThread = new Callable<List<Double>>() {	
				@Override
				public List<Double> call() throws Exception {
					List<Double> resultList = null;
					if ((currentList != null) && (currentList.size() != 0)) {
						resultList = ListFactory.createList(binList.getPrecision(), currentList.size());
						// compute the stdev for the chromosome
						double sd = DoubleLists.standardDeviation(currentList, 0, currentList.size() - 1);
						if (sd != 0) {
							// compute the value the local standard deviation must be for a bin to be accepted
							double minAcceptedSD = nbSDAccepted * sd;
							for (int j = 0; j < currentList.size(); j++) {
								if (currentList.get(j) != 0) {
									int indexStart = j - halfWidth;
									int indexStop = j + halfWidth;
									// if the start index is negative we set it to 0
									if (indexStart < 0) {
										indexStart = 0;
									}
									// if the stop index is out of range we set it to the size of the list
									if (indexStop > currentList.size() - 1) {
										indexStop = currentList.size() - 1;
									}
									// we compute the local stdev centered on the current bin, btw start and stop index
									double localStdev = DoubleLists.standardDeviation(currentList, indexStart, indexStop);
									if ((localStdev != 0) && (localStdev >= minAcceptedSD)) {
										// if the local stdev is higher than the threshold we keep the bin
										resultList.set(j, currentList.get(j));
									} else {
										// otherwise we set it to zero
										resultList.set(j, 0d);
									}
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
			BinList resultList = new BinList(binSize, binList.getPrecision(), result);
			return resultList;
		} else {
			return null;
		}
	}	


	@Override
	public String getDescription() {
		return "Operation: Search Peaks, Local Stdev Width = " +  (halfWidth * 2 * binList.getBinSize()) + " bp, Threshold = " + nbSDAccepted + " Stdev";
	}
	
	
	@Override
	public int getStepCount() {
		return BinList.getCreationStepCount(binList.getBinSize()) + 1;
	}
	
	
	@Override
	public String getProcessingDescription() {
		return "Searching Peaks";
	}
}
