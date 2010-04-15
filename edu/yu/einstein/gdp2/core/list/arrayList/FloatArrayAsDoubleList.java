/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.list.arrayList;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import yu.einstein.gdp2.exception.CompressionException;


/**
 * An array of floats encapsulated in order to implement the {@link List} interface with Double parameter
 * It means that the methods get and set work with Double objects
 * @author Julien Lajugie
 * @version 0.1
 */
public final class FloatArrayAsDoubleList extends ArrayAsDoubleList<float[]> implements Serializable, List<Double>, CompressibleList {

	private static final long serialVersionUID = -5280328695672981245L;	// generated ID
	
	
	/**
	 * Creates an instance of {@link FloatArrayAsDoubleList}
	 */
	public FloatArrayAsDoubleList() {
		super();
		this.data = new float[0];
	}
	
	
	/**
	 * Creates an instance of {@link FloatArrayAsDoubleList}
	 * @param size size of the array
	 */
	public FloatArrayAsDoubleList(int size) {
		super(size);
		this.data = new float[size];
	}
	

	@Override
	public void sort() {
		// throw an exception if the list is compressed
		if (isCompressed()) {
			throw new CompressionException("Compressed List: Invalid Operation");
		}
		Arrays.sort(data);
	};

	
	@Override
	public boolean add(Double e) {
		// throw an exception if the list is compressed
		if (isCompressed()) {
			throw new CompressionException("Compressed List: Invalid Operation");
		}
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
		// throw an exception if the list is compressed
		if (isCompressed()) {
			throw new CompressionException("Compressed List: Invalid Operation");
		}
		return (double)data[index];
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
		data[index] = element.floatValue();
		return null;
	}
}
