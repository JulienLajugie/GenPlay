/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.list.binList.operation;

import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.operation.Operation;


/**
 * Calculates the minimum score to display on a BinList track
 * @author Julien Lajugie
 * @version 0.1
 */
public class BLOMinScoreToDisplay implements Operation<Double> {

	private final BinList 	binList;		// input BinList
	
	
	/**
	 * Calculates the minimum score to display on a BinList track
	 * @param binList input {@link BinList}
	 */
	public BLOMinScoreToDisplay(BinList binList) {
		this.binList = binList;
	}
	
	
	@Override
	public Double compute() {
		// if the min is positive we return 0
		final double realMin = binList.getMin();
		if (realMin >= 0) {
			return 0d;
		}
		// if the min of the BinList can be written as -10^x we return this value as a minimum
		double minScoreDisplayed = -1;
		while (realMin / minScoreDisplayed > 1) {
			minScoreDisplayed *= 10;
		}
		if (realMin / minScoreDisplayed == 1) {
			return realMin;
		}
		// otherwise we try to find the closest 10^x value under (average - stdev) 
		final double proposedMin = binList.getAverage() - binList.getStDev(); 
		if (proposedMin >= 0) {
			return 0d;
		}
		minScoreDisplayed = -1;
		while (proposedMin / minScoreDisplayed > 1) {
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
}
