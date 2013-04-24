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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import edu.yu.einstein.genplay.dataStructure.halfFloat.HalfFloat;
import edu.yu.einstein.genplay.exception.ExceptionManager;
import edu.yu.einstein.genplay.exception.exceptions.CompressionException;

/**
 * A memory efficient implementation of the {@link List} interface with Float generic parameter.
 * The data of the list are stored in {@link ArrayList} objects of arrays of char primitives.
 * The char primitives are used to store 16-bit floating-point values. The are converted into
 * and from float using the tools from the {@link HalfFloat} class.
 * @author Julien Lajugie
 */
public class ListOfHalfArraysAsFloatList extends AbstractList<Float> implements Serializable, List<Float>, CompressibleList {

	/** Generated serial ID */
	private static final long serialVersionUID = -4262827816090843034L;

	/** Version number of the class */
	private static final transient int CLASS_VERSION_NUMBER = 0;

	/** Size of the sub arrays */
	private static final int ARRAY_SIZE = 10000;

	/** List compressed as a ByteArrayOutputStream */
	private transient ByteArrayOutputStream	compressedData = null;

	/** Index of the last element in the current sub-array of the list */
	private int currentIndex = 0;

	/** Data of the list */
	private List<char[]> data;

	/** True if the list is compressed */
	private boolean isCompressed = false;


	/**
	 * Creates an instance of {@link ListOfHalfArraysAsFloatList}
	 */
	public ListOfHalfArraysAsFloatList() {
		data = new ArrayList<char[]>();
		data.add(new char[ARRAY_SIZE]);
	}


	/**
	 * Creates an instance of {@link ListOfHalfArraysAsFloatList}
	 * @param size size of the list
	 */
	public ListOfHalfArraysAsFloatList(int size) {
		int listCount = (size / ARRAY_SIZE) + 1;
		data = new ArrayList<char[]>(listCount);
	}


	@Override
	public boolean add(Float e) {
		char[] currentArray = data.get(data.size() - 1);
		if (currentIndex < currentArray.length) {
			currentArray[currentIndex] = HalfFloat.fromFloat(e);
			currentIndex++;
			return true;
		} else {
			data.add(new char[ARRAY_SIZE]);
			currentIndex = 0;
			return this.add(e);
		}
	}


	@Override
	public void compress() throws CompressionException {
		try {
			if ((!isCompressed) && (data != null)) {
				compressedData = new ByteArrayOutputStream();
				GZIPOutputStream gz = new GZIPOutputStream(compressedData);
				ObjectOutputStream oos = new ObjectOutputStream(gz);
				oos.writeObject(data);
				oos.flush();
				oos.close();
				gz.flush();
				gz.close();
				data = null;
				isCompressed = true;
			}
		} catch (IOException e) {
			throw new CompressionException("An error occure during the data compression");
		}
	}


	@Override
	public Float get(int index) {
		char[] currentArray = data.get(index / ARRAY_SIZE);
		int currentIndex = index % ARRAY_SIZE;
		return HalfFloat.toFloat(currentArray[currentIndex]);
	}


	@Override
	public boolean isCompressed() {
		return isCompressed;
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
		// read the non-final fields
		currentIndex = in.readInt();
		data = (List<char[]>) in.readObject();
		isCompressed = in.readBoolean();
		// compress the list if it was compressed when serialized
		if (isCompressed) {
			compress();
		}
	}


	@Override
	public Float set(int index, Float element) {
		char[] currentArray = data.get(index / ARRAY_SIZE);
		int currentIndex = index % ARRAY_SIZE;
		currentArray[currentIndex] = HalfFloat.fromFloat(element);
		return null;
	}


	@Override
	public int size() {
		int size = ((data.size() - 1) * ARRAY_SIZE) + currentIndex;
		return size;
	}


	@SuppressWarnings("unchecked")
	@Override
	public void uncompress() throws CompressionException {
		try {
			if ((isCompressed) && (compressedData != null)) {
				ByteArrayInputStream bais = new ByteArrayInputStream(compressedData.toByteArray());
				GZIPInputStream gz = new GZIPInputStream(bais);
				ObjectInputStream ois = new ObjectInputStream(gz);
				data = (List<char[]>) ois.readObject();
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
		// write the non-final fields
		out.writeInt(currentIndex);
		/* Makes sure that the list is uncompressed before serialization.
		   Recompresses the list is the list has been uncompressed. */
		if (isCompressed()) {
			try {
				uncompress();
				out.writeObject(data);
				compress();
			} catch (CompressionException e) {
				ExceptionManager.getInstance().caughtException(e);
			}
		} else {
			out.writeObject(data);
		}
		out.writeBoolean(isCompressed);
	}
}