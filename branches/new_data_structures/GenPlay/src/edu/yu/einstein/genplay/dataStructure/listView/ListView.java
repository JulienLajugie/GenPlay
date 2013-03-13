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
package edu.yu.einstein.genplay.dataStructure.listView;

import java.io.Serializable;


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
	 * @return the number of elements in the {@link ListView}
	 */
	public int size();


	/**
	 * @param elementIndex an index in the {@link ListView}
	 * @return the element at the specified index
	 */
	public T get(int elementIndex);
}
