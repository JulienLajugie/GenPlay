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
import java.util.List;


/**
 * This class implements the List of Boolean interface but internally
 * it contains an array of byte that is dynamically resized in order to
 * be more memory efficient.
 * @author Julien Lajugie
 * @author Nicolas Fourel
 */
public class ByteArrayAsBooleanList extends AbstractList<Boolean> implements Serializable, List<Boolean> {

	private static final long serialVersionUID = -8787392051503707843L; // generated ID
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 0;			// saved format version
	private static final int 	RESIZE_MIN = 1000;		// minimum length added every time the array is resized
	private static final int 	RESIZE_MAX = 10000000;	// maximum length added every time the array is resized
	private static final int 	RESIZE_FACTOR = 2;		// multiplication factor of the length of the array every time it's resized
	private byte[] 				value;					// int value array
	private int 				size;					// size of the list


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeObject(value);
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
		value = (byte[]) in.readObject();
		size = in.readInt();
	}


	/**
	 * Creates an instance of {@link ByteArrayAsBooleanList}
	 */
	public ByteArrayAsBooleanList() {
		this.value = new byte[0];
		this.size = 0;
	}


	/**
	 * Creates an instance of {@link ByteArrayAsBooleanList}
	 * @param size size of the array
	 */
	public ByteArrayAsBooleanList(int size) {
		this.value = new byte[size];
		this.size = size;
	}


	@Override
	public boolean add(Boolean b) {
		// if the array is to small we resize it before adding the data
		if (size >= value.length) {
			// we multiply the current size by the resize multiplication factor
			int newLength = value.length * RESIZE_FACTOR;
			// we make sure we don't add less than RESIZE_MIN elements
			newLength = Math.max(newLength, value.length + RESIZE_MIN);
			// we make sure we don't add more than RESIZE_MAX elements
			newLength = Math.min(newLength, value.length + RESIZE_MAX);
			byte[] newValue = new byte[newLength];
			for (int i = 0; i < value.length; i++) {
				newValue[i] = value[i];
			}
			value = newValue;
		}
		if (b) {
			value[size] = 1;
		} else {
			value[size] = 0;
		}
		size++;
		return true;
	}


	@Override
	public Boolean get(int index) {
		if (value[index] == 1) {
			return true;
		}
		return false;
	}


	/**
	 * @return null in order to accelerate the operation
	 */
	@Override
	public Boolean set(int index, Boolean b) {
		if (b) {
			value[index] = 1;
		} else {
			value[index] = 0;
		}
		return null;
	}


	@Override
	public int size() {
		return size;
	}


	/**
	 * Recreates the arrays with the right size in order to optimize the memory usage.
	 */
	public void compact() {
		byte[] valueTmp = new byte[size];
		for (int i = 0; i < size; i++) {
			valueTmp[i] = value[i];
		}
		value = valueTmp;
	}


	/**
	 * Shows the content of this object
	 */
	public void show () {
		String info = "size = " + size + " -> ";
		for (int i = 0; i < value.length; i++) {
			info += value[i] + "; ";
		}
		System.out.println(info);
	}
}
