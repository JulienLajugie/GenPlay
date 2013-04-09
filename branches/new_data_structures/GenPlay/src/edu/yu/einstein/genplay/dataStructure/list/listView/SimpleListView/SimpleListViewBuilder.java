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
package edu.yu.einstein.genplay.dataStructure.list.listView.SimpleListView;

import java.util.ArrayList;
import java.util.List;

import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListViewBuilder;
import edu.yu.einstein.genplay.exception.exceptions.ObjectAlreadyBuiltException;


/**
 * Implementation of the {@link ListViewBuilder} interface vending {@link SimpleListView} objects.
 * @param <T> type of the data of the {@link ListView} to build
 * @author Julien Lajugie
 */
public class SimpleListViewBuilder<T> implements ListViewBuilder<T>{

	/** Data of the list to build */
	private List<T> data = null;


	/**
	 * Creates an instance of {@link SimpleListViewBuilder}
	 */
	public SimpleListViewBuilder() {
		data = new ArrayList<T>();
	}


	@Override
	public void addElementToBuild(T element) throws ObjectAlreadyBuiltException {
		if (data != null) {
			data.add(element);
		} else {
			throw new ObjectAlreadyBuiltException();
		}

	}


	@Override
	public SimpleListViewBuilder<T> clone() throws CloneNotSupportedException {
		return new SimpleListViewBuilder<T>();
	}


	@Override
	public ListView<T> getListView() {
		ListView<T> newList = new SimpleListView<T>(data);
		data = null;
		return newList;
	}
}
