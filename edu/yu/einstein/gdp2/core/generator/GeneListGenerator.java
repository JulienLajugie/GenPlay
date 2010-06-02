/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.generator;

import java.util.concurrent.ExecutionException;

import yu.einstein.gdp2.core.list.geneList.GeneList;
import yu.einstein.gdp2.exception.InvalidChromosomeException;

/**
 * The interface GeneListGenerator could be implemented by a class able to create a {@link GeneList}
 * @author Julien Lajugie
 * @version 0.1
 */
public interface GeneListGenerator extends Generator {
	
	/**
	 * @return a new {@link GeneList}
	 * @throws InvalidChromosomeException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public GeneList toGeneList() throws InvalidChromosomeException, InterruptedException, ExecutionException;
}
