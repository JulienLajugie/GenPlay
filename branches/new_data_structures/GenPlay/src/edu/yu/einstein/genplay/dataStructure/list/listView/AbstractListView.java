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
 * This class provides a skeletal implementation of the {@link ListView} interface
 * to minimize the effort required to implement this interface.
 * @author Julien Lajugie
 * @param <T> type of the elements of the {@link ListView}
 */
public abstract class AbstractListView<T> implements ListView<T>{

	/** Generated serial ID */
	private static final long serialVersionUID = 9010133286271425051L;

	@Override
	public Iterator<T> iterator() {
		return new ListViewIterator<T>(this);
	}


	@Override
	public ListView<T> subList(int fromIndex, int toIndex) {
		return new ContinuousSubListView<T>(this, fromIndex, toIndex);
	}


	@Override
	public ListView<T> subList(List<Integer> indexes) {
		return new DiscontinuousSubListView<T>(this, indexes);
	}
	
	
	@Override
	public boolean isEmpty() {
		return size() == 0;
	}
}
