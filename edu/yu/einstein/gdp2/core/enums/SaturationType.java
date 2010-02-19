/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.enums;

import yu.einstein.gdp2.core.list.binList.BinListOperations;

/**
 * A type of saturation used in the class {@link BinListOperations}
 * @author Julien Lajugie
 * @version 0.1
 */
public enum SaturationType {
	
	/**
	 * saturation that saturates a fixed number of values
	 */
	COUNT ("Count"),
	/**
	 * saturation that saturates a percentage of extreme values
	 */
	PERCENTAGE ("Percentage"),
	/**
	 * saturation that saturates values above or under a specified threshold
	 */
	THRESHOLD ("Threashold");


	private final String name; // String representing the saturation 


	/**
	 * Private constructor. Creates an instance of {@link SaturationType}
	 * @param name
	 */
	private SaturationType(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}
}
