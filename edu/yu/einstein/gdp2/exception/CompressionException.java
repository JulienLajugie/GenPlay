/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.exception;


/**
 * Exception thrown when there is a problem during data compression
 * @author Julien Lajugie
 * @version 0.1
 */
public class CompressionException extends Exception {

	private static final long serialVersionUID = -7441678640263974386L; // generated ID

	
	/**
	 * Creates an instance of {@link CompressionException}
	 */
	public CompressionException() {
		super();
	}


	/**
	 * Creates an instance of {@link CompressionException}
	 * @param message error message
	 */
	public CompressionException(String message) {
		super(message);
	}
}
