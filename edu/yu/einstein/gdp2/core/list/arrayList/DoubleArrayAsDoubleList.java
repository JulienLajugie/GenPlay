/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.list.arrayList;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;


/**
 * An array of doubles encapsulated in order to implement the {@link List} interface with Double parameter
 * @author Julien Lajugie
 * @version 0.1
 */
public final class DoubleArrayAsDoubleList extends ArrayAsDoubleList<double[]> implements Serializable, List<Double>, CompressibleList {

	private static final long serialVersionUID = -5280328695672981245L;	// generated ID
	
	
	/**
	 * Creates an instance of {@link DoubleArrayAsDoubleList}
	 */
	public DoubleArrayAsDoubleList() {
		super();
		this.data = new double[0];
	}
	
	
	/**
	 * Creates an instance of {@link DoubleArrayAsDoubleList}
	 * @param size size of the list
	 */
	public DoubleArrayAsDoubleList(int size) {
		super(size);
		this.data = new double[size];
	}


	@Override	
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
			double[] newData = new double[newLength];			
			for (int i = 0; i < data.length; i++) {
				newData[i] = data[i];
			}
			data = newData;			
		}
		data[size] = e;
		size++;
		return true;
	}


	@Override
	public Double get(int index) {
		return data[index];
	}


	/**
	 * @return null in order to accelerate the operation
	 */
	@Override
	public Double set(int index, Double element) {
		data[index] = element;
		return null;
	}
}
