/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.exception;

import yu.einstein.gdp2.core.ChromosomeWindow;

/**
 * The ChromosomeWindowException class represents an exception associated to a {@link ChromosomeWindow}. 
 * @author Julien Lajugie
 * @version 0.1
 */
public final class ChromosomeWindowException extends Exception {

	private static final long serialVersionUID = -4357641195693048950L;	// Generated ID
	
	/**
	 * Creates an instance of {@link ChromosomeWindowException}.
	 */
	public ChromosomeWindowException() {
		super("Invalid window.");
	}
	
	
	/**
	 * Creates an instance of {@link ChromosomeWindowException}.
	 * @param msg message of the error
	 */
	public ChromosomeWindowException(String msg) {
		super (msg);
	}	
}
