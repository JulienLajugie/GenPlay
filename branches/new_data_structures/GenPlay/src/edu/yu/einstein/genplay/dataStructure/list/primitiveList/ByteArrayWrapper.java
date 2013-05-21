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

import java.io.Serializable;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.List;


/**
 * Implementation of the {@link List} interface wrapping arrays of byte primitives.
 * @author Julien Lajugie
 */
class ByteArrayWrapper extends AbstractList<Byte> implements Serializable, List<Byte>,  PrimitiveArrayWrapper<Byte> {

	/** Generated serial ID */
	private static final long serialVersionUID = 1379175931787549433L;

	/** Data of the list */
	private final byte[] elementData;


	/**
	 * Creates an instance of {@link ByteArrayWrapper}
	 * @param size size of the
	 */
	ByteArrayWrapper(int capacity) {
		elementData = new byte[capacity];
	}


	@Override
	public Byte get(int index) {
		return elementData[index];
	}


	@Override
	public Byte set(int index, Byte element) {
		Byte oldElement = get(index);
		elementData[index] = element;
		return oldElement;
	}


	@Override
	public int size() {
		return elementData.length;
	}


	@Override
	public void trimToSize(int newCapacity) {
		Arrays.copyOf(elementData, newCapacity);
	}
}
