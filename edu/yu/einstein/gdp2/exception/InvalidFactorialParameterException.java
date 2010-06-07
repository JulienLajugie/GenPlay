/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.exception;

/**
 * Exception thrown when the factorial parameter is inferior or equal to zero
 * @author Nicolas Fourel
 * @version 0.1
 */

public final class InvalidFactorialParameterException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6501319100070818381L;

	/**
	 * Creates an instance of {@link InvalidFactorialParameterException}
	 */
	public InvalidFactorialParameterException() {
		super("Factorial parameter cannot be negative or equal to 0.");
	}
	
	public InvalidFactorialParameterException(String message) {
		super(message);
	}
}
