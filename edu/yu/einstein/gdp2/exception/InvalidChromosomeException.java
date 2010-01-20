/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.exception;


/**
 * The InvalidChromosomeException class is thrown when a Chromosome is not valid. 
 * @author Julien Lajugie
 * @version 0.1
 */
public final class InvalidChromosomeException extends RuntimeException {

	private static final long serialVersionUID = -2244843030262784715L;	// Generated ID
	
	public InvalidChromosomeException() {
		super("Chromosome invalid.");
	}
}
