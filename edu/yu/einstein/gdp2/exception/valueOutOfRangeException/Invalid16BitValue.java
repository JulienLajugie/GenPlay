/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.exception.valueOutOfRangeException;

import java.text.DecimalFormat;

import yu.einstein.gdp2.core.list.arrayList.ShortArrayAsDoubleList;


/**
 * {@link RuntimeException} thrown when a value is out of the range of a 16Bit data type
 * @author Julien Lajugie
 * @version 0.1
 */
public class Invalid16BitValue	extends ValueOutOfRangeException {

	private static final long serialVersionUID = 5100775209357414910L; // generated ID

	
	/**
	 * Creates an instance of {@link Invalid16BitValue}
	 * @param data the data that is out of range
	 */
	public Invalid16BitValue(Double data) {
		super("Invalid Data (score = " + new DecimalFormat("#.#").format(data) + "). A 16Bit value must be between " + ShortArrayAsDoubleList.MIN_VALUE + " and " + ShortArrayAsDoubleList.MAX_VALUE);
	}
}
