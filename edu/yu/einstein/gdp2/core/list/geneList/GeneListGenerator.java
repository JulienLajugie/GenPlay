/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.list.geneList;

import yu.einstein.gdp2.exception.InvalidChromosomeException;
import yu.einstein.gdp2.exception.ManagerDataNotLoadedException;

/**
 * The interface GeneListGenerator could be implemented by a class able to create a {@link GeneList}
 * @author Julien Lajugie
 * @version 0.1
 */
public interface GeneListGenerator {
	
	/**
	 * @return a new {@link GeneList}
	 */
	public GeneList toGeneList() throws ManagerDataNotLoadedException, InvalidChromosomeException;
}
