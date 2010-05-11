/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.exception;

/**
 * @author Julien Lajugie
 * @version 0.1
 * The DifferentWindowException is thrown when an operation is attempted on two BinList with different window sizes.
 */
public class BinListDifferentWindowSizeException extends BinListException {
	/**
	 * Generated ID 
	 */
	private static final long serialVersionUID = -9080423756562654857L;
	/**
	 * Constructor.
	 */
	public BinListDifferentWindowSizeException() {
		super(new String("The window size of the two lists are different"));
	}
}
