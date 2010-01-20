/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.enums;

import yu.einstein.gdp2.core.list.binList.BinList;


/**
 * Enumeration representing a method for the calculation of the scores of a {@link BinList}
 * @author Julien Lajugie
 * @version 0.1
 */
public enum ScoreCalculationMethod {

	AVERAGE ("Average"),
	MAXIMUM ("Maximum"),
	SUM ("Sum");
	
	private final String name;	// name of the method of score calculation
	
	/**
	 * Private constructor. Creates an instance of a {@link ScoreCalculationMethod}
	 * @param name name of the method of score calculation
	 */
	private ScoreCalculationMethod(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
