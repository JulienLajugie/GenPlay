/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.list.SCWList.operation;

import yu.einstein.gdp2.core.list.SCWList.ScoredChromosomeWindowList;
import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.operation.Operation;


/**
 * Calculates the maximum score to display on a ScoredChromosomeWindowList track
 * @author Julien Lajugie
 * @version 0.1
 */
public class SCWLOMaxScoreToDisplay implements Operation<Double> {

	private final ScoredChromosomeWindowList 	scwList;		// input list
	private boolean								stopped = false;// true if the operation must be stopped


	/**
	 * Calculates the maximum score to display on a ScoredChromosomeWindowList track
	 * @param binList input {@link BinList}
	 */
	public SCWLOMaxScoreToDisplay(ScoredChromosomeWindowList scwList) {
		this.scwList = scwList;
	}


	@Override
	public Double compute() {
		final double realMax = scwList.getMax();
		// if the max is negative we return 0
		if (realMax <= 0) {
			return 0d;
		}
		// if the max of the BinList can be written as 10^x we return this value as a maximum
		double maxScoreDisplayed = 1;
		while (realMax / maxScoreDisplayed > 1 && !stopped) {
			maxScoreDisplayed *= 10;
		}
		if (realMax / maxScoreDisplayed == 1) {
			return realMax;
		}
		// otherwise we try to find the closest 10^x value above (average + stdev) 
		double proposedMax = scwList.getAverage() + scwList.getStDev(); 
		if (proposedMax <= 0) {
			return 0d;
		}
		maxScoreDisplayed = 1;
		while (proposedMax / maxScoreDisplayed > 1 && !stopped) {
			maxScoreDisplayed *= 10;
		}
		return maxScoreDisplayed;
	}


	@Override
	public String getDescription() {
		return "Operation: Maximum Score to Display";
	}


	@Override
	public int getStepCount() {
		return 1;
	}


	@Override
	public String getProcessingDescription() {
		return "Searching Maximum";
	}

	
	@Override
	public void stop() {
		this.stopped = true;
	}
}
