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
import java.util.Arrays;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;


/**
 * An array of floats encapsulated in order to implement the {@link List} interface with Double parameter
 * It means that the methods get and set work with Double objects
 * @author Julien Lajugie
 * @version 0.1
 */
public final class FloatArrayAsDoubleList extends AbstractList<Double> implements Serializable, List<Double>, CompressibleList {

	private static final long serialVersionUID = -5280328695672981245L;	// generated ID
	private static final int 	RESIZE_MIN = 1000;		// minimum length added every time the array is resized
	private static final int 	RESIZE_MAX = 10000000;	// maximum length added every time the array is resized
	private static final int 	RESIZE_FACTOR = 2;		// multiplication factor of the length of the array every time it's resized
	private float[] 			data;					// float data array
	private int 				size;					// size of the list
	private boolean				isCompressed = false;	// true if the list is compressed
	transient private ByteArrayOutputStream	compressedData = null; // list compressed as a ByteArrayOutputStream
	
	
	/**
	 * Creates an instance of {@link FloatArrayAsDoubleList}
	 */
	public FloatArrayAsDoubleList() {
		this.data = new float[0];
		this.size = 0;
	}
	
	
	/**
	 * Creates an instance of {@link FloatArrayAsDoubleList}
	 * @param size size of the array
	 */
	public FloatArrayAsDoubleList(int size) {
		this.data = new float[size];
		this.size = size;
	}
	

	/**
	 * Sorts the list
	 */
	public void sort() {
		Arrays.sort(data);
	};

	
	@Override
	public boolean add(Double e) {
		// if the array is to small we resize it before adding the data
		if (size >= data.length) {
			// we multiply the current size by the resize multiplication factor
			int newLength = data.length * RESIZE_FACTOR;
			// we make sure we don't add less than RESIZE_MIN elements
			newLength = Math.max(newLength, data.length + RESIZE_MIN);
			// we make sure we don't add more than RESIZE_MAX elements
			newLength = Math.min(newLength, data.length + RESIZE_MAX);
			float[] newData = new float[newLength];			
			for (int i = 0; i < data.length; i++) {
				newData[i] = data[i];
			}
			data = newData;			
		}
		data[size] = e.floatValue();
		size++;
		return true;
	}
	
	
	@Override
	public Double get(int index) {
www
	}

	
	/**
 	 * @return null in order to accelerate the operation
	 */
	@Override
	public Double set(int index, Double element) {
		data[index] = element.floatValue();
		return null;
	}

	
	@Override
	public int size() {
		return size;
	}
	
	
	@Override
	public void compress() throws IOException {
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
		System.gc();
	}


	@Override
	public void uncompress() throws IOException, ClassNotFoundException {
		ByteArrayInputStream bais = new ByteArrayInputStream(compressedData.toByteArray());
		GZIPInputStream gz = new GZIPInputStream(bais);
		ObjectInputStream ois = new ObjectInputStream(gz);
		data = (float[])ois.readObject();
		compressedData = null;
		isCompressed = false;
		System.gc();
	}
	
	
	@Override
	public boolean isCompressed() {
		return isCompressed;
	}
	
	
	private void writeObject(ObjectOutputStream out) throws IOException {
		if (isCompressed()) {
			try {
				uncompress();
				out.defaultWriteObject();
				compress();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		} else {
			out.defaultWriteObject();
		}
	}


	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		if (isCompressed) {
			compress();
		}
	}
}
