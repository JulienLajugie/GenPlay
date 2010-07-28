/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.generator;

import java.util.concurrent.ExecutionException;

import yu.einstein.gdp2.core.enums.ScoreCalculationMethod;
import yu.einstein.gdp2.core.list.SCWList.ScoredChromosomeWindowList;
import yu.einstein.gdp2.exception.InvalidChromosomeException;

/**
 * The interface ScoredChromosomeWindowListGenerator could be implemented by a class able to create a {@link ScoredChromosomeWindowList}
 * @author Julien Lajugie
 * @version 0.1
 */
public interface ScoredChromosomeWindowListGenerator extends Generator {
	
	/**
	 * @param scm ScoreCalculationMethod to know how to calculate score in case of overlapping
	 * @return a new {@link ScoredChromosomeWindowList}
	 * @throws InvalidChromosomeException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public ScoredChromosomeWindowList toScoredChromosomeWindowList(ScoreCalculationMethod scm) throws InvalidChromosomeException, InterruptedException, ExecutionException;
	
	/**
	 * Allows to check if overlapping regions exists.
	 * @return	boolean
	 */
	public boolean overlapped ();
}
