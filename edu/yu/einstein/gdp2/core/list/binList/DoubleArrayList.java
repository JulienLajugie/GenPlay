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
 * An array of doubles encapsulated in order to implement the {@link List} interface with Double parameter
 * @author Julien Lajugie
 * @version 0.1
 */
public final class DoubleArrayList extends AbstractList<Double> implements Serializable, List<Double> {

	private static final long serialVersionUID = -5280328695672981245L;	// generated ID
	private double[] 	data;					// double data array
	
	
	/**
	 * Creates an instance of {@link DataArrayList}
	 * @param size size of the array
	 */
	public DoubleArrayList(int size) {
		data = new double[size];
	}
	

	public void sort() {
		Arrays.sort(data);
	};

	
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
		return data.length;
	}
}
