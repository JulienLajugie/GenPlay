/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.list.binList.operation;

import java.util.Arrays;
import java.util.List;

import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.operation.Operation;


/**
 * Removes (ie sets to zero) a percentage of the greatest and smallest values
 * @author Julien Lajugie
 * @version 0.1
 */
public class BLOFilterPercentage implements Operation<BinList> {

	private final BinList 	binList;		// {@link BinList} to filter
	private final double 	lowPercentage;	// percentage of low values to filter
	private final double 	highPercentage;	// percentage of high values to filter


	/**
	 * Creates an instance of {@link BLOFilterPercentage}
	 * @param binList {@link BinList} to filter
	 * @param lowPercentage percentage of low values to filter
	 * @param highPercentage percentage of high values to filter
	 */
	public BLOFilterPercentage(BinList binList, double lowPercentage, double highPercentage) {
		this.binList = binList;
		this.lowPercentage = lowPercentage;
		this.highPercentage = highPercentage;
	}
	
	
	@Override
	public BinList compute() throws Exception {
		if ((highPercentage < 0) || (highPercentage > 1) || (lowPercentage < 0) ||(lowPercentage > 1)) {
			throw new IllegalArgumentException("The percentage value must be between 0 and 1");
		}
		if (lowPercentage + highPercentage > 1) {
			throw new IllegalArgumentException("The sum of the low and high percentages value must be between 0 and 1");
		}
		boolean[] selectedChromo = new boolean[binList.size()];
		Arrays.fill(selectedChromo, true);
		int totalLenght = new BLOCountNonNullBins(binList,selectedChromo).compute().intValue();
		double[] allScores = new double[totalLenght];
		int i = 0;
		for (List<Double> currentList: binList) {
			if (currentList != null) {
				for (Double currentScore: currentList) {
					if (currentScore != 0) {
						allScores[i] = currentScore;
						i++;
					}
				}
			}
		}
		int lowValuesCount = (int)(lowPercentage * allScores.length);
		int highValuesCount = (int)(highPercentage * allScores.length);
		Arrays.sort(allScores);
		double minValue = lowValuesCount == 0 ? Double.NEGATIVE_INFINITY : allScores[lowValuesCount - 1];
		double maxValue = highValuesCount == 0 ? Double.POSITIVE_INFINITY : allScores[allScores.length - highValuesCount];
		return new BLOFilterThreshold(binList, minValue, maxValue, 1).compute();
	}

	
	@Override
	public String getDescription() {
		return "Operation: Filter, " + (lowPercentage * 100) + "% smallest values, " + (highPercentage * 100) + "% greatest values";
	}
	

	@Override
	public String getProcessingDescription() {
		return "Filtering";
	}

	
	@Override
	public int getStepCount() {
		return 1 + BinList.getCreationStepCount(binList.getBinSize());
	}
}
