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
package edu.yu.einstein.genplay.core.multiGenome.data.display.array;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import edu.yu.einstein.genplay.core.multiGenome.data.display.variant.Variant;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class MGVariantArray implements Serializable {

	/** Default serial version ID */
	private static final long serialVersionUID = -708407184414835799L;
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 0;		// saved format version
	private static final int 	DEFAULT_SIZE = 700000;				// minimum length added every time the array is resized
	private static final int 	RESIZE_MIN = 1000;					// minimum length added every time the array is resized
	private static final int 	RESIZE_MAX = 10000000;				// maximum length added every time the array is resized
	private static final int 	RESIZE_FACTOR = 2;					// multiplication factor of the length of the array every time it's resized
	private Variant[] 			data;								// int data array
	private int 				size;								// size of the list


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
		data = (Variant[]) in.readObject();
		size = in.readInt();
	}


	/**
	 * Creates an instance of {@link MGVariantArray}
	 */
	public MGVariantArray() {
		initialize(DEFAULT_SIZE);
	}


	/**
	 * Creates an instance of {@link MGVariantArray}
	 * @param size size of the array
	 */
	public MGVariantArray(int size) {
		initialize(size);
	}


	/**
	 * Initialize the data array
	 * @param size size of the array
	 */
	private void initialize (int size) {
		this.data = new Variant[size];
		this.size = size;
		fill(null);
	}


	/**
	 * Fill the data array with an unique value
	 * @param value the value to set
	 */
	private void fill (Variant value) {
		for (int i = 0; i < size; i++) {
			data[i] = value;
		}
	}


	/**
	 * Resize the array
	 */
	@SuppressWarnings("unused")
	private void resize () {
		if (size >= data.length) {
			// we multiply the current size by the resize multiplication factor
			int newLength = data.length * RESIZE_FACTOR;
			// we make sure we don't add less than RESIZE_MIN elements
			newLength = Math.max(newLength, data.length + RESIZE_MIN);
			// we make sure we don't add more than RESIZE_MAX elements
			newLength = Math.min(newLength, data.length + RESIZE_MAX);
			Variant[] newData = new Variant[newLength];
			for (int i = 0; i < data.length; i++) {
				newData[i] = data[i];
			}
			data = newData;
		}
	}


	/**
	 * @param index the index of the element to get
	 * @return	the element at the given index
	 */
	public Variant get(int index) {
		return data[index];
	}


	/**
	 * @param index index to set the element
	 * @param element element to set
	 * @return the element at the index before the set
	 */
	public Variant set(int index, Variant element) {
		Variant old = data[index];
		data[index] = element;
		return old;
	}


	/**
	 * @return the size of the array
	 */
	public int size() {
		return size;
	}


	/**
	 * Resize the array copying every value to a new array from the beginning until the new size
	 * @param newSize the new size
	 */
	public void resize (int newSize) {
		Variant[] newData = new Variant[newSize];
		for (int i = 0; i < newSize; i++) {
			newData[i] = data[i];
		}
		data = newData;
		size = newSize;
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
