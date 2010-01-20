/**
 * @author Alexander Golec
 * @version 0.1
 */
package yu.einstein.gdp2.exception;

/**
 * Exception thrown when a Poisson distribution is asked to be made from 
 * an invalid parameter. 
 * @author Alexander Golec
 * @version 0.1
 */
public final class PoissonInvalidParameterException extends Exception {
	
	private static final long serialVersionUID = -6091702295893962445L;

	/**
	 * Creates an instance of {@link PoissonInvalidParameterException}
	 */
	public PoissonInvalidParameterException() {
		super("Invalid parameter passed to Poisson distribution. ");
	}
	
	public PoissonInvalidParameterException(String message) {
		super(message);
	}
}
