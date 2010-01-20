/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.list.binList;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.List;


/**
 * An array of floats encapsulated in order to implement the {@link List} interface with Double parameter
 * It means that the methods get and set work with Double objects
 * @author Julien Lajugie
 * @version 0.1
 */
public final class FloatArrayList extends AbstractList<Double> implements Serializable, List<Double> {

	private static final long serialVersionUID = -5280328695672981245L;	// generated ID
	private float[] 	data;	// float data array
	
	
	/**
	 * Creates an instance of {@link DataArrayList}
	 * @param size size of the array
	 */
	public FloatArrayList(int size) {
		data = new float[size];
	}
	

	public void sort() {
		Arrays.sort(data);
	};

	
	@Override
	public Double get(int index) {
		return (double)data[index];
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
		return data.length;
	}
}
