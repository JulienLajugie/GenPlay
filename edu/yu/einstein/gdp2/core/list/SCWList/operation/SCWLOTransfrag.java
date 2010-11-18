/**
 * @author Chirag Gorasia
 * @version 0.1
 */
package yu.einstein.gdp2.core.list.SCWList.operation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

import yu.einstein.gdp2.core.ScoredChromosomeWindow;
import yu.einstein.gdp2.core.enums.ScoreCalculationMethod;
import yu.einstein.gdp2.core.list.SCWList.ScoredChromosomeWindowList;
import yu.einstein.gdp2.core.operation.Operation;
import yu.einstein.gdp2.core.operationPool.OperationPool;
import yu.einstein.gdp2.util.SCWLists;

/**
 * Defines regions as "islands" of non zero value ScoredChromosomeWindows 
 * separated by more than a specified number of zero value ScoredChromosomeWindows.
 * Computes the average/sum/max on these regions.
 * Returns a new {@link ScoredChromosomeWindowList} with the defined regions having their average/max/sum as a score
 * @author Chirag Gorasia
 * @version 0.1
 */
public class SCWLOTransfrag implements Operation<ScoredChromosomeWindowList> {

	private ScoredChromosomeWindowList 	scwList;		// input list
	private int 						zeroSCWGap;		// minimum size of the gap separating two intervals
	private ScoreCalculationMethod 		operation;		// operation to use to compute the score of the intervals
	private boolean						stopped = false;// true if the operation must be stopped


	/**
	 * Creates an instance of {@link SCWLOTransfrag}
	 * @param scwList input list
	 * @param zeroSCWGap minimum size of the gap separating two intervals
	 * @param operation operation to use to compute the score of the intervals
	 */
	public SCWLOTransfrag(ScoredChromosomeWindowList scwList, int zeroSCWGap, ScoreCalculationMethod operation) {
		this.scwList = scwList;
		this.zeroSCWGap = zeroSCWGap;
		this.operation = operation;
	}


	@Override
	public ScoredChromosomeWindowList compute() throws Exception {

		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<List<ScoredChromosomeWindow>>> threadList = new ArrayList<Callable<List<ScoredChromosomeWindow>>>();

		for (short i = 0; i < scwList.size(); i++) {
			final List<ScoredChromosomeWindow> currentList = scwList.get(i);	

			Callable<List<ScoredChromosomeWindow>> currentThread = new Callable<List<ScoredChromosomeWindow>>() {	
				@Override
				public List<ScoredChromosomeWindow> call() throws Exception {
					List<ScoredChromosomeWindow> resultList = null;
					if ((currentList != null) && (currentList.size() != 0)) {
						resultList = new ArrayList<ScoredChromosomeWindow>();
						int j = 0;				
						while (j < currentList.size() && !stopped) {
							// skip zero values
							while ((j < currentList.size()) && (currentList.get(j) == null) && !stopped) {
								j++;
							}
							int regionStartIndex = j;
							int regionStopIndex = regionStartIndex;
							// a region stops when there is maxZeroWindowGap consecutive zero bins
							while ((j + 1 < currentList.size()) && (currentList.get(j + 1).getStart() - currentList.get(j).getStop() <= zeroSCWGap) && !stopped) {
								regionStopIndex = j+1;
								j++;
							}
							if (regionStopIndex >= currentList.size()) {
								regionStopIndex = currentList.size()-1;
							}
							if (regionStopIndex >= regionStartIndex) { 
								double regionScore = 0;
								if (operation == ScoreCalculationMethod.AVERAGE) {
									// all the windows of the region are set with the average value on the region
									regionScore = SCWLists.average(currentList, regionStartIndex, regionStopIndex);
								} else if (operation == ScoreCalculationMethod.MAXIMUM) {
									// all the windows of the region are set with the max value on the region
									regionScore = SCWLists.maxNoZero(currentList, regionStartIndex, regionStopIndex);
								} else {
									// all the windows of the region are set with the sum value on the region
									regionScore = SCWLists.sum(currentList, regionStartIndex, regionStopIndex);
								}
								resultList.add(new ScoredChromosomeWindow(currentList.get(regionStartIndex).getStart(), currentList.get(regionStopIndex).getStop(), regionScore));
							}
							j++;
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
		return "Operation: Transfrag, Gap Size = " + zeroSCWGap + " Zero Value Successive ScoredChromosomeWindows";
	}


	@Override
	public String getProcessingDescription() {
		return "Calculating Transfrag";
	}


	@Override
	public int getStepCount() {
		return 3;
	}


	@Override
	public void stop() {
		this.stopped = true;
	}
}
