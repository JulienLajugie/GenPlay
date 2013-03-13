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
package edu.yu.einstein.genplay.core.list.arrayList;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.List;


/**
 * This class implements the List of Integer interface but internally
 * it contains an array of int that is dynamically resized in order to
 * be more memory efficient.
 * @author Julien Lajugie
 */
public class IntArrayAsIntegerList extends AbstractList<Integer> implements Serializable, List<Integer> {

	private static final long serialVersionUID = -8787392051503707843L; // generated ID
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 0;			// saved format version
	private static final int 	RESIZE_MIN = 1000;		// minimum length added every time the array is resized
	private static final int 	RESIZE_MAX = 10000000;	// maximum length added every time the array is resized
	private static final int 	RESIZE_FACTOR = 2;		// multiplication factor of the length of the array every time it's resized
	private int[] 				data;					// int data array
	private int 				size;					// size of the list


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeObject(data);
		out.writeInt(size);
	}


	/**
	 * Method used for unserialization
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.readInt();
		data = (int[]) in.readObject();
		size = in.readInt();
	}


	/**
	 * Creates an instance of {@link IntArrayAsIntegerList}
	 */
	public IntArrayAsIntegerList() {
		this.data = new int[0];
		this.size = 0;
	}


	/**
	 * Creates an instance of {@link IntArrayAsIntegerList}
	 * @param size size of the array
	 */
	public IntArrayAsIntegerList(int size) {
		this.data = new int[size];
		this.size = size;
	}


	/**
	 * Sorts the list
	 */
	public void sort() {
		Arrays.sort(data);
	};


	@Override
	public boolean add(Integer e) {
		// if the array is to small we resize it before adding the data
		if (size >= data.length) {
			// we multiply the current size by the resize multiplication factor
			int newLength = data.length * RESIZE_FACTOR;
			// we make sure we don't add less than RESIZE_MIN elements
			newLength = Math.max(newLength, data.length + RESIZE_MIN);
			// we make sure we don't add more than RESIZE_MAX elements
			newLength = Math.min(newLength, data.length + RESIZE_MAX);
			int[] newData = new int[newLength];
			for (int i = 0; i < data.length; i++) {
				newData[i] = data[i];
			}
			data = newData;
		}
		// true if e is not zero
		data[size] = e;
		size++;
		return true;
	}


	@Override
	public Integer get(int index) {
		return data[index];
	}


	/**
	 * @return null in order to accelerate the operation
	 */
	@Override
	public Integer set(int index, Integer element) {
		data[index] = element;
		return null;
	}


	@Override
	public int size() {
		return size;
	}


	/**
	 * Recursive function. Returns the index where the value is found
	 * or the index right after if the exact value is not found.
	 * @param value			value
	 * @return the index where the start value of the window is found or -1 if the value is not found
	 */
	public int getIndex (int value) {
		int index = getIndex(value, 0, size - 1);
		if (data[index] == value) {
			return index;
		} else {
			int lengthCurrentIndex = Math.abs(data[index] - value);
			int lengthSecondIndex = -1;
			int secondIndex = -1;
			if (value > data[index]) {
				if ((index + 1) < size) {
					secondIndex = index + 1;
				}
			} else {
				if ((index - 1) > 0) {
					secondIndex = index -1;
				}
			}

			if (secondIndex >= 0) {
				lengthSecondIndex = Math.abs(data[secondIndex] - value);
				if (lengthSecondIndex < lengthCurrentIndex) {
					index = secondIndex;
				}
			}
		}
		return index;
	}


	/**
	 * Recursive function. Returns the index where the value is found
	 * or the index right after if the exact value is not found.
	 * @param value			value
	 * @param indexStart	start index (in the data array)
	 * @param indexStop		stop index (in the data array)
	 * @return the index where the start value of the window is found or the index right after if the exact value is not found
	 */
	private int getIndex (int value, int indexStart, int indexStop) {
		int middle = (indexStop - indexStart) / 2;
		if (indexStart == indexStop) {
			return indexStart;
		} else if (value == data[indexStart + middle]) {
			return indexStart + middle;
		} else if (value > data[indexStart + middle]) {
			return getIndex(value, indexStart + middle + 1, indexStop);
		} else {
			return getIndex(value, indexStart, indexStart + middle);
		}
	}


	/**
	 * Shows the content of the list
	 */
	public void show () {
		String info = "";
		for (int i = 0; i < size; i++) {
			info += "(" + i + "; " + data[i] + ") ";
		}
		System.out.println(info);
	}
}
