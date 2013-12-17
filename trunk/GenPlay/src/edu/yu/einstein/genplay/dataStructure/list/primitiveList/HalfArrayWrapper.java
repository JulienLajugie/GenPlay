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
package edu.yu.einstein.genplay.dataStructure.list.primitiveList;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.List;

import edu.yu.einstein.genplay.dataStructure.halfFloat.HalfFloat;


/**
 * Implementation of the {@link List} interface wrapping arrays of
 * half precision primitives (represented as chars).
 * @author Julien Lajugie
 */
class HalfArrayWrapper extends AbstractList<Float> implements Serializable, List<Float>,  PrimitiveArrayWrapper<Float> {

	/** Generated serial ID */
	private static final long serialVersionUID = -4034533925862158855L;

	/** Version number of the class */
	private static final transient int CLASS_VERSION_NUMBER = 0;

	/** Data of the list */
	private char[] elementData;


	/**
	 * Creates an instance of {@link HalfArrayWrapper}
	 * @param size size of the
	 */
	HalfArrayWrapper(int capacity) {
		elementData = new char[capacity];
	}


	@Override
	public Float get(int index) {
		return HalfFloat.toFloat(elementData[index]);
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
		elementData = (char[]) in.readObject();
	}


	@Override
	public Float set(int index, Float element) {
		Float oldElement = get(index);
		elementData[index] = HalfFloat.fromFloat(element);
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
