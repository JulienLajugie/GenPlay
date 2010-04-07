/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.list.SCWList;

import java.util.List;

import yu.einstein.gdp2.core.ScoredChromosomeWindow;
import yu.einstein.gdp2.core.list.ChromosomeListOfLists;


/**
 * This class contains various static methods for manipulating {@link ScoredChromosomeWindowList}
 * @author Julien Lajugie
 * @version 0.1
 */
public class SCWListOperations {


	/**
	 * Returns the average of the list defined as: 
	 * sum(score * length) / sum(length)
	 * @param scwList ChromosomeListOfLists of ScoredChromosomeWindow
	 * @param chromoList set to true each chromosome of this list that you want to use in the calculation
	 * Perform the operation on every chromosome if null
	 * @return the average of the list
	 */
	public static double average(ChromosomeListOfLists<ScoredChromosomeWindow> scwList, boolean[] chromoList) {
		double sumScoreByLength = 0;
		long sumLength = 0;
		for (int i = 0; i < scwList.size(); i++) {
			if (((chromoList == null) || ((i < chromoList.length) && (chromoList[i]))) && (scwList.get(i) != null)) {
				List<ScoredChromosomeWindow> currentList = scwList.get(i);
				for(ScoredChromosomeWindow currentWindow : currentList) {	
					if (currentWindow.getScore() != 0) {
						int length = currentWindow.getStop() - currentWindow.getStart();
						sumScoreByLength += currentWindow.getScore() * length;
						sumLength += length;
					}
				}
			}
		}
		if (sumLength == 0) {
			return 0d;
		} else {
			return (sumScoreByLength / (double)sumLength);
		}
	}

	
	/**
	 * Returns the greatest value of the list
	 * @param scwList ChromosomeListOfLists of ScoredChromosomeWindow
	 * @param chromoList set to true each chromosome of this list that you want to use in the calculation
	 * Perform the operation on every chromosome if null
	 * @return the greatest value of the list
	 */
	public static double max(ChromosomeListOfLists<ScoredChromosomeWindow> scwList, boolean[] chromoList) {
		double max = Double.NEGATIVE_INFINITY;
		for (int i = 0; i < scwList.size(); i++) {
			if (((chromoList == null) || ((i < chromoList.length) && (chromoList[i]))) && (scwList.get(i) != null)) {
				List<ScoredChromosomeWindow> currentList = scwList.get(i);
				for(ScoredChromosomeWindow currentWindow : currentList) {
					max = Math.max(max, currentWindow.getScore());
				}
			}
		}
		return max;
	}
	
	
	/**
	 * @param scwList a {@link ChromosomeListOfLists} of {@link ScoredChromosomeWindow}
	 * @return the maximum score to display on a track
	 */
	public static double maxScoreToDisplay(ChromosomeListOfLists<ScoredChromosomeWindow> scwList) {
		double realMax = max(scwList, null);
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
		final double proposedMax = (average(scwList, null) + standardDeviation(scwList, null)) * 2; 
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
	 * Returns the smallest value of the list
	 * @param scwList ChromosomeListOfLists of ScoredChromosomeWindow
	 * @param chromoList set to true each chromosome of this list that you want to use in the calculation
	 * Perform the operation on every chromosome if null
	 * @return the smallest value of the list
	 */
	public static double min(ChromosomeListOfLists<ScoredChromosomeWindow> scwList, boolean[] chromoList) {
		double min = Double.POSITIVE_INFINITY;
		for (int i = 0; i < scwList.size(); i++) {
			if (((chromoList == null) || ((i < chromoList.length) && (chromoList[i]))) && (scwList.get(i) != null)) {
				List<ScoredChromosomeWindow> currentList = scwList.get(i);
				for(ScoredChromosomeWindow currentWindow : currentList) {
					min = Math.min(min, currentWindow.getScore());
				}
			}
		}
		return min;
	}

	
	/**
	 * @param scwList a {@link ChromosomeListOfLists} of {@link ScoredChromosomeWindow}
	 * @return the minimum score to display on a track
	 */
	public static double minScoreToDisplay(ChromosomeListOfLists<ScoredChromosomeWindow> scwList) {
		// if the min is positive we return 0
		double realMin = min(scwList, null);
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
		final double proposedMin = (average(scwList, null) - standardDeviation(scwList, null)) * 2; 
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
	 * Returns the standard deviation of the list
	 * @param scwList ChromosomeListOfLists of ScoredChromosomeWindow
	 * @param chromoList set to true each chromosome of this list that you want to use in the calculation
	 * Perform the operation on every chromosome if null
	 * @return the standard deviation of the list
	 */
	public static double standardDeviation(ChromosomeListOfLists<ScoredChromosomeWindow> scwList, boolean[] chromoList) {
		double stDev = 0;
		double sumLength = 0;
		double mean = average(scwList, chromoList);		
		for (int i = 0; i < scwList.size(); i++) {
			if (((chromoList == null) || ((i < chromoList.length) && (chromoList[i]))) && (scwList.get(i) != null)) {
				List<ScoredChromosomeWindow> currentList = scwList.get(i);
				for(ScoredChromosomeWindow currentWindow : currentList) {
					int length = currentWindow.getStop() - currentWindow.getStart();
					stDev += Math.pow(currentWindow.getScore() - mean, 2) * length;
					sumLength += length;
				}
			}
		}		
		if (sumLength == 0) {
			return 0d;
		} else {
			return Math.sqrt(stDev / sumLength); 
		}		
	}
}
