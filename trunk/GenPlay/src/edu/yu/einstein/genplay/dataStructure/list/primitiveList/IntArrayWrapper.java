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
package edu.yu.einstein.genplay.dataStructure.list.primitiveList;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.List;


/**
 * Implementation of the {@link List} interface wrapping arrays of int primitives.
 * @author Julien Lajugie
 */
class IntArrayWrapper extends AbstractList<Integer> implements Serializable, List<Integer>,  PrimitiveArrayWrapper<Integer> {

	/** Generated serial ID */
	private static final long serialVersionUID = -880124320299615177L;

	/** Version number of the class */
	private static final transient int CLASS_VERSION_NUMBER = 0;

	/** Data of the list */
	private int[] elementData;


	/**
	 * Creates an instance of {@link IntArrayWrapper}
	 * @param size size of the
	 */
	IntArrayWrapper(int capacity) {
		elementData = new int[capacity];
	}


	@Override
	public Integer get(int index) {
		return elementData[index];
	}


	/**
	 * Method used for deserialization
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		// read the class version number
		in.readInt();
		// read other fields
		elementData = (int[]) in.readObject();
	}


	@Override
	public Integer set(int index, Integer element) {
		Integer oldElement = get(index);
		elementData[index] = element;
		return oldElement;
	}


	@Override
	public int size() {
		return elementData.length;
	}


	@Override
	public void trimToSize(int newCapacity) {
		elementData = Arrays.copyOf(elementData, newCapacity);
	}


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		// write the class version number
		out.writeInt(CLASS_VERSION_NUMBER);
		// write other fields
		out.writeObject(elementData);
	}
}
