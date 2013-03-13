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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.List;

/**
 * Simple implementation of the {@link ListView} with data stored in a {@link List} implementation
 * @param <T> type of the data of the {@link SimpleListView}
 * @author Julien Lajugie
 */
public class SimpleListView<T> implements ListView<T>{

	/** Generated serial ID */
	private static final long serialVersionUID = 2581587146772942209L;

	/** Saved format version  */
	private static final int SAVED_FORMAT_VERSION_NUMBER = 0;

	/** Data of the view */
	private List<T> data;


	/**
	 * Creates an instance of {@link SimpleListView}
	 * @param data data of the view.  This object should not be modified after the creation of the view
	 */
	public SimpleListView(List<T> data) {
		this.data = data;
	}


	@Override
	public T get(int elementIndex) {
		return data.get(elementIndex);
	}


	@Override
	public Iterator<T> iterator() {
		return data.iterator();
	}


	/**
	 * Method used for deserialization
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.readInt();
		data = (List<T>) in.readObject();
	}


	@Override
	public int size() {
		return data.size();
	}


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeObject(data);
	}
}
