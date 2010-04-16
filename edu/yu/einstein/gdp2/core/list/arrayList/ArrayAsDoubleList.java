/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.list.arrayList;

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

import yu.einstein.gdp2.exception.CompressionException;


/**
 * Abstract class. Represents a {@link Serializable} and {@link CompressibleList} of {@link Double}.
 * The internal implementation of the data storage is left to the subclasses.
 * @author Julien Lajugie
 * @version 0.1
 * @param T type of the internal data representing the List of Double
 */
public abstract class ArrayAsDoubleList<T> extends AbstractList<Double> implements Serializable, List<Double>, CompressibleList {

	private static final long serialVersionUID = -4745728013829849L; // generated ID
	protected static final int 	RESIZE_MIN = 1000;		// minimum length added every time the array is resized
	protected static final int 	RESIZE_MAX = 10000000;	// maximum length added every time the array is resized
	protected static final int 	RESIZE_FACTOR = 2;		// multiplication factor of the length of the array every time it's resized
	protected T					data;					// byte data array (8 booleans / byte)
	protected int 				size = 0;				// size of the list	
	transient private boolean				isCompressed = false;	// true if the list is compressed
	transient private ByteArrayOutputStream	compressedData = null; 	// list compressed as a ByteArrayOutputStream


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


	/**
	 * Makes sure that the list is uncompressed before serialization.
	 * Recompresses the list is the list has been uncompressed.
	 * @param out {@link ObjectOutputStream}
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		if (isCompressed()) {
			try {
				uncompress();
				out.defaultWriteObject();
				compress();
			} catch (CompressionException e) {
				e.printStackTrace();
			}
		} else {
			out.defaultWriteObject();
		}
	}
}
