/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.exception.valueOutOfRangeException;


/**
 * {@link RuntimeException} thrown when a value is out of the range of a specific data type
 * @author Julien Lajugie
 * @version 0.1
 */
public class ValueOutOfRangeException extends RuntimeException {

	private static final long serialVersionUID = 7840275107495242440L; // generated ID

	
	/**
	 * Creates an instance of {@link ValueOutOfRangeException}
	 * @param message error message
	 */
	public ValueOutOfRangeException(String message) {
		super(message);
	}
}
