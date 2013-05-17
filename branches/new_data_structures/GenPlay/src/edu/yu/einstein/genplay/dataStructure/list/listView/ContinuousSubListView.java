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
class ContinuousSubListView<T> implements ListView<T> {

	/** Generated serial ID */
	private static final long serialVersionUID = 8814010349357079420L;

	/** Parent {@link ListView} of the sublist */
	private final ListView<T> parent;

	/** Offset of the start of the list view */
	private final int offset;

	/** Size of the {@link ListView} */
	private final int size;


	/**
	 * Creates an instance of {@link ContinuousSubListView}
	 * @param parent parent {@link ListView} of the sublist
	 * @param fromIndex first index of the sublist in the parent list
	 * @param toIndex last index of the sublist in the parent list
	 */
	ContinuousSubListView(ListView<T> parent, int fromIndex, int toIndex) {
		this(parent, 0, fromIndex, toIndex);
	}


	/**
	 * Creates an instance of {@link ContinuousSubListView}
	 * @param parent parent {@link ListView} of the sublist
	 * @param offset offset of the parent {@link ListView} (only if the parent is already a sublist)
	 * @param fromIndex first index of the sublist in the parent list
	 * @param toIndex last index of the sublist in the parent list
	 */
	private ContinuousSubListView(ListView<T> parent, int offset, int fromIndex, int toIndex) {
		subListRangeCheck(fromIndex, toIndex, parent.size());
		this.parent = parent;
		this.offset = offset + fromIndex;
		this.size = toIndex - fromIndex;
	}


	@Override
	public T get(int elementIndex) {
		rangeCheck(elementIndex);
		return parent.get(offset + elementIndex);
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
		return "Index: " + index + ", Size: " + this.size;
	}

	/**
	 * Checks if the specified index is valid
	 * @param index
	 */
	private void rangeCheck(int index) {
		if ((index < 0) || (index >= this.size)) {
			throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
		}
	}


	@Override
	public int size() {
		return this.size;
	}


	@Override
	public ListView<T> subList(int fromIndex, int toIndex) {
		return new ContinuousSubListView<T>(parent, offset, fromIndex, toIndex);
	}


	@Override
	public ListView<T> subList(List<Integer> indexes) {
		for (int i = 0; i < indexes.size(); i++) {
			int newIndex = offset + indexes.get(i);
			indexes.set(i, newIndex);
		}
		return new DiscontinuousSubListView<T>(parent, indexes);
	}


	/**
	 * Checks if the sublist bounds are valid.
	 * @param fromIndex
	 * @param toIndex
	 * @param size
	 */
	private void subListRangeCheck(int fromIndex, int toIndex, int size) {
		if (fromIndex < 0) {
			throw new IndexOutOfBoundsException("fromIndex = " + fromIndex);
		}
		if (toIndex > size) {
			throw new IndexOutOfBoundsException("toIndex = " + toIndex);
		}
		if (fromIndex > toIndex) {
			throw new IllegalArgumentException("fromIndex(" + fromIndex + ") > toIndex(" + toIndex + ")");
		}
	}
}
