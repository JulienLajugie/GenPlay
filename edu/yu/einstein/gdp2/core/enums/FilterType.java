/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.enums;

import yu.einstein.gdp2.core.list.binList.BinListOperations;


/**
 * A type of filter used in the class {@link BinListOperations}
 * @author Julien Lajugie
 * @version 0.1
 */
public enum FilterType {
	/**
	 * filter that passes high values but cut low ones
	 */
	HIGH_PASS_FILTER ("High Pass Filter"),
	/**
	 * filter that passes low values but cut high ones
	 */
	LOW_PASS_FILTER ("Low Pass Filter");
	
	
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
