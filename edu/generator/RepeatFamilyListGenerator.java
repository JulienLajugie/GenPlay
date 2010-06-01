/**
 * @author Julien Lajugie
 * @version 0.1
 */
package generator;

import yu.einstein.gdp2.core.list.repeatFamilyList.RepeatFamilyList;
import yu.einstein.gdp2.exception.InvalidChromosomeException;

/**
 * The interface RepeatFamilyListGenerator could be implemented by a class able to create a {@link RepeatFamilyList}
 * @author Julien Lajugie
 * @version 0.1
 */
public interface RepeatFamilyListGenerator extends Generator {
	
	/**
	 * @return a new {@link RepeatFamilyList}
	 * @throws InvalidChromosomeException
	 */
	public RepeatFamilyList toRepeatFamilyList() throws InvalidChromosomeException;
}
