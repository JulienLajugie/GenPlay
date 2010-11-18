/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.list.SCWList.operation;

import yu.einstein.gdp2.core.list.SCWList.ScoredChromosomeWindowList;
import yu.einstein.gdp2.core.operation.Operation;


/**
 * Calculates the minimum score to display on a ScoredChromosomeWindowList track
 * @author Julien Lajugie
 * @version 0.1
 */
public class SCWLOMinScoreToDisplay implements Operation<Double> {

	private final ScoredChromosomeWindowList 	scwList;	// input list
	private boolean								stopped = false;// true if the operation must be stopped


	/**
	 * Calculates the minimum score to display on a ScoredChromosomeWindowList track
	 * @param scwList input {@link ScoredChromosomeWindowList}
	 */
	public SCWLOMinScoreToDisplay(ScoredChromosomeWindowList scwList) {
		this.scwList = scwList;
	}


	@Override
	public Double compute() {
		// if the min is positive we return 0
		double realMin = scwList.getMin();
		if (realMin >= 0) {
			return 0d;
		}
		// if the min of the list can be written as -10^x we return this value as a minimum
		double minScoreDisplayed = -1;
		while (realMin / minScoreDisplayed > 1 && !stopped) {
			minScoreDisplayed *= 10;
		}
		if (realMin / minScoreDisplayed == 1) {
			return realMin;
		}
		// otherwise we try to find the closest 10^x value under (average - stdev) 
		double proposedMin = scwList.getAverage() - scwList.getStDev(); 
		if (proposedMin >= 0) {
			return 0d;
		}
		minScoreDisplayed = -1;
		while (proposedMin / minScoreDisplayed > 1 && !stopped) {
			minScoreDisplayed *= 10;
		}
		return minScoreDisplayed;
	}


	@Override
	public String getDescription() {
		return "Operation: Minimum Score to Display";
	}


	@Override
	public int getStepCount() {
		return 1;
	}


	@Override
	public String getProcessingDescription() {
		return "Searching Minimum";
	}

	
	@Override
	public void stop() {
		this.stopped = true;
	}
}
