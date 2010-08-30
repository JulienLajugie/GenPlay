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

	private final BinList 		binList;			// {@link BinList} to filter
	private final double 		lowPercentage;		// percentage of low values to filter
	private final double 		highPercentage;		// percentage of high values to filter
	private final boolean		isSaturation;		// true if we saturate, false if we remove the filtered values 
	private boolean				stopped = false;	// true if the operation must be stopped
	private Operation<BinList> 	bloFilterThreshold;	// threshold filter that does the real fitering operation
	

	/**
	 * Creates an instance of {@link BLOFilterPercentage}
	 * @param binList {@link BinList} to filter
	 * @param lowPercentage percentage of low values to filter
	 * @param highPercentage percentage of high values to filter
	 * @param isSaturation true to saturate, false to remove the filtered values
	 */
	public BLOFilterPercentage(BinList binList, double lowPercentage, double highPercentage, boolean isSaturation) {
		this.binList = binList;
		this.lowPercentage = lowPercentage;
		this.highPercentage = highPercentage;
		this.isSaturation = isSaturation;
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
				for (int j = 0; j < currentList.size() && !stopped; j++) {
					Double currentScore = currentList.get(j);
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
		bloFilterThreshold = new BLOFilterThreshold(binList, minValue, maxValue, isSaturation); 
		return bloFilterThreshold.compute();
	}

	
	@Override
	public String getDescription() {
		String optionStr;
		if (isSaturation) {
			optionStr = ", option = saturation";
		} else {
			optionStr = ", option = remove";
		}
		return "Operation: Filter, " + (lowPercentage * 100) + "% smallest values, " + (highPercentage * 100) + "% greatest values" + optionStr;
	}
	

	@Override
	public String getProcessingDescription() {
		return "Filtering";
	}

	
	@Override
	public int getStepCount() {
		return 1 + BinList.getCreationStepCount(binList.getBinSize());
	}

	
	@Override
	public void stop() {
		this.stopped = true;
		if (bloFilterThreshold != null) {
			bloFilterThreshold.stop();
		}
	}
}
