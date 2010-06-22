/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.enums;


/**
 * A type of filter
 * @author Julien Lajugie
 * @version 0.1
 */
public enum FilterType {
	
	/**
	 * filter a fixed number of values
	 */
	COUNT ("Count"),
	/**
	 * 
	 */	
	DENSITY ("Density"),
	/**
	 * filter a percentage of extreme values
	 */
	PERCENTAGE ("Percentage"),
	/**
	 * filter values above or under a specified threshold
	 */
	THRESHOLD ("Threshold"),
	/**
	 * filter values between two threshold
	 */
	BANDSTOP ("Band-Stop");

	
	private final String name; // String representing the filter 
	
	
	/**
	 * Private constructor. Creates an instance of {@link FilterType}
	 * @param name
	 */
	private FilterType(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
