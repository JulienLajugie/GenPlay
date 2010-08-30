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
 * Removes (ie sets to zero) a specified number of low and high values
 * @author Julien Lajugie
 * @version 0.1
 */
public class BLOFilterCount implements Operation<BinList> {

	private final BinList 		binList;			// {@link BinList} to filter
	private final int 			lowValuesCount;		// number of low values to filter
	private final int 			highValuesCount;	// number of high values to filter
	private final boolean		isSaturation;		// true if we saturate, false if we remove the filtered values 
	private boolean				stopped = false;	// true if the operation must be stopped
	private Operation<BinList> 	bloFilterThreshold;	// threshold filter that does the real fitering operation
	

	/**
	 * Creates an instance of {@link BLOFilterCount}
	 * @param binList {@link BinList} to filter
	 * @param lowValuesCount number of low values to filter
	 * @param highValuesCount number of high values to filter
	 * @param isSaturation true to saturate, false to remove the filtered values
	 */
	public BLOFilterCount(BinList binList, int lowValuesCount, int highValuesCount, boolean isSaturation) {
		this.binList = binList;
		this.lowValuesCount = lowValuesCount;
		this.highValuesCount = highValuesCount;
		this.isSaturation = isSaturation;
	}


	@Override
	public BinList compute() throws Exception {
		if ((lowValuesCount < 0) || (highValuesCount < 0)) {
			throw new IllegalArgumentException("The number of values to filter must be positive");
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
		return "Operation: Filter, " + lowValuesCount + " smallest values, " + highValuesCount + " greatest values" + optionStr;
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
