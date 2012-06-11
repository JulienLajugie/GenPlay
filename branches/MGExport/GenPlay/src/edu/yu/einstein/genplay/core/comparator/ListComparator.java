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
package edu.yu.einstein.genplay.core.comparator;

import java.util.Comparator;
import java.util.List;


/**
 * @author Nicolas Fourel
 * @version 0.1
 * @param <K>
 */
public class ListComparator<K> implements Comparator<List<K>> {


	@Override
	/**
	 * This method compares two lists but uses special code meaning:
	 * 
	 * 0: both lists are equal
	 * 
	 * 1: lists are different
	 * 2: first list is bigger than the second list
	 * 3: first list is smaller than the second list
	 *
	 * -1: the first list if not null but the second list is null
	 * -2: the first list if null but the second list is not null
	 * -3: both lists are null
	 */
	public int compare(List<K> o1, List<K> o2) {
		if (o1 == null && o2 == null) {
			return -3;
		} else if (o1 == null && o2 != null) {
			return -2;
		} else if (o1 != null && o2 == null) {
			return -1;
		} else {
			if (o1.size() > o2.size()) {
				return 2;
			} else if (o1.size() < o2.size()) {
				return 3;
			} else {
				for (K k: o1) {
					if (!o2.contains(k)) {
						return 1;
					}
				}
				return 0;
			}
		}
	}
	
	
	/**
	 * @param o1	the first list
	 * @param o2	the second list
	 * @return		true if lists are different (null lists possible), false otherwise
	 */
	public boolean areDifferent (List<K> o1, List<K> o2) {
		int result = compare(o1, o2);
		if (result > 0 || result == -1 || result == -2) {
			return true;
		}
		return false;
	}
	
	
	/**
	 * @param o1	the first list
	 * @param o2	the second list
	 * @return		true if lists are different (null lists impossible), false otherwise
	 */
	public boolean areDifferentNonNull (List<K> o1, List<K> o2) {
		int result = compare(o1, o2);
		if (result > 0) {
			return true;
		}
		return false;
	}
	
}