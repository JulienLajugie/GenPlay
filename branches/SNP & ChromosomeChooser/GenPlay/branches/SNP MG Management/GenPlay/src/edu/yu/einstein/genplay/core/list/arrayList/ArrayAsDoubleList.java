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
 *     Author: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *     Website: <http://genplay.einstein.yu.edu>
 *******************************************************************************/
package edu.yu.einstein.genplay.core.list.arrayList;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import edu.yu.einstein.genplay.exception.CompressionException;



/**
 * Abstract class. Represents a {@link Serializable} and {@link CompressibleList} of {@link Double}.
 * The internal implementation of the data storage is left to the subclasses.
 * @param <T> type of the internal data representing the List of Double
 * @author Julien Lajugie
 * @version 0.1
 */
public abstract class ArrayAsDoubleList<T> extends AbstractList<Double> implements Serializable, List<Double>, CompressibleList {

	private static final long serialVersionUID = -4745728013829849L; // generated ID
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 0;		// saved format version
	protected static final int 	RESIZE_MIN = 1000;		// minimum length added every time the array is resized
	protected static final int 	RESIZE_MAX = 10000000;	// maximum length added every time the array is resized
	protected static final int 	RESIZE_FACTOR = 2;		// multiplication factor of the length of the array every time it's resized
	protected T					data;					// byte data array (8 booleans / byte)
	protected int 				size = 0;				// size of the list	
	private boolean				isCompressed = false;	// true if the list is compressed
	transient private ByteArrayOutputStream	compressedData = null; 	// list compressed as a ByteArrayOutputStream


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		/* Makes sure that the list is uncompressed before serialization.
		   Recompresses the list is the list has been uncompressed. */
		if (isCompressed()) {
			try {
				uncompress();
				out.writeObject(data);
				compress();
			} catch (CompressionException e) {
				e.printStackTrace();
			}
		} else {
			out.writeObject(data);
		}
		out.writeInt(size);
	}


	/**
	 * Method used for unserialization
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.readInt();
		data = (T) in.readObject();
		size = in.readInt();
		isCompressed = false;
	}


	/**
	 * Sorts the list
	 */
	public abstract void sort();


	/**
	 * Default constructor. Do nothing.
	 */
	public ArrayAsDoubleList() {
		super();
	}


	/**
	 * Constructor. Sets the size of the list.
	 * @param size size of the array
	 */
	public ArrayAsDoubleList(int size) {
		super();
		this.size = size;
	}


	@Override
	public int size() {
		return size;
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


	@SuppressWarnings("unchecked")
	@Override
	public void uncompress() throws CompressionException {
		try {
			if ((isCompressed) && (compressedData != null)) {
				ByteArrayInputStream bais = new ByteArrayInputStream(compressedData.toByteArray());
				GZIPInputStream gz = new GZIPInputStream(bais);
				ObjectInputStream ois = new ObjectInputStream(gz);
				data = (T) ois.readObject();
				compressedData = null;
				isCompressed = false;
			}
		} catch (IOException e) {
			throw new CompressionException("An error occure during the data uncompression");
		} catch (ClassNotFoundException e) {
			throw new CompressionException("An error occure during the data uncompression");
		}
	}


	@Override
	public boolean isCompressed() {
		return isCompressed;
	}	 
}
