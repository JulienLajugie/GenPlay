/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.list.binList;

import java.util.List;

import yu.einstein.gdp2.core.enums.DataPrecision;
import yu.einstein.gdp2.core.list.arrayList.BooleanArrayAsDoubleList;
import yu.einstein.gdp2.core.list.arrayList.ByteArrayAsDoubleList;
import yu.einstein.gdp2.core.list.arrayList.DoubleArrayAsDoubleList;
import yu.einstein.gdp2.core.list.arrayList.FloatArrayAsDoubleList;
import yu.einstein.gdp2.core.list.arrayList.ShortArrayAsDoubleList;

/**
 * Factory creating a subtype of {@link List} of double
 * @author Julien Lajugie
 * @version 0.1
 */
public class ListFactory {

	/**
	 * @param precision precision of the data
	 * @param size size of the list
	 * @return a subtype of List<Double>
	 * @throws IllegalArgumentException thrown if the precision is not valid
	 */
	public static List<Double> createList(DataPrecision precision, int size) throws IllegalArgumentException {
		switch (precision) {
		case PRECISION_1BIT:
			return new BooleanArrayAsDoubleList(size);
		case PRECISION_8BIT:
			return new ByteArrayAsDoubleList(size);
		case PRECISION_16BIT:
			return new ShortArrayAsDoubleList(size);
		case PRECISION_32BIT:
			return new FloatArrayAsDoubleList(size);
		case PRECISION_64BIT:
			return new DoubleArrayAsDoubleList(size);
		default: 
			throw new IllegalArgumentException("invalid precision");
		}				
	}
}
