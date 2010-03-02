/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.list.binList;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import yu.einstein.gdp2.core.enums.DataPrecision;
import yu.einstein.gdp2.core.enums.FilterType;
import yu.einstein.gdp2.core.enums.ScoreCalculationMethod;
import yu.einstein.gdp2.exception.BinListDifferentWindowSizeException;
import yu.einstein.gdp2.util.DoubleLists;


/**
 * This class contains various static methods for manipulating {@link BinList}
 * @author Julien Lajugie
 * @version 0.1
 */
public class BinListOperations {


	/**
	 * @param binList1
	 * @param binList2
	 * @param precision precision of the result {@link BinList} 
	 * @return a new {@link BinList} resulting of the operation binList1 + binList2
	 * @throws BinListDifferentWindowSizeException
	 */
	public static BinList addition(BinList binList1, BinList binList2, DataPrecision precision) throws BinListDifferentWindowSizeException{
		if (binList1.getBinSize() != binList2.getBinSize()) {
			throw new BinListDifferentWindowSizeException();
		}
		BinList resultList = new BinList(binList1.getChromosomeManager(), binList1.getBinSize(), precision);
		for(short i = 0; i < binList1.size(); i++)  {
			if((binList1.get(i) == null) || (i >= binList2.size()) || (binList2.get(i) == null)) {
				resultList.add(null);
			} else {
				List<Double> listToAdd = ListFactory.createList(precision, binList1.size(i));
				resultList.add(listToAdd);
				for(int j = 0; j < binList1.size(i); j++) {
					if(j < binList2.size(i)) {
						resultList.set(i, j, binList1.get(i, j) + binList2.get(i, j));
					} else {
						resultList.set(i, j, 0d);
					}
				}
			}
		}
		return resultList;
	}


	/**
	 * Adds a constant to every value of the specified {@link BinList}
	 * @param binList 
	 * @param constant value to add
	 * @param precision precision of the data of the new {@link BinList} 
	 * @return A new {@link BinList}
	 */
	public static BinList addition(BinList binList, double constant, DataPrecision precision) {
		if (constant == 0) {
			return binList.deepClone();
		}
		BinList resultList = new BinList(binList.getChromosomeManager(), binList.getBinSize(), precision);
		for (short i = 0; i < binList.size(); i++) {
			if ((binList.get(i) == null) || (binList.size(i) == 0)) {
				resultList.add(null);
			} else {
				List<Double> listToAdd = ListFactory.createList(precision, binList.size(i));
				resultList.add(listToAdd);
				// We add a constant to each element
				for (int j = 0; j < binList.size(i); j++) {
					resultList.set(i, j, binList.get(i, j) + constant);
				}
			}
		}
		return resultList;		
	}


	/**
	 * @param binList
	 * @return the average value of the specified {@link BinList}
	 */
	public static double average(BinList binList) {
		int n = 0;
		double sum = 0;
		for (int i = 0; i < binList.size(); i++) {
			if (binList.get(i) != null) {
				for (int j = 0; j < binList.size(i); j++) {
					if (binList.get(i, j) != 0) {
						sum += binList.get(i, j);
						n++;						
					}
				}
			}
		}
		if (n == 0) {
			return 0;
		} else {
			return sum / n;
		}
	}


	/**
	 * @param binList
	 * @param chromoList set to true each chromosome of this list that you want to use in the calculation
	 * @return the average value of the specified {@link BinList}
	 */
	public static double average(BinList binList, boolean[] chromoList) {
		int n = 0;
		double sum = 0;
		for (int i = 0; i < binList.size(); i++) {
			if ((binList.get(i) != null) && (i < chromoList.length) && (chromoList[i])) {
				for (int j = 0; j < binList.size(i); j++) {
					if (binList.get(i, j) != 0) {
						sum += binList.get(i, j);
						n++;						
					}
				}
			}
		}
		if (n == 0) {
			return 0;
		} else {
			return sum / n;
		}
	}


	/**
	 * @param binList a {@link BinList}
	 * @param chromoList set to true each chromosome of this list that you want to use in the calculation
	 * @return the number of non-null Bins on the selected chromosomes of the specified BinList 
	 */
	public static long binCount(BinList binList, boolean[] chromoList) {
		long binCount = 0;
		for (int i = 0; i < binList.size(); i++) {
			if ((binList.get(i) != null) && (i < chromoList.length) && (chromoList[i])) {
				for (int j = 0; j < binList.size(i); j++) {
					if (binList.get(i, j) != 0) {
						binCount++;
					}
				}
			}
		}
		return binCount;
	}


