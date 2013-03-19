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
 *     Authors:	Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *     			Nicolas Fourel <nicolas.fourel@einstein.yu.edu>
 *     Website: <http://genplay.einstein.yu.edu>
 *******************************************************************************/
package edu.yu.einstein.genplay.util;

import java.util.List;


/**
 * Provides operation on {@link List} of Float elements
 * @author Julien Lajugie
 * @version 0.1
 */
public class FloatLists {


	/**
	 * @param list {@link List} of Float elements
	 * @return the average value of the list
	 */
	public static float average(List<Float> list) {
		float sum = 0;
		int n = 0;
		for (Float currentValue : list) {
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
	 * @param list {@link List} of Float elements
	 * @param indexStart index where to start in the list
	 * @param indexStop index where to stop in the list
	 * @return the average value of the list between indexStart and indexStop included
	 */
	public static float average(List<Float> list, int indexStart, int indexStop) {
		if (indexStart > indexStop) {
			return 0;
		} else if (indexStart == indexStop) {
			return list.get(indexStart);
		}

		float sum = 0;
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
	public static float maxNoZero(List<Float> list) {
		float max = Float.NEGATIVE_INFINITY;
		for (Float currentValue : list) {
			if (currentValue != 0) {
				max = Math.max(max, currentValue);
			}
		}
		return max;
	}


	/**
	 * Returns the maximum of the list in parameter. Doesn't take the 0 value elements into account.
	 * @param list
	 * @param indexStart index where to start in the list
	 * @param indexStop index where to stop in the list
	 * @return the non-zero maximum of the specified list
	 */
	public static float maxNoZero(List<Float> list, int indexStart, int indexStop) {
		if (indexStart > indexStop) {
			return 0;
		} else if (indexStart == indexStop) {
			return list.get(indexStart);
		}

		float max = Float.NEGATIVE_INFINITY;
		int i = indexStart;
		while ((i <= indexStop) && (i < list.size())) {
			float currentValue = list.get(i);
			if (currentValue != 0) {
				max = Math.max(max, currentValue);
			}
			i++;
		}
		return max;
	}


	/**
	 * Returns the minimum of the list in parameter. Doesn't take the 0 value elements into account.
	 * @param list
	 * @return the non-zero minimum of the specified list
	 */
	public static float minNoZero(List<Float> list) {
		Float min = Float.POSITIVE_INFINITY;
		for (Float currentValue : list) {
			if (currentValue != 0) {
				min = Math.min(min, currentValue);
			}
		}
		return min;
	}


	/**
	 * @param list {@link List} of Float elements
	 * @param indexStart index where to start in the list
	 * @param indexStop index where to stop in the list
	 * @return the standard deviation of the values of the list between indexStart and indexStop included
	 */
	public static float standardDeviation(List<Float> list, int indexStart, int indexStop) {
		if (indexStart > indexStop) {
			return 0;
		} else if (indexStart == indexStop) {
			return list.get(indexStart);
		}

		float mean = average(list, indexStart, indexStop);
		float sum = 0;
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
	 * @param list {@link List} of Float elements
	 * @return the sum of the values of the list
	 */
	public static float sum(List<Float> list) {
		float result = 0;
		for (Float currentValue : list) {
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
	public static float sum(List<Float> list, int indexStart, int indexStop) {
		if (indexStart > indexStop) {
			return 0;
		} else if (indexStart == indexStop) {
			return list.get(indexStart);
		}

		float sum = 0;
		int i = indexStart;
		while ((i <= indexStop) && (i < list.size())) {
			sum += list.get(i);
			i++;
		}
		return sum;
	}

}
