/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.list.binList.operation;

import java.util.Arrays;
import java.util.List;

import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.list.binList.BinListOperations;
import yu.einstein.gdp2.core.list.binList.ListFactory;


/**
 * This class provides saturation operations on {@link BinList}
 * @author Julien Lajugie
 * @version 0.1
 */
public class BinListSaturation {
	
	
	/**
	 * Saturates a specify number of low and high values
	 * @param binList {@link BinList} to saturate
	 * @param lowValuesCount number of low values to saturate
	 * @param highValuesCount number of high values to saturate
	 * @return a saturated {@link BinList}
	 * @throws IllegalArgumentException
	 */
	public static BinList saturationCount(BinList binList, int lowValuesCount, int highValuesCount) throws IllegalArgumentException {
		if ((lowValuesCount < 0) || (highValuesCount < 0)) {
			throw new IllegalArgumentException("The number of values to saturate must be positive");
		}

		boolean[] selectedChromo = new boolean[binList.size()];
		Arrays.fill(selectedChromo, true);
		int totalLenght = (int)BinListOperations.binCount(binList,selectedChromo);
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
		return saturationThreshold(binList, minValue, maxValue);
	}


	/**
	 * Saturates a percentage of the greatest and smallest values
	 * @param binList {@link BinList} to saturate
	 * @param lowPercentage percentage of low values to saturate
	 * @param highPercentage percentage of high values to saturate
	 * @return a saturated {@link BinList}
	 * @throws IllegalArgumentException
	 */
	public static BinList saturationPercentage(BinList binList, double lowPercentage, double highPercentage) throws IllegalArgumentException {
		if ((highPercentage < 0) || (highPercentage > 1) || (lowPercentage < 0) ||(lowPercentage > 1)) {
			throw new IllegalArgumentException("The percentage value must be between 0 and 1");
		}
		if (lowPercentage + highPercentage > 1) {
			throw new IllegalArgumentException("The sum of the low and high percentages value must be between 0 and 1");
		}

		boolean[] selectedChromo = new boolean[binList.size()];
		Arrays.fill(selectedChromo, true);
		int totalLenght = (int)BinListOperations.binCount(binList,selectedChromo);
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
		return saturationThreshold(binList, minValue, maxValue);
	}


	/**
	 * Saturates the values above and under specified thresholds
	 * @param binList {@link BinList} to saturate
	 * @param minSaturated saturates the values under this threshold
	 * @param maxSaturated saturates the values above this threshold
	 * @return a saturated {@link BinList}
	 * @throws IllegalArgumentException
	 */
	public static BinList saturationThreshold(BinList binList, double minSaturated, double maxSaturated) throws IllegalArgumentException {
		if (minSaturated >= maxSaturated) {
			throw new IllegalArgumentException("The maximum must be greater than the minimum");
		}
		BinList resultList = new BinList(binList.getChromosomeManager(), binList.getBinSize(), binList.getPrecision());
		for (List<Double> currentList: binList) {
			if ((currentList == null) || (currentList.size() == 0)) {
				resultList.add(null);
			} else {
				List<Double> listToAdd = ListFactory.createList(binList.getPrecision(), currentList.size());
				for (int i = 0; i < currentList.size(); i++) {
					double currentScore = currentList.get(i);
					if (currentScore != 0) {
						if (currentScore > maxSaturated) {
							listToAdd.set(i, maxSaturated);
						} else if (currentScore < minSaturated) {
							listToAdd.set(i, minSaturated);
						} else {
							listToAdd.set(i, currentScore);
						}
					}
				}
				resultList.add(listToAdd);
			}
		}
		return resultList;
	}
}