	/**
	 * Computes the average, the max or the sum of the {@link BinList} on intervals defined by another BinList
	 * @param intervalList BinList defining the intervals
	 * @param valueList BinList defining the values for the calculation
	 * @param percentageAcceptedValues the calculation is calculated only on the x% greatest values of each interval 
	 * @param precision precision of the result BinList
	 * @return a new BinList
	 * @throws BinListDifferentWindowSizeException
	 * @throws {@link IllegalArgumentException}
	 */
	public static BinList calculationOnProjection(BinList intervalList, BinList valueList, int percentageAcceptedValues, ScoreCalculationMethod method, DataPrecision precision) throws BinListDifferentWindowSizeException, IllegalArgumentException {
		if (intervalList.getBinSize() != valueList.getBinSize()) {
			throw new BinListDifferentWindowSizeException();
		}
		BinList resultList = new BinList(valueList.getChromosomeManager(), valueList.getBinSize(), precision);
		for (short i = 0; i < intervalList.size(); i++)  {
			if ((intervalList.get(i) == null) || (i >= valueList.size()) || (valueList.get(i) == null)) {
				resultList.add(null);
			} else {
				List<Double> listToAdd = ListFactory.createList(precision, intervalList.size(i));
				resultList.add(listToAdd);
				int j = 0;
				while ((j < intervalList.size(i)) && (j < valueList.size(i))) {
					while ((j < intervalList.size(i)) && (j < valueList.size(i)) && (intervalList.get(i, j) == 0)) {
						resultList.set(i, j, 0d);
						j++;
					}
					int k = j;
					List<Double> values = new ArrayList<Double>();
					while ((j < intervalList.size(i)) && (j < valueList.size(i)) && (intervalList.get(i, j) != 0)) {
						if (valueList.get(i, j) != 0) {
							values.add(valueList.get(i, j));					
						}
						j++;
					}
					if (values.size() > 0) {
						Collections.sort(values);
						int indexStart = values.size() - (int)(values.size() * (double)percentageAcceptedValues / 100d);
						double result = 0;
						switch (method) {
						case AVERAGE:
							result = DoubleLists.average(values, indexStart, values.size() - 1);
							break;
						case MAXIMUM:
							List<Double> listTmp = values.subList(indexStart, values.size() - 1);
							if ((listTmp != null) && (listTmp.size() > 0)) {
								result = Collections.max(listTmp);
							}
							break;							
						case SUM:
							result = DoubleLists.sum(values, indexStart, values.size() - 1);
							break;
						default:
							throw new IllegalArgumentException("Invalid score calculation method");
						}

						for (; k <= j; k++) {
							if (k < resultList.size(i)) {
								resultList.set(i, k, result);
							}
						}
					}
				}
			}
		}
		return resultList;
	}


	/**
	 * Creates a new {@link BinList} that is the image of the specified BinList
	 * with a new size of bins
	 * @param binList input BinList
	 * @param binSize new size for the bins
	 * @param method new method of calculation
	 * @return a new BinList
	 */
	public static BinList changeBinSize(BinList binList, int binSize, ScoreCalculationMethod method) {
		return new BinList(binList.getChromosomeManager(), binSize, binList.getPrecision(), method, binList);
	}


	/**
	 * Copies the values of the specified {@link BinList} into a new BinList with a specified {@link DataPrecision}
	 * @param binList input BinList
	 * @param precision precision of the data of the new BinList
	 * @return a new BinList
	 */
	public static BinList changePrecision(BinList binList, DataPrecision precision) {
		BinList resultList = new BinList(binList.getChromosomeManager(), binList.getBinSize(), precision);
		for (List<Double> currentList: binList) {
			if (currentList == null) {
				resultList.add(null);
			} else {
				List<Double> listToAdd = ListFactory.createList(precision, currentList.size());
				for (int i = 0; i < currentList.size(); i++) {
					listToAdd.set(i, currentList.get(i));
				}
				resultList.add(listToAdd);
			}
		}
		return resultList;
	}


	/**
	 * Computes the coefficient of correlation between two {@link BinList}.
	 * Only the chromosomes set to <i>true</i> in chromoList will be used in the calculation. 
	 * @param binList1
	 * @param binList2
	 * @param chromoList set to true each chromosome of this list that you want to use in the calculation
	 * @return the coefficient of correlation between the two lists. 
	 * @throws BinListDifferentWindowSizeException
	 */
	public static double correlation(BinList binList1, BinList binList2, boolean[] chromoList) throws BinListDifferentWindowSizeException {
		if (binList1.getBinSize() != binList2.getBinSize()) {
			throw new BinListDifferentWindowSizeException();
		}

		int j, n = 0;
		double mean1 = 0;
		double mean2 = 0;
		// compute the mean
		for (short i = 0; i < binList1.size(); i++) {
			// We want to compute the correlation only for the chromosomes where chromoList is set to true
			if ((i < chromoList.length) && (chromoList[i]) && (binList1.get(i) != null) && (i < binList2.size()) && (binList2.get(i) != null)) {
				j = 0;
				while ((j < binList1.size(i)) && (j < binList2.size(i))) {
					if ((binList1.get(i, j) != 0) && (binList2.get(i, j) != 0)) {
						mean1 += binList1.get(i, j);
						mean2 += binList2.get(i, j);
						n++;
					}
					j++;
				}
			}
		}
		if (n == 0) {
			return 0d;
		}

		mean1 /= n;
		mean2 /= n;

		double stdDev1 = 0;
		double stdDev2 = 0;
		double correlationCoef = 0;

		// We compute standard deviations
		for (short i = 0; i < binList1.size(); i++) {
			// We want to compute the correlation only for the chromosomes where chromoList is set to true
			if ((i < chromoList.length) && (chromoList[i]) && (binList1.get(i) != null) && (i < binList2.size()) && (binList2.get(i) != null)) {
				j = 0;
				while ((j < binList1.size(i)) && (j < binList2.size(i))) {
					if ((binList1.get(i, j) != 0) && (binList2.get(i, j) != 0)) {
						stdDev1 += Math.pow(binList1.get(i, j) - mean1, 2);
						stdDev2 += Math.pow(binList2.get(i, j) - mean2, 2);
						correlationCoef += (binList1.get(i, j) * binList2.get(i,j));
					}
					j++;
				}
			}
		}
		stdDev1 = Math.sqrt(stdDev1 / n);
		stdDev2 = Math.sqrt(stdDev2 / n);
		// We compute the correlation 
		correlationCoef = (correlationCoef - (n * mean1 * mean2)) / ((n - 1) * stdDev1 * stdDev2);
		return correlationCoef;
	}


