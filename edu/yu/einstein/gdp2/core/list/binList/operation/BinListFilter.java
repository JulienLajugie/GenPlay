package yu.einstein.gdp2.core.list.binList.operation;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import yu.einstein.gdp2.core.enums.DataPrecision;
import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.list.binList.ListFactory;

public class BinListFilter {
	
	
	/**
	 * Removes (ie sets to zero) a specify number of low and high values
	 * @param binList {@link BinList} to filter
	 * @param lowValuesCount number of low values to remove
	 * @param highValuesCount number of high values to remove
	 * @return a filtered {@link BinList}
	 * @throws IllegalArgumentException
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	public static BinList countFilter(BinList binList, int lowValuesCount, int highValuesCount) throws IllegalArgumentException, InterruptedException, ExecutionException {
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
		return thresholdFilter(binList, minValue, maxValue, 1);
	}


	/**
	 * Removes (ie sets to zero) a percentage of the greatest and smallest values
	 * @param binList {@link BinList} to filter
	 * @param lowPercentage percentage of low values to filter
	 * @param highPercentage percentage of high values to filter
	 * @return a filtered {@link BinList}
	 * @throws IllegalArgumentException
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	public static BinList percentageFilter(BinList binList, double lowPercentage, double highPercentage) throws IllegalArgumentException, InterruptedException, ExecutionException {
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
		return thresholdFilter(binList, minValue, maxValue, 1);
	}


	/**
	 * Removes the values above and under specified thresholds
	 * @param binList {@link BinList} to filter
	 * @param lowThreshold filters the values under this threshold
	 * @param highThreshold filters the values above this threshold
	 * @return a filtered {@link BinList}
	 * @throws IllegalArgumentException
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	public static BinList thresholdFilter(BinList binList, double lowThreshold, double highThreshold, int nbConsecutiveValues) throws IllegalArgumentException, InterruptedException, ExecutionException {
		if (lowThreshold >= highThreshold) {
			throw new IllegalArgumentException("The high threshold must be greater than the low one");
		}	
		BinList resultList = new BinList(binList.getChromosomeManager(), binList.getBinSize(), binList.getPrecision());
		for(short i = 0; i < binList.size(); i++)  {
			if ((binList.get(i) == null) || (binList.size(i) == 0)) {
				resultList.add(null);
			} else {
				List<Double> listToAdd = ListFactory.createList(binList.getPrecision(), binList.size(i));
				resultList.add(listToAdd);
				for (int j = 0; j < binList.size(i) - nbConsecutiveValues; j++) {
					boolean selected = true;
					int k = 0;
					// we accept a window if there is nbConsecutiveValues above or under the filter
					while ((selected) && (k < nbConsecutiveValues) && (j + k < binList.size(i))) {
						// depending on the filter type we accept values above or under the threshold
						selected = (binList.get(i, j + k) > lowThreshold) && (binList.get(i, j + k) < highThreshold);						
						k++;
					}
					if (selected) {
						while ((j < binList.size(i)) && (binList.get(i, j) > lowThreshold) && (binList.get(i, j) < highThreshold)) {
							resultList.set(i, j, binList.get(i, j));
							j++;
						}
					} else {
						resultList.set(i, j, 0d);
					}					
				}
			}
		}
		resultList.finalizeConstruction();
		return resultList;
	}
	
	
	/**
	 * Applies a filter on a {@link BinList} selecting region where there is at least the specified 
	 * density of bins above and under specified thresholds
	 * The result is returned in a new BinList.
	 * @param binList 
	 * @param lowThreshold select values above this threshold
	 * @param highThreshold select values under this threshold
	 * @param density minimum density of windows above and under the thresholds for a region to be selected (percentage btw 0 and 1)
	 * @param regionSize size of the region (in number of bins) 
	 * @return a new {@link BinList} with only the selected windows
	 * @throws IllegalArgumentException if the low threshold is greater than the high 
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	public static BinList densityFilter(BinList binList, double lowThreshold, double highThreshold, double density, int regionSize) throws IllegalArgumentException, InterruptedException, ExecutionException {
		if (lowThreshold >= highThreshold) {
			throw new IllegalArgumentException("The high threshold must be greater than the low one");
		}		
		// if percentage is zero, everything is selected
		if (density == 0) {
			return binList.deepClone();
		}		
		// we calculate the min number of bins above the threshold needed to select a region
		int minBinCount = (int)Math.ceil(regionSize * density);
		DataPrecision precision = binList.getPrecision();
		BinList resultList = new BinList(binList.getChromosomeManager(), binList.getBinSize(), precision);
		for(short i = 0; i < binList.size(); i++)  {
			if ((binList.get(i) == null) || (binList.size(i) == 0)) {
				resultList.add(null);
			} else {
				List<Double> listToAdd = ListFactory.createList(precision, binList.size(i));
				resultList.add(listToAdd);
				for (int j = 0; j < binList.size(i) - regionSize; j++) {
					int binSelectedCount = 0;
					int k = 0;
					// we accept a window if there is nbConsecutiveValues above or under the filter
					while ((binSelectedCount < minBinCount) && (k < regionSize) && (j + k < binList.size(i))) {
						// depending on the filter type we accept values above or under the threshold
						if ((binList.get(i, j + k) > lowThreshold) && (binList.get(i, j + k) < highThreshold)) {
							binSelectedCount++;
						}
						k++;
					}
					// we calculate where the current region ends
					k = 0;
					if (binSelectedCount >= minBinCount) {
						while ((j + k < binList.size(i)) && (k < regionSize)) {
							resultList.set(i, j + k, binList.get(i, j + k));
							k++;
						}
					}
				}
			}
		}
		resultList.finalizeConstruction();
		return resultList;		
	}
}
