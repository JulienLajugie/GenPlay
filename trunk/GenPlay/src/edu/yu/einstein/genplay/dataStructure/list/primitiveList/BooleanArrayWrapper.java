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
 * Implementation of the {@link List} interface wrapping arrays of byte primitives.
 * @author Julien Lajugie
 */
class BooleanArrayWrapper extends AbstractList<Boolean> implements Serializable, List<Boolean>,  PrimitiveArrayWrapper<Boolean> {

	/** Generated serial ID */
	private static final long serialVersionUID = 542881840789544287L;

	/** Version number of the class */
	private static final transient int CLASS_VERSION_NUMBER = 0;

	/** Data of the list */
	private transient byte[] elementData;

	/** size of the list */
	private transient int size;


	/**
	 * Creates an instance of {@link BooleanArrayWrapper}
	 * @param size size of the
	 */
	BooleanArrayWrapper(int capacity) {
		size = capacity;
		// 1 byte = 8 booleans so the size of the byte array is 8 times smaller
		int realCapacity = (capacity / 8) + 1;
		elementData = new byte[realCapacity];
	}


	@Override
	public Boolean get(int index) {
		rangeCheck(index);
		// real index is the index divided by 8
		int realIndex = index / 8;
		// compute the position of the desired boolean inside the byte
		int offset = index % 8;
		int result = elementData[realIndex] & (1 << offset);
		return result != 0 ? true : false;
	}


	/**
	 * @param index
	 * @return A message specifying what was the index and what was the size of the list
	 */
	private String outOfBoundsMsg(int index) {
		return "Index: " + index + ", Size: " + size();
	}


	/**
	 * Checks if the given index is in range.  If not, throws an appropriate
	 * runtime exception.  This method does *not* check if the index is
	 * negative: It is always used immediately prior to an array access,
	 * which throws an ArrayIndexOutOfBoundsException if index is negative.
	 * @param index an index
	 */
	private void rangeCheck(int index) {
		if (index >= size) {
			throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
		}
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
		size = in.readInt();
		elementData = (byte[]) in.readObject();
	}


	@Override
	public Boolean set(int index, Boolean element) {
		rangeCheck(index);
		Boolean oldElement = get(index);
		// real index is the index divided by 8
		int realIndex = index / 8;
		// compute the position of the desired boolean inside the byte
		int offset = index % 8;
		if (element) {
			// set the specified bit of the byte to 1
			elementData[realIndex] = (byte) (elementData[realIndex] | (1 << offset));
		} else {
			// set the specified bit of the byte to 0
			elementData[realIndex] = (byte)(elementData[realIndex] & (0xff ^ (1 << offset)));
		}
		return oldElement;
	}


	@Override
	public int size() {
		return size;
	}


	@Override
	public void trimToSize(int newCapacity) {
		size = newCapacity;
		// 1 byte = 8 booleans so the size of the byte array is 8 times smaller
		int realCapacity = (newCapacity / 8) + 1;
		elementData = Arrays.copyOf(elementData, realCapacity);
	}


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		// write the class version number
		out.writeInt(CLASS_VERSION_NUMBER);
		out.writeInt(size);
		out.writeObject(elementData);
	}
}
