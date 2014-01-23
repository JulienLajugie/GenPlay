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

import java.io.Serializable;
import java.util.List;



/**
 * A read-only ordered collection.
 * This interface provides method to access elements of a list but no method to modify them.
 * Implementation of {@link ListView} should be immutable in order to create immutable lists.
 * The elements of the list should also be immutable in order to guaranty that {@link ListView} are immutable.
 * @param <T> type of the elements of the {@link ListView}. It is preferable that these elements are immutable.
 * @author Julien Lajugie
 */
public interface ListView<T> extends Serializable, Iterable<T> {

	/**
	 * @param elementIndex an index in the {@link ListView}
	 * @return the element at the specified index
	 */
	public T get(int elementIndex);


	/**
	 * @return true if the {@link ListView} contains no elements
	 */
	public boolean isEmpty();


	/**
	 * @return the number of elements in the {@link ListView}
	 */
	public int size();


	/**
	 * Returns a view of the portion of this listview between the specified
	 * {@code fromIndex}, inclusive, and {@code toIndex}, exclusive.  (If
	 * {@code fromIndex} and {@code toIndex} are equal, the returned list is
	 * empty.)
	 *
	 * <p>This method eliminates the need for explicit range operations (of
	 * the sort that commonly exist for arrays).  Any operation that expects
	 * a list can be used as a range operation by passing a subList view
	 * instead of a whole list.
	 * @param fromIndex
	 * @param toIndex
	 * @return a {@link ListView}
	 * @throws IndexOutOfBoundsException
	 * @throws IllegalArgumentException
	 */
	public ListView<T> subList(int fromIndex, int toIndex);


	/**
	 * Returns a view of this listview containing only the specified
	 * indexes.
	 *
	 * <p>This method eliminates the need for explicit range operations (of
	 * the sort that commonly exist for arrays).  Any operation that expects
	 * a list can be used as a range operation by passing a subList view
	 * instead of a whole list.
	 * @param indexes indexes of the elements of the sublist in this listview.
	 * @return a {@link ListView}
	 * @throws IndexOutOfBoundsException
	 * @throws IllegalArgumentException
	 */
	public ListView<T> subList(List<Integer> indexes);
}
