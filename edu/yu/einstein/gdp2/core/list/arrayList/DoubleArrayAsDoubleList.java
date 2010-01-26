/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.list.arrayList;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.List;


/**
 * An array of doubles encapsulated in order to implement the {@link List} interface with Double parameter
 * @author Julien Lajugie
 * @version 0.1
 */
public final class DoubleArrayAsDoubleList extends AbstractList<Double> implements Serializable, List<Double> {

	private static final long serialVersionUID = -5280328695672981245L;	// generated ID
	private static final int 	RESIZE_STEP = 10000;	// length added every time the array is resized
	private double[] 			data;					// double data array
	private int 				size;					// size of the list				

	
	/**
	 * Creates an instance of {@link DoubleArrayAsDoubleList}
	 */
	public DoubleArrayAsDoubleList() {
		this.data = new double[0];
		this.size = 0;
	}
	
	
	/**
	 * Creates an instance of {@link DoubleArrayAsDoubleList}
	 * @param size size of the list
	 */
	public DoubleArrayAsDoubleList(int size) {
		this.data = new double[size];
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
			double[] newData = new double[data.length + RESIZE_STEP];			
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


	@Override
	public int size() {
		return size;
	}
}
