package yu.einstein.gdp2.core.list.arrayList;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.List;

public class IntArrayAsIntegerList extends AbstractList<Integer> implements Serializable, List<Integer> {

	private static final long serialVersionUID = -8787392051503707843L;
	private static final int 	RESIZE_STEP = 10000;	// length added every time the array is resized
	private int[] 				data;					// int data array
	private int 				size;					// size of the list
	
	
	/**
	 * Creates an instance of {@link IntArrayAsIntegerList}
	 */
	public IntArrayAsIntegerList() {
		this.data = new int[0];
		this.size = 0;
	}
	
	
	/**
	 * Creates an instance of {@link IntArrayAsIntegerList}
	 * @param size size of the array
	 */
	public IntArrayAsIntegerList(int size) {
		this.data = new int[size];
		this.size = size;
	}
	

	/**
	 * Sorts the list
	 */
	public void sort() {
		Arrays.sort(data);
	};

	
	@Override
	public boolean add(Integer e) {
		// if the array is to small we resize it before adding the data
		if (size >= data.length) {
			int[] newData = new int[data.length + RESIZE_STEP];			
			for (int i = 0; i < data.length; i++) {
				newData[i] = data[i];
			}
			data = newData;			
		}
		// true if e is not zero
		data[size] = e;
		size++;
		return true;
	}
	
	
	@Override
	public Integer get(int index) {
		return data[index];
	}

	
	/**
 	 * @return null in order to accelerate the operation
	 */
	@Override
	public Integer set(int index, Integer element) {
		data[index] = element;
		return null;
	}

	
	@Override
	public int size() {
		return size;
	}
}
