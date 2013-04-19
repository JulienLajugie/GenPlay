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
package edu.yu.einstein.genplay.util.ListView;

import java.util.Comparator;
import java.util.List;

import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;


/**
 * Provides utilities for {@link ListView} objects.
 * @author Julien Lajugie
 */
public class ListViews {

	/**
	 * Searches the specified {@link ListView} for the specified object using the binary
	 * search algorithm.  The list must be sorted into ascending order
	 * according to the {@linkplain Comparable natural ordering} of its
	 * elements.
	 * If it is not sorted, the results are undefined.  If the list
	 * contains multiple elements equal to the specified object, there is no
	 * guarantee which one will be found.
	 *
	 * <p>This method runs in log(n) time.
	 *
	 * @param  list the list to be searched.
	 * @param  key the key to be searched for.
	 * @return the index of the search key, if it is contained in the list;
	 *         otherwise, <tt>(-(<i>insertion point</i>) - 1)</tt>.  The
	 *         <i>insertion point</i> is defined as the point at which the
	 *         key would be inserted into the list: the index of the first
	 *         element greater than the key, or <tt>list.size()</tt> if all
	 *         elements in the list are less than the specified key.  Note
	 *         that this guarantees that the return value will be &gt;= 0 if
	 *         and only if the key is found.
	 * @throws ClassCastException if the list contains elements that are not
	 *         <i>mutually comparable</i> (for example, strings and
	 *         integers), or the search key is not mutually comparable
	 *         with the elements of the list.
	 */
	public static <T> int binarySearch(List<? extends Comparable<? super T>> list, T key) {
		int low = 0;
		int high = list.size()-1;

		while (low <= high) {
			int mid = (low + high) >>> 1;
			Comparable<? super T> midVal = list.get(mid);
			int cmp = midVal.compareTo(key);

			if (cmp < 0) {
				low = mid + 1;
			} else if (cmp > 0) {
				high = mid - 1;
			}
			else {
				return mid; // key found
			}
		}
		return -(low + 1);  // key not found
	}


	/**
	 * Searches the specified {@link ListView} for the specified object using the binary
	 * search algorithm.  The list must be sorted into ascending order
	 * according to the specified comparator. If it is not sorted, the results are undefined.
	 * If the list contains multiple elements equal to the specified object, there is no
	 * guarantee which one will be found.
	 *
	 * <p>This method runs in log(n) time.
	 *
	 * @param  list the list to be searched.
	 * @param  key the key to be searched for.
	 * @param  c the comparator by which the list is ordered.
	 *         A <tt>null</tt> value indicates that the elements'
	 *         {@linkplain Comparable natural ordering} should be used.
	 * @return the index of the search key, if it is contained in the list;
	 *         otherwise, <tt>(-(<i>insertion point</i>) - 1)</tt>.  The
	 *         <i>insertion point</i> is defined as the point at which the
	 *         key would be inserted into the list: the index of the first
	 *         element greater than the key, or <tt>list.size()</tt> if all
	 *         elements in the list are less than the specified key.  Note
	 *         that this guarantees that the return value will be &gt;= 0 if
	 *         and only if the key is found.
	 * @throws ClassCastException if the list contains elements that are not
	 *         <i>mutually comparable</i> using the specified comparator,
	 *         or the search key is not mutually comparable with the
	 *         elements of the list using this comparator.
	 */
	public static <T> int binarySearch(ListView<? extends T> list, T key, Comparator<? super T> c) {
		int low = 0;
		int high = list.size() - 1;

		while (low <= high) {
			int mid = (low + high) >>> 1;
			T midVal = list.get(mid);
			int cmp = c.compare(midVal, key);
			if (cmp < 0) {
				low = mid + 1;
			} else if (cmp > 0) {
				high = mid - 1;
			} else {
				return mid; // key found
			}
		}
		return -(low + 1);  // key not found.
	}
}
