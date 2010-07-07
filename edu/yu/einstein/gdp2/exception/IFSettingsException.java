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

public final class IFSettingsException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6501319100070818381L;

	/**
	 * Creates an instance of {@link IFSettingsException}
	 */
	public IFSettingsException() {
		super("Please select at least one result type.");
	}
	
	public IFSettingsException(String message) {
		super(message);
	}
}
