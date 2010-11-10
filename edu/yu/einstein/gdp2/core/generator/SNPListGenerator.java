/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.generator;

import java.util.concurrent.ExecutionException;

import yu.einstein.gdp2.core.SNPList.SNPList;
import yu.einstein.gdp2.exception.InvalidChromosomeException;


/**
 * The interface SNPListGenerator could be implemented by the class able to create a {@link SNPList}
 * @author Julien Lajugie
 * @version 0.1
 */
public interface SNPListGenerator extends Generator {

	
	/**
	 * @return a new SNPList created from the extracted data
	 * @throws InvalidChromosomeException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public SNPList toSNPList() throws InvalidChromosomeException, InterruptedException, ExecutionException;
}
