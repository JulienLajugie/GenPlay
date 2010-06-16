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
 * Saturates a specify number of low and high values
 * @author Julien Lajugie
 * @version 0.1
 */
public class BLOSaturateCount implements Operation<BinList> {

	private final BinList 	binList;		// {@link BinList} to saturate
	private final int 		lowValuesCount;	// number of low values to saturate
	private final int 		highValuesCount;// number of high values to saturate


	/**
	 * Creates an instance of {@link BLOSaturateCount}
	 * @param binList {@link BinList} to saturate
	 * @param lowValuesCount number of low values to saturate
	 * @param highValuesCount number of high values to saturate
	 */
	public BLOSaturateCount(BinList binList, int lowValuesCount, int highValuesCount) {
		this.binList = binList;
		this.lowValuesCount = lowValuesCount;
		this.highValuesCount = highValuesCount;
	}
	
	
	@Override
	public BinList compute() throws Exception {
		if ((lowValuesCount < 0) || (highValuesCount < 0)) {
			throw new IllegalArgumentException("The number of values to saturate must be positive");
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
		Arrays.sort(allScores);
		double minValue = lowValuesCount == 0 ? Double.NEGATIVE_INFINITY : allScores[lowValuesCount - 1];
		double maxValue = highValuesCount == 0 ? Double.POSITIVE_INFINITY : allScores[allScores.length - highValuesCount];
		return new BLOSaturateThreshold(binList, minValue, maxValue).compute();
	}

	
	@Override
	public String getDescription() {
		return "Operation: Saturate, " + lowValuesCount + " smallest values, " + highValuesCount + " greatest values";
	}
	

	@Override
	public String getProcessingDescription() {
		return "Saturating";
	}

	
	@Override
	public int getStepCount() {
		return 1 + BinList.getCreationStepCount(binList.getBinSize());
	}
}
