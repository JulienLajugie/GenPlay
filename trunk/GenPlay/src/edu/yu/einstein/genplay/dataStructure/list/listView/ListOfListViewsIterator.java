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
package edu.yu.einstein.genplay.dataStructure.list.listView;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import edu.yu.einstein.genplay.dataStructure.chromosomeWindow.ChromosomeWindow;

/**
 * Iterates in a {@link List} of sorted {@link ListView} of {@link Comparable} objects.
 * The {@link #next()} method will return the next element in the sorted order.
 * @param <T> any type implementing the {@link ChromosomeWindow} interface
 * @author Julien Lajugie
 */
public class ListOfListViewsIterator<T extends Comparable<? super T>> implements Iterator<T> {

	/** List of list view */
	private final List<ListView<T>> listOfLV;

	/** Indexes of the current element in each {@link ListView} */
	private final int[] listViewIndexes;

	/** Index of the list that returned the last next element */
	private int lastNextListIndex;


	/**
	 * Creates an instance of {@link ListOfListViewsIterator}
	 * @param listOfLV {@link List} of {@link ListView} objects for the iterator to iterate
	 */
	public ListOfListViewsIterator(List<ListView<T>> listOfLV) {
		this.listOfLV = listOfLV;
		listViewIndexes = new int[listOfLV.size()];
	}


	/**
	 * @return The index of the list that contains the last element returned by the {@link #next()} method.
	 */
	public int getLastNextListIndex() {
		return lastNextListIndex;
	}


	@Override
	public boolean hasNext() {
		for (int i = 0; i < listOfLV.size(); i++) {
			int lvIndex = listViewIndexes[i];
			int lvSize = listOfLV.get(i).size();
			if (lvIndex < lvSize) {
				return true;
			}
		}
		return false;
	}


	@Override
	public T next() {
		int indexNextList = -1;
		T nextPosition = null;
		// retrieve the element with the smallest start position
		for (int i = 0; i < listOfLV.size(); i++) {
			int index = listViewIndexes[i];
			if (index < listOfLV.get(i).size()) {
				T currentElement = listOfLV.get(i).get(index);
				// if they were no element so far or if the current element is the smallest so far
				// it becomes the new smallest element
				if ((nextPosition == null) || (nextPosition.compareTo(currentElement) > 0)) {
					indexNextList = i;
					nextPosition = currentElement;
				}
			}
		}
		// case where there is no more elements
		if (indexNextList == -1) {
			throw new NoSuchElementException();
		}
		// return the element with the smallest start position
		listViewIndexes[indexNextList]++;
		lastNextListIndex = indexNextList;
		return nextPosition;
	}


	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
}
