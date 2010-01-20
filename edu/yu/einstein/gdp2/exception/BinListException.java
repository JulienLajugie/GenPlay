/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.exception;

/**
 * @author Julien Lajugie
 * @version 0.1
 * The BinListException abstract class is extended by all the exceptions related to the BinList class.
 */
public abstract class BinListException extends Exception {

	/**
	 * Generated ID 
	 */
	private static final long serialVersionUID = -8382815211843422128L;

	/**
	 * Constructor
	 * @param message Error message.
	 * @param reason Reason of the error.
	 */
	public BinListException(String message, Throwable reason) {
		super(message);
	}
	
	/**
	 * Constructor.
	 * @param message Error message.
	 */
	public BinListException(String message) {
		super(message);
	}
}
