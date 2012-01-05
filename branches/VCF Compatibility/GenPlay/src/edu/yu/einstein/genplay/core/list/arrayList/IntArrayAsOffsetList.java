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
import java.util.List;

import edu.yu.einstein.genplay.core.multiGenome.synchronization.MGOffset;


/**
 * This class implements the List of MGOffset interface but internally 
 * it contains an array of int that is dynamically resized in order to 
 * be more memory efficient. 
 * @author Julien Lajugie
 * @author Nicolas Fourel
 */
public class IntArrayAsOffsetList extends AbstractList<MGOffset> implements Serializable, List<MGOffset> {

	private static final long serialVersionUID = -8787392051503707843L; // generated ID
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 0;			// saved format version
	private static final int 	RESIZE_MIN = 1000;		// minimum length added every time the array is resized
	private static final int 	RESIZE_MAX = 10000000;	// maximum length added every time the array is resized
	private static final int 	RESIZE_FACTOR = 2;		// multiplication factor of the length of the array every time it's resized
	private int[] 				position;				// int position array
	private int[] 				value;					// int value array
	private int 				size;					// size of the list


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeObject(position);
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
		position = (int[]) in.readObject();
		value = (int[]) in.readObject();
		size = in.readInt();		
	}


	/**
	 * Creates an instance of {@link IntArrayAsOffsetList}
	 */
	public IntArrayAsOffsetList() {
		this.position = new int[0];
		this.value = new int[0];
		this.size = 0;
	}


	/**
	 * Creates an instance of {@link IntArrayAsOffsetList}
	 * @param size size of the array
	 */
	public IntArrayAsOffsetList(int size) {
		this.position = new int[size];
		this.value = new int[size];
		this.size = size;
	}


	@Override
	public boolean add(MGOffset offset) {
		// if the array is to small we resize it before adding the data
		if (size >= position.length) {
			// we multiply the current size by the resize multiplication factor
			int newLength = position.length * RESIZE_FACTOR;
			// we make sure we don't add less than RESIZE_MIN elements
			newLength = Math.max(newLength, position.length + RESIZE_MIN);
			// we make sure we don't add more than RESIZE_MAX elements
			newLength = Math.min(newLength, position.length + RESIZE_MAX);
			int[] newPosition = new int[newLength];
			int[] newValue = new int[newLength];
			for (int i = 0; i < position.length; i++) {
				newPosition[i] = position[i];
				newValue[i] = value[i];
			}
			position = newPosition;
			value = newValue;
		}
		// true if e is not zero
		position[size] = offset.getPosition();
		value[size] = offset.getValue();
		size++;
		return true;
	}


	@Override
	public MGOffset get(int index) {
		return new MGOffset(position[index], value[index]);
	}


	/**
	 * @return null in order to accelerate the operation
	 */
	@Override
	public MGOffset set(int index, MGOffset offset) {
		position[index] = offset.getPosition();
		value[index] = offset.getValue();
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
		int[] positionTmp = new int[size];
		int[] valueTmp = new int[size];
		for (int i = 0; i < size; i++) {
			positionTmp[i] = position[i];
			valueTmp[i] = value[i];
		}
		position = positionTmp;
		value = valueTmp;
	}

}