	/**
	 * Computes the density of bin with values on a region of halfWidth * 2 + 1 bins
	 * @param binList input {@link BinList}
	 * @param halfWidth half size of the region (in number of bin)
	 * @return a {@link BinList} showing the densities 
	 */
	public static BinList density(BinList binList, int halfWidth) {
		// the result is returned in 32 bits because the result is btw 0 and 1
		DataPrecision defaultPrecision = DataPrecision.PRECISION_32BIT;
		BinList resultList = new BinList(binList.getChromosomeManager(), binList.getBinSize(), defaultPrecision);
		if (halfWidth < 1) {
			return null;
		}
		int binCount = 2 * halfWidth + 1;
		for(short i = 0; i < binList.size(); i++)  {
			if ((binList.get(i) == null) || (binList.size(i) == 0)) {
				resultList.add(null);
			} else {
				List<Double> listToAdd = ListFactory.createList(defaultPrecision, binList.size(i));
				resultList.add(listToAdd);
				for (int j = 0; j < binList.size(i); j++) {
					int noneZeroBinCount = 0;
					for (int k = -halfWidth; k <= halfWidth; k++) {
						if((j + k >= 0) && ((j + k) < binList.size(i)))  {
							if (binList.get(i, j + k) != 0) {
								noneZeroBinCount++;
							}
						}
					}
					resultList.set(i, j, noneZeroBinCount / (double)binCount);
				}
			}
		}
		return resultList;
	}


