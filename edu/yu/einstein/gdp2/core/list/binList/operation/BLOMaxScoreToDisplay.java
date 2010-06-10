/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.list.binList.operation;

import yu.einstein.gdp2.core.list.binList.BinList;


/**
 * Calculates the maximum score to display on a BinList track
 * @author Julien Lajugie
 * @version 0.1
 */
public class BLOMaxScoreToDisplay implements BinListOperation<Double> {

	private final BinList 	binList;		// input BinList
	
	
	/**
	 * Calculates the maximum score to display on a BinList track
	 * @param binList input {@link BinList}
	 */
	public BLOMaxScoreToDisplay(BinList binList) {
		this.binList = binList;
	}
	
	
	@Override
	public Double compute() {
		final double realMax = binList.getMax();
		// if the max is negative we return 0
		if (realMax <= 0) {
			return 0d;
		}
		// if the max of the BinList can be written as 10^x we return this value as a maximum
		double maxScoreDisplayed = 1;
		while (realMax / maxScoreDisplayed > 1) {
			maxScoreDisplayed *= 10;
		}
		if (realMax / maxScoreDisplayed == 1) {
			return realMax;
		}
		// otherwise we try to find the closest 10^x value above (average + stdev) 
		final double proposedMax = (binList.getAverage() + binList.getStDev()); 
		if (proposedMax <= 0) {
			return 0d;
		}
		maxScoreDisplayed = 1;
		while (proposedMax / maxScoreDisplayed > 1) {
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
}
