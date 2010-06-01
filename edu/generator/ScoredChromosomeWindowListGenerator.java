/**
 * @author Julien Lajugie
 * @version 0.1
 */
package generator;

import yu.einstein.gdp2.core.list.SCWList.ScoredChromosomeWindowList;
import yu.einstein.gdp2.exception.InvalidChromosomeException;

/**
 * The interface ScoredChromosomeWindowListGenerator could be implemented by a class able to create a {@link ScoredChromosomeWindowList}
 * @author Julien Lajugie
 * @version 0.1
 */
public interface ScoredChromosomeWindowListGenerator extends Generator {
	
	/**
	 * @return a new {@link ScoredChromosomeWindowList}
	 * @throws InvalidChromosomeException
	 */
	public ScoredChromosomeWindowList toScoredChromosomeWindowList() throws InvalidChromosomeException;
}
