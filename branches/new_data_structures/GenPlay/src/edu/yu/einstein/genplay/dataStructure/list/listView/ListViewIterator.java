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

/**
 * Iterator on a {@link ListView} object.
 * @param <T> type of the elements of the {@link ListView}.
 * @author Julien Lajugie
 */
public class ListViewIterator<T> implements Iterator<T> {

	/** List to iterate */
	private final ListView<T> listView;

	/** Current index of the iterator */
	private int iteratorIndex;


	/**
	 * Create an instance of {@link ListViewIterator}
	 * @param listView {@link ListView} to iterate
	 */
	public ListViewIterator(ListView<T> listView) {
		this.listView = listView;
		iteratorIndex = 0;
	}


	@Override
	public boolean hasNext() {
		return iteratorIndex < listView.size();
	}


	@Override
	public T next() {
		int currentIndex = iteratorIndex;
		iteratorIndex++;
		return listView.get(currentIndex);
	}


	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
}
