/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.list.chromosomeWindowList;

import yu.einstein.gdp2.exception.InvalidChromosomeException;
import yu.einstein.gdp2.exception.ManagerDataNotLoadedException;

/**
 * The interface ChromosomeWindowListGenerator can be implemented by a class able to create a {@link ChromosomeWindowList}
 * @author Julien Lajugie
 * @version 0.1
 */
public interface ChromosomeWindowListGenerator {
	
	/**
	 * @return a new {@link ChromosomeWindowList}
	 */
	public ChromosomeWindowList toChromosomeWindowList() throws ManagerDataNotLoadedException, InvalidChromosomeException;
}
