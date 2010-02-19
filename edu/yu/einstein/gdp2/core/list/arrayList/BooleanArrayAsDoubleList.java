/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.list.arrayList;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.List;


/**
 * An array of bytes encapsulated in order to implement the {@link List} interface with Double parameter
 * <br>Each bytes represent 8 booleans. 
 * <br>This is an optimization in the memory usage since: 
 * <br>1 byte (representing 8 booleans) = 1 byte in memory
 * <br>better than 1 boolean = 1 byte in memory
 * <br>The methods get and set work with Double objects
 * @author Julien Lajugie
 * @version 0.1
 */
public final class BooleanArrayAsDoubleList extends AbstractList<Double> implements Serializable, List<Double> {

	private static final long serialVersionUID = -5280328695672981245L;	// generated ID
	private static final int 	RESIZE_STEP = 10000;	// length added every time the array is resized
	private byte[] 				data;					// byte data array (8 booleans / byte)
	private int 				size;					// size of the list
	
	
	/**
	 * Creates an instance of {@link BooleanArrayAsDoubleList}
	 */
	public BooleanArrayAsDoubleList() {
		this.data = new byte[0];
		this.size = 0;
	}
	
	
	/**
	 * Creates an instance of {@link BooleanArrayAsDoubleList}
	 * @param size size of the array
	 */
	public BooleanArrayAsDoubleList(int size) {
		// 1 byte = 8 booleans so the size of the byte array is 8 times smaller
		int realSize = (int)Math.ceil(size / 8) + 1;
		this.data = new byte[realSize];
		this.size = size;
	}
	

	/**
	 * Unsupported operation
	 */
	public void sort() {
		// no sort with booleans
		throw (new UnsupportedOperationException("Invalid operation, can't sort a boolean array"));
	};

	
	@Override
	public boolean add(Double e) {
		// if the array is to small we resize it before adding the data
		if (size >= data.length * 8) {
			int realResizeStep = (int)Math.ceil(RESIZE_STEP / 8);
			byte[] newData = new byte[data.length + realResizeStep];			
			for (int i = 0; i < data.length; i++) {
				newData[i] = data[i];
			}
			data = newData;
		}
		// we add the data
		set(size, e);
		size++;
		return true;
	}
	
	
	@Override
	public Double get(int index) {
		// real index is the index divided by 8
		int realIndex = index / 8;
		// compute the position of the desired boolean inside the byte
		int offset = index % 8;
		int result = data[realIndex] & (1 << offset);
		return result != 0 ? 1d : 0d;
	}

	
	/**
 	 * @return null in order to accelerate the operation
	 */
	@Override
	public Double set(int index, Double element) {
		// real index is the index divided by 8
		int realIndex = index / 8;
		// compute the position of the desired boolean inside the byte
		int offset = index % 8;
		if (element != 0) {
			// set the specified bit of the byte to 1
			data[realIndex] = (byte)(data[realIndex] | (1 << offset));
		} else {
			// set the specified bit of the byte to 0
			data[realIndex] = (byte)(data[realIndex] & (0xff ^ (1 << offset)));
		}
		return null;
	}

	
	@Override
	public int size() {
		return size;
	}
}
