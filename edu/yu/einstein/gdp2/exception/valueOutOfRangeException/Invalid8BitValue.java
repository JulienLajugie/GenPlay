/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.exception.valueOutOfRangeException;

import java.text.DecimalFormat;

import yu.einstein.gdp2.core.list.arrayList.ByteArrayAsDoubleList;


/**
 * {@link RuntimeException} thrown when a value is out of the range of a 8Bit data type
 * @author Julien Lajugie
 * @version 0.1
 */
public class Invalid8BitValue extends ValueOutOfRangeException {

	private static final long serialVersionUID = -7111260909692592549L; // generated ID

	
	/**
	 * Creates an instance of {@link Invalid8BitValue}
	 * @param data the data that is out of range
	 */
	public Invalid8BitValue(Double data) {
		super("Invalid Data (score = " + new DecimalFormat("#.#").format(data) + "). A 8Bit value must be between " + ByteArrayAsDoubleList.MIN_VALUE + " and " + ByteArrayAsDoubleList.MAX_VALUE);
	}
}
