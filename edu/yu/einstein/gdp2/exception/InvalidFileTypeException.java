/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.exception;

/**
 * Exception thrown when a file can't be extracted
 * @author Julien Lajugie
 * @version 0.1
 */
public final class InvalidFileTypeException extends Exception {

	private static final long serialVersionUID = -2653999804996484400L; // generated ID
	
	/**
	 * Creates an instance of {@link InvalidFileTypeException}
	 */
	public InvalidFileTypeException() {
		super("Invalid file, the data can't be extracted");
	}
}
