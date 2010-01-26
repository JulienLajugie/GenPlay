/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.list.arrayList;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.List;


/**
 * An array of booleans encapsulated in order to implement the {@link List} interface with Double parameter
 * It means that the methods get and set work with Double objects
 * @author Julien Lajugie
 * @version 0.1
 */
public final class BooleanArrayAsDoubleList extends AbstractList<Double> implements Serializable, List<Double> {

	private static final long serialVersionUID = -5280328695672981245L;	// generated ID
	private static final int 	RESIZE_STEP = 10000;	// length added every time the array is resized
	private boolean[] 			data;					// boolean data array
	private int 				size;					// size of the list
	
	
	/**
	 * Creates an instance of {@link BooleanArrayAsDoubleList}
	 */
	public BooleanArrayAsDoubleList() {
		this.data = new boolean[0];
		this.size = 0;
	}
	
	
	/**
	 * Creates an instance of {@link BooleanArrayAsDoubleList}
	 * @param size size of the array
	 */
	public BooleanArrayAsDoubleList(int size) {
		this.data = new boolean[size];
		this.size = size;
	}
	

	/**
	 * Unsupported operation
	 */
	public void sort() {
		throw (new UnsupportedOperationException("Invalid operation, can't sort a boolean array"));
	};

	
	@Override
	public boolean add(Double e) {
		// if the array is to small we resize it before adding the data
		if (size >= data.length) {
			boolean[] newData = new boolean[data.length + RESIZE_STEP];			
			for (int i = 0; i < data.length; i++) {
				newData[i] = data[i];
			}
			data = newData;			
		}
		// true if e is not zero
		data[size] = (e != 0d);
		size++;
		return true;
	}
	
	
	@Override
	public Double get(int index) {
		return (data[index] ? 1d : 0d);
	}

	
	/**
 	 * @return null in order to accelerate the operation
	 */
	@Override
	public Double set(int index, Double element) {
		data[index] = (element != 0);
		return null;
	}

	
	@Override
	public int size() {
		return size;
	}
}
