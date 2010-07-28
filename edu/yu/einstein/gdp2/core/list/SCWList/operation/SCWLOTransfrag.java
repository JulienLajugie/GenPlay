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
 * Returns a new {@link SCWList} with the defined regions having their average/max/sum as a score
 * @author Chirag Gorasia
 * @version 0.1
 */
public class SCWLOTransfrag implements Operation<ScoredChromosomeWindowList> {

	private ScoredChromosomeWindowList scwList;
	private int zeroSCWGap;
	private ScoreCalculationMethod operation;

	/**
	 * Creates an instance of {@link SCWLOTransfrag}
	 * @param scwList
	 * @param zeroBinGap
	 * @param operation
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
						while (j < currentList.size()) {
							// skip zero values
							while ((j < currentList.size()) && (currentList.get(j) == null)) {
								j++;
							}
							int regionStart = j;
							int regionStop = regionStart;
							int zeroWindowCount = 0;
							// a region stops when there is maxZeroWindowGap consecutive zero bins
							while ((j < currentList.size()) && (zeroWindowCount < zeroSCWGap)) {
								if (currentList.get(j) == null) {
									zeroWindowCount++;
								} else {
									zeroWindowCount = 0;
									regionStop = j;
								}
								j++;
							}
							if (regionStop == currentList.size()) {
								regionStop--;
							}
							if (regionStop >= regionStart) { 
								double regionScore = 0;
								if (operation == ScoreCalculationMethod.AVERAGE) {
									// all the windows of the region are set with the average value on the region
									regionScore = SCWLists.average(currentList, regionStart, regionStop);
								} else if (operation == ScoreCalculationMethod.SUM) {
									// all the windows of the region are set with the max value on the region
									regionScore = SCWLists.maxNoZero(currentList);
								} else {
									// all the windows of the region are set with the sum value on the region
									regionScore = SCWLists.sum(currentList, regionStart, regionStop);
								}
								for (j = regionStart; j <= regionStop; j++) {
									if (j < resultList.size()) {
										resultList.set(j, new ScoredChromosomeWindow(regionStart, regionStop, regionScore));
									}
								}
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
		return 2;
	}
}
