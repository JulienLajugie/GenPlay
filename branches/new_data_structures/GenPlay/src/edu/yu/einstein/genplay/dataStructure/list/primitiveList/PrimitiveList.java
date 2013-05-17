package edu.yu.einstein.genplay.dataStructure.list.primitiveList;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.AbstractList;
import java.util.List;
import java.util.RandomAccess;

import edu.yu.einstein.genplay.dataStructure.compressible.CompressibleList;
import edu.yu.einstein.genplay.exception.exceptions.CompressionException;

public class PrimitiveList<T> extends AbstractList<T> implements Serializable, RandomAccess, CompressibleList<T> {

	/** Version number of the class */
	private static final transient int CLASS_VERSION_NUMBER = 0;

	/** Size of the sub arrays */
	private static final int ARRAY_SIZE = 5000;

	/** List compressed as a ByteArrayOutputStream */
	private transient ByteArrayOutputStream	compressedData = null;

	/** Size of the list */
	private final int size = 0;

	/** Data of the list */
	private List<List<T>> data;

	/** True if the list is compressed */
	private final boolean isCompressed = false;



	@Override
	public boolean add(T e) {
		/*	if (data.isEmpty()) {
			data.add(new byte[ARRAY_SIZE]);
		}
		List<T> currentArray = data.get(data.size() - 1);
		if (currentIndex < currentArray.length) {
			currentArray[currentIndex] = e;
			currentIndex++;
			return true;
		} else {
			data.add(new byte[ARRAY_SIZE]);
			currentIndex = 0;
			return this.add(e);
		}
		new ArrayList<E>();*/
		return true;
	}

	@Override
	public void compress() throws CompressionException {
		// TODO Auto-generated method stub

	}

	@Override
	public T get(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isCompressed() throws CompressionException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void uncompress() {
		// TODO Auto-generated method stub

	}

}
