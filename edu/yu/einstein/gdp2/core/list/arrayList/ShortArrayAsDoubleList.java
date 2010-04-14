/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.list.arrayList;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import yu.einstein.gdp2.exception.valueOutOfRangeException.Invalid16BitValue;


/**
 * An array of shorts encapsulated in order to implement the {@link List} interface with Double parameter
 * It means that the methods get and set work with Double objects
 * @author Julien Lajugie
 * @version 0.1
 */
public final class ShortArrayAsDoubleList extends ArrayAsDoubleList<short[]> implements Serializable, List<Double>, CompressibleList {

	private static final long serialVersionUID = -5280328695672981245L;	// generated ID
	
	
	/**
	 * Maximum value on 16Bit 
	 */
	public static final int MAX_VALUE = Short.MAX_VALUE / 10;
	
	
	/**
	 * Minimum value on 16Bit
	 */
	public static final int MIN_VALUE = Short.MIN_VALUE / 10;
	
	
	/**
	 * Creates an instance of {@link ShortArrayAsDoubleList}
	 */
	public ShortArrayAsDoubleList() {
		super();
		this.data = new short[0];
	}
	
	
	/**
	 * Creates an instance of {@link ShortArrayAsDoubleList}
	 * @param size size of the array
	 */
	public ShortArrayAsDoubleList(int size) {
		super(size);
		this.data = new short[size];
	}
	

	@Override
	public void sort() {
		Arrays.sort(data);
	};

	
	@Override
	public boolean add(Double e) {
		// check if the value is in the range
		if ((e > MAX_VALUE) || (e < MIN_VALUE)) {
			throw new Invalid16BitValue(e);
		}
		// we multiply the result by 10 so we have a value btw  
		// Short.MIN_VALUE / 10 and Short.MAX_VALUE / 10 
		// with 1 digit after the point
		e *= 10;
		// we round the value
		e =  Math.rint(e);
		
		// if the array is to small we resize it before adding the data
		if (size >= data.length) {
			// we multiply the current size by the resize multiplication factor
			int newLength = data.length * RESIZE_FACTOR;
			// we make sure we don't add less than RESIZE_MIN elements
			newLength = Math.max(newLength, data.length + RESIZE_MIN);
			// we make sure we don't add more than RESIZE_MAX elements
			newLength = Math.min(newLength, data.length + RESIZE_MAX);
			short[] newData = new short[newLength];			
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
		return (double) (data[index] / 10);
	}

	
	/**
 	 * @return null in order to accelerate the operation
	 */
	@Override
	public Double set(int index, Double element) {
		// check if the value is in the range
		if ((element> MAX_VALUE) || (element < MIN_VALUE)) {
			throw new Invalid16BitValue(element);
		}
		// we multiply the result by 10 so we have a value btw  
		// Short.MIN_VALUE / 10 and Short.MAX_VALUE / 10 
		// with 1 digit after the point
		element *= 10;
		// we round the value
		element =  Math.rint(element);
		
		data[index] = element.shortValue();
		return null;
	}
}
