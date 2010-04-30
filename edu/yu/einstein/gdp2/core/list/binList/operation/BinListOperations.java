/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.list.binList.operation;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import yu.einstein.gdp2.core.enums.DataPrecision;
import yu.einstein.gdp2.core.enums.ScoreCalculationMethod;
import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.list.binList.ListFactory;
import yu.einstein.gdp2.exception.BinListDifferentWindowSizeException;
import yu.einstein.gdp2.util.DoubleLists;


/**
 * This class contains various static methods for manipulating {@link BinList}
 * @author Julien Lajugie
 * @version 0.1
 */
public class BinListOperations {


	/**
	 * @param chromoList array of boolean. 
	 * @return true if all the booleans are set to true or if the array is null. False otherwise 
	 */
	private static boolean allChromosomeSelected(boolean[] chromoList) {
		if (chromoList == null) {
			return true;
		} 
		for (boolean isSelected: chromoList) {
			if (!isSelected) {
				return false;
			}
		}
		return true;
	}


	/**
	 * @param binList
	 * @param chromoList set to true each chromosome of this list that you want to use in the calculation
	 * Perform the operation on every chromosome if null
	 * @return the average value of the specified {@link BinList}
	 */
	public static double average(BinList binList, boolean[] chromoList) {
		// if the result has already been calculated for the BinList we return it
		if ((allChromosomeSelected(chromoList)) && (binList.getAverage() != null)) {
			return binList.getAverage();
		}
		int n = 0;
		double sum = 0;
		for (int i = 0; i < binList.size(); i++) {
			if (((chromoList == null) || ((i < chromoList.length) && (chromoList[i]))) && (binList.get(i) != null)) {
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
		// if the result has already been calculated for the BinList we return it
		if ((allChromosomeSelected(chromoList)) && (binList.getBinCount() != null)) {
			return binList.getBinCount();
		}
		long binCount = 0;
		for (int i = 0; i < binList.size(); i++) {
			if (((chromoList == null) || ((i < chromoList.length) && (chromoList[i]))) && (binList.get(i) != null)) {
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
		resultList.finalizeConstruction();
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
		BinList resultList = new BinList(binList.getChromosomeManager(), binSize, binList.getPrecision(), method, binList);
		return resultList;
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
		resultList.finalizeConstruction();
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
		resultList.finalizeConstruction();
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
		resultList.finalizeConstruction();
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
		double mean = average(binList, null); 
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
		resultList.finalizeConstruction();
		return resultList;
	}


	/**
	 * @param binList
	 * @param chromoList set to true each chromosome of this list that you want to use in the calculation
	 * Perform the operation on every chromosome if null
	 * @return the greatest score value of the specified {@link BinList}. Zero value bins are excluded
	 */
	public static double max(BinList binList, boolean[] chromoList) {
		// if the result has already been calculated for the BinList we return it
		if ((allChromosomeSelected(chromoList)) && (binList.getMax() != null)) {
			return binList.getMax();
		}
		double max = Double.NEGATIVE_INFINITY;
		for (int i = 0; i < binList.size(); i++) {
			if (((chromoList == null) || ((i < chromoList.length) && (chromoList[i]))) && (binList.get(i) != null)) {
				for (int j = 0; j < binList.size(i); j++) {
					if (binList.get(i, j) != 0) {
						max = Math.max(max, binList.get(i, j));
					}
				}
			}
		}
		return max;
	}


	/**
	 * @param binList a {@link BinList}
	 * @return the maximum score to display on a BinList track
	 */
	public static double maxScoreToDisplay(BinList binList) {
		final double realMax = max(binList, null);
		// if the max is negative we return 0
		if (realMax <= 0) {
			return 0;
		}
		// if the max of the BinList can be written as 10^x we return this value as a maximum
		int maxScoreDisplayed = 1;
		while (realMax / maxScoreDisplayed > 1) {
			maxScoreDisplayed *= 10;
		}
		if (realMax / maxScoreDisplayed == 1) {
			return realMax;
		}
		// otherwise we try to find the closest 10^x value above 2 * (average + stdev) 
		final double proposedMax = (average(binList, null) + standardDeviation(binList, null)) * 2; 
		if (proposedMax <= 0) {
			return 0;
		}
		maxScoreDisplayed = 1;
		while (proposedMax / maxScoreDisplayed > 1) {
			maxScoreDisplayed *= 10;
		}
		return maxScoreDisplayed;
	}


	/**
	 * @param binList
	 * @param chromoList set to true each chromosome of this list that you want to use in the calculation
	 * Perform the operation on every chromosome if null
	 * @return the smallest score value of the specified {@link BinList}. Zero value bins are excluded
	 */
	public static double min(BinList binList, boolean[] chromoList) {
		// if the result has already been calculated for the BinList we return it
		if ((allChromosomeSelected(chromoList)) && (binList.getMin() != null)) {
			return binList.getMin();
		}
		double min = Double.POSITIVE_INFINITY;
		for (int i = 0; i < binList.size(); i++) {
			if (((chromoList == null) || ((i < chromoList.length) && (chromoList[i]))) && (binList.get(i) != null)) {
				for (int j = 0; j < binList.size(i); j++) {
					if (binList.get(i, j) != 0) {
						min = Math.min(min, binList.get(i, j));
					}
				}
			}
		}
		return min;
	}


	/**
	 * @param binList a {@link BinList}
	 * @return the minimum score to display on a BinList track
	 */
	public static double minScoreToDisplay(BinList binList) {
		// if the min is positive we return 0
		final double realMin = min(binList, null);
		if (realMin >= 0) {
			return 0;
		}
		// if the min of the BinList can be written as -10^x we return this value as a minimum
		int minScoreDisplayed = -1;
		while (realMin / minScoreDisplayed > 1) {
			minScoreDisplayed *= 10;
		}
		if (realMin / minScoreDisplayed == 1) {
			return realMin;
		}
		// otherwise we try to find the closest 10^x value under 2 * (average - stdev) 
		final double proposedMin = (average(binList, null) - standardDeviation(binList, null)) * 2; 
		if (proposedMin >= 0) {
			return 0;
		}
		minScoreDisplayed = -1;
		while (proposedMin / minScoreDisplayed > 1) {
			minScoreDisplayed *= 10;
		}
		return minScoreDisplayed;
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
		resultList.finalizeConstruction();
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
		double max = max(binList, null);
		max = Math.max(max, 0);
		double min = min(binList, null);
		min = Math.min(min, 0);
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
	 * @param binList a {@link BinList}
	 * @param chromoList set to true each chromosome of this list that you want to use in the calculation
	 * @return the sum of the scores on the selected chromosomes of the specified BinList
	 */
	public static double scoreCount(BinList binList, boolean[] chromoList) {
		// if the result has already been calculated for the BinList we return it
		if ((allChromosomeSelected(chromoList)) && (binList.getScoreCount() != null)) {
			return binList.getScoreCount();
		}
		int scoreCount = 0;
		for (int i = 0; i < binList.size(); i++) {
			if (((chromoList == null) || ((i < chromoList.length) && (chromoList[i]))) && (binList.get(i) != null)) {
				for (int j = 0; j < binList.size(i); j++) {
					scoreCount += binList.get(i, j);
				}
			}
		}
		return scoreCount;
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
	 * @param chromoList set to true each chromosome of this list that you want to use in the calculation. 
	 * Perform the operation on every chromosome if null
	 * @return the standard deviation of the specified BinList on the selected chromosomes 
	 */
	public static Double standardDeviation(BinList binList, boolean[] chromoList) {
		// if the result has already been calculated for the BinList we return it
		if ((allChromosomeSelected(chromoList)) && (binList.getStDev() != null)) {
			return binList.getStDev();
		}
		double stdDev = 0d;
		int n = 0;
		// we compute the mean
		double mean = average(binList, chromoList);
		// We compute standard deviations
		for (short i = 0; i < binList.size(); i++) {
			// We want to compute the correlation only for the chromosomes where chromoList is set to true
			if (((chromoList == null) || ((i < chromoList.length) && (chromoList[i]))) && (binList.get(i) != null)) {
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
			return (Math.sqrt(stdDev / (double)n));
		}
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
		resultList.finalizeConstruction();
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
