/*******************************************************************************
 *     GenPlay, Einstein Genome Analyzer
 *     Copyright (C) 2009, 2011 Albert Einstein College of Medicine
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *     
 *     Author: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *     Website: <http://genplay.einstein.yu.edu>
 *******************************************************************************/
package edu.yu.einstein.genplay.util;

import java.util.List;

import edu.yu.einstein.genplay.core.ScoredChromosomeWindow;


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
		if (indexStart > indexStop || indexStop >= list.size()) {
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
	public static double maxNoZero(List<ScoredChromosomeWindow> list, int regionStart, int regionStop) {
		double max = Double.NEGATIVE_INFINITY;
		for (int j = regionStart; j <= regionStop; j++) {
			if (list.get(j) != null) {
				max = Math.max(max, list.get(j).getScore());
			}
		}
		return max;
	}
	
	
	/**
	 * Returns the minimum of the list in parameter. Doesn't take the 0 value elements into account.
	 * @param list
	 * @return the non-zero minimum of the specified list
	 */
	public static double minNoZero(List<ScoredChromosomeWindow> list, int regionStart, int regionStop) {
		double min = Double.POSITIVE_INFINITY;
		for (int j = regionStart; j <= regionStop; j++) {
			if (list.get(j) != null) {
				min = Math.min(min, list.get(j).getScore());
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
