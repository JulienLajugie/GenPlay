/*******************************************************************************
 * GenPlay, Einstein Genome Analyzer
 * Copyright (C) 2009, 2014 Albert Einstein College of Medicine
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * Authors: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *          Nicolas Fourel <nicolas.fourel@einstein.yu.edu>
 *          Eric Bouhassira <eric.bouhassira@einstein.yu.edu>
 * 
 * Website: <http://genplay.einstein.yu.edu>
 ******************************************************************************/
package edu.yu.einstein.genplay.util.ListView;

import edu.yu.einstein.genplay.dataStructure.chromosomeWindow.SimpleChromosomeWindow;
import edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.SCWListView.generic.GenericSCWListView;
import edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.SCWListView.generic.GenericSCWListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;


/**
 * Provides utilities for {@link ListView} objects of items implementing {@link ScoredChromosomeWindow}.
 * @author Julien Lajugie
 */
public class SCWListViews {


	/**
	 * @param list {@link ListView} of {@link ScoredChromosomeWindow}
	 * @return the average value of the list
	 */
	public static final double average(ListView<ScoredChromosomeWindow> list) {
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
	 * @param list {@link ListView} of {@link ScoredChromosomeWindow}
	 * @param indexStart index where to start in the list
	 * @param indexStop index where to stop in the list
	 * @return the average value of the list between indexStart and indexStop included
	 */
	public static final double average(ListView<ScoredChromosomeWindow> list, int indexStart, int indexStop) {
		if ((indexStart > indexStop) || (indexStop >= list.size())) {
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
	 * Factory method that creates a {@link GenericSCWListView} with only one element
	 * having the specified start, stop and score values
	 * @param start start position of the only element of the list
	 * @param stop stop position of the only element of the list
	 * @param score score of the only element of the list
	 * @return a {@link GenericSCWListView} with only one element
	 */
	public static final ListView<ScoredChromosomeWindow> createGenericSCWListView(int start, int stop, float score) {
		GenericSCWListViewBuilder builder = new GenericSCWListViewBuilder();
		builder.addElementToBuild(start, stop, score);
		return builder.getListView();
	}


	/**
	 * Returns the maximum of the list in parameter. Doesn't take the 0 value elements into account.
	 * @param list input list of ScoredChromosomeWindow
	 * @param regionStart index of the first {@link SimpleChromosomeWindow}
	 * @param regionStop index of the last {@link SimpleChromosomeWindow}
	 * @return the non-zero maximum of the specified list
	 */
	public static final float maxNoZero(ListView<ScoredChromosomeWindow> list, int regionStart, int regionStop) {
		float max = Float.NEGATIVE_INFINITY;
		for (int j = regionStart; j <= regionStop; j++) {
			if (list.get(j) != null) {
				max = Math.max(max, list.get(j).getScore());
			}
		}
		return max;
	}


	/**
	 * Returns the minimum of the list in parameter. Doesn't take the 0 value elements into account.
	 * @param list input list of ScoredChromosomeWindow
	 * @param regionStart index of the first {@link SimpleChromosomeWindow}
	 * @param regionStop index of the last {@link SimpleChromosomeWindow}
	 * @return the non-zero minimum of the specified list
	 */
	public static final float minNoZero(ListView<ScoredChromosomeWindow> list, int regionStart, int regionStop) {
		float min = Float.POSITIVE_INFINITY;
		for (int j = regionStart; j <= regionStop; j++) {
			if (list.get(j) != null) {
				min = Math.min(min, list.get(j).getScore());
			}
		}
		return min;
	}


	/**
	 * @param list {@link ListView} of {@link ScoredChromosomeWindow}
	 * @param indexStart index where to start in the list
	 * @param indexStop index where to stop in the list
	 * @return the standard deviation of the values of the list between indexStart and indexStop included
	 */
	public static final double standardDeviation(ListView<ScoredChromosomeWindow> list, int indexStart, int indexStop) {
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
	 * @param list {@link ListView} of {@link ScoredChromosomeWindow}
	 * @return the sum of the values of the list
	 */
	public static final double sum(ListView<ScoredChromosomeWindow> list) {
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
	public static final double sum(ListView<ScoredChromosomeWindow> list, int indexStart, int indexStop) {
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
