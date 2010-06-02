/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.generator;

import java.util.concurrent.ExecutionException;

import yu.einstein.gdp2.core.list.chromosomeWindowList.ChromosomeWindowList;
import yu.einstein.gdp2.exception.InvalidChromosomeException;


/**
 * The interface ChromosomeWindowListGenerator can be implemented by a class able to create a {@link ChromosomeWindowList}
 * @author Julien Lajugie
 * @version 0.1
 */
public interface ChromosomeWindowListGenerator extends Generator {
	
	/**
	 * @return a new {@link ChromosomeWindowList}
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public ChromosomeWindowList toChromosomeWindowList() throws InvalidChromosomeException, InterruptedException, ExecutionException;
}
