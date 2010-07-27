/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.util;

import java.util.List;


/**
 * Provides operation on {@link List} of Double
 * @author Julien Lajugie
 * @version 0.1
 */
public class DoubleLists {
	
	
	/**
	 * @param list {@link List} of Double
	 * @return the average value of the list
	 */
	public static double average(List<Double> list) {
		double sum = 0;
		int n = 0; 
		for (Double currentValue : list) {
			if (currentValue != 0) {
				sum += currentValue;
				n++;
			}
		}
		if (n == 0) {
			return 0;
		} else {
			return sum / n;
		}
	}
	
	
	/**
	 * @param list {@link List} of Double
	 * @param indexStart index where to start in the list
	 * @param indexStop index where to stop in the list
	 * @return the average value of the list between indexStart and indexStop included
	 */
	public static double average(List<Double> list, int indexStart, int indexStop) {
		if (indexStart > indexStop) {
			return 0;
		} else if (indexStart == indexStop) {
			return list.get(indexStart);
		}
		
		double sum = 0;
		int n = 0;
		int i = indexStart;
		while ((i <= indexStop) && (i < list.size())) {
			if (list.get(i) != 0) {
				sum += list.get(i);
				n++;
			}
			i++;
		}
		if (n == 0) {
			return 0;
		} else {
			return sum / n;
		}
	}
	
	
	/**
	 * Returns the maximum of the list in parameter. Doesn't take the 0 value elements into account.
	 * @param list
	 * @return the non-zero maximum of the specified list
	 */
	public static double maxNoZero(List<Double> list) {
		double max = Double.NEGATIVE_INFINITY;
		for (Double currentValue : list) {
			if (currentValue != 0) {
				max = Math.max(max, currentValue);
			}
		}
		return max;
	}
	
	
	/**
	 * Returns the minimum of the list in parameter. Doesn't take the 0 value elements into account.
	 * @param list
	 * @return the non-zero minimum of the specified list
	 */
	public static double minNoZero(List<Double> list) {
		double min = Double.POSITIVE_INFINITY;
		for (Double currentValue : list) {
			if (currentValue != 0) {
				min = Math.min(min, currentValue);
			}
		}
		return min;
	}
	
	
	/**
	 * @param list {@link List} of Double
	 * @param indexStart index where to start in the list
	 * @param indexStop index where to stop in the list
	 * @return the standard deviation of the values of the list between indexStart and indexStop included
	 */
	public static double standardDeviation(List<Double> list, int indexStart, int indexStop) {
		if (indexStart > indexStop) {
			return 0;
		} else if (indexStart == indexStop) {
			return list.get(indexStart);
		}
		
		double mean = average(list, indexStart, indexStop);
		double sum = 0;	
		int n = 0;
		int i = indexStart;		
		while ((i <= indexStop) && (i < list.size())) {			
			if (list.get(i) != 0) {
				sum += Math.pow(list.get(i) - mean, 2);
				n++;
			}
			i++;
		}		
		if (n == 0) {
			return 0;
		} else {
			return sum / n;			
		}
	}


	/**
	 * @param list {@link List} of Double
	 * @return the sum of the values of the list
	 */
	public static double sum(List<Double> list) {
		double result = 0;
		for (Double currentValue : list) {
			result += currentValue;
		}
		return result;
	}
	
	
	/**
	 * Sum all the value of the specified list between the start index and the stop index
	 * @param list
	 * @param indexStart start index
	 * @param indexStop stop index
	 * @return the sum of the values of the list between start index and stop index
	 */
	public static double sum(List<Double> list, int indexStart, int indexStop) {
		if (indexStart > indexStop) {
			return 0;
		} else if (indexStart == indexStop) {
			return list.get(indexStart);
		}
		
		double sum = 0;	
		int i = indexStart;		
		while ((i <= indexStop) && (i < list.size())) {			
			sum += list.get(i);
			i++;
		}		
		return sum;			
	}
	
}
