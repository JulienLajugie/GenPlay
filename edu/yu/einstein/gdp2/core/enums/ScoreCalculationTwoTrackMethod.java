/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.enums;

import yu.einstein.gdp2.core.list.binList.BinList;


/**
 * Enumeration representing a method for the calculation of the scores of a {@link BinList}
 * @author Julien Lajugie
 * @author Nicolas Fourel
 * @version 0.1
 */
public enum ScoreCalculationTwoTrackMethod {

	ADDITION ("Addition"),
	SUBTRACTION ("Subtraction"),
	MULTIPLICATION ("Multiplication"),
	DIVISION ("Division"),
	AVERAGE ("Average"),
	MAXIMUM ("Maximum"),
	MINIMUM ("Minimum");
	
	private final String name;	// name of the method of score calculation
	
	/**
	 * Private constructor. Creates an instance of a {@link ScoreCalculationTwoTrackMethod}
	 * @param name name of the method of score calculation
	 */
	private ScoreCalculationTwoTrackMethod(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	
	public ScoreCalculationTwoTrackMethod[] getOperationTwoTracks() {
		ScoreCalculationTwoTrackMethod[] result = {ADDITION, DIVISION};
		return result;
	}
}
