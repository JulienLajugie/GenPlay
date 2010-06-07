/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.exception;

/**
 * Exception thrown when the lambda parameter for Poisson calculation is inferior or equal to zero
 * @author Nicolas Fourel
 * @version 0.1
 */

public final class InvalidLambdaPoissonParameterException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6501319100070818381L;

	/**
	 * Creates an instance of {@link InvalidLambdaPoissonParameterException}
	 */
	public InvalidLambdaPoissonParameterException() {
		super("Lambda parameter cannot be negative or equal to 0 to calculate Poisson value.");
	}
	
	public InvalidLambdaPoissonParameterException(String message) {
		super(message);
	}
}
