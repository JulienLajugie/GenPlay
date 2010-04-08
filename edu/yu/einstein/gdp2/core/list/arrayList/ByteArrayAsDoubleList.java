/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.list.arrayList;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.List;

import yu.einstein.gdp2.exception.valueOutOfRangeException.Invalid8BitValue;


/**
 * An array of bytes encapsulated in order to implement the {@link List} interface with Double parameter
 * It means that the methods get and set work with Double objects
 * @author Julien Lajugie
 * @version 0.1
 */
public final class ByteArrayAsDoubleList extends AbstractList<Double> implements Serializable, List<Double> {

	private static final long serialVersionUID = -5280328695672981245L;	// generated ID
	private static final int 	RESIZE_MIN = 1000;		// minimum length added every time the array is resized
	private static final int 	RESIZE_MAX = 10000000;	// maximum length added every time the array is resized
	private static final int 	RESIZE_FACTOR = 2;		// multiplication factor of the length of the array every time it's resized
	private byte[] 				data;					// byte data array
	private int 				size;					// size of the list
	

	/**
	 * Maximum value on 8Bit 
	 */
	public static final int MAX_VALUE = Byte.MAX_VALUE + 128;
	
	
	/**
	 * Minimum value on 8Bit
	 */
	public static final int MIN_VALUE = Byte.MIN_VALUE + 128;
	
	
	/**
	 * Creates an instance of {@link ByteArrayAsDoubleList}
	 */
	public ByteArrayAsDoubleList() {
		this.data = new byte[0];
		this.size = 0;
	}
	
	
	/**
	 * Creates an instance of {@link ByteArrayAsDoubleList}
	 * @param size size of the array
	 */
	public ByteArrayAsDoubleList(int size) {
		this.data = new byte[size];
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
		// check if the value is in the range
		if ((e > MAX_VALUE) || (e < MIN_VALUE)) {
			throw new Invalid8BitValue(e);
		}
		// we round the value
		e =  Math.rint(e);
		// we subtract 128 because bytes are btw -128 and 127 and we want values btw 0 and 255
		e -= 128;
		
		// if the array is to small we resize it before adding the data
		if (size >= data.length) {
			// we multiply the current size by the resize multiplication factor
			int newLength = data.length * RESIZE_FACTOR;
			// we make sure we don't add less than RESIZE_MIN elements
			newLength = Math.max(newLength, data.length + RESIZE_MIN);
			// we make sure we don't add more than RESIZE_MAX elements
			newLength = Math.min(newLength, data.length + RESIZE_MAX);
			byte[] newData = new byte[newLength];			
			for (int i = 0; i < data.length; i++) {
				newData[i] = data[i];
			}
			data = newData;			
		}
		data[size] = e.byteValue();
		size++;
		return true;
	}
	
	
	@Override
	public Double get(int index) {
		// we add 128 because bytes are btw -128 and 127 and we want values btw 0 and 255
		return (double)(data[index] + 128);
	}

	
	/**
 	 * @return null in order to accelerate the operation
	 */
	@Override
	public Double set(int index, Double element) {
		// check if the value is in the range
		if ((element> MAX_VALUE) || (element < MIN_VALUE)) {
			throw new Invalid8BitValue(element);
		}
		// we round the value
		element =  Math.rint(element);
		// we subtract 128 because bytes are btw -128 and 127 and we want values btw 0 and 255
		element -= 128;
		data[index] = element.byteValue();
		return null;
	}

	
	@Override
	public int size() {
		return size;
	}
}
