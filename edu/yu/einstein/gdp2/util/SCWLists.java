/**
 * @author Chirag Gorasia
 * @version 0.1
 */
package yu.einstein.gdp2.util;

import java.util.List;

import yu.einstein.gdp2.core.ScoredChromosomeWindow;

/**
 * Provides operation on {@link List} of SCW
 * @author Chirag Gorasia
 * @version 0.1
 */
public class SCWLists {

	/**
	 * @param list {@link List} of Double
	 * @return the average value of the list
	 */
	public static double average(List<ScoredChromosomeWindow> list) {
		double sum = 0;
		int n = 0; 
		for (ScoredChromosomeWindow currentValue : list) {
			if (currentValue != null) {
				sum += currentValue.getScore();
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
	public static double average(List<ScoredChromosomeWindow> list, int indexStart, int indexStop) {
		if (indexStart > indexStop) {
			return 0;
		} else if (indexStart == indexStop) {
			return list.get(indexStart).getScore();
		}
		
		double sum = 0;
		int n = 0;
		int i = indexStart;
		while ((i <= indexStop) && (i < list.size())) {
			if (list.get(i) != null) {
				sum += list.get(i).getScore();
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
	public static double maxNoZero(List<ScoredChromosomeWindow> list) {
		double max = Double.NEGATIVE_INFINITY;
		for (ScoredChromosomeWindow currentValue : list) {
			if (currentValue != null) {
				max = Math.max(max, currentValue.getScore());
			}
		}
		return max;
	}
	
	
	/**
	 * Returns the minimum of the list in parameter. Doesn't take the 0 value elements into account.
	 * @param list
	 * @return the non-zero minimum of the specified list
	 */
	public static double minNoZero(List<ScoredChromosomeWindow> list) {
		double min = Double.POSITIVE_INFINITY;
		for (ScoredChromosomeWindow currentValue : list) {
			if (currentValue != null) {
				min = Math.min(min, currentValue.getScore());
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
	public static double standardDeviation(List<ScoredChromosomeWindow> list, int indexStart, int indexStop) {
		if (indexStart > indexStop) {
			return 0;
		} else if (indexStart == indexStop) {
			return list.get(indexStart).getScore();
		}
		
		double mean = average(list, indexStart, indexStop);
		double sum = 0;	
		int n = 0;
		int i = indexStart;		
		while ((i <= indexStop) && (i < list.size())) {			
			if (list.get(i) != null) {
				sum += Math.pow(list.get(i).getScore() - mean, 2);
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
	public static double sum(List<ScoredChromosomeWindow> list) {
		double result = 0;
		for (ScoredChromosomeWindow currentValue : list) {
			result += currentValue.getScore();
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
	public static double sum(List<ScoredChromosomeWindow> list, int indexStart, int indexStop) {
		if (indexStart > indexStop) {
			return 0;
		} else if (indexStart == indexStop) {
			return list.get(indexStart).getScore();
		}
		
		double sum = 0;	
		int i = indexStart;		
		while ((i <= indexStop) && (i < list.size())) {			
			sum += list.get(i).getScore();
			i++;
		}		
		return sum;			
	}	
}
