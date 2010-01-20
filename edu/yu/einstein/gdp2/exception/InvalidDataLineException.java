/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.exception;


/**
 * The InvalidDataLineException class is thrown when an extractor can't extract a line. 
 * @author Julien Lajugie
 * @version 0.1
 */
public final class InvalidDataLineException extends Exception {

	private static final long serialVersionUID = 7000180996789501289L;	// generated ID
	
	public InvalidDataLineException(String line) {
		super("Invalid data line: \"" + line + "\"");
	}
}
