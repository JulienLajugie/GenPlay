/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.list.repeatFamilyList;

import yu.einstein.gdp2.exception.InvalidChromosomeException;
import yu.einstein.gdp2.exception.ManagerDataNotLoadedException;

/**
 * The interface RepeatFamilyListGenerator could be implemented by a class able to create a {@link RepeatFamilyList}
 * @author Julien Lajugie
 * @version 0.1
 */
public interface RepeatFamilyListGenerator {
	
	/**
	 * @return a new {@link RepeatFamilyList}
	 */
	public RepeatFamilyList toRepeatFamilyList() throws ManagerDataNotLoadedException, InvalidChromosomeException;
}
