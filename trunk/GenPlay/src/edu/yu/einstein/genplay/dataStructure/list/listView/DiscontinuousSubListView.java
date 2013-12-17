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
package edu.yu.einstein.genplay.dataStructure.list.listView;

import java.util.Iterator;
import java.util.List;


/**
 * Sublists of a {@link ListView}
 * @param <T> type of the elements of the SubListView
 * @author Julien Lajugie
 */
class DiscontinuousSubListView<T> implements ListView<T> {

	/** Generated serial ID */
	private static final long serialVersionUID = 8814010349357079420L;

	/** Parent {@link ListView} of the sublist */
	private final ListView<T> parent;

	/** Offset of the start of the list view */
	private final int[] indexes;


	/**
	 * Creates an instance of {@link DiscontinuousSubListView}
	 * @param parent parent {@link ListView} of the sublist
	 * @param indexes List containing the indexes of the elements in the parent list that are present in the
	 */
	DiscontinuousSubListView(ListView<T> parent, List<Integer> indexes) {
		this.parent = parent;
		this.indexes = new int[indexes.size()];
		for (int i = 0; i < indexes.size(); i++) {
			int currentIndex = indexes.get(i);
			subListRangeCheck(currentIndex);
			this.indexes[i] = currentIndex;
		}
	}


	@Override
	public T get(int elementIndex) {
		rangeCheck(elementIndex);
		return parent.get(indexes[elementIndex]);
	}


	@Override
	public boolean isEmpty() {
		return size() == 0;
	}


	@Override
	public Iterator<T> iterator() {
		return new ListViewIterator<T>(this);
	}


	/**
	 * @param index
	 * @return a message for {@link IndexOutOfBoundsException}
	 */
	private String outOfBoundsMsg(int index) {
		return "Index: " + index + ", Size: " + size();
	}

	/**
	 * Checks if the specified index is valid
	 * @param index
	 */
	private void rangeCheck(int index) {
		if ((index < 0) || (index >= size())) {
			throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
		}
	}


	@Override
	public int size() {
		return indexes.length;
	}


	@Override
	public ListView<T> subList(int fromIndex, int toIndex) {
		return new ContinuousSubListView<T>(this, fromIndex, toIndex);
	}


	@Override
	public ListView<T> subList(List<Integer> indexes) {
		for (int i = 0; i < indexes.size(); i++) {
			indexes.set(i, this.indexes[i]);
		}
		return new DiscontinuousSubListView<T>(parent, indexes);
	}


	/**
	 * Checks if the sublist bounds are valid.
	 * @param fromIndex
	 * @param toIndex
	 * @param size
	 */
	private void subListRangeCheck(int index) {
		if (index < 0) {
			throw new IndexOutOfBoundsException("Index = " + index);
		}
		if (index > parent.size()) {
			throw new IndexOutOfBoundsException("Index = " + index);
		}
	}
}
