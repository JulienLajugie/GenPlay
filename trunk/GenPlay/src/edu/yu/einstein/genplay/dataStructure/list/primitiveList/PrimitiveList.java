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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.RandomAccess;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import edu.yu.einstein.genplay.dataStructure.compressible.CompressibleList;
import edu.yu.einstein.genplay.dataStructure.enums.ScorePrecision;
import edu.yu.einstein.genplay.exception.ExceptionManager;
import edu.yu.einstein.genplay.exception.exceptions.CompressionException;
import edu.yu.einstein.genplay.exception.exceptions.InvalidPrimitiveArrayGenericParameterException;


/**
 * Abstract class defining the common methods and properties for the different of
 * primitive list wrappers implementing the {@link List} interface
 * @param <T> type of the data of the list
 * @author Julien Lajugie
 */
public class PrimitiveList<T> extends AbstractList<T> implements Cloneable, Serializable, List<T>, RandomAccess, CompressibleList<T> {

	/** Generated serial ID */
	private static final long serialVersionUID = -3250259696514106453L;

	/** Version number of the class */
	private static final transient int CLASS_VERSION_NUMBER = 0;

	/** Size of the sub arrays */
	private static final int DEFAULT_SUBARRAY_SIZE = 5000;

	/** Precision of the data of the project */
	private transient static ScorePrecision scorePrecision = ScorePrecision.PRECISION_32BIT;

	/**
	 * Sets the score precision of the project
	 * @param scorePrecision
	 */
	public static void setScorePrecision(ScorePrecision scorePrecision) {
		PrimitiveList.scorePrecision = scorePrecision;
	}

	/** We need to store the type of the element of the list because java
	 * generics are not reified */
	private final Class<T> elementClass;

	/** Size of the subarrays of the list */
	private final int subarraySize;

	/** List compressed as a ByteArrayOutputStream */
	private transient ByteArrayOutputStream	compressedData = null;

	/** Size of the list */
	private transient int size = 0;

	/** Data of the list */
	private transient List<PrimitiveArrayWrapper<T>> elementData;


	/** True if the list is compressed */
	private transient boolean isCompressed = false;


	/**
	 * Creates an instance of {@link PrimitiveList} with default initial capacity.
	 * @param elementClass class of the elements of the list
	 */
	public PrimitiveList(Class<T> elementClass) {
		this(elementClass, DEFAULT_SUBARRAY_SIZE);
	}


	/**
	 * Constructs an empty list with the specified initial capacity.
	 * @param elementClass class of the elements of the list
	 * @param subarraySize size of the subarrays constituting the list
	 * @throws IllegalArgumentException If the specified initial capacity is negative
	 */
	public PrimitiveList(Class<T> elementClass, int subarraySize) {
		super();
		if (subarraySize < 0) {
			throw new IllegalArgumentException("Illegal Subarray Size: " + subarraySize);
		}
		this.elementClass = elementClass;
		this.subarraySize = subarraySize;
		elementData = new ArrayList<PrimitiveArrayWrapper<T>>();
	}


	@Override
	public boolean add(T e) {
		int subarrayIndex = getSubarrayIndex(size);
		int indexWithinSubarray = getIndexWithinSubarray(size);
		ensureCapacity(++size);
		elementData.get(subarrayIndex).set(indexWithinSubarray, e);
		return true;
	}


	@Override
	public void compress() throws CompressionException {
		try {
			if ((!isCompressed) && (elementData != null)) {
				compressedData = new ByteArrayOutputStream();
				GZIPOutputStream gz = new GZIPOutputStream(compressedData);
				ObjectOutputStream oos = new ObjectOutputStream(gz);
				oos.writeObject(elementData);
				oos.flush();
				oos.close();
				gz.flush();
				gz.close();
				elementData = null;
				isCompressed = true;
			}
		} catch (IOException e) {
			throw new CompressionException("An error occure during the data compression");
		}
	}


	/**
	 * Increases the capacity of this PrimitiveList instance, if
	 * necessary, to ensure that it can hold at least the number of elements
	 * specified by the minimum capacity argument.
	 * @param   minCapacity   the desired minimum capacity
	 */
	public void ensureCapacity(int minCapacity) {
		modCount++;
		int oldCapacity = elementData.size() * subarraySize;
		while (minCapacity > oldCapacity) {
			@SuppressWarnings("unchecked")
			PrimitiveArrayWrapper<T> newSubarray = (PrimitiveArrayWrapper<T>) generateEmptyPrimitiveArrayWrapper(subarraySize);
			elementData.add(newSubarray);
			oldCapacity += subarraySize;
		}
	}


