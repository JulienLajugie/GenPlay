/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.list.arrayList;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import yu.einstein.gdp2.exception.CompressionException;
import yu.einstein.gdp2.exception.valueOutOfRangeException.Invalid8BitValue;


/**
 * An array of bytes encapsulated in order to implement the {@link List} interface with Double parameter
 * It means that the methods get and set work with Double objects
 * @author Julien Lajugie
 * @version 0.1
 */
public final class ByteArrayAsDoubleList extends ArrayAsDoubleList<byte[]> implements Serializable, List<Double>, CompressibleList {

	private static final long serialVersionUID = -5280328695672981245L;	// generated ID
	

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
		super();
		this.data = new byte[0];
	}
	
	
	/**
	 * Creates an instance of {@link ByteArrayAsDoubleList}
	 * @param size size of the array
	 */
	public ByteArrayAsDoubleList(int size) {
		super(size);
		this.data = new byte[size];
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
		// throw an exception if the list is compressed
		if (isCompressed()) {
			throw new CompressionException("Compressed List: Invalid Operation");
		}
		// we add 128 because bytes are btw -128 and 127 and we want values btw 0 and 255
		return (double)(data[index] + 128);
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
}