	/**
	 * Applies a filter on a {@link BinList} selecting region where there is at least the specified 
	 * density of bins above (or under depending on the filter type) a specified threshold
	 * The result is returned in a new BinList.
	 * @param binList 
	 * @param filterType type of filter. Can be pass the high values or the low values
	 * @param threshold the selected windows must be above (or under) this value
	 * @param density minimum density of windows above (or under) the threshold for a region to be selected (percentage btw 0 and 1)
	 * @param regionSize size of the region (in number of bins) 
	 * @param precision precision of the result BinList
	 * @return a new {@link BinList} with only the selected windows
	 * @throws IllegalArgumentException if the filter is not correct
	 */
	public static BinList densityFilter(BinList binList, FilterType filterType, double threshold, double density, int regionSize, DataPrecision precision) throws IllegalArgumentException {
		// if percentage is zero, everything is selected
		if (density == 0) {
			return binList.deepClone();
		}		
		// we calculate the min number of bins above the threshold needed to select a region
		int minBinCount = (int)Math.ceil(regionSize * density);

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
						if (filterType == FilterType.LOW_PASS_FILTER) {
							if (binList.get(i, j + k) <= threshold) {
								binSelectedCount++;
							}
						} else if (filterType == FilterType.HIGH_PASS_FILTER) { 
							if (binList.get(i, j + k) >= threshold) {
								binSelectedCount++;
							}
						} else {
							throw new IllegalArgumentException("Invalid filter type");
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
		return resultList;		
	}


	/**
	 * @param binList1
	 * @param binList2
	 * @param precision precision of the result {@link BinList} 
	 * @return a new {@link BinList} resulting of the operation binList1 / binList2
	 * @throws BinListDifferentWindowSizeException
	 */
	public static BinList division(BinList binList1, BinList binList2, DataPrecision precision) throws BinListDifferentWindowSizeException{
		if (binList1.getBinSize() != binList2.getBinSize()) {
			throw new BinListDifferentWindowSizeException();
		}
		BinList resultList = new BinList(binList1.getChromosomeManager(), binList1.getBinSize(), precision);
		for(short i = 0; i < binList1.size(); i++)  {
			if((binList1.get(i) == null) || (i >= binList2.size()) || (binList2.get(i) == null)) {
				resultList.add(null);
			} else {
				List<Double> listToAdd = ListFactory.createList(precision, binList1.size(i));
				resultList.add(listToAdd);
				for(int j = 0; j < binList1.size(i); j++) {
					if ((j < binList2.size(i)) && (binList2.get(i, j) != 0)) {
						resultList.set(i, j, binList1.get(i, j) / binList2.get(i, j));
					} else {
						resultList.set(i, j, 0d);
					}
				}
			}
		}
		return resultList;
	}


	/**
	 * Divides every value of the specified {@link BinList} by a specified constant
	 * @param binList 
	 * @param constant 
	 * @param precision precision of the data of the new {@link BinList} 
	 * @return a new {@link BinList}
	 * @throws ArithmeticException when the constant is zero
	 */
	public static BinList division(BinList binList, double constant, DataPrecision precision) throws ArithmeticException {
		if (constant == 0) {
			throw new ArithmeticException("Division by Zero");
		}
		if (constant == 1) {
			return binList.deepClone();
		}
		BinList resultList = new BinList(binList.getChromosomeManager(), binList.getBinSize(), precision);
		for (short i = 0; i < binList.size(); i++) {
			if ((binList.get(i) == null) || (binList.size(i) == 0)) {
				resultList.add(null);
			} else {
				List<Double> listToAdd = ListFactory.createList(precision, binList.size(i));
				resultList.add(listToAdd);
				// We add a constant to each element
				for (int j = 0; j < binList.size(i); j++) {
					resultList.set(i, j, binList.get(i, j) / constant);
				}
			}
		}
		return resultList;		
	}


	/**
	 * Applies a gaussian filter on the BinList and returns the result in a new BinList.
	 * Sigma is used to configure the gaussian filter
	 * @param binList {@link BinList} to gauss
	 * @param sigma parameter of the gaussian filter
	 * @param precision precision of the result {@link BinList}
	 * @return a new {@link BinList}
	 */
	public static BinList gauss(BinList binList, int sigma, DataPrecision precision) {
		int binSize =  binList.getBinSize();
		int distance;
		int halfWidth = 2 * sigma / binSize;
		// we create an array of coefficients. The index correspond to a distance and for each distance we calculate a coefficient 
		double[] coefTab = new double[halfWidth + 1];
		for(int i = 0; i <= halfWidth; i++) {
			coefTab[i] = Math.exp(-(Math.pow(((double) (i * binSize)), 2) / (2.0 * Math.pow((double) sigma, 2))));
		}
		// we gauss
		BinList resultList = new BinList(binList.getChromosomeManager(), binList.getBinSize(), precision);	
		for(short i = 0; i < binList.size(); i++) {
			if ((binList.get(i) == null) || (binList.size(i) == 0)) {
				resultList.add(null);
			} else {
				List<Double> listToAdd = ListFactory.createList(precision, binList.size(i));
				resultList.add(listToAdd);
				for(int j = 0; j < binList.size(i); j++) {
					if(binList.get(i, j) != 0)  {
						// apply the array of coefficients centered on the current value to gauss
						double SumCoef = 0;
						double SumNormSignalCoef = 0;
						for (int k = -halfWidth; k <= halfWidth; k++) {
							if((j + k >= 0) && ((j + k) < binList.size(i)))  {
								distance = Math.abs(k);
								if(binList.get(i, j + k) != 0)  {
									SumCoef += coefTab[distance];
									SumNormSignalCoef += coefTab[distance] * binList.get(i, j + k);
								}
							}
						}
						if(SumCoef == 0) {
							resultList.set(i, j, 0d);
						} else {
							resultList.set(i, j, SumNormSignalCoef / SumCoef);
						}
					} else {
						resultList.set(i, j, 0d);
					}
				}
			}
		}
		return resultList;
	}


	/**
	 * Indexes the scores between <i>indexDown</i> and <i>indexUp</i> 
	 * based on the highest and the lowest value of the whole genome.
	 * @param binList {@link BinList} to index
	 * @param saturation percentage of the highest and lowest value saturated (btw 0 and 1)
	 * @param indexDown Smallest value of the indexed data
	 * @param indexUp Greatest value of the indexed data
	 * @param precision precision of the data of the result {@link BinList}
	 * @return new {@link BinList} resulting from the indexing
	 */
	public static BinList index(BinList binList, double saturation, double indexDown, double indexUp, DataPrecision precision) {
		double halfSaturation = saturation / 2;
		double percentUp = (1 - halfSaturation);
		double percentDown = halfSaturation;
		double[] listTmp;
		int k = 0, totalLength = 0;

		// We create an array containing all the intensities of all chromosomes
		for (short i = 0; i < binList.size(); i++) {
			if(binList.get(i) != null) {
				totalLength += binList.size(i);
			}
		}
		listTmp = new double[totalLength];			
		for (short i = 0; i < binList.size(); i++) {
			if(binList.get(i) != null) {
				for (int j = 0; j < binList.size(i); j++) {
					if(binList.get(i, j) != 0) {
						listTmp[k] = binList.get(i, j);
						k++;
					}
				}
			}
		}
		if (k > 0) {
			// We create a new array containing all the values different from 0 
			double[] listTmpBis = new double[k];
			for(int i = 0; i < listTmpBis.length; i++) {
				listTmpBis[i] = listTmp[i];		
			}
			// We want to have the values of intensities sorted for the whole genome
			Arrays.sort(listTmpBis);
			// We research the highest and the lowest value 
			int rankUp = (int)(percentUp * (listTmpBis.length - 1));
			int rankDown = (int)(percentDown * (listTmpBis.length - 1));
			double valueUp = listTmpBis[rankUp];
			double valueDown = listTmpBis[rankDown];
			// We calculate the difference between the highest and the lowest value
			double distanceValueUpDown = valueUp - valueDown;
			double distanceIndexUpDown = indexUp - indexDown;
			BinList resultList = new BinList(binList.getChromosomeManager(), binList.getBinSize(), precision);
			for (short i = 0; i < binList.size(); i++) {
				if ((binList.get(i) == null) || (binList.size(i) == 0)) {
					resultList.add(null);
				} else {
					// We index the intensities
					List<Double> listToAdd = ListFactory.createList(precision, binList.size(i));
					resultList.add(listToAdd);
					for (int j = 0; j < binList.size(i); j++) {
						if (binList.get(i, j) == 0) {
							resultList.set(i, j, 0d);
						} else if(binList.get(i, j) < valueDown) {
							resultList.set(i, j, indexDown);
						} else if(binList.get(i, j) > valueUp) {
							resultList.set(i, j, indexUp);
						} else { 
							resultList.set(i, j, distanceIndexUpDown * (binList.get(i, j) - valueDown) / distanceValueUpDown + indexDown);
						}
					}
				}
			}
			return resultList;
		} else {
			return null;
		}
	}


	/**
	 * Indexes the scores between <i>indexDown</i> and <i>indexUp</i> based
	 * on the highest and the lowest value of each chromosome
	 * @param binList {@link BinList} to index
	 * @param saturation percentage of the highest and lowest value saturated (btw 0 and 1) 
	 * @param indexDown smallest value of the indexed data
	 * @param indexUp greatest value of the indexed data
	 * @param precision precision of the indexed {@link BinList}
	 * @return new {@link BinList} resulting from the indexing
	 */
	public static BinList indexByChromo(BinList binList, double saturation, double indexDown, double indexUp, DataPrecision precision) {
		double halfSaturation = saturation / 2;
		double percentUp = (1 - halfSaturation);
		double percentDown = halfSaturation;
		double distanceIndexUpDown = indexUp - indexDown;

		BinList resultList = new BinList(binList.getChromosomeManager(), binList.getBinSize(), precision);
		for(short i = 0; i < binList.size(); i++) {
			if ((binList.get(i) == null) || (binList.size(i) == 0)) {
				resultList.add(null);
			} else {
				double[] listTmp = new double[binList.size(i)];
				List<Double> listToAdd = ListFactory.createList(precision, binList.size(i));
				resultList.add(listToAdd);
				int k = 0;
				for (int j = 0; j < binList.size(i); j++) {
					if(binList.get(i, j) != 0) {
						listTmp[k] = binList.get(i, j);
						k++;
					}
				}
				if(k > 0) {
					// We create a new array containing all the values different from 0 
					double[] listTmpBis = new double[k];
					for(int j = 0; j < listTmpBis.length; j++) {
						listTmpBis[j] = listTmp[j];
					}
					// We want to have the values of intensities sorted for each chromosome
					Arrays.sort(listTmpBis);
					// We research the highest and the lowest value 
					int rankUp = (int)(percentUp * (listTmpBis.length - 1)); 
					int rankDown = (int)(percentDown * (listTmpBis.length - 1));

					double valueUp = listTmpBis[rankUp];
					double valueDown = listTmpBis[rankDown];

					// We calculate the difference between the highest and the lowest value
					double distanceValueUpDown = valueUp - valueDown;
					// We index the intensities 
					for (int j = 0; j < binList.size(i); j++) {
						if(binList.get(i, j) == 0) {
							resultList.set(i, j, 0d);
						} else if(binList.get(i, j) < valueDown) {
							resultList.set(i, j, indexDown);
						} else if(binList.get(i, j) > valueUp) {
							resultList.set(i, j, indexUp);
						} else { 
							resultList.set(i, j, distanceIndexUpDown * (binList.get(i, j) - valueDown) / distanceValueUpDown + indexDown);
						}
					}
				}
			}
		}
		return resultList;
	}


	/**
	 * Applies the function f(x) = log2(x) to each element x of the specified BinList. 
	 * Returns the result in a new BinList
	 * @param binList
	 * @param precision precision of the data of the result list
	 * @return a new binList resulting of the calculation
	 */
	public static BinList log2(BinList binList, DataPrecision precision) {
		BinList resultList = new BinList(binList.getChromosomeManager(), binList.getBinSize(), precision);
		for(short i = 0; i < binList.size(); i++) {
			if ((binList.get(i) == null) || (binList.size(i) == 0)) {
				resultList.add(null);
			} else {
				List<Double> listToAdd = ListFactory.createList(precision, binList.size(i));
				resultList.add(listToAdd);
				// we want to calculate the log2 for each element
				for (int j = 0; j < binList.size(i); j++) {
					// log is defined on R+*
					if(binList.get(i, j) > 0) {
						// change of base: logb(x) = logk(x) / logk(b)
						resultList.set(i, j, Math.log(binList.get(i, j)) / Math.log(2));
					} else {
						resultList.set(i, j, 0d);
					}
				}
			}
		}
		return resultList;
	}


	/**
	 * Applies the function f(x) = log2(x + damper) - log2(average + damper) to each element x of the current BinList. 
	 * Returns the result in a new BinList
	 * @param binList
	 * @param damper this parameter can be used to damp the signal
	 * @param precision precision of the data of the result list
	 * @return a new binList resulting of the calculation
	 */
	public static BinList log2(BinList binList, double damper, DataPrecision precision) {
		BinList resultList = new BinList(binList.getChromosomeManager(), binList.getBinSize(), precision);
		double mean = average(binList); 
		double logMean = 0;
		// log is defined on R+*
		if (mean + damper > 0) {
			// change of base: logb(x) = logk(x) / logk(b)
			logMean = Math.log(mean + damper) / Math.log(2);
		}
		for(short i = 0; i < binList.size(); i++) {
			if ((binList.get(i) == null) || (binList.size(i) == 0)) {
				resultList.add(null);
			} else {
				List<Double> listToAdd = ListFactory.createList(precision, binList.size(i));
				resultList.add(listToAdd);
				// we want to calculate the log2 for each element
				for (int j = 0; j < binList.size(i); j++) {
					// log is defined on R+*
					if (binList.get(i, j) + damper > 0) {
						// change of base: logb(x) = logk(x) / logk(b)
						resultList.set(i, j, Math.log(binList.get(i, j) + damper) / Math.log(2) - logMean);
					} else {
						resultList.set(i, j, 0d);
					}
				}
			}
		}
		return resultList;
	}


	/**
	 * @param binList
	 * @return the greatest score value of the specified {@link BinList}
	 */
	public static double max(BinList binList) {
		double max = Double.NEGATIVE_INFINITY;
		for (List<Double> currentList : binList) {
			if (currentList != null) {
				max = Math.max(max, Collections.max(currentList));
			}
		}
		return max;
	}


	/**
	 * @param binList a {@link BinList}
	 * @return the maximum score to display on a BinList track
	 */
	public static double maxDisplayedScore(BinList binList) {
		final double realMax = average(binList); 
		int maxScoreDisplayed = 1;
		while (realMax / maxScoreDisplayed > 1) {
			maxScoreDisplayed *= 10;
		}
		return maxScoreDisplayed;
	}


	/**
	 * @param binList
	 * @return the smallest score value of the specified {@link BinList}
	 */
	public static double min(BinList binList) {
		double min = Double.POSITIVE_INFINITY;
		for (List<Double> currentList : binList) {
			if (currentList != null) {
				min = Math.min(min, Collections.min(currentList));
			}
		}
		return min;
	}


	/**
	 * @param binList a {@link BinList}
	 * @return the minimum score to display on a BinList track
	 */
	public static double minDisplayedScore(BinList binList) {
		return Math.min(0, min(binList));
	}


	/**
	 * @param binList1
	 * @param binList2
	 * @param precision precision of the result {@link BinList} 
	 * @return a new {@link BinList} resulting of the operation binList1 * binList2
	 * @throws BinListDifferentWindowSizeException
	 */
	public static BinList multiplication(BinList binList1, BinList binList2, DataPrecision precision) throws BinListDifferentWindowSizeException{
		if (binList1.getBinSize() != binList2.getBinSize()) {
			throw new BinListDifferentWindowSizeException();
		}
		BinList resultList = new BinList(binList1.getChromosomeManager(), binList1.getBinSize(), precision);
		for(short i = 0; i < binList1.size(); i++)  {
			if((binList1.get(i) == null) || (i >= binList2.size()) || (binList2.get(i) == null)) {
				resultList.add(null);
			} else {
				List<Double> listToAdd = ListFactory.createList(precision, binList1.size(i));
				resultList.add(listToAdd);
				for(int j = 0; j < binList1.size(i); j++) {
					if(j < binList2.size(i)) {
						resultList.set(i, j, binList1.get(i, j) * binList2.get(i, j));
					} else {
						resultList.set(i, j, 0d);
					}
				}
			}
		}
		return resultList;
	}


	/**
	 * Multiplies every value of the specified {@link BinList} by the specified constant
	 * @param binList 
	 * @param constant 
	 * @param precision precision of the data of the new {@link BinList} 
	 * @return a new {@link BinList}
	 */
	public static BinList multiplication(BinList binList, double constant, DataPrecision precision) {
		if (constant == 1) {
			return binList.deepClone();
		}
		BinList resultList = new BinList(binList.getChromosomeManager(), binList.getBinSize(), precision);
		for (short i = 0; i < binList.size(); i++) {
			if ((binList.get(i) == null) || (binList.size(i) == 0)) {
				resultList.add(null);
			} else {
				List<Double> listToAdd = ListFactory.createList(precision, binList.size(i));
				resultList.add(listToAdd);
				// We add a constant to each element
				for (int j = 0; j < binList.size(i); j++) {
					resultList.set(i, j, binList.get(i, j) * constant);
				}
			}
		}
		return resultList;		
	}


	/**
	 * @param binList {@link BinList} to normalize
	 * @param factor multiply the result by this value
	 * @param precision precision of the result BinList
	 * @return a new {@link BinList} resulting of the normalization of the input BinList
	 */
	public static BinList normalize(BinList binList, int factor, DataPrecision precision)  {
		BinList resultList = new BinList(binList.getChromosomeManager(), binList.getBinSize(), precision);		
		// We calculate the sum of the scores
		double scoreCount = 0;
		for (int i = 0; i < binList.size(); i++) {
			if (binList.get(i) != null) {
				for (int j = 0; j < binList.size(i); j++) {
					scoreCount += binList.get(i, j);
				}
			}
		}		
		// We normalize
		double normalizerFactor = (double)factor / (double)scoreCount;
		for(short i = 0; i < binList.size(); i++) {
			if ((binList.get(i) == null) || (binList.size(i) == 0)) {
				resultList.add(null);
			} else {
				List<Double> listToAdd = ListFactory.createList(precision, binList.size(i));
				resultList.add(listToAdd);
				for (int j = 0; j < binList.size(i); j++) {
					resultList.set(i, j, binList.get(i, j) * normalizerFactor);
				}
			}
		}
		return resultList;
	}


	/**
	 * Prints the specified {@link BinList} on the standard output
	 * @param binList
	 */
	public static void print(BinList binList) {
		int binSize = binList.getBinSize();
		for(short i = 0; i < binList.size(); i++) {
			if(binList.get(i) != null) {
				for (int j = 0; j < binList.size(i); j++)
					System.out.println(binList.getChromosomeManager().getChromosome(i).getName() + "\t" + (j * binSize) + "\t" + ((j + 1) * binSize) + "\t" + binList.get(i, j));
			}
		}
	}


	/**
	 * Creates bins of score with a size of <i>scoreBinsSize</i>, 
	 * and computes how many bins of the BinList there is in each bin of score.
	 * Writes the result in a file. 
	 * @param binList
	 * @param scoreBinsSize Size of the bins of scores.
	 * @param file Output file containing the result. 
	 * @throws IllegalArgumentException
	 * @throws IOException
	 */
	public static void repartition(BinList binList, double scoreBinsSize, File file) throws IllegalArgumentException, IOException {
		if(scoreBinsSize <= 0) {
			throw new IllegalArgumentException("the size of the score bins must be strictly positive");
		}
		// search the greatest and smallest score
		double max = max(binList);
		double min = min(binList);
		double distanceMinMax = max - min;

		int result[] = new int[(int)(distanceMinMax / scoreBinsSize) + 1];
		for (short i = 0; i < binList.size(); i++) {
			if (binList.get(i) != null) {
				for(int j = 0; j < binList.size(i); j++) 
					result[(int)((binList.get(i, j) - min) / scoreBinsSize)]++;
			}
		}	
		BufferedWriter writer = null;
		// try to create a output file
		try {
			writer = new BufferedWriter(new FileWriter(file));
			for(int i = 0; i < result.length; i++) {
				double position = i * scoreBinsSize + min; 
				writer.write(Double.toString(position) + ", " + Double.toString(position + scoreBinsSize) + ", " + Integer.toString(result[i]));
				writer.newLine();		
			}
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}


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
		int totalLenght = (int)binCount(binList,selectedChromo);
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
		int totalLenght = (int)binCount(binList,selectedChromo);
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


	/**
	 * @param binList a {@link BinList}
	 * @param chromoList set to true each chromosome of this list that you want to use in the calculation
	 * @return the sum of the scores on the selected chromosomes of the specified BinList
	 */
	public static double scoreCount(BinList binList, boolean[] chromoList) {
		int scoreCount = 0;
		for (int i = 0; i < binList.size(); i++) {
			if ((binList.get(i) != null) && (i < chromoList.length) && (chromoList[i])) {
				for (int j = 0; j < binList.size(i); j++) {
					scoreCount += binList.get(i, j);
				}
			}
		}
		return scoreCount;
	}


	/**
	 * Searches the peaks of a specified {@link BinList}. We consider a point as a peak when the 
	 * moving standard deviation = <i>nbSDAccepted</i> * global standard deviation.
	 * @param binList
	 * @param sizeMovingSD Width (in bp) of the moving standard deviation.
	 * @param nbSDAccepted
	 * @param precision precision of the result {@link BinList}  
	 * @return a new {@link BinList} containing only the peaks of the input {@link BinList}
	 */
	public static BinList searchPeaks(BinList binList, int sizeMovingSD, double nbSDAccepted, DataPrecision precision) {
		int binSize = binList.getBinSize();
		int halfWidth = sizeMovingSD / binSize;
		BinList resultList = new BinList(binList.getChromosomeManager(), binSize, precision);

		for(short i = 0; i < binList.size(); i++) {
			if ((binList.get(i) == null) || (binList.size(i) == 0)) {
				resultList.add(null);
			} else {
				List<Double> listToAdd = ListFactory.createList(precision, binList.size(i));
				resultList.add(listToAdd);

				double sd = DoubleLists.standardDeviation(binList.get(i), 0, binList.size(i) - 1);
				if (sd != 0) {
					double minAcceptedSD = nbSDAccepted * sd;
					for (int j = 0; j < binList.size(i); j++) {
						if (binList.get(i, j) != 0) {
							int indexStart = j - halfWidth;
							int indexStop = j + halfWidth;
							if (indexStart < 0) {
								indexStart = 0;
							}
							if (indexStop > binList.size(i) - 1) {
								indexStop = binList.size(i) - 1;
							}
							double localSd = DoubleLists.standardDeviation(binList.get(i), indexStart, indexStop);
							if ((localSd != 0) && (localSd > minAcceptedSD)) {
								resultList.set(i, j, binList.get(i,j));
							} else {
								resultList.set(i, j, 0d);
							}
						}
					}
				}
			}			
		}
		return resultList;		
	}



	/**
	 * Zips and serializes a BinList into a {@link ByteArrayOutputStream}
	 * @param binList a {@link BinList}
	 * @return a ByteArrayOutputStream
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static ByteArrayOutputStream serializeAndZip(BinList binList) throws IOException, ClassNotFoundException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		GZIPOutputStream gz = new GZIPOutputStream(baos);
		ObjectOutputStream oos = new ObjectOutputStream(gz);
		oos.writeObject(binList);
		oos.flush();
		oos.close();
		gz.flush();
		gz.close();
		return baos;
	}


	/**
	 * @param binList a {@link BinList}
	 * @param chromoList set to true each chromosome of this list that you want to use in the calculation
	 * @return the standard deviation of the specified BinList on the selected chromosomes 
	 */
	public static Double standardDeviation(BinList binList, boolean[] chromoList) {
		double stdDev = 0d;
		int n = 0;
		// we compute the mean
		double mean = average(binList, chromoList);
		// We compute standard deviations
		for (short i = 0; i < binList.size(); i++) {
			// We want to compute the correlation only for the chromosomes where chromoList is set to true
			if ((i < chromoList.length) && (chromoList[i]) && (binList.get(i) != null)) {
				for (int j = 0; j < binList.size(i); j++) {
					if ((binList.get(i, j) != 0)) {
						stdDev += Math.pow(binList.get(i, j) - mean, 2);
						n++;
					}
				}
			}
		}
		if (n == 0) {
			return 0d;
		} else {
			return (stdDev / (double)n);
		}
	}


	/**
	 * @param binList1
	 * @param binList2
	 * @param precision precision of the result {@link BinList} 
	 * @return a new {@link BinList} resulting of the operation binList1 - binList2
	 * @throws BinListDifferentWindowSizeException
	 */
	public static BinList subtraction(BinList binList1, BinList binList2, DataPrecision precision) throws BinListDifferentWindowSizeException{
		if (binList1.getBinSize() != binList2.getBinSize()) {
			throw new BinListDifferentWindowSizeException();
		}
		BinList resultList = new BinList(binList1.getChromosomeManager(), binList1.getBinSize(), precision);
		for(short i = 0; i < binList1.size(); i++)  {
			if((binList1.get(i) == null) || (i >= binList2.size()) || (binList2.get(i) == null)) {
				resultList.add(null);
			} else {
				List<Double> listToAdd = ListFactory.createList(precision, binList1.size(i));
				resultList.add(listToAdd);
				for(int j = 0; j < binList1.size(i); j++) {
					if(j < binList2.size(i)) {
						resultList.set(i, j, binList1.get(i, j) - binList2.get(i, j));
					} else {
						resultList.set(i, j, 0d);
					}
				}
			}
		}
		return resultList;
	}


	/**
	 * Subtracts a constant from every value of the specified {@link BinList}
	 * @param binList 
	 * @param constant subtracts this value from the {@link BinList}
	 * @param precision precision of the data of the new {@link BinList} 
	 * @return A new {@link BinList}
	 */
	public static BinList subtraction(BinList binList, double constant, DataPrecision precision) {
		if (constant == 0) {
			return binList.deepClone();
		}
		BinList resultList = new BinList(binList.getChromosomeManager(), binList.getBinSize(), precision);
		for (short i = 0; i < binList.size(); i++) {
			if ((binList.get(i) == null) || (binList.size(i) == 0)) {
				resultList.add(null);
			} else {
				List<Double> listToAdd = ListFactory.createList(precision, binList.size(i));
				resultList.add(listToAdd);
				// We add a constant to each element
				for (int j = 0; j < binList.size(i); j++) {
					resultList.set(i, j, binList.get(i, j) - constant);
				}
			}
		}
		return resultList;		
	}


	/**
	 * Applies a filter on a {@link BinList} selecting the windows above (or under depending on the filter type) 
	 * a specified threshold.
	 * The result is returned in a new BinList.
	 * @param binList
	 * @param filterType type of filter. Can be pass the high values or the low values
	 * @param threshold the selected windows must be above (or under) this value
	 * @param nbConsecutiveValues select only if there is x consecutive windows above (or under) the threshold
	 * @param precision precision of the result {@link BinList} 
	 * @return a new {@link BinList} with only the selected windows
	 * @throws IllegalArgumentException if the filter is not correct
	 */
	public static BinList thresholdFilter(BinList binList, FilterType filterType, double threshold, int nbConsecutiveValues, DataPrecision precision) throws IllegalArgumentException {
		BinList resultList = new BinList(binList.getChromosomeManager(), binList.getBinSize(), precision);
		for(short i = 0; i < binList.size(); i++)  {
			if ((binList.get(i) == null) || (binList.size(i) == 0)) {
				resultList.add(null);
			} else {
				List<Double> listToAdd = ListFactory.createList(precision, binList.size(i));
				resultList.add(listToAdd);
				for (int j = 0; j < binList.size(i) - nbConsecutiveValues; j++) {
					boolean selected = true;
					int k = 0;
					// we accept a window if there is nbConsecutiveValues above or under the filter
					while ((selected) && (k < nbConsecutiveValues) && (j + k < binList.size(i))) {
						// depending on the filter type we accept values above or under the threshold
						if (filterType == FilterType.LOW_PASS_FILTER) {
							selected = binList.get(i, j + k) <= threshold;
						} else if (filterType == FilterType.HIGH_PASS_FILTER) {
							selected = binList.get(i, j + k) >= threshold;
						} else {
							throw new IllegalArgumentException("Invalid filter type");
						}
						k++;
					}
					if (selected) {
						while ((j < binList.size(i)) && 
								(((binList.get(i, j) >= threshold) && (filterType == FilterType.HIGH_PASS_FILTER)) ||
										(binList.get(i, j) <= threshold) && (filterType == FilterType.LOW_PASS_FILTER))) {
							resultList.set(i, j, binList.get(i, j));
							j++;
						}
					} else {
						resultList.set(i, j, 0d);
					}
				}
			}
		}
		return resultList;
	}


	/**
	 * Defines regions as "islands" of non zero value bins 
	 * separated by more than a specified number of zero value bins.
	 * Computes the average on these regions.
	 * Returns a new {@link BinList} with the defined regions having their average as a score
	 * @param binList input BinList
	 * @param zeroWindowGap number of zero value windows defining a gap between two islands
	 * @return a new BinList
	 */
	public static BinList transfrag(BinList binList, int zeroWindowGap) {
		DataPrecision precision = binList.getPrecision();
		BinList resultList = new BinList(binList.getChromosomeManager(), binList.getBinSize(), precision);
		for(short i = 0; i < binList.size(); i++)  {
			if ((binList.get(i) == null) || (binList.size(i) == 0)) {
				resultList.add(null);
			} else {
				List<Double> listToAdd = ListFactory.createList(precision, binList.size(i));
				resultList.add(listToAdd);
				int j = 0;				
				while (j < binList.size(i)) {
					// skip zero values
					while ((j < binList.size(i)) && (binList.get(i, j) == 0)) {
						j++;
					}
					int regionStart = j;
					int regionStop = regionStart;
					int zeroWindowCount = 0;
					// a region stops when there is maxZeroWindowGap consecutive zero windows
					while ((j < binList.size(i)) && (zeroWindowCount < zeroWindowGap)) {
						if (binList.get(i, j) == 0) {
							zeroWindowCount++;
						} else {
							zeroWindowCount = 0;
							regionStop = j;
						}
						j++;
					}
					if (regionStart != regionStop) { 
						// all the windows of the region are set with the average value on the region
						double regionScoreAvg = DoubleLists.average(binList.get(i), regionStart, regionStop);
						for (j = regionStart; j <= regionStop; j++) {
							if (j < resultList.size(i)) {
								resultList.set(i, j, regionScoreAvg);
							}
						}
					}
					j++;
				}
			}
		}
		return resultList;
	}


	/**
	 * Unzips and unserializes a {@link ByteArrayOutputStream} and returns a {@link BinList}
	 * @param baos a {@link ByteArrayOutputStream}
	 * @return a BinList 
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static BinList unzipAndUnserialize(ByteArrayOutputStream baos) throws IOException, ClassNotFoundException {
		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		GZIPInputStream gz = new GZIPInputStream(bais);
		ObjectInputStream ois = new ObjectInputStream(gz);
		BinList binList = (BinList)ois.readObject();
		ois.close();
		gz.close();
		return binList;
	}
}
