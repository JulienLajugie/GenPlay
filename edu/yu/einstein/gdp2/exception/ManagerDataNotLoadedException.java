/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.exception;

/**
 * The ManagerDataNotLoadedException class is thrown when a manager is used
 * without beeing initialized first. 
 * @author Julien Lajugie
 * @version 0.1
 */
public final class ManagerDataNotLoadedException extends RuntimeException {

	private static final long serialVersionUID = 6679771937728711079L;

	
	/**
	 * Creates an instance of {@link ManagerDataNotLoadedException}
	 */
	public ManagerDataNotLoadedException() {
		super("The manager data must be loaded before beeing used.");
	}
}
