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
 * An array of shorts encapsulated in order to implement the {@link List} interface with Double parameter
 * It means that the methods get and set work with Double objects
 * @author Julien Lajugie
 * @version 0.1
 */
public final class ShortArrayAsDoubleList extends AbstractList<Double> implements Serializable, List<Double> {

	private static final long serialVersionUID = -5280328695672981245L;	// generated ID
	private static final int 	RESIZE_STEP = 10000;	// length added every time the array is resized
	private short[] 			data;					// short data array
	private int 				size;					// size of the list
	
	
	/**
	 * Creates an instance of {@link ShortArrayAsDoubleList}
	 */
	public ShortArrayAsDoubleList() {
		this.data = new short[0];
		this.size = 0;
	}
	
	
	/**
	 * Creates an instance of {@link ShortArrayAsDoubleList}
	 * @param size size of the array
	 */
	public ShortArrayAsDoubleList(int size) {
		this.data = new short[size];
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
			short[] newData = new short[data.length + RESIZE_STEP];			
			for (int i = 0; i < data.length; i++) {
				newData[i] = data[i];
			}
			data = newData;			
		}
		data[size] = e.shortValue();
		size++;
		return true;
	}
	
	
	@Override
	public Double get(int index) {
		return (double)data[index];
	}

	
	/**
 	 * @return null in order to accelerate the operation
	 */
	@Override
	public Double set(int index, Double element) {
		data[index] = element.shortValue();
		return null;
	}

	
	@Override
	public int size() {
		return size;
	}
}