	/**
	 * @param capacity
	 * @return an empty {@link PrimitiveArrayWrapper} list and with the specified capacity
	 * @throws InvalidPrimitiveArrayGenericParameterException If the generic paramter of the instance is not valid
	 */
	private PrimitiveArrayWrapper<?> generateEmptyPrimitiveArrayWrapper(int capacity) throws InvalidPrimitiveArrayGenericParameterException {
		if (elementClass == Integer.class) {
			return new IntArrayWrapper(capacity);
		} else if (elementClass == Float.class) {
			if (scorePrecision == ScorePrecision.PRECISION_16BIT) {
				return new HalfArrayWrapper(capacity);
			} else if (scorePrecision == ScorePrecision.PRECISION_32BIT) {
				return new FloatArrayWrapper(capacity);
			}
		} else if (elementClass == Byte.class) {
			return new ByteArrayWrapper(capacity);
		} else if (elementClass == Boolean.class) {
			return new BooleanArrayWrapper(capacity);
		}
		throw new InvalidPrimitiveArrayGenericParameterException(elementClass);
	}


	@Override
	public T get(int index) {
		rangeCheck(index);
		int subarrayIndex = getSubarrayIndex(index);
		int indexWithinSubarray = getIndexWithinSubarray(index);
		return elementData.get(subarrayIndex).get(indexWithinSubarray);
	}


	/**
	 * @param index an index
	 * @return the index within the subarray containing the specified index
	 */
	private int getIndexWithinSubarray(int index) {
		return index % subarraySize;
	}


	/**
	 * @param index an index
	 * @return the index of the subarray containing the specified index
	 */
	private int getSubarrayIndex(int index) {
		return index / subarraySize;
	}


	@Override
	public boolean isCompressed() throws CompressionException {
		return isCompressed;
	}


	@Override
	public boolean isEmpty() {
		return size == 0;
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
	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		// read the class version number
		in.readInt();
		// read non transient fields
		in.defaultReadObject();
		// read other fields
		size = in.readInt();
		elementData = (List<PrimitiveArrayWrapper<T>>) in.readObject();
		isCompressed = in.readBoolean();
		// compress the list if it was compressed when serialized
		if (isCompressed) {
			compress();
		}
	}


	@Override
	public T set(int index, T element) {
		rangeCheck(index);
		int subarrayIndex = getSubarrayIndex(index);
		int indexWithinSubarray = getIndexWithinSubarray(index);
		return elementData.get(subarrayIndex).set(indexWithinSubarray, element);
	}


	@Override
	public int size() {
		return this.size;
	}


	/**
	 * Trims the capacity of this <tt>ArrayList</tt> instance to be the
	 * list's current size.  An application can use this operation to minimize
	 * the storage of an <tt>ArrayList</tt> instance.
	 */
	public void trimToSize() {
		modCount++;
		int oldCapacity = elementData.size() * subarraySize;
		if (size < oldCapacity) {
			PrimitiveArrayWrapper<T> lastSubarray = elementData.get(elementData.size() - 1);
			int lastSubarraySize = getIndexWithinSubarray(size);
			lastSubarray.trimToSize(lastSubarraySize);
		}
	}


	@SuppressWarnings("unchecked")
	@Override
	public void uncompress() throws CompressionException {
		try {
			if ((isCompressed) && (compressedData != null)) {
				ByteArrayInputStream bais = new ByteArrayInputStream(compressedData.toByteArray());
				GZIPInputStream gz = new GZIPInputStream(bais);
				ObjectInputStream ois = new ObjectInputStream(gz);
				elementData = (List<PrimitiveArrayWrapper<T>>) ois.readObject();
				compressedData = null;
				isCompressed = false;
			}
		} catch (IOException e) {
			throw new CompressionException("An error occure during the data uncompression");
		} catch (ClassNotFoundException e) {
			throw new CompressionException("An error occure during the data uncompression");
		}
	}


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		// write the class version number
		out.writeInt(CLASS_VERSION_NUMBER);
		// write non transient fields
		out.defaultWriteObject();
		// write the other fields
		out.writeInt(size);
		/* Makes sure that the list is uncompressed before serialization.
		   Recompresses the list is the list has been uncompressed. */
		if (isCompressed()) {
			try {
				uncompress();
				out.writeObject(elementData);
				compress();
			} catch (CompressionException e) {
				ExceptionManager.getInstance().caughtException(e);
			}
		} else {
			out.writeObject(elementData);
		}
		out.writeBoolean(isCompressed);
	}
}
