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
package edu.yu.einstein.genplay.dataStructure.list.arrayList;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

import edu.yu.einstein.genplay.exception.exceptions.CompressionException;



/**
 * An array of 8 booleans packed to the byte encapsulated in order to implement the {@link List} interface with Double parameter
 * <br>Each element of the internal array are bytes and represent 8 booleans. 
 * <br>This is an optimization in the memory usage since: 
 * <br>1 byte (representing 8 booleans) = 1 byte in memory
 * <br>better than 1 boolean = 1 byte in memory
 * <br>The methods get and set work with Double objects
 * @author Julien Lajugie
 * @version 0.1
 */
public final class BooleanArrayAsDoubleList extends ArrayAsDoubleList<byte[]> implements Serializable, List<Double>, CompressibleList {

	private static final long serialVersionUID = -5280328695672981245L;	// generated ID
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 0;			// saved format version

	
	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
	}


	/**
	 * Method used for unserialization
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.readInt();
	}
	
		
	/**
	 * Creates an instance of {@link BooleanArrayAsDoubleList}
	 */
	public BooleanArrayAsDoubleList() {
		super();
		this.data = new byte[0];
	}
	
	
	/**
	 * Creates an instance of {@link BooleanArrayAsDoubleList}
	 * @param size size of the array
	 */
	public BooleanArrayAsDoubleList(int size) {
		super(size);
		// 1 byte = 8 booleans so the size of the byte array is 8 times smaller
		int realSize = (int)Math.ceil(size / 8) + 1;
		this.data = new byte[realSize];
	}
	

	/**
	 * Unsupported operation
	 */
	@Override
	public void sort() {
		// no sort with booleans
		throw (new UnsupportedOperationException("Invalid operation, can't sort a boolean array"));
	};

	
	@Override
	public boolean add(Double e) {
		// throw an exception if the list is compressed
		if (isCompressed()) {
			throw new CompressionException("Compressed List: Invalid Operation");
		}
		// if the array is to small we resize it before adding the data
		if (size >= data.length * 8) {
			// we multiply the current size by the resize multiplication factor
			int newLength = data.length * RESIZE_FACTOR;
			// we make sure we don't add less than RESIZE_MIN elements
			newLength = Math.max(newLength, data.length + RESIZE_MIN / 8);
			// we make sure we don't add more than RESIZE_MAX elements
			newLength = Math.min(newLength, data.length + RESIZE_MAX / 8);
			byte[] newData = new byte[newLength];			
			for (int i = 0; i < data.length; i++) {
				newData[i] = data[i];
			}
			data = newData;
		}
		// we add the data
		set(size, e);
		size++;
		return true;
	}
	
	
	@Override
	public Double get(int index) {
		// throw an exception if the list is compressed
		if (isCompressed()) {
			throw new CompressionException("Compressed List: Invalid Operation");
		}
		// real index is the index divided by 8
		int realIndex = index / 8;
		// compute the position of the desired boolean inside the byte
		int offset = index % 8;
		int result = data[realIndex] & (1 << offset);
		return result != 0 ? 1d : 0d;
	}

	
	/**
 	 * @return null in order to accelerate the operation
	 */
	@Override
	public Double set(int index, Double element) {
		// throw an exception if the list is compressed
		if (isCompressed()) {
			throw new CompressionException("Compressed List: Invalid Operation");
		}
		// real index is the index divided by 8
		int realIndex = index / 8;
		// compute the position of the desired boolean inside the byte
		int offset = index % 8;
		if (element != 0) {
			// set the specified bit of the byte to 1
			data[realIndex] = (byte)(data[realIndex] | (1 << offset));
		} else {
			// set the specified bit of the byte to 0
			data[realIndex] = (byte)(data[realIndex] & (0xff ^ (1 << offset)));
		}
		return null;
	}
}
